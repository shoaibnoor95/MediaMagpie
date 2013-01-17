package de.wehner.mediamagpie.conductor.mail;

import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.Callable;

import javax.mail.MessagingException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import de.wehner.mediamagpie.common.persistence.entity.properties.MailServerConfiguration;
import de.wehner.mediamagpie.conductor.persistence.TransactionHandler;
import de.wehner.mediamagpie.conductor.persistence.dao.ConfigurationDao;

/**
 * checkout: james, greenMail and spring.mail for inline smtp server functionality?
 * 
 * @author ralfwehner
 * 
 */
@Service
public class MailSenderFacade {

    private static final Logger LOG = LoggerFactory.getLogger(MailSenderFacade.class);

    private boolean _enabled = true;

    private JavaMailSenderImpl _mailSender;

    private InternetAddress _senderAdress;

    @Autowired
    public MailSenderFacade(final ConfigurationDao configurationDao, TransactionHandler transactionHandler, @Qualifier("mail.enabled") Boolean enabled)
            throws UnsupportedEncodingException {
        _enabled = enabled;
        MailServerConfiguration serverConf = transactionHandler.executeInTransaction(new Callable<MailServerConfiguration>() {

            @Override
            public MailServerConfiguration call() throws Exception {
                return configurationDao.getConfiguration(MailServerConfiguration.class);
            }
        });

        initialize(serverConf);
    }

    public void initialize(MailServerConfiguration serverConf) throws UnsupportedEncodingException {
        _mailSender = new JavaMailSenderImpl();
        _mailSender.setHost(serverConf.getHostName());
        _mailSender.setPort((serverConf.getPort() != null) ? serverConf.getPort() : 25);
        _mailSender.setUsername(serverConf.getUserName());
        _mailSender.setPassword(serverConf.getPassword());
        Properties javaMailProperties = _mailSender.getJavaMailProperties();
        if (serverConf.isUseTls()) {
            javaMailProperties.setProperty("mail.smtp.auth", "true");
            javaMailProperties.setProperty("mail.smtp.starttls.enable", "true");
        } else {
            javaMailProperties.remove("mail.smtp.auth");
            javaMailProperties.remove("mail.smtp.starttls.enable");
        }
        _mailSender.setJavaMailProperties(javaMailProperties);
        _senderAdress = new InternetAddress(serverConf.getSenderAddress(), serverConf.getSenderName());
    }

    public boolean isEnabled() {
        return _enabled;
    }

    public void setEnabled(boolean enabled) {
        _enabled = enabled;
    }

    public void sendMail(final List<InternetAddress> receivers, final String subject, final String content, boolean isHtml) throws MessagingException {
        sendMail(_senderAdress, receivers, subject, content, isHtml);
    }

    protected void sendMail(InternetAddress senderAddress, List<InternetAddress> receivers, String subject, String content, boolean isHtml)
            throws MessagingException {
        if (!isEnabled()) {
            LOG.warn("[mail-disabled] send mail to '" + receivers + "'\nsubject: " + subject + "\ncontent:\n" + content);
            return;
        }
        MimeMessage message = _mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message);
        for (InternetAddress to : receivers) {
            helper.addTo(to);
        }
        helper.setFrom(senderAddress);
        helper.setSubject(subject);
        helper.setText(content, isHtml);
        _mailSender.send(message);
    }

}

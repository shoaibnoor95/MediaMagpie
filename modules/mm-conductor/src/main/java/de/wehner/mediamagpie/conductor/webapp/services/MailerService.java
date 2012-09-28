package de.wehner.mediamagpie.conductor.webapp.services;

import java.io.IOException;
import java.io.StringWriter;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.mail.MessagingException;
import javax.mail.internet.InternetAddress;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import de.wehner.mediamagpie.common.persistence.entity.Registration;
import de.wehner.mediamagpie.common.persistence.entity.User;
import de.wehner.mediamagpie.conductor.mail.MailSenderFacade;
import de.wehner.mediamagpie.conductor.mail.MailTemplateType;
import de.wehner.mediamagpie.conductor.util.Env;
import freemarker.template.Configuration;
import freemarker.template.DefaultObjectWrapper;
import freemarker.template.Template;
import freemarker.template.TemplateException;

@Service
public class MailerService {

    private final Configuration _configuration;
    private final MailSenderFacade _mailSenderFacade;

    @Autowired
    public MailerService(MailSenderFacade mailSenderFacade) {
        super();
        _mailSenderFacade = mailSenderFacade;
        _configuration = new Configuration();
        _configuration.setClassForTemplateLoading(MailerService.class, "/freemarker");
        _configuration.setObjectWrapper(new DefaultObjectWrapper());
    }

    public void sendTestMail(User user, Locale locale, String subject, String message, String name, String hostName, String port, String username,
            String password) throws IOException, TemplateException, MessagingException {
        MailTemplateType mailType = MailTemplateType.TEST;
        Map<String, Object> dataMap = new HashMap<String, Object>();
        dataMap.put("subject", subject);
        dataMap.put("message", StringUtils.replace(message, "\r\n", "<br/>"));
        dataMap.put("name", name);
        dataMap.put("hostName", hostName);
        dataMap.put("port", port);
        dataMap.put("username", username);
        dataMap.put("password", password);
        InternetAddress receiver = new InternetAddress(user.getEmail(), user.getForename() + " " + user.getSurname());
        sendMail(Arrays.asList(receiver), locale, mailType, dataMap);
    }

    public void sendRegistrationPerformedNotificationMail(Registration registration, int timeoutHours, Locale locale) throws IOException,
            TemplateException, MessagingException {
        MailTemplateType mailType = MailTemplateType.NEW_REGISTRATION;
        Map<String, Object> dataMap = new HashMap<String, Object>();
        dataMap.put("user", registration.getUser());
        dataMap.put("activationLink", registration.getActivationLink());
        dataMap.put("host", getConductorAddress());
        dataMap.put("timeout", "" + timeoutHours);
        InternetAddress receiver = new InternetAddress(registration.getEmail(), registration.getForename() + " " + registration.getSurname());
        sendMail(Arrays.asList(receiver), locale, mailType, dataMap);
    }

    public void sendResetPasswordMail(User user, String newPassword, Locale locale) throws IOException, TemplateException, MessagingException {
        Map<String, Object> dataMap = new HashMap<String, Object>();
        dataMap.put("host", getConductorAddress());
        dataMap.put("user", user.getName());
        dataMap.put("password", newPassword);
        InternetAddress receiver = new InternetAddress(user.getEmail(), user.getForename() + " " + user.getSurname());
        sendMail(Arrays.asList(receiver), locale, MailTemplateType.RESET_PASSWORD, dataMap);
    }

    private void sendMail(List<InternetAddress> receivers, Locale locale, MailTemplateType mailType, Map<String, Object> dataMap) throws IOException,
            TemplateException, MessagingException {
        String subject = renderSubject(mailType, locale, dataMap);
        String content = renderContent(mailType, locale, dataMap);
        _mailSenderFacade.sendMail(receivers, subject, content, mailType.isHtml());
    }

    private String renderSubject(MailTemplateType mailType, Locale locale, Map<String, Object> data) throws IOException, TemplateException {
        return renderTemplateAsString(mailType.getTemplateFileName() + ".subject.ftl", data, locale);
    }

    private String renderContent(MailTemplateType mailType, Locale locale, Map<String, Object> data) throws IOException, TemplateException {
        return renderTemplateAsString(mailType.getTemplateFileName() + ".content.ftl", data, locale);
    }

    private String renderTemplateAsString(String templateName, Map<String, Object> model, Locale locale) throws IOException, TemplateException {
        Template template = _configuration.getTemplate(templateName, locale, "UTF-8");
        StringWriter writer = new StringWriter();
        template.process(model, writer);
        writer.close();
        return writer.toString();
    }

    private String getConductorAddress() {
        String serverAddress = Env.getServerAddress();
        if (serverAddress == null) {
            return "localhost:8088";
        }
        return "http://" + serverAddress;
    }
}

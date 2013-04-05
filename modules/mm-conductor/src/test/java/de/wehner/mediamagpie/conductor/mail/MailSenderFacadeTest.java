package de.wehner.mediamagpie.conductor.mail;

import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.List;

import javax.mail.internet.InternetAddress;

import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.subethamail.wiser.WiserMessage;

import de.wehner.mediamagpie.common.persistence.entity.properties.MailServerConfiguration;
import de.wehner.mediamagpie.conductor.persistence.dao.ConfigurationDao;
import de.wehner.mediamagpie.core.util.WiserClient;
import de.wehner.mediamagpie.persistence.TransactionHandlerMock;

public class MailSenderFacadeTest {

    private final static String SMTP_HOST = "localhost";

    private static final String SENDER_NAME = "magpie";

    private static final String SENDER_ADDRESS = "do-not-reply@mediamagpie.org";

    private WiserClient _wiserClient = new WiserClient();

    private MailSenderFacade _mailSenderFacade;

    private MailServerConfiguration _mailServerConfiguration;

    @BeforeClass()
    public static void init() {
        Logger.getRootLogger().setLevel(Level.DEBUG);
        Logger.getRootLogger().addAppender(new ConsoleAppender(new PatternLayout("%-5p [%x] %c#%M (line %L) - %m%n")));
    }

    @Before
    public void setUp() throws Exception {
        _wiserClient.start();
        ConfigurationDao configurationDao = mock(ConfigurationDao.class);
        _mailServerConfiguration = new MailServerConfiguration();
        _mailServerConfiguration.setHostName(SMTP_HOST);
        _mailServerConfiguration.setPort(_wiserClient.getPort());
        _mailServerConfiguration.setSenderName(SENDER_NAME);
        _mailServerConfiguration.setSenderAddress(SENDER_ADDRESS);
        when(configurationDao.getConfiguration(MailServerConfiguration.class)).thenReturn(_mailServerConfiguration);
        _mailSenderFacade = new MailSenderFacade(configurationDao, new TransactionHandlerMock(), true);
    }

    @After
    public void onTearDown() throws Exception {
        _wiserClient.stop();
    }

    @Test
    public void testSendMail() throws Exception {
        InternetAddress receiver = new InternetAddress("rwe@ralfwehner.org", "Ralf Wehner");

        _mailSenderFacade.sendMail(Arrays.asList(receiver), "test subject", "test content", true);

        List<WiserMessage> messages = _wiserClient.getMessages();
        assertThat(messages).hasSize(1);
        WiserMessage mailMessage = messages.get(0);
        assertThat(((InternetAddress) mailMessage.getMimeMessage().getFrom()[0]).getAddress()).isEqualTo(SENDER_ADDRESS);
        assertThat(((InternetAddress) mailMessage.getMimeMessage().getFrom()[0]).getPersonal()).isEqualTo(SENDER_NAME);
        assertThat(((InternetAddress) mailMessage.getMimeMessage().getAllRecipients()[0]).getAddress()).isEqualTo("rwe@ralfwehner.org");
        assertThat(((InternetAddress) mailMessage.getMimeMessage().getAllRecipients()[0]).getPersonal()).isEqualTo("Ralf Wehner");
    }

    @Test
    public void testInitialize() throws Exception {
        _mailServerConfiguration.setSenderAddress("info@newhost.org");

        _mailSenderFacade.initialize(_mailServerConfiguration);

        InternetAddress receiver = new InternetAddress("rwe@ralfwehner.org", "Ralf Wehner");
        _mailSenderFacade.sendMail(Arrays.asList(receiver), "test subject", "test content", false);

        List<WiserMessage> messages = _wiserClient.getMessages();
        assertThat(messages).hasSize(1);
        WiserMessage mailMessage = messages.get(0);
        assertThat(messages).hasSize(1);
        assertThat(((InternetAddress) mailMessage.getMimeMessage().getFrom()[0]).getAddress()).isEqualTo("info@newhost.org");
        assertThat(((InternetAddress) mailMessage.getMimeMessage().getFrom()[0]).getPersonal()).isEqualTo(SENDER_NAME);
    }

}

package de.wehner.mediamagpie.conductor.webapp.services;

import static org.mockito.Matchers.*;

import static org.mockito.Mockito.*;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import javax.mail.MessagingException;
import javax.mail.internet.InternetAddress;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.internal.matchers.CapturingMatcher;

import de.wehner.mediamagpie.common.persistence.entity.User;
import de.wehner.mediamagpie.common.persistence.entity.User.Role;
import de.wehner.mediamagpie.conductor.mail.MailSenderFacade;
import freemarker.template.TemplateException;

public class MailerServiceTest {

    @Mock
    private MailSenderFacade _mailSender;
    private MailerService _mailerService;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        _mailerService = new MailerService(_mailSender);
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testTestMail() throws IOException, TemplateException, MessagingException {
        final Locale locale = Locale.GERMAN;

        _mailerService.sendTestMail(new User("name", "", Role.ADMIN), locale, "subject", "line1\r\nline2", "name", "host name", "port", "user name",
                "password");
        verify(_mailSender).sendMail((List<InternetAddress>) anyObject(), anyString(), anyString(), anyBoolean());
        CapturingMatcher<String> contentToMailSender = new CapturingMatcher<String>();
        verify(_mailSender).sendMail((List<InternetAddress>) anyObject(), anyString(), argThat(contentToMailSender), anyBoolean());
        String mailContentSent = contentToMailSender.getLastValue();
        System.out.println(mailContentSent);
    }

    @Test
    public void testSendResetPasswordMail() throws IOException, TemplateException, MessagingException {
        final Locale locale = Locale.GERMAN;

        _mailerService.sendResetPasswordMail(new User("bb", "berd.blau@localhost", Role.ADMIN), "myNewPassword", locale);
        CapturingMatcher<String> contentToMailSender = new CapturingMatcher<String>();
        verify(_mailSender).sendMail((List<InternetAddress>) anyObject(), anyString(), argThat(contentToMailSender), anyBoolean());
        String mailContentSent = contentToMailSender.getLastValue();
        System.out.println(mailContentSent);
    }
}

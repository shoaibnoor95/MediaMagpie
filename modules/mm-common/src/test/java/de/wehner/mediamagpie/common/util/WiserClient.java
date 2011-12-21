package de.wehner.mediamagpie.common.util;

import java.io.IOException;
import java.util.List;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import org.subethamail.wiser.Wiser;
import org.subethamail.wiser.WiserMessage;

/**
 * Helper class for the {@link Wiser} smtp server which provides some convenient methods to start/stop and access sent emails.<br/>
 * The wiser server is set up to port 2501. To work with the wiser in junit tests, you have to set the property
 * <code>System.setProperty("deploy.mode", "test")</code> before the spring context is set up. This will force spring to inject the
 * {@link LocalhostMailer} into <code>MailserService</code> constructor. '
 * 
 * @author rwe-extern
 */
public class WiserClient {

    public static final int PORT = 2501;
    
    private Wiser _wiser;

    public WiserClient() {
        super();
        _wiser = new Wiser();
    }

    public void start() throws InterruptedException {
        _wiser.setPort(PORT);
        _wiser.start();
        do {
            Thread.sleep(50);
        } while (!_wiser.getServer().isRunning());

    }

    public void stop() throws InterruptedException {
        _wiser.stop();
        do {
            Thread.sleep(50);
            System.out.print(".");
        } while (_wiser.getServer().isRunning());
    }

    public Wiser getWiser() {
        return _wiser;
    }

    public List<WiserMessage> getMessages() {
        return _wiser.getMessages();
    }

    public void showEmailSummary() throws MessagingException {
        List<WiserMessage> messages = _wiser.getMessages();
        System.out.println("Got '" + messages.size() + "' emails:");
        for (WiserMessage wiserMessage : messages) {
            System.out.println("  TO: " + wiserMessage.getEnvelopeReceiver() + ", subject: '" + wiserMessage.getMimeMessage().getSubject() + "'");
        }
    }

    public void showMessageContent(int index) throws MessagingException, IOException {
        List<WiserMessage> messages = _wiser.getMessages();
        MimeMessage mimeMessage = messages.get(index).getMimeMessage();
        Object content = mimeMessage.getContent();
        System.out.println(content.toString());

    }
}

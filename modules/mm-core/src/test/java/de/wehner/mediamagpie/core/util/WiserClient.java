package de.wehner.mediamagpie.core.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import javax.activation.DataHandler;
import javax.mail.BodyPart;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import org.apache.commons.io.IOUtils;
import org.subethamail.wiser.Wiser;
import org.subethamail.wiser.WiserMessage;

import de.wehner.mediamagpie.core.util.FindFreeSocket;

/**
 * Helper class for the {@link Wiser} smtp server which provides some convenient methods to start/stop and access sent emails.<br/>
 * The wiser server is set up to port 2501 by default. If this port is not available, it start scanning for next free port up to 2600.<br/>
 * General, to work with the wiser in junit tests, you have to set the property <code>System.setProperty("deploy.mode", "test")</code>
 * before the spring context is set up. This will force spring to inject the {@link LocalhostMailer} into <code>MailserService</code>
 * constructor. '
 * <p>
 * To integrate the <code>WiserClient</code> into your testcases, use this pattern:
 * </p>
 * 
 * <pre>
 * public class Test {
 * 
 *     private WiserClient _wiserClient = new WiserClient();
 * 
 *     &#64;Before
 *     public void setUp() throws Exception {
 *         _wiserClient.start();
 *         // setup some properties for your test (at least the port number for wiser)
 *         // ...
 *     }
 * 
 *     &#64;After
 *     public void onTearDown() throws Exception {
 *         _wiserClient.stop();
 *     }
 * ...
 * </pre>
 * 
 */
public class WiserClient {

    private Integer _port;

    private Wiser _wiser;

    public static class FilenameAndData {
        private final String name;

        private final byte[] data;

        public FilenameAndData(String name, byte[] data) {
            super();
            this.name = name;
            this.data = data;
        }

        public String getName() {
            return name;
        }

        public byte[] getData() {
            return data;
        }
    }

    public WiserClient() {
        super();
        _wiser = new Wiser();
    }

    public void start() throws InterruptedException {
        _port = FindFreeSocket.findFreeSocket(2501, 2600);
        _wiser.setPort(_port);
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

    public Integer getPort() {
        return _port;
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

    /**
     * Parses an Attachment of the mail and provide its data and file name.
     * 
     * @param wiserMessage
     * @param attachmentIndex
     * @return
     * @throws MessagingException
     * @throws IOException
     */
    public static FilenameAndData getAttachment(WiserMessage wiserMessage, int attachmentIndex) throws MessagingException, IOException {
        MimeMessage mimeMessage = wiserMessage.getMimeMessage();
        Object content = mimeMessage.getContent();
        if (content instanceof MimeMultipart) {
            MimeMultipart mm = (MimeMultipart) content;
            BodyPart bodyPart = mm.getBodyPart(attachmentIndex);
            String fileName = bodyPart.getFileName();
            DataHandler dataHandler = bodyPart.getDataHandler();
            InputStream inputStream = dataHandler.getInputStream();
            byte[] byteArray = IOUtils.toByteArray(inputStream);
            return new FilenameAndData(fileName, byteArray);
        } else {
            System.out.println("Content of MimeMessage is not class 'MimeMultipart', it is '" + content.getClass().getSimpleName() + "'.");
        }
        return null;
    }
}

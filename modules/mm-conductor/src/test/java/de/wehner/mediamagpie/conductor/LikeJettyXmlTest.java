package de.wehner.mediamagpie.conductor;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.FileUtils;
import org.eclipse.jetty.server.Connector;
import org.eclipse.jetty.server.HttpConfiguration;
import org.eclipse.jetty.server.HttpConnectionFactory;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.SecureRequestCustomizer;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.server.SslConnectionFactory;
import org.eclipse.jetty.server.handler.AbstractHandler;
import org.eclipse.jetty.util.security.Password;
import org.eclipse.jetty.util.ssl.SslContextFactory;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import de.wehner.mediamagpie.core.testsupport.TestEnvironment;

public class LikeJettyXmlTest {

    public static class HelloHandler extends AbstractHandler {

        @Override
        public void handle(String target, Request baseRequest, HttpServletRequest request, HttpServletResponse response) throws IOException,
                ServletException {
            response.setContentType("text/html;charset=utf-8");
            response.setStatus(HttpServletResponse.SC_OK);
            baseRequest.setHandled(true);
            response.getWriter().println("<h1>Hello World</h1>");
        }

        public static void main(String[] args) throws Exception {
            Server server = new Server(8080);
            server.setHandler(new HelloHandler());
            server.start();
            server.join();
        }
    };

    private TestEnvironment _testEnvironment = new TestEnvironment(getClass());
    private File jetty_home;
    private File keyStore;

    @Before
    public void setUp() throws IOException {
        _testEnvironment.cleanWorkingDir();
        jetty_home = new File(_testEnvironment.getWorkingDir(), "jettyHome");
        System.setProperty("jetty.home", jetty_home.getPath());
        new File(jetty_home, "logs").mkdirs();
        keyStore = new File(jetty_home, "etc/keystore");
        keyStore.getParentFile().mkdirs();
        FileUtils.copyFile(new File("src/main/resources/ssl/keystore"), keyStore);
    }

    @Test
    public void test() throws Exception {
        // LikeJettyXml.main(null);
        InternalJettyServer.main(null);
    }

    @Test
    @Ignore
    public void getPassword() {
        System.out.println(Password.obfuscate("Hallo1234"));
    }

    @Test
    public void testManyConnectors() throws Exception {
        Server server = new Server();
        HttpConfiguration http_config = new HttpConfiguration();
        http_config.setSecureScheme("https");
        http_config.setSecurePort(8443);
        http_config.setOutputBufferSize(32768);
        ServerConnector http = new ServerConnector(server, new HttpConnectionFactory(http_config));
        http.setPort(8080);
        http.setIdleTimeout(30000);
        SslContextFactory sslContextFactory = new SslContextFactory();

        sslContextFactory.setKeyStorePath(jetty_home + "/etc/keystore");
        sslContextFactory.setKeyStorePassword("OBF:1doz1igd1kft1k8q1x1b1k5g1kcl1idt1dnv"/* "OBF:1vny1zlo1x8e1vnw1vn61x8g1zlu1vn4" */);
//        sslContextFactory.setTrustStorePath(jetty_home + "/etc/keystore");
//        sslContextFactory.setTrustStorePassword("OBF:1doz1igd1kft1k8q1x1b1k5g1kcl1idt1dnv"/* "OBF:1vny1zlo1x8e1vnw1vn61x8g1zlu1vn4" */);
        
        
//        sslContextFactory.setKeyManagerPassword("OBF:1u2u1wml1z7s1z7a1wnl1u2g");
        HttpConfiguration https_config = new HttpConfiguration(http_config);
        https_config.addCustomizer(new SecureRequestCustomizer());
        ServerConnector https = new ServerConnector(server, new SslConnectionFactory(sslContextFactory, "http/1.1"), new HttpConnectionFactory(
                https_config));
        https.setPort(8443);
        https.setIdleTimeout(500000);
        server.setConnectors(new Connector[] { http, https });
        server.setHandler(new HelloHandler());
        server.start();
        server.join();
    }

    // @Test
    public void testGenerateKeyStore() throws KeyStoreException, NoSuchAlgorithmException, CertificateException, IOException {
        KeyStore ks = KeyStore.getInstance(KeyStore.getDefaultType());

        char[] password = "keyStorePassword".toCharArray();
        ks.load(null, password);

        // Store away the keystore.
        FileOutputStream fos = new FileOutputStream(keyStore);
        ks.store(fos, password);
        fos.close();

        // CREATE A KEYSTORE OF TYPE "Java Key Store"
        // rwe:? KeyStore ks = KeyStore.getInstance("JKS");
        /*
         * LOAD THE STORE The first time you're doing this (i.e. the keystore does not yet exist - you're creating it), you HAVE to load the
         * keystore from a null source with null password. Before any methods can be called on your keystore you HAVE to load it first.
         * Loading it from a null source and null password simply creates an empty keystore. At a later time, when you want to verify the
         * keystore or get certificates (or whatever) you can load it from the file with your password.
         */
        ks.load(null, null);
        // GET THE FILE CONTAINING YOUR CERTIFICATE
        FileInputStream fis = new FileInputStream("MyCert.cer");
        BufferedInputStream bis = new BufferedInputStream(fis);
        // I USE x.509 BECAUSE THAT'S WHAT keytool CREATES
        CertificateFactory cf = CertificateFactory.getInstance("X.509");
        // NOTE: THIS IS java.security.cert.Certificate NOT java.security.Certificate
        Certificate cert = null;
        /*
         * I ONLY HAVE ONE CERT, I JUST USED "while" BECAUSE I'M JUST DOING TESTING AND WAS TAKING WHATEVER CODE I FOUND IN THE API
         * DOCUMENTATION. I COULD HAVE DONE AN "if", BUT I WANTED TO SHOW HOW YOU WOULD HANDLE IT IF YOU GOT A CERT FROM VERISIGN THAT
         * CONTAINED MULTIPLE CERTS
         */
        // GET THE CERTS CONTAINED IN THIS ROOT CERT FILE
        while (bis.available() > 0) {
            cert = cf.generateCertificate(bis);
            ks.setCertificateEntry("SGCert", cert);
        }
        // ADD TO THE KEYSTORE AND GIVE IT AN ALIAS NAME
        ks.setCertificateEntry("SGCert", cert);
        // SAVE THE KEYSTORE TO A FILE
        /*
         * After this is saved, I believe you can just do setCertificateEntry to add entries and then not call store. I believe it will
         * update the existing store you load it from and not just in memory.
         */
        ks.store(new FileOutputStream("NewClientKeyStore"), "MyPass".toCharArray());
    }
}

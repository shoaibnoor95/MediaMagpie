package de.wehner.mediamagpie.conductor;

import static org.junit.Assert.*;

import static org.fest.assertions.Assertions.*;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.management.ManagementFactory;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.FileUtils;
import org.eclipse.jetty.jmx.MBeanContainer;
import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.HttpConfiguration;
import org.eclipse.jetty.server.HttpConnectionFactory;
import org.eclipse.jetty.server.LowResourceMonitor;
import org.eclipse.jetty.server.NCSARequestLog;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.SecureRequestCustomizer;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.server.SslConnectionFactory;
import org.eclipse.jetty.server.handler.AbstractHandler;
import org.eclipse.jetty.server.handler.ContextHandlerCollection;
import org.eclipse.jetty.server.handler.HandlerCollection;
import org.eclipse.jetty.server.handler.RequestLogHandler;
import org.eclipse.jetty.server.handler.StatisticsHandler;
import org.eclipse.jetty.util.security.Password;
import org.eclipse.jetty.util.ssl.SslContextFactory;
import org.eclipse.jetty.util.thread.QueuedThreadPool;
import org.eclipse.jetty.util.thread.ScheduledExecutorScheduler;
import org.eclipse.jetty.webapp.WebAppContext;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import de.wehner.mediamagpie.core.testsupport.TestEnvironment;

/**
 * This test is based on the jetty's source code example <code>org.eclipse.jetty.embedded.LikeJettyXml</code>.
 * 
 * @author ralfwehner
 * 
 */
@Ignore("Not really a test")
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

    private static final String HELLO_WORLD = "Hello world. The quick brown fox jumped over the lazy dog. How now brown cow. The rain in spain falls mainly on the plain.\n";

    public static class HelloWorldHandler extends AbstractHandler {
        @Override
        public void handle(String target, Request baseRequest, HttpServletRequest request, HttpServletResponse response) throws IOException,
                ServletException {
            // System.err.println("HANDLE "+request.getRequestURI());
            String ssl_id = (String) request.getAttribute("javax.servlet.request.ssl_session_id");
            assertNotNull(ssl_id);

            if (request.getParameter("dump") != null) {
                ServletOutputStream out = response.getOutputStream();
                byte[] buf = new byte[Integer.valueOf(request.getParameter("dump"))];
                // System.err.println("DUMP "+buf.length);
                for (int i = 0; i < buf.length; i++)
                    buf[i] = (byte) ('0' + (i % 10));
                out.write(buf);
                out.close();
            } else {
                PrintWriter out = response.getWriter();
                out.print(HELLO_WORLD);
                out.close();
            }
        }
    }

    private TestEnvironment _testEnvironment = new TestEnvironment(getClass());
    private File jetty_home;
    private File keyStore;

    @Before
    public void setUp() throws IOException {
        _testEnvironment.cleanWorkingDir();
        jetty_home = new File(_testEnvironment.getWorkingDir(), "jettyHome");
        System.setProperty("jetty.home", jetty_home.getPath());
        new File(jetty_home, "logs").mkdirs();
        keyStore = new File(jetty_home, "etc/keystore.jks");
        keyStore.getParentFile().mkdirs();
        FileUtils.copyFile(new File("src/main/resources/ssl/keystore.jks"), keyStore);
    }

    @Test
    public void getPassword() {
        System.out.println(Password.obfuscate("hAmster123"));
    }

    @Test
    public void testInternalJettyServer() throws Exception {
        String jetty_home = System.getProperty("jetty.home");

        // === jetty.xml ===
        // Setup Threadpool
        QueuedThreadPool threadPool = new QueuedThreadPool();
        threadPool.setMaxThreads(500);

        // Server
        Server server = new Server(threadPool);

        // Scheduler
        server.addBean(new ScheduledExecutorScheduler());

        // HTTP Configuration
        HttpConfiguration tlsHttpConfiguration = new HttpConfiguration();
        tlsHttpConfiguration.setSecureScheme("https");
        tlsHttpConfiguration.setSecurePort(8443);
        tlsHttpConfiguration.setOutputBufferSize(32768);
        tlsHttpConfiguration.setRequestHeaderSize(8192);
        tlsHttpConfiguration.setResponseHeaderSize(8192);
        tlsHttpConfiguration.setSendServerVersion(true);
        tlsHttpConfiguration.setSendDateHeader(false);
        // httpConfig.addCustomizer(new ForwardedRequestCustomizer());

        // webapp handler
        WebAppContext webAppContext = new WebAppContext();
        webAppContext.setServer(server);
        String contextPath = "/";
        webAppContext.setContextPath(contextPath);
        webAppContext.setWar("src/main/webapp");

        // Handler Structure
        HandlerCollection handlers = new HandlerCollection();
        ContextHandlerCollection contexts = new ContextHandlerCollection();
        handlers.setHandlers(new Handler[] { contexts, webAppContext });
        server.setHandler(handlers);

        // Extra options
        server.setDumpAfterStart(false);
        server.setDumpBeforeStop(false);
        server.setStopAtShutdown(true);

        // === jetty-jmx.xml ===
        MBeanContainer mbContainer = new MBeanContainer(ManagementFactory.getPlatformMBeanServer());
        server.addBean(mbContainer);

        // === jetty-http.xml ===
        ServerConnector http = new ServerConnector(server, new HttpConnectionFactory(tlsHttpConfiguration));
        http.setPort(8080);
        http.setIdleTimeout(30000);
        server.addConnector(http);

        // === jetty-https.xml ===
        // SSL Context Factory
        SslContextFactory sslContextFactory = new SslContextFactory();
        sslContextFactory.setKeyStorePath(jetty_home + "/etc/keystore.jks");
        sslContextFactory.setKeyStorePassword("OBF:1juc1bwu1kfv1yf41w261w1c1yf21kcj1bw01jre");
        // sslContextFactory.setTrustStorePath(jetty_home + "/etc/keystore");
        // sslContextFactory.setTrustStorePassword("OBF:1doz1igd1kft1k8q1x1b1k5g1kcl1idt1dnv"/* "OBF:1vny1zlo1x8e1vnw1vn61x8g1zlu1vn4" */);
        sslContextFactory.setExcludeCipherSuites("SSL_RSA_WITH_DES_CBC_SHA", "SSL_DHE_RSA_WITH_DES_CBC_SHA", "SSL_DHE_DSS_WITH_DES_CBC_SHA",
                "SSL_RSA_EXPORT_WITH_RC4_40_MD5", "SSL_RSA_EXPORT_WITH_DES40_CBC_SHA", "SSL_DHE_RSA_EXPORT_WITH_DES40_CBC_SHA",
                "SSL_DHE_DSS_EXPORT_WITH_DES40_CBC_SHA");

        // SSL HTTP Configuration
        HttpConfiguration https_config = new HttpConfiguration(tlsHttpConfiguration);
        https_config.addCustomizer(new SecureRequestCustomizer());

        // SSL Connector
        ServerConnector sslConnector = new ServerConnector(server, new SslConnectionFactory(sslContextFactory, "http/1.1"), new HttpConnectionFactory(
                https_config));
        sslConnector.setPort(8443);
        server.addConnector(sslConnector);

        // === jetty-stats.xml ===
        StatisticsHandler stats = new StatisticsHandler();
        stats.setHandler(server.getHandler());
        server.setHandler(stats);

        // === jetty-requestlog.xml ===
        NCSARequestLog requestLog = new NCSARequestLog();
        requestLog.setFilename(jetty_home + "/logs/yyyy_mm_dd.request.log");
        requestLog.setFilenameDateFormat("yyyy_MM_dd");
        requestLog.setRetainDays(90);
        requestLog.setAppend(true);
        requestLog.setExtended(true);
        requestLog.setLogCookies(false);
        requestLog.setLogTimeZone("GMT");
        RequestLogHandler requestLogHandler = new RequestLogHandler();
        requestLogHandler.setRequestLog(requestLog);
        handlers.addHandler(requestLogHandler);

        // === jetty-lowresources.xml ===
        LowResourceMonitor lowResourcesMonitor = new LowResourceMonitor(server);
        lowResourcesMonitor.setPeriod(1000);
        lowResourcesMonitor.setLowResourcesIdleTimeout(200);
        lowResourcesMonitor.setMonitorThreads(true);
        lowResourcesMonitor.setMaxConnections(0);
        lowResourcesMonitor.setMaxMemory(0);
        lowResourcesMonitor.setMaxLowResourcesTime(5000);
        server.addBean(lowResourcesMonitor);

        // === test-realm.xml ===
        // HashLoginService login = new HashLoginService();
        // login.setName("Test Realm");
        // login.setConfig(jetty_home + "/etc/realm.properties");
        // login.setRefreshInterval(0);
        // server.addBean(login);

        // Start the server
        server.start();
        server.join();
    }

    @Test
    public void testSimpleSsl() throws Exception {
        File testKeyStore = new File("src/test/resources/ssl/keystore");
        assertThat(testKeyStore).exists();
        SslContextFactory sslContextFactory = new SslContextFactory();
        sslContextFactory.setKeyStorePath(testKeyStore.getAbsolutePath());
        sslContextFactory.setKeyStorePassword("storepwd");
        sslContextFactory.setKeyManagerPassword("keypwd");

        Server server = new Server();
        HttpConnectionFactory http = new HttpConnectionFactory();
        http.setInputBufferSize(512);
        http.getHttpConfiguration().setRequestHeaderSize(512);
        ServerConnector connector = new ServerConnector(server, sslContextFactory, http);
        connector.setPort(0);

        server.addConnector(connector);

        /* --------------------------- */
        server.setHandler(new HelloWorldHandler());
        server.start();

        int port = connector.getLocalPort();
        System.out.println(port);

        server.stop();
        server.join();
    }
}

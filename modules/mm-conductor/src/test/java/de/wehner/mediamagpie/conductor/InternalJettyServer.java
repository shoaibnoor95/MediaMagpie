package de.wehner.mediamagpie.conductor;

import java.lang.management.ManagementFactory;

import org.eclipse.jetty.deploy.DeploymentManager;
import org.eclipse.jetty.deploy.PropertiesConfigurationManager;
import org.eclipse.jetty.deploy.providers.WebAppProvider;
import org.eclipse.jetty.jmx.MBeanContainer;
import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.HttpConfiguration;
import org.eclipse.jetty.server.HttpConnectionFactory;
import org.eclipse.jetty.server.LowResourceMonitor;
import org.eclipse.jetty.server.NCSARequestLog;
import org.eclipse.jetty.server.SecureRequestCustomizer;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.server.SslConnectionFactory;
import org.eclipse.jetty.server.handler.ContextHandlerCollection;
import org.eclipse.jetty.server.handler.DefaultHandler;
import org.eclipse.jetty.server.handler.HandlerCollection;
import org.eclipse.jetty.server.handler.RequestLogHandler;
import org.eclipse.jetty.server.handler.StatisticsHandler;
import org.eclipse.jetty.util.ssl.SslContextFactory;
import org.eclipse.jetty.util.thread.QueuedThreadPool;
import org.eclipse.jetty.util.thread.ScheduledExecutorScheduler;
import org.eclipse.jetty.webapp.WebAppContext;

public class InternalJettyServer {

    public static void main(String[] args) throws Exception {
        // String jetty_home = System.getProperty("jetty.home","../../jetty-distribution/target/distribution");
        // System.setProperty("jetty.home",jetty_home);
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
 //       webAppContext.setServer(server);
        String contextPath = "/";
        webAppContext.setContextPath(contextPath);
        webAppContext.setWar("src/main/webapp");

        // Handler Structure
        HandlerCollection handlers = new HandlerCollection();
        ContextHandlerCollection contexts = new ContextHandlerCollection();
        handlers.setHandlers(new Handler[] { /*contexts, webAppContext*/ new DefaultHandler() });
        server.setHandler(handlers);

        // Extra options
        server.setDumpAfterStart(false);
        server.setDumpBeforeStop(false);
        server.setStopAtShutdown(true);

        // === jetty-jmx.xml ===
//        MBeanContainer mbContainer = new MBeanContainer(ManagementFactory.getPlatformMBeanServer());
//        server.addBean(mbContainer);

        // === jetty-http.xml ===
        ServerConnector http = new ServerConnector(server, new HttpConnectionFactory(tlsHttpConfiguration));
        http.setPort(8080);
        http.setIdleTimeout(30000);
        server.addConnector(http);

        // === jetty-https.xml ===
        // SSL Context Factory
        SslContextFactory sslContextFactory = new SslContextFactory();
        sslContextFactory.setKeyStorePath(jetty_home + "/etc/keystore");
        sslContextFactory.setKeyStorePassword("OBF:1doz1igd1kft1k8q1x1b1k5g1kcl1idt1dnv"/* "OBF:1vny1zlo1x8e1vnw1vn61x8g1zlu1vn4" */);
        sslContextFactory.setTrustStorePath(jetty_home + "/etc/keystore");
        sslContextFactory.setTrustStorePassword("OBF:1doz1igd1kft1k8q1x1b1k5g1kcl1idt1dnv"/* "OBF:1vny1zlo1x8e1vnw1vn61x8g1zlu1vn4" */);
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
}

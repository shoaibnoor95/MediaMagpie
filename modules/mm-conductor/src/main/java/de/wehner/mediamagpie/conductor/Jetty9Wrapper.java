package de.wehner.mediamagpie.conductor;

import java.lang.management.ManagementFactory;
import java.util.Properties;

import org.eclipse.jetty.jmx.MBeanContainer;
import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.HttpConfiguration;
import org.eclipse.jetty.server.HttpConnectionFactory;
import org.eclipse.jetty.server.LowResourceMonitor;
import org.eclipse.jetty.server.SecureRequestCustomizer;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.server.SslConnectionFactory;
import org.eclipse.jetty.server.handler.ContextHandlerCollection;
import org.eclipse.jetty.server.handler.HandlerCollection;
import org.eclipse.jetty.server.handler.StatisticsHandler;
import org.eclipse.jetty.util.ssl.SslContextFactory;
import org.eclipse.jetty.util.thread.QueuedThreadPool;
import org.eclipse.jetty.util.thread.ScheduledExecutorScheduler;
import org.eclipse.jetty.webapp.WebAppContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.wehner.mediamagpie.conductor.spring.deploy.impl.DynamicPropertiesConfigurer;
import de.wehner.mediamagpie.core.util.ClassPathUtil;

public class Jetty9Wrapper {

    private static Logger LOG = LoggerFactory.getLogger(Jetty9Wrapper.class);
    private final Server server;

    public Jetty9Wrapper() throws Exception {

        // setup Systemproperties for apache's common logging framework used in HttpClient
        System.setProperty("org.apache.commons.logging.Log", "org.apache.commons.logging.impl.SimpleLog");
        System.setProperty("org.apache.commons.logging.simplelog.showdatetime", "true");
        System.setProperty("org.apache.commons.logging.simplelog.log.org.apache.commons.httpclient", "info");

        // setup jetty specific properties
        LOG.info("Using DynamicPropertiesConfigurer to preload properties for jetty start.");
        DynamicPropertiesConfigurer.setupDeployModeAndSpringProfile();
        DynamicPropertiesConfigurer propertiesConfigurer = new DynamicPropertiesConfigurer("/properties/deploy");
        Properties properties = propertiesConfigurer.getProperties();
        setJettySecificConfigurationIntoSystemProperties(properties);

        // === jetty.xml ===
        // Setup Threadpool
        QueuedThreadPool threadPool = new QueuedThreadPool();
        threadPool.setMaxThreads(500);

        // Server
        server = new Server(threadPool);

        // Scheduler
        server.addBean(new ScheduledExecutorScheduler());

        // HTTP Configuration
        HttpConfiguration tlsHttpConfiguration = new HttpConfiguration();
        tlsHttpConfiguration.setSecureScheme("https");
        tlsHttpConfiguration.setSecurePort(Integer.parseInt(properties.getProperty(ApplicationConstants.WEB_APP_PORT_HTTPS)));
        tlsHttpConfiguration.setOutputBufferSize(32768);
        tlsHttpConfiguration.setRequestHeaderSize(8192);
        tlsHttpConfiguration.setResponseHeaderSize(8192);
        tlsHttpConfiguration.setSendServerVersion(true);
        tlsHttpConfiguration.setSendDateHeader(false);
        // httpConfig.addCustomizer(new ForwardedRequestCustomizer());

        // webapp handler
        WebAppContext webAppContext = new WebAppContext();
        webAppContext.setServer(server);
        String contextPath = properties.getProperty(ApplicationConstants.WEB_APP_CONTEXTPATH);
        LOG.info("Using context path '" + contextPath + "'.");
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
        http.setPort(Integer.parseInt(properties.getProperty(ApplicationConstants.WEB_APP_PORT_HTTP)));
        http.setIdleTimeout(30000);
        server.addConnector(http);

        // === jetty-https.xml ===
        // SSL Context Factory
        SslContextFactory sslContextFactory = new SslContextFactory();
        sslContextFactory.setKeyStorePath(ClassPathUtil.findResourceInClassPath("/ssl/keystore.jks").getURI().toString());
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
        sslConnector.setPort(Integer.parseInt(properties.getProperty(ApplicationConstants.WEB_APP_PORT_HTTPS)));
        server.addConnector(sslConnector);

        // === jetty-stats.xml ===
        StatisticsHandler stats = new StatisticsHandler();
        stats.setHandler(server.getHandler());
        server.setHandler(stats);

        // === jetty-requestlog.xml ===
        // NCSARequestLog requestLog = new NCSARequestLog();
        // requestLog.setFilename(jetty_home + "/logs/yyyy_mm_dd.request.log");
        // requestLog.setFilenameDateFormat("yyyy_MM_dd");
        // requestLog.setRetainDays(90);
        // requestLog.setAppend(true);
        // requestLog.setExtended(true);
        // requestLog.setLogCookies(false);
        // requestLog.setLogTimeZone("GMT");
        // RequestLogHandler requestLogHandler = new RequestLogHandler();
        // requestLogHandler.setRequestLog(requestLog);
        // handlers.addHandler(requestLogHandler);

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

        // setup some configuration information into application's pageContext
        webAppContext.setAttribute(ApplicationConstants.WEB_APP_PORT_HTTP, http.getPort());
    }

    private void setJettySecificConfigurationIntoSystemProperties(Properties properties) {
        if (properties.getProperty(ApplicationConstants.WEB_APP_PORT_HTTP) == null) {
            properties.setProperty(ApplicationConstants.WEB_APP_PORT_HTTP, "8088");
        }
        if (properties.getProperty(ApplicationConstants.WEB_APP_PORT_HTTPS) == null) {
            properties.setProperty(ApplicationConstants.WEB_APP_PORT_HTTPS, "8443");
        }
        if (properties.getProperty(ApplicationConstants.WEB_APP_CONTEXTPATH) == null) {
            properties.setProperty(ApplicationConstants.WEB_APP_CONTEXTPATH, "/");
        }
    }

    public Server start() throws Exception {
        server.start();
        // server.join();
        return server;
    }

    public void stop() throws Exception {
        server.stop();
    }
}

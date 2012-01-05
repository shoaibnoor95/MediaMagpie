package de.wehner.mediamagpie.conductor;

import java.util.Properties;
import java.util.concurrent.CountDownLatch;

import org.mortbay.jetty.Connector;
import org.mortbay.jetty.Server;
import org.mortbay.jetty.bio.SocketConnector;
import org.mortbay.jetty.webapp.WebAppContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.wehner.mediamagpie.common.util.Holder;
import de.wehner.mediamagpie.conductor.spring.deploy.impl.DynamicPropertiesConfigurer;

public class StartJetty {

    private static Logger LOG = LoggerFactory.getLogger(StartJetty.class);
    public static final String WEB_APP_PORT = "webapp.port";
    public static final String WEB_APP_CONTEXTPATH = "webapp.context.path";

    public static void main(String[] args) throws Exception {

        // setup jetty specific properties
        LOG.info("Using DynamicPropertiesConfigurer to preload properties for jetty start.");
        DynamicPropertiesConfigurer.setupDeployModeAndSpringProfile();
        DynamicPropertiesConfigurer propertiesConfigurer = new DynamicPropertiesConfigurer("/properties/deploy");
        Properties properties = propertiesConfigurer.getProperties();
        setJettySecificConfigurationIntoSystemProperties(properties);

        // add shutdown hook
        final Holder<Boolean> terminate = new Holder<Boolean>(false);
        final CountDownLatch serverIsDown = new CountDownLatch(1);
        Runtime.getRuntime().addShutdownHook(new Thread() {
            public void run() {
                LOG.info("Got shutdown signal to stop server.");

                // TODO Do some cleanup tasks etc.
                // ..

                terminate.set(true);
                try {
                    serverIsDown.await();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });

        Server server = new Server();
        SocketConnector connector = new SocketConnector();

        // Set some timeout options to make debugging easier.
        connector.setMaxIdleTime(1000 * 60 * 60);
        connector.setSoLingerTime(-1);
        connector.setPort(Integer.parseInt(properties.getProperty(WEB_APP_PORT)));
        server.setConnectors(new Connector[] { connector });

        WebAppContext webAppContext = new WebAppContext();
        webAppContext.setServer(server);
        String contextPath = properties.getProperty(WEB_APP_CONTEXTPATH);
        LOG.info("Using context path '" + contextPath + "'.");
        webAppContext.setContextPath(contextPath);
        webAppContext.setWar("src/main/webapp");

        server.addHandler(webAppContext);
        try {
            LOG.info(">>> STARTING EMBEDDED JETTY SERVER");
            server.start();
            try {
                while (!terminate.get()) {
                    Thread.sleep(500);
                }
            } catch (InterruptedException e) {
                LOG.info("Got InterruptedException.");
                terminate.set(true);
            }
            LOG.info(">>> STOPPING EMBEDDED JETTY SERVER");
            server.stop();
            server.join();
            serverIsDown.countDown();
        } catch (Exception e) {
            e.printStackTrace();
            LOG.error("unexpected shutdown", e);
            serverIsDown.countDown();
            System.exit(100);
        } finally {
            LOG.info(">>> JETTY SERVER IS DOWN");
        }
    }

    private static void setJettySecificConfigurationIntoSystemProperties(Properties properties) {
        if (properties.getProperty(WEB_APP_PORT) == null) {
            properties.setProperty(WEB_APP_PORT, "8088");
        }
        if (properties.getProperty(WEB_APP_CONTEXTPATH) == null) {
            properties.setProperty(WEB_APP_CONTEXTPATH, "/");
        }
    }
}

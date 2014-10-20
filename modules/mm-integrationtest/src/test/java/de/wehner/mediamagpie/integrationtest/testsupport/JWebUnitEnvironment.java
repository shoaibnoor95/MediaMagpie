package de.wehner.mediamagpie.integrationtest.testsupport;

import static net.sourceforge.jwebunit.junit.JWebUnit.*;

import org.apache.commons.lang3.StringUtils;
import org.junit.rules.ExternalResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This Rule initializes the baseUrl attribute of JHtmlUnit Test framework with the value of system.properties <code>BASE_URL</code>
 * setting. Use something like: <code>BASE_URL=http://54.171.82.164/</code>
 * 
 * @author Ralf Wehner
 *
 */
public class JWebUnitEnvironment extends ExternalResource {

    @SuppressWarnings("unused")
    private static final Logger LOG = LoggerFactory.getLogger(JWebUnitEnvironment.class);

    public JWebUnitEnvironment() {
        String baseUrl = System.getProperty("BASE_URL");
        if (StringUtils.isBlank(baseUrl)) {
            baseUrl = "http://localhost:8088/";
        }
        LOG.info("Using connection to '{}'.", baseUrl);
        setBaseUrl(baseUrl);
    }

    @Override
    protected void after() {
        super.after();
    }

    @Override
    protected void before() throws Throwable {
        super.before();
    }

}

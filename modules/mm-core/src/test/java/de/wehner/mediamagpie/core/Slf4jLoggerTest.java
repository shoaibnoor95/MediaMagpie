package de.wehner.mediamagpie.core;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Slf4jLoggerTest {

    private final static Logger LOG = LoggerFactory.getLogger(Slf4jLoggerTest.class);

    @Test
    public void test() {
        LOG.trace("This is {} trace level.", "my");
        LOG.debug("This is {} debug level.", "my");
        LOG.info("This is {} info level.", "my");
        LOG.warn("This is {} warn level.", "my");
        LOG.error("This is {} error level.", "my");
        System.out.println("Logger.getName()=" + LOG.getName());
    }
}

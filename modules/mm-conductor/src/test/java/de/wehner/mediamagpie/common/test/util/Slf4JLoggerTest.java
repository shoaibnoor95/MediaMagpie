package de.wehner.mediamagpie.common.test.util;

import java.io.File;

import org.apache.log4j.Appender;
import org.apache.log4j.LogManager;
import org.apache.log4j.spi.RootLogger;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Slf4JLoggerTest {

    @SuppressWarnings("unused")
    private static final Logger LOG = LoggerFactory.getLogger(Slf4JLoggerTest.class);

    @Test
    public void testGetRootLoggerAndFileAppender() {
        org.apache.log4j.Logger rootLogger2 = LogManager.getRootLogger();
        if (rootLogger2 instanceof RootLogger) {
            Appender appender = rootLogger2.getAppender("file");
            if (appender != null) {
                File logFile = new File(appender.getName());
                System.out.println(logFile.getPath());
            }
        }
    }
}
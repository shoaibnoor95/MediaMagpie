package de.wehner.mediamagpie.core.util;

import java.io.IOException;
import java.net.ServerSocket;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FindFreeSocket {

    private static final Logger LOG = LoggerFactory.getLogger(FindFreeSocket.class);

    public static int findFreeSocket(int startPort, int endPortRange) {
        if (startPort <= 0) {
            throw new IllegalArgumentException("The parameter 'startPort' must be greater than zero.");
        }
        if (endPortRange < startPort) {
            throw new IllegalArgumentException("The argument 'endPortRange' must be greater or equal to parameter 'startPort'.");
        }
        for (int testPort = startPort; testPort <= endPortRange; testPort++) {
            if (isSocketAvailable(testPort)) {
                return testPort;
            }
        }
        throw new RuntimeException("No port in range " + startPort + " - " + endPortRange + " is available.");
    }

    private static synchronized boolean isSocketAvailable(int port) {
        LOG.trace("trying to open port " + port);
        ServerSocket ss = null;
        try {
            ss = new ServerSocket(port);
            ss.setReuseAddress(true);
            return true;
        } catch (IOException e) {
            LOG.info("port " + port + " is unavailable");
        } finally {
            if (ss != null) {
                try {
                    ss.close();
                } catch (IOException e) {
                    LOG.error("Unable to close socket " + port);
                }
            }
        }
        return false;
    }

    @Override
    public String toString() {
        return ReflectionToStringBuilder.toString(this, ToStringStyle.SHORT_PREFIX_STYLE);
    }
}

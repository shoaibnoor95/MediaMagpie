package de.wehner.mediamagpie.conductor.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import de.wehner.mediamagpie.core.util.StringUtil;

@Service
public class SystemInformationProvider {

    private static Logger LOG = LoggerFactory.getLogger(SystemInformationProvider.class);

    private int mb = 1024 * 1024;

    public SystemInformationProvider() {
        logInformation();
    }

    public void logInformation() {

        // Getting the runtime reference from system
        Runtime runtime = Runtime.getRuntime();

        // This will return Long.MAX_VALUE if there is no preset limit
        long maxMemory = runtime.maxMemory();
        LOG.info("Available Processors:                                  {}", runtime.availableProcessors());
        LOG.info("Maximum amount of memory the JVM will attempt to use : {}",
                (maxMemory == Long.MAX_VALUE ? "no limit" : StringUtil.formatBytesToHumanReadableRepresentation(maxMemory)));
        LOG.info("Total memory currently in use by the JVM:              {}", StringUtil.formatBytesToHumanReadableRepresentation(runtime.totalMemory()));
        LOG.info("Free memory available to the JVM:                      {}", StringUtil.formatBytesToHumanReadableRepresentation(runtime.freeMemory()));
        LOG.info("Currently used memory:                                 {}",
                StringUtil.formatBytesToHumanReadableRepresentation(runtime.totalMemory() - runtime.freeMemory()));
    }

    /**
     * @return The maximum amount of memory the JVM will attempt to use in MB.
     */
    public int getMaxMemory() {
        long maxMemory = Runtime.getRuntime().maxMemory();
        if (maxMemory == Long.MAX_VALUE) {
            // Limit to 16 GB
            return 16 * 1024;
        }
        return (int) (maxMemory / mb);
    }
}

package de.wehner.mediamagpie.conductor.util;

import static org.fest.assertions.Assertions.*;

import org.junit.Test;

public class SystemInformationProviderTest {

    @Test
    public void test_logInformation() {
        new SystemInformationProvider().logInformation();
    }

    @Test
    public void test_getMaxMemory() {
        SystemInformationProvider systemInformationProvider = new SystemInformationProvider();
        int maxMemory = systemInformationProvider.getMaxMemory();

        assertThat(maxMemory).isLessThan(16 * 1024);
    }
}

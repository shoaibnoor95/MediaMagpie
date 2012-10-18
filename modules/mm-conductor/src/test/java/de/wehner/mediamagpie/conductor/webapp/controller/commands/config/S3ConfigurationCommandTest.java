package de.wehner.mediamagpie.conductor.webapp.controller.commands.config;

import static org.fest.assertions.Assertions.*;

import org.apache.commons.lang3.StringUtils;
import org.junit.Test;

public class S3ConfigurationCommandTest {

    @Test
    public void test_getAnonymizedSecretKey_withNoValue() {
        assertThat(new S3ConfigurationCommand(null, null).getAnonymizedSecretKey()).isEqualTo("<not set>");
        assertThat(new S3ConfigurationCommand(null, "").getAnonymizedSecretKey()).isEqualTo("<not set>");
        assertThat(new S3ConfigurationCommand(null, " ").getAnonymizedSecretKey()).isEqualTo("<not set>");
    }
    
    @Test
    public void test_getAnonymizedSecretKey_withTooShortValue() {
        assertThat(new S3ConfigurationCommand(null, "1").getAnonymizedSecretKey()).isEqualTo("*");
        assertThat(new S3ConfigurationCommand(null, "123").getAnonymizedSecretKey()).isEqualTo("***");
    }
    @Test
    public void test_getAnonymizedSecretKey() {
        assertThat(new S3ConfigurationCommand(null, "1234").getAnonymizedSecretKey()).isEqualTo("***4");
        assertThat(new S3ConfigurationCommand(null, "1234"+StringUtils.repeat('5', 100)).getAnonymizedSecretKey()).isEqualTo("*****************555");
    }
}

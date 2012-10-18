package de.wehner.mediamagpie.conductor.webapp.controller.commands.config;

import org.apache.commons.lang.builder.ReflectionToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
import org.apache.commons.lang3.StringUtils;

import de.wehner.mediamagpie.common.persistence.entity.properties.S3Configuration;

public class S3ConfigurationCommand extends S3Configuration {

    private final int MAX_ANONYMIZED_LENGTH = 20;
    private final char ANONYM_CHAR = '*';

    public S3ConfigurationCommand(String accessKey, String secretKey) {
        _accessKey = (accessKey != null) ? accessKey.trim() : null;
        _secretKey = (secretKey != null) ? secretKey.trim() : null;
    }

    public S3ConfigurationCommand() {
    }

    public static S3ConfigurationCommand createCommand(S3Configuration s3Configuration) {
        S3ConfigurationCommand command = new S3ConfigurationCommand();
        command._accessKey = s3Configuration.getAccessKey();
        command._secretKey = s3Configuration.getSecretKey();
        return command;
    }

    public String getAnonymizedSecretKey() {
        if (StringUtils.isEmpty(_secretKey)) {
            return "<not set>";
        }

        int length = _secretKey.length();
        if (length <= 3) {
            return StringUtils.repeat(ANONYM_CHAR, length);
        }
        String clearPart = StringUtils.right(_secretKey, Math.min(3, length - 3));
        length = Math.min(MAX_ANONYMIZED_LENGTH, length);
        return StringUtils.repeat(ANONYM_CHAR, length - clearPart.length()) + clearPart;
    }

    @Override
    public String toString() {
        return ReflectionToStringBuilder.toString(this, ToStringStyle.SHORT_PREFIX_STYLE);
    }

}

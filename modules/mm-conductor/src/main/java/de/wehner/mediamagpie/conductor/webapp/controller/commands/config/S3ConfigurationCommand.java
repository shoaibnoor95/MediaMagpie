package de.wehner.mediamagpie.conductor.webapp.controller.commands.config;

import ma.glasnost.orika.MapperFacade;
import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.impl.DefaultMapperFactory;

import org.apache.commons.lang.builder.ReflectionToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
import org.apache.commons.lang3.StringUtils;

import de.wehner.mediamagpie.persistence.entity.properties.S3Configuration;

public class S3ConfigurationCommand extends S3Configuration {

    private final int MAX_ANONYMIZED_LENGTH = 20;
    private final char ANONYM_CHAR = '*';
    private static MapperFactory mapperFactory = new DefaultMapperFactory.Builder().build();

    public S3ConfigurationCommand(String accessKey, String secretKey) {
        this.accessKey = (accessKey != null) ? accessKey.trim() : null;
        this.secretKey = (secretKey != null) ? secretKey.trim() : null;
    }

    public S3ConfigurationCommand() {
    }

    public static S3ConfigurationCommand createCommand(S3Configuration s3Configuration) {
        MapperFacade mapper = mapperFactory.getMapperFacade();
        S3ConfigurationCommand command = mapper.map(s3Configuration, S3ConfigurationCommand.class);
        return command;
    }

    public String getAnonymizedSecretKey() {
        if (StringUtils.isEmpty(secretKey)) {
            return "<not set>";
        }

        int length = secretKey.length();
        if (length <= 3) {
            return StringUtils.repeat(ANONYM_CHAR, length);
        }
        String clearPart = StringUtils.right(secretKey, Math.min(3, length - 3));
        length = Math.min(MAX_ANONYMIZED_LENGTH, length);
        return StringUtils.repeat(ANONYM_CHAR, length - clearPart.length()) + clearPart;
    }

    @Override
    public String toString() {
        return ReflectionToStringBuilder.toString(this, ToStringStyle.SHORT_PREFIX_STYLE);
    }

}

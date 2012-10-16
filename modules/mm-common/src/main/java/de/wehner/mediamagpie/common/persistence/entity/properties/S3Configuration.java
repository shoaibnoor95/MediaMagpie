package de.wehner.mediamagpie.common.persistence.entity.properties;

import org.apache.commons.lang.builder.ReflectionToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

import de.wehner.mediamagpie.common.util.properties.PropertiesBacked;

@PropertiesBacked(prefix = "aws.s3.configuration", initFromProperties = false)
public class S3Configuration implements UserPropertyBackedConfiguration {

    private String _accessKey;

    private String _secretKey;

    public S3Configuration() {
    }

    public String getAccessKey() {
        return _accessKey;
    }

    public void setAccessKey(String accessKey) {
        _accessKey = accessKey;
    }

    public String getSecretKey() {
        return _secretKey;
    }

    public void setSecretKey(String secretKey) {
        _secretKey = secretKey;
    }

    @Override
    public String toString() {
        return ReflectionToStringBuilder.toString(this, ToStringStyle.SHORT_PREFIX_STYLE);
    }

}

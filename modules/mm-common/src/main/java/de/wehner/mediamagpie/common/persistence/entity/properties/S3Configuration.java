package de.wehner.mediamagpie.common.persistence.entity.properties;

import org.apache.commons.lang.builder.ReflectionToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

import de.wehner.mediamagpie.common.util.properties.Encrypted;
import de.wehner.mediamagpie.common.util.properties.PropertiesBacked;

@PropertiesBacked(prefix = "aws.s3.configuration", initFromProperties = false)
public class S3Configuration implements UserPropertyBackedConfiguration {

    protected String _accessKey;

    @Encrypted
    protected String _secretKey;

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
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((_accessKey == null) ? 0 : _accessKey.hashCode());
        result = prime * result + ((_secretKey == null) ? 0 : _secretKey.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        S3Configuration other = (S3Configuration) obj;
        if (_accessKey == null) {
            if (other._accessKey != null)
                return false;
        } else if (!_accessKey.equals(other._accessKey))
            return false;
        if (_secretKey == null) {
            if (other._secretKey != null)
                return false;
        } else if (!_secretKey.equals(other._secretKey))
            return false;
        return true;
    }

    @Override
    public String toString() {
        return ReflectionToStringBuilder.toString(this, ToStringStyle.SHORT_PREFIX_STYLE);
    }

}

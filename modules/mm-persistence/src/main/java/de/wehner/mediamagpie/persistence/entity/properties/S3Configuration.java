package de.wehner.mediamagpie.persistence.entity.properties;

import org.apache.commons.lang.builder.ReflectionToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.validator.constraints.Length;

import de.wehner.mediamagpie.core.util.properties.Encrypted;
import de.wehner.mediamagpie.core.util.properties.PropertiesBacked;

@PropertiesBacked(prefix = "aws.s3.configuration", initFromProperties = false)
public class S3Configuration implements UserPropertyBackedConfiguration {

    @Length(min = 20, max = 20)
    protected String accessKey;

    @Length(max = 40)
    @Encrypted
    protected String secretKey;

    protected boolean syncToS3;

    public S3Configuration() {
    }

    public String getAccessKey() {
        return accessKey;
    }

    public void setAccessKey(String accessKey) {
        this.accessKey = accessKey;
    }

    public String getSecretKey() {
        return secretKey;
    }

    public void setSecretKey(String secretKey) {
        this.secretKey = secretKey;
    }

    public boolean isSyncToS3() {
        return syncToS3;
    }

    public void setSyncToS3(boolean syncToS3) {
        this.syncToS3 = syncToS3;
    }

    public boolean isConfigurationComplete() {
        return (!StringUtils.isEmpty(accessKey) && !StringUtils.isEmpty(secretKey));
    }

    public boolean hasToSyncToS3() {
        return (isConfigurationComplete() && isSyncToS3());
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((accessKey == null) ? 0 : accessKey.hashCode());
        result = prime * result + ((secretKey == null) ? 0 : secretKey.hashCode());
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
        if (accessKey == null) {
            if (other.accessKey != null)
                return false;
        } else if (!accessKey.equals(other.accessKey))
            return false;
        if (secretKey == null) {
            if (other.secretKey != null)
                return false;
        } else if (!secretKey.equals(other.secretKey))
            return false;
        return true;
    }

    @Override
    public String toString() {
        return ReflectionToStringBuilder.toString(this, ToStringStyle.SHORT_PREFIX_STYLE);
    }

}

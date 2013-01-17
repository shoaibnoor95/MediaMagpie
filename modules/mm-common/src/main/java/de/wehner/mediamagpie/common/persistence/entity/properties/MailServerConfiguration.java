package de.wehner.mediamagpie.common.persistence.entity.properties;

import org.apache.commons.lang.builder.ReflectionToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
import org.hibernate.validator.constraints.NotEmpty;
import org.hibernate.validator.constraints.Range;

import de.wehner.mediamagpie.common.util.properties.Encrypted;
import de.wehner.mediamagpie.common.util.properties.PropertiesBacked;

@PropertiesBacked(prefix = "mail")
public class MailServerConfiguration implements PropertyBackedConfiguration {

    private boolean _enabled;
    private String _senderName;
    private String _senderAddress;
    private String _emailPrefix;
    private String _hostName;
    private Integer _port;
    private String _userName;
    @Encrypted
    private String _password;
    private boolean _useTls;

    public boolean isEnabled() {
        return _enabled;
    }

    public void setEnabled(boolean enabled) {
        _enabled = enabled;
    }

    @NotEmpty
    public String getSenderName() {
        return _senderName;
    }

    public void setSenderName(String senderName) {
        _senderName = senderName;
    }

    @NotEmpty
    public String getSenderAddress() {
        return _senderAddress;
    }

    public void setSenderAddress(String senderAddress) {
        _senderAddress = senderAddress;
    }

    public String getEmailPrefix() {
        return _emailPrefix;
    }

    public void setEmailPrefix(String emailPrefix) {
        _emailPrefix = emailPrefix;
    }

    @NotEmpty
    public String getHostName() {
        return _hostName;
    }

    public void setHostName(String hostName) {
        _hostName = hostName;
    }

    @Range(min = 25, max = 65535)
    public Integer getPort() {
        return _port;
    }

    public void setPort(Integer port) {
        _port = port;
    }

    public String getUserName() {
        return _userName;
    }

    public void setUserName(String userName) {
        _userName = userName;
    }

    public String getPassword() {
        return _password;
    }

    public void setPassword(String password) {
        _password = password;
    }

    public boolean isUseTls() {
        return _useTls;
    }

    public void setUseTls(boolean useTls) {
        _useTls = useTls;
    }

    @Override
    public String toString() {
        return ReflectionToStringBuilder.toString(this, ToStringStyle.SHORT_PREFIX_STYLE);
    }
}

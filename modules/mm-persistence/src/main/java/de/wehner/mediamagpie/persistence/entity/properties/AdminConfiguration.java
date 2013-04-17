package de.wehner.mediamagpie.persistence.entity.properties;

import org.apache.commons.lang.builder.ReflectionToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
import org.hibernate.validator.constraints.NotEmpty;

import de.wehner.mediamagpie.core.util.properties.PropertiesBacked;

@PropertiesBacked(prefix = "admin")
public class AdminConfiguration implements PropertyBackedConfiguration {

    private String _email;
    private String _password;

    @NotEmpty
    public String getEmail() {
        return _email;
    }

    public void setEmail(String email) {
        _email = email;
    }

    @NotEmpty
    public String getPassword() {
        return _password;
    }

    public void setPassword(String password) {
        _password = password;
    }

    @Override
    public String toString() {
        return ReflectionToStringBuilder.toString(this, ToStringStyle.SHORT_PREFIX_STYLE);
    }

}

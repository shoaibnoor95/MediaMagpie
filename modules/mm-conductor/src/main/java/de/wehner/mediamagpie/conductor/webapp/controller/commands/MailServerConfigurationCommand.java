package de.wehner.mediamagpie.conductor.webapp.controller.commands;

import org.apache.commons.lang.builder.ReflectionToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

import de.wehner.mediamagpie.persistence.entity.properties.MailServerConfiguration;

public class MailServerConfigurationCommand extends MailServerConfiguration {

    private String _passwordConfirm;

    public String getPasswordConfirm() {
        return _passwordConfirm;
    }

    public void setPasswordConfirm(String passwordConfirm) {
        _passwordConfirm = passwordConfirm;
    }

    @Override
    public String toString() {
        return ReflectionToStringBuilder.toString(this, ToStringStyle.SHORT_PREFIX_STYLE);
    }

}

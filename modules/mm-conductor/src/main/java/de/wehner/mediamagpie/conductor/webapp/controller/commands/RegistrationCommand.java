package de.wehner.mediamagpie.conductor.webapp.controller.commands;

import org.hibernate.validator.constraints.NotEmpty;

import de.wehner.mediamagpie.persistence.entity.PasswordConfiguration;
import de.wehner.mediamagpie.persistence.entity.Registration;

public class RegistrationCommand extends Registration implements PasswordConfiguration{

    private String _passwordConfirm;

    public void setPasswordConfirm(String passwordConfirm) {
        _passwordConfirm = passwordConfirm;
    }

    @NotEmpty
    public String getPasswordConfirm() {
        return _passwordConfirm;
    }

}

package de.wehner.mediamagpie.conductor.webapp.controller.commands;

import org.hibernate.validator.constraints.NotEmpty;

public class PasswordResetCommand {

    private String _user;

    @NotEmpty
    public String getUser() {
        return _user;
    }

    public void setUser(String user) {
        _user = user;
    }

}

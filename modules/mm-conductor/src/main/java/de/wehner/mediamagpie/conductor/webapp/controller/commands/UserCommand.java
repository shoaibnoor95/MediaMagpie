package de.wehner.mediamagpie.conductor.webapp.controller.commands;

import de.wehner.mediamagpie.common.persistence.entity.User;

@Deprecated
public class UserCommand extends User {

    private static final long serialVersionUID = 1L;

    private String _passwordConfirm;

    public UserCommand() {
    }

    public UserCommand(String name, String email, Role role) {
        super(name, email, role);
    }

    public void setPasswordConfirm(String passwordConfirm) {
        _passwordConfirm = passwordConfirm;
    }

    public String getPasswordConfirm() {
        return _passwordConfirm;
    }

}

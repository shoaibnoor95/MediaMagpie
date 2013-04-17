package de.wehner.mediamagpie.conductor.webapp.controller.commands;

import de.wehner.mediamagpie.persistence.entity.UserGroup;

public class UserGroupCommand extends UserGroup {

    public UserGroupCommand() {
        super(null);
    }

    public UserGroupCommand(String name) {
        super(name);
    }

}

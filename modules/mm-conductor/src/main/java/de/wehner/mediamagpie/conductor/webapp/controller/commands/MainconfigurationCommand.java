package de.wehner.mediamagpie.conductor.webapp.controller.commands;

import de.wehner.mediamagpie.persistence.entity.properties.MainConfiguration;

public class MainconfigurationCommand extends MainConfiguration {

    private boolean _createDirectories = false;

    public boolean isCreateDirectories() {
        return _createDirectories;
    }

    public void setCreateDirectories(boolean createDirectories) {
        _createDirectories = createDirectories;
    }

}

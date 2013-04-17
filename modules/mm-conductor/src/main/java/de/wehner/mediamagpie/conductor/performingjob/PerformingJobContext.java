package de.wehner.mediamagpie.conductor.performingjob;

import java.io.File;

import de.wehner.mediamagpie.persistence.entity.properties.MainConfiguration;

public class PerformingJobContext {

    private final MainConfiguration _mainConfiguration;

    public PerformingJobContext(MainConfiguration mainConfiguration) {
        _mainConfiguration = mainConfiguration;
    }

    public MainConfiguration getMainConfiguration() {
        return _mainConfiguration;
    }

    public File getTempMediaPath() {
        return new File(_mainConfiguration.getTempMediaPath());
    }
}

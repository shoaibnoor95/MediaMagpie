package de.wehner.mediamagpie.common.persistence.entity.properties;

import de.wehner.mediamagpie.core.util.properties.PropertiesBacked;

@PropertiesBacked(prefix = "system", initFromProperties = false)
public class SystemConfiguration implements PropertyBackedConfiguration {

    private boolean _setupComplete;
    private long _setupCompleteTime;

    public SystemConfiguration(boolean setupComplete) {
        _setupComplete = setupComplete;
        _setupCompleteTime = System.currentTimeMillis();
    }

    protected SystemConfiguration() {
        // for persistence
    }

    public boolean isSetupComplete() {
        return _setupComplete;
    }

    public void setSetupComplete(boolean setupComplete) {
        _setupComplete = setupComplete;
    }

    public long getSetupCompleteTime() {
        return _setupCompleteTime;
    }

    public void setSetupCompleteTime(long setupCompleteTime) {
        _setupCompleteTime = setupCompleteTime;
    }

}

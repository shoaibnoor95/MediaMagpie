package de.wehner.mediamagpie.common.persistence.entity.properties;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.springframework.beans.propertyeditors.StringArrayPropertyEditor;

import de.wehner.mediamagpie.common.util.properties.PropertiesBacked;
import de.wehner.mediamagpie.common.util.properties.PropertyDef;

@PropertiesBacked(prefix = "setuptasks", initFromProperties = false)
public class RequiredSetupTasks implements PropertyBackedConfiguration {

    @PropertyDef(editorClass = StringArrayPropertyEditor.class)
    private String[] _setupTasksInternal = new String[0];

    public String[] getSetupTasksInternal() {
        return _setupTasksInternal;
    }

    public void setSetupTasksInternal(String[] setupTasksInternal) {
        _setupTasksInternal = setupTasksInternal;
    }

    public void add(SetupTask newSetupTask) {
        Set<SetupTask> setupTasks = getSetupTasks();
        setupTasks.add(newSetupTask);
        List<String> setupTasksInternal = new ArrayList<String>();
        for (SetupTask setupTask : setupTasks) {
            setupTasksInternal.add(setupTask.toString());
        }
        _setupTasksInternal = setupTasksInternal.toArray(new String[0]);
    }

    public Set<SetupTask> getSetupTasks() {
        HashSet<SetupTask> set = new HashSet<SetupTask>();
        for (String setupTask : _setupTasksInternal) {
            set.add(SetupTask.valueOf(setupTask));
        }
        return set;
    }

    @Override
    public String toString() {
        return ReflectionToStringBuilder.toString(this, ToStringStyle.SHORT_PREFIX_STYLE);
    }

    public void remove(SetupTask configureSystemDirs) {
        Set<SetupTask> setupTasks = getSetupTasks();
        setupTasks.remove(configureSystemDirs);
        List<String> setupTasksInternal = new ArrayList<String>();
        for (SetupTask setupTask : setupTasks) {
            setupTasksInternal.add(setupTask.toString());
        }
        _setupTasksInternal = setupTasksInternal.toArray(new String[0]);

    }

}

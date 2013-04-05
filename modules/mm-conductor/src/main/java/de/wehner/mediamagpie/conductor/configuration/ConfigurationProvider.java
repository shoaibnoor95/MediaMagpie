package de.wehner.mediamagpie.conductor.configuration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import de.wehner.mediamagpie.common.persistence.entity.properties.MainConfiguration;
import de.wehner.mediamagpie.common.persistence.entity.properties.RequiredSetupTasks;
import de.wehner.mediamagpie.conductor.persistence.dao.ConfigurationDao;
import de.wehner.mediamagpie.conductor.webapp.controller.commands.MainconfigurationCommand;
import de.wehner.mediamagpie.core.util.Holder;

@Service
public class ConfigurationProvider {

    private final ConfigurationDao _configurationDao;

    private final Holder<MainconfigurationCommand> _mainConfigurationHolder = new Holder<MainconfigurationCommand>();
    // TODO rwe: cache this attribute
    private final Holder<RequiredSetupTasks> _requiredSetupTasksHolder = new Holder<RequiredSetupTasks>();

    @Autowired
    public ConfigurationProvider(ConfigurationDao configurationDao) {
        super();
        _configurationDao = configurationDao;
    }

    public MainconfigurationCommand getMainConfiguration() {
        synchronized (_mainConfigurationHolder) {
            if (_mainConfigurationHolder.get() == null) {
                _mainConfigurationHolder.set(_configurationDao.getConfiguration(MainconfigurationCommand.class));
            }
            return _mainConfigurationHolder.get();
        }
    }

    public void saveOrUpdateMainConfiguration(MainConfiguration mainConfiguration) {
        synchronized (_mainConfigurationHolder) {
            _configurationDao.saveOrUpdateConfiguration(mainConfiguration);
            _mainConfigurationHolder.set(null);
        }
    }

    public RequiredSetupTasks getRequiredSetupTasks() {
        synchronized (_requiredSetupTasksHolder) {
            if (_requiredSetupTasksHolder.get() == null) {
                _requiredSetupTasksHolder.set(_configurationDao.getConfiguration(RequiredSetupTasks.class));
            }
            return _requiredSetupTasksHolder.get();
        }
    }

    public void saveOrUpdateRequiredSetupTasks(RequiredSetupTasks requiredSetupTasks) {
        synchronized (_requiredSetupTasksHolder) {
            _configurationDao.saveOrUpdateConfiguration(requiredSetupTasks);
            _requiredSetupTasksHolder.set(null);
        }
    }
}

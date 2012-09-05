package de.wehner.mediamagpie.conductor.webapp.services;

import java.io.File;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import de.wehner.mediamagpie.common.persistence.entity.properties.MainConfiguration;
import de.wehner.mediamagpie.common.persistence.entity.properties.RequiredSetupTasks;
import de.wehner.mediamagpie.common.persistence.entity.properties.SetupTask;
import de.wehner.mediamagpie.conductor.configuration.ConfigurationProvider;
import de.wehner.mediamagpie.conductor.persistence.dao.ConfigurationDao;

@Service
public class SetupVerificationService {

    private static final Logger LOG = LoggerFactory.getLogger(SetupVerificationService.class);

    private final ConfigurationProvider _configurationProvider;
    private final ConfigurationDao _configurationDao;

    @Autowired
    public SetupVerificationService(ConfigurationProvider configurationProvider, ConfigurationDao configurationDao) {
        super();
        _configurationProvider = configurationProvider;
        _configurationDao = configurationDao;
    }

    public void checkSetupStatus() {
        MainConfiguration mainConfiguration = _configurationProvider.getMainConfiguration();
        RequiredSetupTasks setupTasks = _configurationDao.getConfiguration(RequiredSetupTasks.class);

        // test existence of directories configured in MainConfiguration
        String tempMediaPath = mainConfiguration.getTempMediaPath();
        String baseUploadPath = mainConfiguration.getBaseUploadPath();
        if (StringUtils.isEmpty(tempMediaPath) || !new File(tempMediaPath).exists() || StringUtils.isEmpty(baseUploadPath)
                || !new File(baseUploadPath).exists()) {
            SetupTask newTaskToAdd = SetupTask.CONFIGURE_SYSTEM_DIRS;
            LOG.warn("Adding new SetupTask '" + newTaskToAdd + "' because some local pathes are missing on system.");
            setupTasks.add(newTaskToAdd);
            _configurationDao.saveOrUpdateConfiguration(setupTasks);
        }
    }

    public void clearSetupTask(SetupTask setupTask) {
        RequiredSetupTasks requiredSetupTasks = _configurationProvider.getRequiredSetupTasks();
        if (requiredSetupTasks.getSetupTasks().contains(setupTask)) {
            requiredSetupTasks.remove(setupTask);
            _configurationProvider.saveOrUpdateRequiredSetupTasks(requiredSetupTasks);
        }
    }

}

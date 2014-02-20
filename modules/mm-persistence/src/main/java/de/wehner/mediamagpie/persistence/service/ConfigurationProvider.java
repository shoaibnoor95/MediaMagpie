package de.wehner.mediamagpie.persistence.service;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;

import de.wehner.mediamagpie.core.util.ExceptionUtil;
import de.wehner.mediamagpie.core.util.Holder;
import de.wehner.mediamagpie.persistence.dao.ConfigurationDao;
import de.wehner.mediamagpie.persistence.dao.UserConfigurationDao;
import de.wehner.mediamagpie.persistence.entity.User;
import de.wehner.mediamagpie.persistence.entity.properties.MainConfiguration;
import de.wehner.mediamagpie.persistence.entity.properties.RequiredSetupTasks;
import de.wehner.mediamagpie.persistence.entity.properties.S3Configuration;
import de.wehner.mediamagpie.persistence.entity.properties.UserConfiguration;
import de.wehner.mediamagpie.persistence.entity.properties.UserPropertyBackedConfiguration;

/**
 * This class stores/loads/caches:
 * <ul>
 * <li><code>MainConfiguration</code></li>
 * <li><code>RequiredSetupTasks</code></li>
 * </ul>
 * 
 * @author ralfwehner
 * 
 */
@Service
public class ConfigurationProvider {

    private final ConfigurationDao _configurationDao;

    private final UserConfigurationDao _userConfigurationDao;

    private final Holder<MainConfiguration> _mainConfigurationHolder = new Holder<MainConfiguration>();

    private final Holder<RequiredSetupTasks> _requiredSetupTasksHolder = new Holder<RequiredSetupTasks>();

    private final Cache<String, UserConfiguration> _userName2UserConfiguration;

    private final Cache<String, S3Configuration> _userName2S3Configuration;

    @Autowired
    public ConfigurationProvider(ConfigurationDao configurationDao, UserConfigurationDao userConfigurationDao) {
        super();
        _configurationDao = configurationDao;
        _userConfigurationDao = userConfigurationDao;
        _userName2UserConfiguration = CacheBuilder.newBuilder().concurrencyLevel(4).maximumSize(10000).expireAfterWrite(10, TimeUnit.MINUTES).build();
        _userName2S3Configuration = CacheBuilder.newBuilder().concurrencyLevel(4).maximumSize(10000).expireAfterWrite(10, TimeUnit.MINUTES).build();
    }

    public MainConfiguration getMainConfiguration() {
        synchronized (_mainConfigurationHolder) {
            if (_mainConfigurationHolder.get() == null) {
                _mainConfigurationHolder.set(_configurationDao.getConfiguration(MainConfiguration.class));
            }
            return _mainConfigurationHolder.get();
        }
    }

    public UserConfiguration getUserConfiguration(final User user) {
        try {
            return _userName2UserConfiguration.get(user.getName(), new Callable<UserConfiguration>() {

                @Override
                public UserConfiguration call() throws Exception {
                    return _userConfigurationDao.getConfiguration(user, UserConfiguration.class);
                }
            });
        } catch (ExecutionException e) {
            throw ExceptionUtil.convertToRuntimeException(e);
        }
    }

    public <T extends UserPropertyBackedConfiguration> void saveOrUpdateUserConfiguration(User user, T configuration) {
        _userConfigurationDao.saveOrUpdateConfiguration(user, configuration);
        _userName2UserConfiguration.invalidate(user.getName());
    }

    public void clearUserConfiguration(User user) {
        _userName2UserConfiguration.invalidate(user.getName());
    }

    public S3Configuration getS3Configuration(final User user) {
        try {
            return _userName2S3Configuration.get(user.getName(), new Callable<S3Configuration>() {

                @Override
                public S3Configuration call() throws Exception {
                    return _userConfigurationDao.getConfiguration(user, S3Configuration.class);
                }
            });
        } catch (ExecutionException e) {
            throw ExceptionUtil.convertToRuntimeException(e);
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

    public void saveOrUpdateMainConfiguration(MainConfiguration mainConfiguration) {
        synchronized (_mainConfigurationHolder) {
            _configurationDao.saveOrUpdateConfiguration(mainConfiguration);
            _mainConfigurationHolder.set(null);
        }
    }

    public void saveOrUpdateRequiredSetupTasks(RequiredSetupTasks requiredSetupTasks) {
        synchronized (_requiredSetupTasksHolder) {
            _configurationDao.saveOrUpdateConfiguration(requiredSetupTasks);
            _requiredSetupTasksHolder.set(null);
        }
    }

    public void saveOrUpdateS3Configuration(User user, S3Configuration newS3Configuration) {
        _userConfigurationDao.saveOrUpdateConfiguration(user, newS3Configuration);
        _userName2S3Configuration.invalidate(user.getName());
    }

    public ConfigurationFacade createConfigurationFacade(User user) {
        return new ConfigurationFacade(getMainConfiguration(), getUserConfiguration(user));
    }
}

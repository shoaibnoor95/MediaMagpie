package de.wehner.mediamagpie.conductor.webapp.controller;

import org.springframework.security.authentication.InsufficientAuthenticationException;

import de.wehner.mediamagpie.common.persistence.entity.User;
import de.wehner.mediamagpie.common.persistence.entity.properties.MainConfiguration;
import de.wehner.mediamagpie.common.persistence.entity.properties.S3Configuration;
import de.wehner.mediamagpie.common.persistence.entity.properties.UserConfiguration;
import de.wehner.mediamagpie.conductor.configuration.ConfigurationProvider;
import de.wehner.mediamagpie.conductor.persistence.dao.UserConfigurationDao;
import de.wehner.mediamagpie.conductor.persistence.dao.UserDao;
import de.wehner.mediamagpie.conductor.webapp.util.security.SecurityUtil;

public class AbstractConfigurationSupportController {

    protected final ConfigurationProvider _configurationProvider;
    protected final UserConfigurationDao _userConfigurationDao;
    protected final UserDao _userDao;

    public AbstractConfigurationSupportController(ConfigurationProvider configurationProvider, UserConfigurationDao userConfigurationDao, UserDao userDao) {
        super();
        _configurationProvider = configurationProvider;
        _userConfigurationDao = userConfigurationDao;
        _userDao = userDao;
    }

    protected UserConfiguration getCurrentUserConfiguration() {
        return _userConfigurationDao.getConfiguration(SecurityUtil.getCurrentUser(), UserConfiguration.class);
    }

    protected S3Configuration getCurrentUserS3Configuration() {
        return _userConfigurationDao.getConfiguration(SecurityUtil.getCurrentUser(), S3Configuration.class);
    }

    protected User getValidatedRelevantUser(Long userId) {
        User user = SecurityUtil.getCurrentUser();
        if (user != null) {
            user = _userDao.getById(user.getId());
        }
        if (userId != null) {
            // only used, when admin wants to configure foreign users
            if (!SecurityUtil.isUserAuthorizedToConfigureOtherUser(user, userId)) {
                throw new InsufficientAuthenticationException("Access denied for userId: " + userId);
            }
            // swith user
            user = _userDao.getById(userId);
        }
        return user;
    }

    protected MainConfiguration getMainConfiguration() {
        return _configurationProvider.getMainConfiguration();
    }
}
package de.wehner.mediamagpie.conductor.webapp.controller;

import org.springframework.security.authentication.InsufficientAuthenticationException;

import de.wehner.mediamagpie.conductor.webapp.util.security.SecurityUtil;
import de.wehner.mediamagpie.persistence.UserDao;
import de.wehner.mediamagpie.persistence.entity.User;
import de.wehner.mediamagpie.persistence.entity.properties.MainConfiguration;
import de.wehner.mediamagpie.persistence.entity.properties.S3Configuration;
import de.wehner.mediamagpie.persistence.entity.properties.UserConfiguration;
import de.wehner.mediamagpie.persistence.service.ConfigurationProvider;

public class AbstractConfigurationSupportController {

    protected final ConfigurationProvider _configurationProvider;
    protected final UserDao _userDao;

    public AbstractConfigurationSupportController(ConfigurationProvider configurationProvider, UserDao userDao) {
        super();
        _configurationProvider = configurationProvider;
        _userDao = userDao;
    }

    protected UserConfiguration getCurrentUserConfiguration() {
        return _configurationProvider.getUserConfiguration(SecurityUtil.getCurrentUser());
    }

    protected S3Configuration getCurrentUsersS3Configuration() {
        return _configurationProvider.getS3Configuration(SecurityUtil.getCurrentUser());
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
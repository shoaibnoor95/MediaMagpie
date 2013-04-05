package de.wehner.mediamagpie.common.persistence.dao;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import de.wehner.mediamagpie.common.persistence.entity.User;
import de.wehner.mediamagpie.common.persistence.entity.properties.Property;
import de.wehner.mediamagpie.common.persistence.entity.properties.UserPropertyBackedConfiguration;
import de.wehner.mediamagpie.common.util.CipherServiceImpl;
import de.wehner.mediamagpie.core.util.properties.PropertiesUtil;
import de.wehner.mediamagpie.persistence.PersistenceService;

@Repository
public class UserConfigurationDao {

    private static final Logger LOG = LoggerFactory.getLogger(UserConfigurationDao.class);

    private CipherServiceImpl _cipherService;
    private final PersistenceService _persistenceService;

    @Autowired
    public UserConfigurationDao(PersistenceService persistenceService, CipherServiceImpl cipherService) {
        _persistenceService = persistenceService;
        _cipherService = cipherService;
    }

    public <T extends UserPropertyBackedConfiguration> T getConfiguration(User user, Class<T> clazz) {
        return readFromDb(user, clazz);
    }

    private <T extends UserPropertyBackedConfiguration> T readFromDb(User user, Class<T> clazz) {
        Set<Property> settings = _persistenceService.reload(user).getSettings();
        Properties allPropertiesOfUser = getRelevantPropertiesOfClass(clazz, settings);
        return PropertiesUtil.readFromProperties(_cipherService, clazz, allPropertiesOfUser);
    }

    private <T> Properties getRelevantPropertiesOfClass(Class<T> clazz, Set<Property> settings) {
        Properties allPropertiesOfUser = convertToProperties(settings);
        List<String> keysToRemove = new ArrayList<String>();
        List<String> relevantPropertyKeys = PropertiesUtil.getPropertyKeys(clazz);
        for (Object keyInUserSetting : allPropertiesOfUser.keySet()) {
            if (!relevantPropertyKeys.contains(keyInUserSetting)) {
                keysToRemove.add((String) keyInUserSetting);
            }
        }
        for (String key : keysToRemove) {
            allPropertiesOfUser.remove(key);
        }
        return allPropertiesOfUser;
    }

    private Properties convertToProperties(Set<Property> settings) {
        Properties properties = new Properties();
        for (Property property : settings) {
            properties.put(property.getName(), property.getValue());
        }
        return properties;
    }

    public <T extends UserPropertyBackedConfiguration> void saveOrUpdateConfiguration(User user, T configuration) {
        Properties newProperties = PropertiesUtil.transformToProperties(_cipherService, configuration);
        // update existing settings
        User reloadedUser = _persistenceService.reload(user);
        for (Entry<Object, Object> newEntrySet : newProperties.entrySet()) {
            String newKey = (String) newEntrySet.getKey();
            String newValue = (String) newEntrySet.getValue();
            boolean hasProperty = false;
            for (Iterator<Property> it = reloadedUser.getSettings().iterator(); it.hasNext() && !hasProperty;) {
                Property existingProperty = it.next();
                if (existingProperty.getName().equals(newKey)) {
                    // update the existing property with new value
                    LOG.debug(String.format("Replace user property name='%s' with value='%s' -> '%s'.", newKey, existingProperty.getValue(), newValue));
                    existingProperty.setValue(newValue);
                    hasProperty = true;
                }
            }
            if (!hasProperty) {
                reloadedUser.addSetting(new Property(newKey, newValue));
            }
        }
        new UserDao(_persistenceService).makePersistent(reloadedUser);
    }

}

package de.wehner.mediamagpie.persistence;

import java.util.List;
import java.util.Properties;

import org.hibernate.Criteria;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import de.wehner.mediamagpie.core.util.properties.PropertiesBacked;
import de.wehner.mediamagpie.core.util.properties.PropertiesUtil;
import de.wehner.mediamagpie.persistence.entity.properties.Property;
import de.wehner.mediamagpie.persistence.entity.properties.PropertyBackedConfiguration;
import de.wehner.mediamagpie.persistence.util.CipherServiceImpl;


@Repository
public class ConfigurationDao extends Dao<Property> {

    public final static String PROPERTY_SETUP_COMPLETE = "datameer.setup.isComplete";
    private CipherServiceImpl _cipherService;

    @Autowired
    public ConfigurationDao(PersistenceService persistenceService, CipherServiceImpl cipherService) {
        super(Property.class, persistenceService);
        _cipherService = cipherService;
    }

    public void saveOrUpdateConfiguration(PropertyBackedConfiguration configuration) {
        List<Property> propertiesFromDb = getByPrefix(PropertiesUtil.getPrefix(configuration.getClass()));
        Properties newProperties = PropertiesUtil.transformToProperties(_cipherService, configuration);
        if (propertiesFromDb.isEmpty()) {
            saveConfiguration(configuration);
        } else {
            // update or delete existing properties
            for (Property property : propertiesFromDb) {
                String newPropertyValue = (String) newProperties.remove(property.getName());
                if (newPropertyValue != null) {
                    property.setValue(newPropertyValue);
                } else {
                    makeTransient(property);
                }
            }
            // save remaining new properties
            saveProperties(newProperties);
        }
    }

    public void saveConfiguration(PropertyBackedConfiguration configuration) {
        Properties properties = PropertiesUtil.transformToProperties(_cipherService, configuration);
        saveProperties(properties);
    }

    private void saveProperties(Properties properties) {
        for (String key : properties.stringPropertyNames()) {
            Property property = new Property(key, properties.getProperty(key));
            makePersistent(property);
        }
    }

    public <T extends PropertyBackedConfiguration> T getConfiguration(Class<T> clazz) {
        return readFromDb(clazz);
    }

    @SuppressWarnings("unchecked")
    protected List<Property> getByPrefix(String prefix) {
        Criteria criteria = createCriteria();
        criteria.add(Restrictions.like("_name", prefix, MatchMode.START));
        return criteria.list();
    }

    private <T> T readFromDb(Class<T> clazz) {
        return PropertiesUtil.readFromProperties(_cipherService, clazz, getPropertiesFromDb(clazz));
    }

    private Properties getPropertiesFromDb(Class<?> clazz) {
        PropertiesBacked annotation = PropertiesUtil.getPropertiesBackedAnnotation(clazz);
        List<Property> properties = getByPrefix(annotation.prefix());
        Properties properties2 = new Properties();
        for (Property property : properties) {
            properties2.setProperty(property.getName(), property.getValue());
        }
        return properties2;
    }

}

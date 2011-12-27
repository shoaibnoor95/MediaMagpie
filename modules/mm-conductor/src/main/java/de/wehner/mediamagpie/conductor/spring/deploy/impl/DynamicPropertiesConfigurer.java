package de.wehner.mediamagpie.conductor.spring.deploy.impl;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.config.ConstructorArgumentValues;
import org.springframework.beans.factory.config.PropertyPlaceholderConfigurer;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.beans.factory.support.RootBeanDefinition;

import de.wehner.mediamagpie.common.util.SearchPathUtil;
import de.wehner.mediamagpie.common.util.StringUtil;

public class DynamicPropertiesConfigurer extends PropertyPlaceholderConfigurer {

    private static Logger LOG = LoggerFactory.getLogger(DynamicPropertiesConfigurer.class);

    public static final String DEPLOY_MODE = "deploy.mode";
    private static final String SPRINGS_PROFILE_ACTIVE = "spring.profiles.active";

    public static final String DEFAULT_PROPERTIES_FILE = "default.properties";
    private static final String SYSTEM_PROPERTY_PREFIX = "system.property.";
    private Properties _systemProperties;

    public DynamicPropertiesConfigurer(String... propertyPaths) throws IOException {
        this(System.getProperties(), propertyPaths);
    }

    public DynamicPropertiesConfigurer(Properties systemProperties, String... propertyPaths) throws IOException {
        _systemProperties = systemProperties;
        setupDeployMode();
        readProperties(propertyPaths);
    }

    private void setupDeployMode() {
        String deployMode = _systemProperties.getProperty(DEPLOY_MODE);
        if (StringUtils.isEmpty(deployMode)) {
            LOG.warn("No system property '" + DEPLOY_MODE + "' is set. Set deploy.mode to 'local' as default development environment.");
            deployMode = "local";
        }
        LOG.info("Add property '" + SPRINGS_PROFILE_ACTIVE + "' to '" + deployMode + "'.");
        System.getProperties().put(SPRINGS_PROFILE_ACTIVE, deployMode);
    }

    private void readProperties(String... propertyPaths) throws IOException {
        List<Properties> allProperties = new ArrayList<Properties>();

        for (String propertiesPath : propertyPaths) {
            Properties defaultProperties = loadProperties(propertiesPath, DEFAULT_PROPERTIES_FILE, null);

            String modeName = propertiesPath.substring(propertiesPath.lastIndexOf("/") + 1);
            String modeSystemKey = modeName + ".mode";
            String setupMode = getSystemProperty(defaultProperties, modeSystemKey);
            if (StringUtils.isEmpty(setupMode)) {
                throw new IllegalStateException("System variable '" + modeSystemKey + "' not defined! Configure with -D" + modeSystemKey + "=<mode>");
            }
            String modePropertiesFile = setupMode + ".properties";
            Properties modeProperties = loadProperties(propertiesPath, modePropertiesFile, defaultProperties);
            setSystemProperties(modeProperties);
            allProperties.add(modeProperties);
            LOG.info("application starts with " + modeName + " properties '" + setupMode + "'");
        }

        setPropertiesArray(allProperties.toArray(new Properties[allProperties.size()]));
    }

    private String getSystemProperty(String key) {
        return _systemProperties.getProperty(key);
    }

    private String getSystemProperty(Properties defaultProperties, String key) {
        return _systemProperties.getProperty(key, defaultProperties.getProperty(SYSTEM_PROPERTY_PREFIX + key));
    }

    private void setSystemProperty(String key, String value) {
        _systemProperties.setProperty(key, value);
    }

    public Properties getProperties() throws IOException {
        return mergeProperties();
    }

    @SuppressWarnings("unchecked")
    private void setSystemProperties(Properties properties) {
        Enumeration<String> propertyNames = (Enumeration<String>) properties.propertyNames();
        while (propertyNames.hasMoreElements()) {
            String propertyName = propertyNames.nextElement();
            if (propertyName.startsWith(SYSTEM_PROPERTY_PREFIX)) {
                String value = properties.getProperty(propertyName);
                String realKey = propertyName.substring(SYSTEM_PROPERTY_PREFIX.length());
                if (StringUtil.isEmpty(getSystemProperty(realKey))) {
                    LOG.info("setting configured system property '" + realKey + "' to '" + value + "'");
                    setSystemProperty(realKey, value);
                } else {
                    LOG.info("do not overwrite configured system property '" + realKey + "'");
                }
            }
        }
    }

    private Properties loadProperties(String parentFolder, String fileName, Properties defaultProperties) throws IOException {
        InputStream inputStream = SearchPathUtil.openStream("conf/" + fileName, parentFolder + "/" + fileName, "classpath:/" + fileName, "classpath:"
                + parentFolder + "/" + fileName);
        Properties properties = new Properties(defaultProperties);
        properties.load(inputStream);
        inputStream.close();

        // overwrite all properties with system properties
        Set<Entry<Object, Object>> entrySet = properties.entrySet();
        for (Entry<Object, Object> entry : entrySet) {
            String systemValue = getSystemProperty((String) entry.getKey());
            if (systemValue != null) {
                entry.setValue(systemValue);
            }
        }

        return properties;
    }

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
        super.postProcessBeanFactory(beanFactory);

        DefaultListableBeanFactory factory = (DefaultListableBeanFactory) beanFactory;

        try {
            Properties properties = mergeProperties();

            Set<Entry<Object, Object>> entrySet = properties.entrySet();
            for (Entry<Object, Object> entry : entrySet) {
                registerProperty(factory, (String) entry.getKey(), (String) entry.getValue(), properties);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void registerProperty(DefaultListableBeanFactory factory, String key, String value, Properties properties) {
        LOG.debug("Registering property " + key + "=" + value);
        RootBeanDefinition rbd = new RootBeanDefinition();
        rbd.setAbstract(false);
        rbd.setLazyInit(true);
        rbd.setAutowireCandidate(true);

        ConstructorArgumentValues constructorArgumentValues = new ConstructorArgumentValues();
        rbd.setConstructorArgumentValues(constructorArgumentValues);

        try {
            int parseInt = Integer.parseInt(value);
            constructorArgumentValues.addIndexedArgumentValue(0, parseInt);
            rbd.setBeanClass(Integer.class);
            LOG.debug("type=Integer");
        } catch (NumberFormatException e) {
            if (value.equals("true") || value.equals("false")) {
                constructorArgumentValues.addIndexedArgumentValue(0, Boolean.valueOf(value));
                rbd.setBeanClass(Boolean.class);
                LOG.debug("type=boolean");
            } else {
                // first we want to resolve nested properties..
                value = StringUtil.interpolateString(value, properties);

                constructorArgumentValues.addIndexedArgumentValue(0, value);
                rbd.setBeanClass(String.class);
                LOG.debug("type=String");
            }
        }

        factory.registerBeanDefinition(key, rbd);
    }
}

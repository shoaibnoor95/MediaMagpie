package de.wehner.mediamagpie.conductor.spring.deploy.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.Callable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Validator;

import de.wehner.mediamagpie.conductor.spring.deploy.DataInjector;
import de.wehner.mediamagpie.conductor.webapp.services.SetupVerificationService;
import de.wehner.mediamagpie.core.util.ClassLocator;
import de.wehner.mediamagpie.core.util.properties.PropertiesBacked;
import de.wehner.mediamagpie.core.util.properties.PropertiesUtil;
import de.wehner.mediamagpie.persistence.dao.ConfigurationDao;
import de.wehner.mediamagpie.persistence.dao.TransactionHandler;
import de.wehner.mediamagpie.persistence.dao.UserConfigurationDao;
import de.wehner.mediamagpie.persistence.dao.UserDao;
import de.wehner.mediamagpie.persistence.entity.User;
import de.wehner.mediamagpie.persistence.entity.properties.MainConfiguration;
import de.wehner.mediamagpie.persistence.entity.properties.PropertyBackedConfiguration;
import de.wehner.mediamagpie.persistence.entity.properties.SystemConfiguration;
import de.wehner.mediamagpie.persistence.entity.properties.UserConfiguration;
import de.wehner.mediamagpie.persistence.util.CipherServiceImpl;

@Component
public class SetupPropertiesInjector implements DataInjector {

    private static final Logger LOG = LoggerFactory.getLogger(SetupPropertiesInjector.class);

    private final DynamicPropertiesConfigurer _dynamicPropertiesConfigurer;
    private final UserDao _userDao;
    private final TransactionHandler _transactionHandler;
    private final ConfigurationDao _configurationDao;
    private final UserConfigurationDao _userConfigurationDao;
    private final Validator _validator;
    private final CipherServiceImpl _cipherService;
    private final SetupVerificationService _setupVerificationService;

    @Autowired
    public SetupPropertiesInjector(DynamicPropertiesConfigurer dynamicPropertiesConfigurer, UserDao userDao, ConfigurationDao configurationDao,
            UserConfigurationDao userConfigurationDao, TransactionHandler transactionHandler, Validator validator, CipherServiceImpl cipherService,
            SetupVerificationService setupVerificationService) throws IOException {
        _dynamicPropertiesConfigurer = dynamicPropertiesConfigurer;
        _userDao = userDao;
        _transactionHandler = transactionHandler;
        _configurationDao = configurationDao;
        _userConfigurationDao = userConfigurationDao;
        _validator = validator;
        _cipherService = cipherService;
        _setupVerificationService = setupVerificationService;
    }

    @Override
    @SuppressWarnings("unchecked")
    public void injectData() throws Exception {

        final Properties properties = _dynamicPropertiesConfigurer.getProperties();
        _transactionHandler.executeInTransaction(new Runnable() {

            // Set non-user configurations if not initialized once before
            @SuppressWarnings("rawtypes")
            @Override
            public void run() {
                boolean runGlobalConfigInit = true;
                if (_configurationDao.countAll() > 0) {
                    LOG.info("Properties already seems to be initialized. Skip property initalisation.");
                    runGlobalConfigInit = false;
                }
                if (runGlobalConfigInit) {
                    final Set<Class<?>> classes = getSetupEntityClasses();
                    final List<Object> configurationObjects = new ArrayList<Object>(classes.size());
                    try {
                        for (Class clazz : classes) {
                            // FIXME rwe: In case we still want to use the spring's Validator to validate classes here, we have to create
                            // our own Annotation class, because the <code>Validatable</code> class comes from hibernate and isn't available
                            // in version 4.0.
                            // Annotation validatable = clazz.getAnnotation(Validatable.class);
                            Object setupEntity = PropertiesUtil.readFromProperties(_cipherService, clazz, properties);
                            // if (validatable == null) {
                            PropertiesUtil.checkPropertyCompleteness(clazz, properties, true);
                            // } else {
                            BindingResult bindingResult = new BeanPropertyBindingResult(setupEntity, "");
                            _validator.validate(setupEntity, bindingResult);
                            if (bindingResult.hasErrors()) {
                                throw new IllegalStateException("following errors on " + setupEntity + ": " + bindingResult.getAllErrors());
                            }
                            // }
                            configurationObjects.add(setupEntity);
                        }
                    } catch (Exception e) {
                        LOG.warn("could not initialize setup properties - will go in setup mode: " + e.getMessage(), e);
                        return;
                    }
                    for (Object configuration : configurationObjects) {
                        _configurationDao.saveConfiguration((PropertyBackedConfiguration) configuration);
                    }
                    SystemConfiguration systemConfiguration = new SystemConfiguration(true);
                    _configurationDao.saveConfiguration(systemConfiguration);
                }
            }
        });

        List<User> allUsers = _transactionHandler.executeInTransaction(new Callable<List<User>>() {

            @Override
            public List<User> call() throws Exception {
                return _userDao.getAll();
            }
        });

        for (final User userDetatched : allUsers) {
            _transactionHandler.executeInTransaction(new Runnable() {
                // Set non-user configurations if not initialized once before
                @SuppressWarnings("rawtypes")
                @Override
                public void run() {
                    User user = _userDao.getById(userDetatched.getId());
                    if (user.getSettings().isEmpty()) {
                        LOG.info("Initialize User Properties for user '" + user.getUsername() + "'.");
                        try {
                            Class clazz = UserConfiguration.class;
                            // FIXME rwe: In case we still want to use the spring's Validator to validate classes here, we have to create
                            // own Annotation class, because the <code>Validatabe</code> class comes from hibernate and isn't available in
                            // version 4.0.
                            // Annotation validatable = clazz.getAnnotation(Validatable.class);
                            Object setupEntity = PropertiesUtil.readFromProperties(_cipherService, clazz, properties);
                            // if (validatable == null) {
                            PropertiesUtil.checkPropertyCompleteness(clazz, properties, false);
                            // } else {
                            BindingResult bindingResult = new BeanPropertyBindingResult(setupEntity, "");
                            _validator.validate(setupEntity, bindingResult);
                            if (bindingResult.hasErrors()) {
                                throw new IllegalStateException("following errors on " + setupEntity + ": " + bindingResult.getAllErrors());
                            }
                            // }
                            LOG.info("Save UserConfiguration with default values for user '{}': {}", user.getName(), setupEntity.toString());
                            _userConfigurationDao.saveOrUpdateConfiguration(user, (UserConfiguration) setupEntity);
                            _transactionHandler.getPersistenceService().flipTransaction();
                        } catch (Exception e) {
                            LOG.warn("could not initialize setup properties - will go in setup mode: " + e.getMessage(), e);
                            return;
                        }
                    }
                }
            });
        }

        _transactionHandler.executeInTransaction(new Runnable() {
            // Set non-user configurations if not initialized once before
            @Override
            public void run() {
                _setupVerificationService.checkSetupStatus();
            }
        });

    }

    @SuppressWarnings("unchecked")
    protected Set<Class<?>> getSetupEntityClasses() {
        ClassLocator classLocator = new ClassLocator();
        classLocator.setPackageNames(MainConfiguration.class.getPackage().getName());
        classLocator.setAnnotations(PropertiesBacked.class);
        Set<Class<?>> classes = classLocator.findClasses();
        for (Iterator<Class<?>> iterator = classes.iterator(); iterator.hasNext();) {
            Class<?> clazz = iterator.next();
            PropertiesBacked annotation = (PropertiesBacked) clazz.getAnnotation(PropertiesBacked.class);
            if (!annotation.initFromProperties()) {
                iterator.remove();
            }
        }
        return classes;
    }
}

package de.wehner.mediamagpie.conductor.spring.deploy.impl;

import static org.mockito.Matchers.*;

import static org.mockito.Mockito.*;

import java.util.Properties;
import java.util.concurrent.Callable;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.validation.Validator;

import de.wehner.mediamagpie.common.persistence.entity.properties.AdminConfiguration;
import de.wehner.mediamagpie.common.persistence.entity.properties.PropertyBackedConfiguration;
import de.wehner.mediamagpie.common.util.CipherService;
import de.wehner.mediamagpie.common.util.properties.PropertiesUtil;
import de.wehner.mediamagpie.conductor.persistence.TransactionHandler;
import de.wehner.mediamagpie.conductor.persistence.dao.ConfigurationDao;
import de.wehner.mediamagpie.conductor.persistence.dao.UserConfigurationDao;
import de.wehner.mediamagpie.conductor.persistence.dao.UserDao;
import de.wehner.mediamagpie.conductor.webapp.services.SetupVerificationService;

public class SetupPropertiesInjectorTest {

    @Mock
    private DynamicPropertiesConfigurer _dynamicPropertiesConfigurer;
    @Mock
    private ConfigurationDao _confDao;
    @Mock
    private UserConfigurationDao _userConfigurationDao;
    @Mock
    private UserDao _userDao;
    @Mock
    private TransactionHandler _transactionHandler;
    private Properties _props = new Properties();
    private ArgumentCaptor<Runnable> _captor = ArgumentCaptor.forClass(Runnable.class);
    @Mock
    private Validator _beanValidator;
    @Mock
    private CipherService _cipherService;
    @Mock
    private SetupVerificationService _setupVerificationService;
    @InjectMocks
    private SetupPropertiesInjector _setupPropertiesInjector;

    @SuppressWarnings("unchecked")
    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        when(_transactionHandler.executeInTransaction(any(Callable.class))).thenReturn(Boolean.FALSE);
    }

    @Test
    public void testNoConfStoringWhenNoProperties() throws Exception {
        when(_dynamicPropertiesConfigurer.getProperties()).thenReturn(_props);
        doNothing().when(_transactionHandler).executeInTransaction(_captor.capture());

        execute(_setupPropertiesInjector);

        verify(_confDao).countAll();
        verifyNoMoreInteractions(_confDao);
    }

    @Test
    public void testNoConfStoringWhenIncompleteProperties() throws Exception {
        AdminConfiguration conf = new AdminConfiguration();
        conf.setEmail("email");
        conf.setPassword("***");
        _props = PropertiesUtil.transformToProperties(_cipherService, conf);
        when(_dynamicPropertiesConfigurer.getProperties()).thenReturn(_props);
        doNothing().when(_transactionHandler).executeInTransaction(_captor.capture());

        execute(_setupPropertiesInjector);

        verify(_confDao).countAll();
        verifyNoMoreInteractions(_confDao);
    }

    @Test
    public void testNoConfStoringWhenConfAlreadyStored() throws Exception {
        _props.load(SetupPropertiesInjector.class.getResourceAsStream("/properties/deploy/default.properties"));
        when(_dynamicPropertiesConfigurer.getProperties()).thenReturn(_props);
        when(_confDao.countAll()).thenReturn(23L);
        doNothing().when(_transactionHandler).executeInTransaction(_captor.capture());

        execute(_setupPropertiesInjector);

        verify(_confDao).countAll();
        verifyNoMoreInteractions(_confDao);
    }

    @Test
    public void testStoring() throws Exception {
        _props.load(SetupPropertiesInjector.class.getResourceAsStream("/properties/deploy/default.properties"));
        when(_dynamicPropertiesConfigurer.getProperties()).thenReturn(_props);
        SetupPropertiesInjector injector = new SetupPropertiesInjector(_dynamicPropertiesConfigurer, _userDao, _confDao, _userConfigurationDao,
                _transactionHandler, _beanValidator, _cipherService, _setupVerificationService);
        doNothing().when(_transactionHandler).executeInTransaction(_captor.capture());

        execute(injector);

        verify(_confDao).countAll();
        verify(_confDao, times(_setupPropertiesInjector.getSetupEntityClasses().size() + 1)).saveConfiguration(any(PropertyBackedConfiguration.class));
        verifyNoMoreInteractions(_confDao);
    }

    private void execute(SetupPropertiesInjector injector) throws Exception {
        injector.injectData();
        _captor.getValue().run();
    }
}
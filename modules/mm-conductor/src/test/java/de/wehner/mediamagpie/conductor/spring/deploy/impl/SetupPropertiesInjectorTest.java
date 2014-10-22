package de.wehner.mediamagpie.conductor.spring.deploy.impl;

import static org.mockito.Matchers.*;

import static org.mockito.Mockito.*;

import java.util.Properties;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.springframework.util.CollectionUtils;
import org.springframework.validation.Validator;

import de.wehner.mediamagpie.conductor.webapp.services.SetupVerificationService;
import de.wehner.mediamagpie.core.util.SearchPathUtil;
import de.wehner.mediamagpie.core.util.properties.PropertiesUtil;
import de.wehner.mediamagpie.persistence.TransactionHandlerMock;
import de.wehner.mediamagpie.persistence.dao.ConfigurationDao;
import de.wehner.mediamagpie.persistence.dao.TransactionHandler;
import de.wehner.mediamagpie.persistence.dao.UserConfigurationDao;
import de.wehner.mediamagpie.persistence.dao.UserDao;
import de.wehner.mediamagpie.persistence.entity.properties.AdminConfiguration;
import de.wehner.mediamagpie.persistence.entity.properties.PropertyBackedConfiguration;
import de.wehner.mediamagpie.persistence.util.CipherServiceImpl;

public class SetupPropertiesInjectorTest {

    @Mock
    private DynamicPropertiesConfigurer _dynamicPropertiesConfigurer;
    @Mock
    private ConfigurationDao _confDao;
    @Mock
    private UserConfigurationDao _userConfigurationDao;
    @Mock
    private UserDao _userDao;
    @Spy
    private TransactionHandler _transactionHandler = new TransactionHandlerMock();
    private Properties _props = new Properties();
    @Mock
    private Validator _beanValidator;
    @Mock
    private CipherServiceImpl _cipherService;
    @Mock
    private SetupVerificationService _setupVerificationService;
    @InjectMocks
    private SetupPropertiesInjector _setupPropertiesInjector;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testNoConfStoringWhenNoProperties() throws Exception {
        when(_dynamicPropertiesConfigurer.getProperties()).thenReturn(_props);
        //
        _setupPropertiesInjector.injectData();

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

        _setupPropertiesInjector.injectData();

        verify(_confDao).countAll();
        verifyNoMoreInteractions(_confDao);
    }

    @Test
    public void testNoConfStoringWhenConfAlreadyStored() throws Exception {
        _props.load(SetupPropertiesInjector.class.getResourceAsStream("/properties/deploy/default.properties"));
        when(_dynamicPropertiesConfigurer.getProperties()).thenReturn(_props);
        when(_confDao.countAll()).thenReturn(23L);

        _setupPropertiesInjector.injectData();

        verify(_confDao).countAll();
        verifyNoMoreInteractions(_confDao);
    }

    @Test
    public void testStoring() throws Exception {
        Properties defaultProps = SearchPathUtil.loadProperties("classpath:/properties/deploy/default.properties");
        Properties testProps = SearchPathUtil.loadProperties("classpath:/properties/deploy/test.properties");
        CollectionUtils.mergePropertiesIntoMap(defaultProps, _props);
        CollectionUtils.mergePropertiesIntoMap(testProps, _props);
        when(_dynamicPropertiesConfigurer.getProperties()).thenReturn(_props);

        _setupPropertiesInjector.injectData();

        verify(_confDao).countAll();
        verify(_confDao, times(_setupPropertiesInjector.getSetupEntityClasses().size() + 1)).saveConfiguration(any(PropertyBackedConfiguration.class));
        verifyNoMoreInteractions(_confDao);
    }
}

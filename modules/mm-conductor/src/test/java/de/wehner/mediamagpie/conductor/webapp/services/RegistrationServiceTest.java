package de.wehner.mediamagpie.conductor.webapp.services;

import static org.fest.assertions.Assertions.*;

import static org.mockito.Mockito.*;

import java.io.IOException;
import java.util.Properties;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.core.io.support.PropertiesLoaderUtils;

import de.wehner.mediamagpie.conductor.exception.RegistrationException;
import de.wehner.mediamagpie.conductor.spring.deploy.impl.DynamicPropertiesConfigurer;
import de.wehner.mediamagpie.persistence.dao.RegistrationDao;
import de.wehner.mediamagpie.persistence.entity.Registration;
import de.wehner.mediamagpie.persistence.entity.User;
import de.wehner.mediamagpie.persistence.entity.properties.UserConfiguration;
import de.wehner.mediamagpie.persistence.util.CipherServiceImpl;

public class RegistrationServiceTest {

    private final String CIPHERKEY = "myCipherKey";
    private RegistrationService _registrationService;
    private CipherServiceImpl _cipherService;
    @Mock
    private RegistrationDao _registrationDao;
    @Mock
    private DynamicPropertiesConfigurer _dynamicPropertiesConfigurer;

    private Registration _registration;

    @Before
    public void setUp() throws IOException {
        MockitoAnnotations.initMocks(this);
        _cipherService = new CipherServiceImpl(CIPHERKEY);
        Properties allProperties = PropertiesLoaderUtils.loadAllProperties("properties/test_default.properties");
        when(_dynamicPropertiesConfigurer.getProperties()).thenReturn(allProperties);
        _registrationService = new RegistrationService(_cipherService, _registrationDao, _dynamicPropertiesConfigurer);
        _registration = new Registration();
        _registration.setUser("rwe");
        _registration.setForename("forename");
        _registration.setSurname("surname");
        _registration.setPassword("password");
        when(_registrationDao.getById(1L)).thenReturn(_registration);
    }

    @Test(expected = RegistrationException.class)
    public void testDecodeRegistrationFromActivationLink_invalidParam() throws RegistrationException {
        _registrationService.decodeRegistrationFromActivationLink("blah");
    }

    @Test(expected = RegistrationException.class)
    public void testDecodeRegistrationFromActivationLink_LinkIsEncodedButHasInvalidContent() throws RegistrationException {
        String param = _cipherService.encryptToBase64("blah");
        _registrationService.decodeRegistrationFromActivationLink(param);
    }

    @Test(expected = RegistrationException.class)
    public void testDecodeRegistrationFromActivationLink_UserDoesNotExist() throws RegistrationException {
        String param = _cipherService.encryptToBase64("0#blah");
        _registrationService.decodeRegistrationFromActivationLink(param);
    }

    @Test(expected = RegistrationException.class)
    public void testDecodeRegistrationFromActivationLink_UnknownUser() throws RegistrationException {
        String param = _cipherService.encryptToBase64("1#blah");
        _registrationService.decodeRegistrationFromActivationLink(param);
    }

    @Test
    public void testDecodeRegistrationFromActivationLink() throws RegistrationException {
        String param = _cipherService.encryptToHex("1#rwe");
        Registration _registration = _registrationService.decodeRegistrationFromActivationLink(param);
        assertThat(_registration).isEqualTo(_registration);
    }

    @Test
    public void testCreateActivationLink_RoundRobin() throws RegistrationException {
        final String PARAM = "activationlink";
        String link = _registrationService.createActivationLink(PARAM, 1L, "rwe");
        System.out.println(link);
        assertThat(link).startsWith("http://");
        assertThat(link).endsWith("/public/account/confirm?" + PARAM + "=" + _cipherService.encryptToHex("1#rwe"));

        int pos = link.indexOf("?" + PARAM + "=");
        String requestParam = link.substring(pos + PARAM.length() + 2);
        Registration registration2 = _registrationService.decodeRegistrationFromActivationLink(requestParam);
        assertThat(registration2).isEqualTo(_registration);
    }

    @Test
    public void testCreateUserFromRegistration() {
        User newUser = _registrationService.createUserFromRegistration(_registration);

        assertThat(newUser.getName()).isEqualTo(_registration.getUser());
        assertThat(newUser.getForename()).isEqualTo(_registration.getForename());
        assertThat(newUser.getSurname()).isEqualTo(_registration.getSurname());
        assertThat(newUser.getEmail()).isEqualTo(_registration.getEmail());
    }

    @Test
    public void testCreateDefaultUserConfiguration() {
        UserConfiguration createDefaultUserConfiguration = _registrationService.createDefaultUserConfiguration();

        assertThat(createDefaultUserConfiguration.getThumbImageSize()).isGreaterThan(0);
    }
}

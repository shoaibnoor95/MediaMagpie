package de.wehner.mediamagpie.conductor.webapp.services;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Properties;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import de.wehner.mediamagpie.conductor.StartJetty;
import de.wehner.mediamagpie.conductor.exception.RegistrationException;
import de.wehner.mediamagpie.conductor.spring.deploy.impl.DynamicPropertiesConfigurer;
import de.wehner.mediamagpie.conductor.webapp.controller.registration.RegistrationProcessController;
import de.wehner.mediamagpie.core.util.ExceptionUtil;
import de.wehner.mediamagpie.core.util.properties.PropertiesUtil;
import de.wehner.mediamagpie.persistence.dao.RegistrationDao;
import de.wehner.mediamagpie.persistence.entity.Registration;
import de.wehner.mediamagpie.persistence.entity.User;
import de.wehner.mediamagpie.persistence.entity.User.Role;
import de.wehner.mediamagpie.persistence.entity.properties.UserConfiguration;
import de.wehner.mediamagpie.persistence.util.CipherServiceImpl;

@Service
public class RegistrationService {

    private final CipherServiceImpl _cipherService;
    private final RegistrationDao _registrationDao;
    private Properties _properties;

    @Autowired
    public RegistrationService(CipherServiceImpl cipherService, RegistrationDao registrationDao, DynamicPropertiesConfigurer dynamicPropertiesConfigurer)
            throws IOException {
        super();
        _cipherService = cipherService;
        _registrationDao = registrationDao;
        _properties = dynamicPropertiesConfigurer.getProperties();
    }

    public String createActivationLink(String requestParam, long registrationId, String loginId) {
        String computername = "localhost";
        try {
            InetAddress localHost = InetAddress.getLocalHost();
            computername = localHost.getHostName();
        } catch (UnknownHostException e) {
            throw ExceptionUtil.convertToRuntimeException(e);
        }
        StringBuilder builder = new StringBuilder("http://");
        builder.append(computername);
        if (_properties.getProperty(StartJetty.WEB_APP_PORT) != null) {
            builder.append(':').append(_properties.getProperty(StartJetty.WEB_APP_PORT));
        }
        if (_properties.getProperty(StartJetty.WEB_APP_CONTEXTPATH) != null && !_properties.getProperty(StartJetty.WEB_APP_CONTEXTPATH).equals("/")) {
            builder.append(_properties.getProperty(StartJetty.WEB_APP_CONTEXTPATH));
        }
        builder.append(RegistrationProcessController.getBaseRequestMappingUrl() + RegistrationProcessController.URL_ACTIVATE);

        String key = registrationId + "#" + loginId;
        builder.append('?').append(requestParam).append('=').append(_cipherService.encryptToHex(key));
        return builder.toString();
    }

    public Registration decodeRegistrationFromActivationLink(String linkParam) throws RegistrationException {

        String decrypt = _cipherService.decryptFromHex(linkParam);
        String[] registrationIdAndLogin = decrypt.split("#");
        if (registrationIdAndLogin.length == 1) {
            throw new RegistrationException("Invalid registration parmater '" + decrypt + "'. Missing charchter '#'.");
        }

        Registration registration = _registrationDao.getById(Long.parseLong(registrationIdAndLogin[0]));
        if (registration == null) {
            throw new RegistrationException("Invalid registration parmater '" + decrypt + "'.Can not find registration for id '"
                    + registrationIdAndLogin[0] + "'.");
        }
        if (registration.getUser().equals(registrationIdAndLogin[1])) {
            return registration;
        }
        throw new RegistrationException("The loaded registration is not suitable to encoded user.");
    }

    /**
     * Creates a new User entity based on a given <code>Registration</code> object. The provided User has no user configuration. To get this
     * call {@linkplain #createDefaultUserConfiguration()}.
     * 
     * @param registration
     * @return
     */
    public User createUserFromRegistration(Registration registration) {
        User newUser = new User(registration.getUser(), registration.getEmail(), Role.USER);
        newUser.setForename(registration.getForename());
        newUser.setSurname(registration.getSurname());
        newUser.setPassword(registration.getPassword());
        return newUser;
    }

    /**
     * Creates a default <code>UserConfiguration</code>, which can be assigned to a new created user.
     * 
     * @return
     */
    public UserConfiguration createDefaultUserConfiguration() {
        UserConfiguration defaultUserConfiguration = PropertiesUtil.readFromProperties(_cipherService, UserConfiguration.class, _properties);

        return defaultUserConfiguration;
    }
}

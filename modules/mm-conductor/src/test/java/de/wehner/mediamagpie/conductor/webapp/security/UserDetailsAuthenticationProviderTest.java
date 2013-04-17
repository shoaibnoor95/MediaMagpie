package de.wehner.mediamagpie.conductor.webapp.security;

import static org.fest.assertions.Assertions.*;

import static org.mockito.Mockito.*;

import java.util.Arrays;
import java.util.Hashtable;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.context.MessageSource;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.util.ReflectionTestUtils;

import de.wehner.mediamagpie.conductor.webapp.services.UserSecurityService;
import de.wehner.mediamagpie.persistence.TransactionHandlerMock;
import de.wehner.mediamagpie.persistence.UserDao;
import de.wehner.mediamagpie.persistence.entity.User;
import de.wehner.mediamagpie.persistence.entity.User.Role;

public class UserDetailsAuthenticationProviderTest {

    private static final String VALID_PASSWORD = "admin";
    private UserSecurityService _userSecurityService;
    @Mock
    private UserDao _userDao;
    @Mock
    private MessageSource _messageSource;
    private UserDetailsAuthenticationProvider _userDetailsAuthenticationProvider;
    private User _adminUser;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        _adminUser = new User("admin", "admin@localhost", Role.ADMIN);
        _adminUser.setPassword(UserSecurityService.crypt(VALID_PASSWORD));
        System.out.println("setup user '" + _adminUser.getUsername() + "' with password '" + _adminUser.getPassword() + "' (" + VALID_PASSWORD + ")");
        when(_userDao.getUserLikeName(_adminUser.getUsername())).thenReturn(Arrays.asList(_adminUser));
        when(_userDao.getByName(_adminUser.getUsername())).thenReturn(_adminUser);
        _userSecurityService = new UserSecurityService(new TransactionHandlerMock(), _userDao);
        _userDetailsAuthenticationProvider = new UserDetailsAuthenticationProvider(_userSecurityService, _messageSource);
        // remove possible locked information in static object
        @SuppressWarnings("unchecked")
        Hashtable<String, Long> lockedTime = (Hashtable<String, Long>) ReflectionTestUtils.getField(_userDetailsAuthenticationProvider, "_lockTime");
        lockedTime.remove(_adminUser.getName());
    }

    @Test
    public void testRetrieveUser() {
        UserDetails foundUser = _userDetailsAuthenticationProvider.retrieveUser("admin", null);

        assertThat(foundUser).isEqualTo(_adminUser);
    }

    @Test
    public void testAdditionalAuthenticationChecks_OK() {
        _userDetailsAuthenticationProvider.additionalAuthenticationChecks(_adminUser, new UsernamePasswordAuthenticationToken("admin", VALID_PASSWORD));
    }

    @Test(expected = BadCredentialsException.class)
    public void testAdditionalAuthenticationChecks_InvalidPassword() {
        
        _userDetailsAuthenticationProvider.additionalAuthenticationChecks(_adminUser, new UsernamePasswordAuthenticationToken("admin", "blah"));
    }

    @Test(expected = LockedException.class)
    public void testAdditionalAuthenticationChecks_LockedDueToTooManyInvalidPassword() {
        for (int i = 0; i < UserDetailsAuthenticationProvider.MAX_ATTEMPTS; i++) {
            try {
                _userDetailsAuthenticationProvider.additionalAuthenticationChecks(_adminUser, new UsernamePasswordAuthenticationToken("admin", "blah"));
            } catch (BadCredentialsException e) {
            }
        }

        // one more erroneous login will cause an .. exception
        _userDetailsAuthenticationProvider.additionalAuthenticationChecks(_adminUser, new UsernamePasswordAuthenticationToken("admin", "blah"));

    }
}

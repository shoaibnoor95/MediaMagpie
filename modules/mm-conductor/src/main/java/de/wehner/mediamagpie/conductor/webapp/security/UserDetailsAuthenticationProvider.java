package de.wehner.mediamagpie.conductor.webapp.security;

import java.util.Date;
import java.util.Hashtable;
import java.util.Locale;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.dao.AbstractUserDetailsAuthenticationProvider;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import de.wehner.mediamagpie.common.persistence.entity.User;
import de.wehner.mediamagpie.conductor.webapp.services.UserSecurityService;

@Service
public class UserDetailsAuthenticationProvider extends AbstractUserDetailsAuthenticationProvider {

    private static final Logger LOG = LoggerFactory.getLogger(UserDetailsAuthenticationProvider.class);

    private static final Hashtable<String, Integer> _loginFailures = new Hashtable<String, Integer>();
    private static final Hashtable<String, Long> _lockTime = new Hashtable<String, Long>();

    /*
     * The time in milliseconds used to block a user from login when the user tries to login with an invalid password for more than
     * MAX_ATTEMPTS times
     */
    static final long LOCK_TIME = 30000;
    static final int MAX_ATTEMPTS = 20;

    private final UserSecurityService userSecurityService;
    private MessageSource messageSource;

    @Autowired
    public UserDetailsAuthenticationProvider(UserSecurityService userSecurityService, MessageSource messageSource) {
        super();
        this.userSecurityService = userSecurityService;
        this.messageSource = messageSource;
        setHideUserNotFoundExceptions(false);
    }

    @Override
    protected void additionalAuthenticationChecks(UserDetails userDetails, UsernamePasswordAuthenticationToken authentication)
            throws AuthenticationException {
        String userName = userDetails.getUsername();

        synchronized (_lockTime) {
            if (_lockTime.containsKey(userName)) {
                if (new Date().getTime() < _lockTime.get(userName)) {
                    throw new LockedException(messageSource.getMessage("user.too.many.failed.authentications", new String[] { userName,
                            "" + (LOCK_TIME / 1000) }, Locale.getDefault()));
                } else {
                    _lockTime.remove(userName);
                }
            }
        }

        User user = (User) userDetails;
        if (!user.isEnabled()) {
            throw new DisabledException("User '" + user.getUsername() + "' is disabled.");
        }

        if (!userSecurityService.verifyPassword(user, authentication.getCredentials().toString())) {
            synchronized (_loginFailures) {
                if (!_loginFailures.containsKey(userName)) {
                    _loginFailures.put(userName, 1);
                } else if (_loginFailures.get(userName) >= MAX_ATTEMPTS - 1) {
                    _loginFailures.remove(userName);
                    _lockTime.put(userName, new Date().getTime() + LOCK_TIME);
                    LOG.info("Benutzer: " + userName + " gesperrt (zuviele Fehlversuche beim Login)");
                } else {
                    _loginFailures.put(userName, _loginFailures.get(userName) + 1);
                }
            }
            throw new BadCredentialsException(messageSource.getMessage("user.bad.password", null, Locale.getDefault()));
        } else {
            _loginFailures.remove(userName);
        }
    }

    @Override
    protected UserDetails retrieveUser(String username, UsernamePasswordAuthenticationToken authentication) throws AuthenticationException {
        return userSecurityService.loadUserByUsername(username);
    }

}

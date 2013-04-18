package de.wehner.mediamagpie.conductor.webapp.services;

import java.util.concurrent.Callable;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import de.wehner.mediamagpie.core.util.Crypt;
import de.wehner.mediamagpie.persistence.dao.TransactionHandler;
import de.wehner.mediamagpie.persistence.dao.UserDao;
import de.wehner.mediamagpie.persistence.entity.User;

@Service
public class UserSecurityService implements UserDetailsService {

    private final TransactionHandler _transactionHandler;
    private final UserDao _userDao;
    private final static String CRYPT_SALT = "mEdiaMAGpi";

    @Autowired
    public UserSecurityService(TransactionHandler transactionHandler, UserDao userDao) {
        super();
        _transactionHandler = transactionHandler;
        _userDao = userDao;
    }

    @Override
    public UserDetails loadUserByUsername(final String username) throws UsernameNotFoundException, DataAccessException {

        if (StringUtils.isEmpty(username)) {
            throw new BadCredentialsException("No login user provided.");
        }

        return _transactionHandler.executeInTransaction(new Callable<UserDetails>() {

            @Override
            public UserDetails call() throws Exception {
                User user = _userDao.getByName(username);
                if (user == null) {
                    throw new UsernameNotFoundException("No unique user found.");
                }

                UserDetails userDetails = createUserDetails(user);

                return userDetails;
            }
        });
    }

    public static UserDetails createUserDetails(final User user) {
        return user;
    }

    public boolean verifyPassword(User user, String passwordToTest) {
        if (user == null) {
            throw new IllegalArgumentException("Parameter 'user' must not be null.");
        }

        return Crypt.crypt(CRYPT_SALT, passwordToTest).equals(user.getPassword());
    }

    /**
     * Used to encrypt the user's password.
     * 
     * @param password
     *            The plain unencrypted password
     * @return The encrypted password (something hashed)
     */
    public static String crypt(String password) {
        return Crypt.crypt(UserSecurityService.CRYPT_SALT, password);
    }
}

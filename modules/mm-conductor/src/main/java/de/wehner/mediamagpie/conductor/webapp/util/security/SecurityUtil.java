package de.wehner.mediamagpie.conductor.webapp.util.security;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

import de.wehner.mediamagpie.common.persistence.entity.User;
import de.wehner.mediamagpie.common.persistence.entity.User.Role;

/**
 * @author Peter Voss, Ralf Wehner
 */
public class SecurityUtil {

    public static User getCurrentUser(boolean hasToExist) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null) {
            if (hasToExist) {
                throw new IllegalStateException("No authenticated user found in security context.");
            }
            return null;
        }
        Object principal = authentication.getPrincipal();

        if (principal instanceof User) {
            return (User) principal;
        }
        return null;
    }

    public static UserDetails getCurrentPrinzipal(boolean hasToExist) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null) {
            if (hasToExist) {
                throw new IllegalStateException("No authenticated user found in security context.");
            }
            return null;
        }
        Object principal = authentication.getPrincipal();

        if (principal instanceof UserDetails) {
            return (UserDetails) principal;
        }
        return null;
    }

    public static User getCurrentUser() {
        return getCurrentUser(true);
    }

    @Deprecated
    public static void setCurrentUser(User user) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Object credentials = null;
        if (authentication != null) {
            credentials = authentication.getCredentials();
        }
        setCurrentUser(user, credentials);
    }

    public static void setCurrentPassword(String password) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken(authentication.getPrincipal(), password));
    }

    public static void setCurrentUser(User user, Object credentials) {
        SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken(user, credentials));
    }

    public static boolean isLoggedIn() {
        return SecurityContextHolder.getContext().getAuthentication() != null;
    }

    public static boolean isUserAuthorizedToConfigureOtherUser(User loggedInUser, Long userIdToTest) {
        if (loggedInUser == null) {
            return false;
        }
        if (loggedInUser.getId() != userIdToTest) {
            // current user is different to userId
            return (loggedInUser.getRole() == Role.ADMIN);
        }
        return true;
    }
}

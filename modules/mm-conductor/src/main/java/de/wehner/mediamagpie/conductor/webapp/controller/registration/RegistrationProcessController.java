package de.wehner.mediamagpie.conductor.webapp.controller.registration;

import java.util.Date;
import java.util.List;
import java.util.Locale;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.apache.commons.lang.time.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.CollectionUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import de.wehner.mediamagpie.common.persistence.dao.UserConfigurationDao;
import de.wehner.mediamagpie.common.persistence.dao.UserDao;
import de.wehner.mediamagpie.common.persistence.entity.Registration;
import de.wehner.mediamagpie.common.persistence.entity.User;
import de.wehner.mediamagpie.common.persistence.entity.properties.UserConfiguration;
import de.wehner.mediamagpie.common.util.TimeProvider;
import de.wehner.mediamagpie.conductor.exception.RegistrationException;
import de.wehner.mediamagpie.conductor.persistence.dao.RegistrationDao;
import de.wehner.mediamagpie.conductor.webapp.controller.commands.PasswordResetCommand;
import de.wehner.mediamagpie.conductor.webapp.controller.commands.RegistrationCommand;
import de.wehner.mediamagpie.conductor.webapp.services.MailerService;
import de.wehner.mediamagpie.conductor.webapp.services.RegistrationService;
import de.wehner.mediamagpie.conductor.webapp.services.UserSecurityService;
import de.wehner.mediamagpie.conductor.webapp.util.PassPhrase;
import de.wehner.mediamagpie.conductor.webapp.util.WebAppUtils;
import de.wehner.mediamagpie.conductor.webapp.validator.PasswordConfirmValidator;

@Controller
@RequestMapping("/public/account")
public class RegistrationProcessController {

    private static final Logger LOG = LoggerFactory.getLogger(RegistrationProcessController.class);

    public static final String URL_REGISTER = "/signup";
    public static final String VIEW_REGISTER = "public/account/registration";
    public static final String VIEW_REGISTER_SENT = "public/account/registration_sent";

    public static final String URL_ACTIVATE = "/confirm";
    public static final String VIEW_ACTIVATE = "public/account/registration_activation_done";

    public static final String URL_RESET_PASSWORD = "/resetPassword";
    public static final String VIEW_RESET_PASSWORD = "public/account/resetPassword";

    private final UserDao _userDao;
    private final UserConfigurationDao _userConfigurationDao;
    private final RegistrationDao _registrationDao;
    private final RegistrationService _registrationService;
    private final TimeProvider _timeProvider;
    private final int _activationTimeout;
    private final MailerService _mailerService;
    private final MessageSource _messageSource;

    @Autowired
    public RegistrationProcessController(UserDao userDao, RegistrationDao registrationDao, UserConfigurationDao userConfigurationDao,
            RegistrationService registrationService, TimeProvider timeProvider, MailerService mailerService, MessageSource messageSource,
            @Qualifier("registration.activation.timeout.hours") Integer activationTimeout) {
        _userDao = userDao;
        _userConfigurationDao = userConfigurationDao;
        _registrationDao = registrationDao;
        _registrationService = registrationService;
        _timeProvider = timeProvider;
        _activationTimeout = activationTimeout;
        _mailerService = mailerService;
        _messageSource = messageSource;
    }

    @RequestMapping(method = RequestMethod.GET, value = URL_REGISTER)
    public String viewRegistration(Model model) {
        model.addAttribute(new RegistrationCommand());
        return VIEW_REGISTER;
    }

    @RequestMapping(method = RequestMethod.POST, value = URL_REGISTER)
    public String submitRegistration(Model model, @Valid RegistrationCommand command, BindingResult errors, HttpServletRequest request) {
        new PasswordConfirmValidator().validate(command, errors);
        if (isUserAlreadyPresent(command.getUser())) {
            errors.rejectValue("user", "user.is.already.used", null, "The user-id is already used by another user. Try another user.");
        }
        if (errors.hasErrors()) {
            LOG.info("Got validation errors: " + errors.toString());
            return VIEW_REGISTER;
        }

        Registration newRegistration = createNewRegistration(command);
        _registrationDao.makePersistent(newRegistration); // get valid id

        String activationLink = _registrationService.createActivationLink("activation", newRegistration.getId(), newRegistration.getUser());
        newRegistration.setActivationLink(activationLink);

        // send out email
        try {
            Locale locale = WebAppUtils.getCurrentLocale(request);
            _mailerService.sendRegistrationPerformedNotificationMail(newRegistration, _activationTimeout, locale);
            _registrationDao.makePersistent(newRegistration);
            return VIEW_REGISTER_SENT;
        } catch (Exception e) {
            LOG.warn("Can not send email", e);
            model.addAttribute("errormessage", "Can not send registration email: " + e.getMessage());
            return VIEW_REGISTER;
        }
    }

    @RequestMapping(method = RequestMethod.GET, value = URL_ACTIVATE)
    public String activateUser(Model model, @RequestParam(required = true, value = "activation") String activation, HttpServletRequest request) {

        try {
            Registration registration = _registrationService.decodeRegistrationFromActivationLink(activation);

            // do some validation checks
            if (registration == null) {
                return leaveWithErrorMessage("publicAccountController.registration.not.found", model, request, VIEW_ACTIVATE);
            }
            Date validUntil = registration.getValidUntil();
            if (validUntil.before(new Date(_timeProvider.getTime()))) {
                return leaveWithErrorMessage("publicAccountController.registration.expired", model, request, VIEW_ACTIVATE, "" + _activationTimeout);
            }
            if (registration.getUserCreationDate() != null) {
                return leaveWithErrorMessage("publicAccountController.registration.already.performed", model, request, VIEW_ACTIVATE,
                        registration.getUser());
            }
            // register now
            // create and save user
            User newUser = _registrationService.createUserFromRegistration(registration);
            _userDao.makePersistent(newUser);
            // create and save a default user configuration
            UserConfiguration newUserConfiguration = _registrationService.createDefaultUserConfiguration();
            _userConfigurationDao.saveOrUpdateConfiguration(newUser, newUserConfiguration);

            // mark this registration as processed
            registration.setUserCreationDate(new Date(_timeProvider.getTime()));
            _registrationDao.makePersistent(registration);
            model.addAttribute("user", newUser);
        } catch (RegistrationException e) {
            LOG.info("Failed to activate user.", e);
            return leaveWithErrorMessage("publicAccountController.invalid.activation.link", model, request, VIEW_ACTIVATE);
        }
        return VIEW_ACTIVATE;
    }

    private String leaveWithErrorMessage(String messageKex, Model model, HttpServletRequest request, String view, String... args) {
        model.addAttribute("errormessage", _messageSource.getMessage(messageKex, args, WebAppUtils.getCurrentLocale(request)));
        return view;
    }

    private Registration createNewRegistration(RegistrationCommand command) {
        Registration registration = new Registration();
        registration.setCreationDate(new Date(_timeProvider.getTime()));
        registration.setEmail(command.getEmail());
        registration.setForename(command.getForename());
        registration.setSurname(command.getSurname());
        registration.setPassword(UserSecurityService.crypt(command.getPassword()));
        registration.setUser(command.getUser());
        registration.setValidUntil(DateUtils.addHours(new Date(), _activationTimeout));
        return registration;
    }

    @RequestMapping(method = RequestMethod.GET, value = URL_RESET_PASSWORD)
    public String viewResetPassword(Model model) {
        model.addAttribute(new PasswordResetCommand());
        return VIEW_RESET_PASSWORD;
    }

    @RequestMapping(method = RequestMethod.POST, value = URL_RESET_PASSWORD)
    public String submitResetPassword(Model model, @Valid PasswordResetCommand command, BindingResult errors, HttpServletRequest request) {
        User user = null;
        if (!errors.hasErrors()) {
            // Try to resolve the user
            user = _userDao.getByName(command.getUser());
            if (user == null) {
                // try to find by email
                user = _userDao.getByEmail(command.getUser());
            }
            if (user == null) {
                errors.rejectValue("user", "reset.password.no.user.found", new String[] { command.getUser() }, "User not found on system.");
            }
        }
        if (errors.hasErrors()) {
            LOG.info("Got validation errors: " + errors.toString());
            return VIEW_RESET_PASSWORD;
        }

        // send out email
        try {
            // update password
            String newPassword = new PassPhrase().getNext();
            user.setPassword(UserSecurityService.crypt(newPassword));
            // send email to user
            Locale locale = WebAppUtils.getCurrentLocale(request);
            _mailerService.sendResetPasswordMail(user, newPassword, locale);
            _userDao.makePersistent(user);
            model.addAttribute("message", _messageSource.getMessage("reset.password.sent", new String[] { user.getEmail() }, locale));
            return VIEW_RESET_PASSWORD;
        } catch (Exception e) {
            LOG.warn("Can not send email", e);
            model.addAttribute("errormessage", "Can not send reset password email: " + e.getMessage());
            return VIEW_RESET_PASSWORD;
        }
    }

    private boolean isUserAlreadyPresent(String user) {
        List<User> byName = _userDao.getUserLikeName(user);
        return !CollectionUtils.isEmpty(byName);
    }

    public static String getBaseRequestMappingUrl() {
        return RegistrationProcessController.class.getAnnotation(RequestMapping.class).value()[0];
    }

}

package de.wehner.mediamagpie.conductor.webapp.controller.configuration;

import java.io.IOException;

import javax.validation.Valid;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.StringArrayPropertyEditor;
import org.springframework.beans.propertyeditors.StringTrimmerEditor;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.bind.support.SessionStatus;

import de.wehner.mediamagpie.conductor.webapp.controller.commands.UserConfigurationCommand;
import de.wehner.mediamagpie.conductor.webapp.services.ImageService;
import de.wehner.mediamagpie.conductor.webapp.services.MediaSyncService;
import de.wehner.mediamagpie.conductor.webapp.services.UserSecurityService;
import de.wehner.mediamagpie.conductor.webapp.util.security.SecurityUtil;
import de.wehner.mediamagpie.conductor.webapp.validator.UserConfigurationValidator;
import de.wehner.mediamagpie.persistence.dao.UserConfigurationDao;
import de.wehner.mediamagpie.persistence.dao.UserDao;
import de.wehner.mediamagpie.persistence.entity.User;
import de.wehner.mediamagpie.persistence.entity.properties.UserConfiguration;

@Controller
@RequestMapping("/config/user")
@SessionAttributes({ "userConfigurationCommand" })
public class UserConfiguratonControllerS2 {

    private static final Logger LOG = LoggerFactory.getLogger(UserConfiguratonControllerS2.class);

    public static final String URL_USERCONFIG_EDIT_S2 = "/edit2";
    public static final String VIEW_USERCONFIG_EDIT_S2 = "config/user/edit_userconfigurationS2";

    private final UserConfigurationDao _userConfigurationDao;
    private final UserDao _userDao;
    private final MediaSyncService _mediaSyncService;

    @Autowired
    public UserConfiguratonControllerS2(UserConfigurationDao userConfigurationDao, UserDao userDao, ImageService imageService,
            MediaSyncService mediaSyncService) {
        super();
        _userConfigurationDao = userConfigurationDao;
        _userDao = userDao;
        _mediaSyncService = mediaSyncService;
    }

    @InitBinder
    public void initBinder(WebDataBinder webDataBinder) {
        webDataBinder.registerCustomEditor(String.class, new StringTrimmerEditor(false));
        webDataBinder.registerCustomEditor(String[].class, new StringArrayPropertyEditor("\n", false, true));
    }

    private User getValidatedRelevantUser(Long userId) {
        User user = SecurityUtil.getCurrentUser();
        if (user != null) {
            user = _userDao.getById(user.getId());
        }
        if (userId != null) {
            // only used, when admin wants to configure foreign users
            if (!SecurityUtil.isUserAuthorizedToConfigureOtherUser(user, userId)) {
                throw new InsufficientAuthenticationException("Access denied for userId: " + userId);
            }
            // load user
            user = _userDao.getById(userId);
        }
        return user;
    }

    @RequestMapping(method = RequestMethod.GET, value = URL_USERCONFIG_EDIT_S2)
    public String showEditUserConfigurationStep2(UserConfigurationCommand command, Model model,
            @RequestParam(value = "userId", required = false) Long userId) {
        User user = getValidatedRelevantUser(userId);
        if (command == null) {
            command = UserConfigurationCommand.createCommand(user, _userConfigurationDao.getConfiguration(user, UserConfiguration.class));
        }
        model.addAttribute(command);
        return VIEW_USERCONFIG_EDIT_S2;
    }

    @RequestMapping(method = RequestMethod.POST, value = URL_USERCONFIG_EDIT_S2)
    public String submitConfiguration(@Valid UserConfigurationCommand command, BindingResult result, Model model,
            @RequestParam(value = "userId", required = false) Long userId, @RequestParam(value = "_back", required = false) String backFlag,
            final SessionStatus status) throws IOException {
        new UserConfigurationValidator().validate(command.getUserConfiguration(), result);
        if (result.hasErrors()) {
            LOG.info(result.toString());
            return VIEW_USERCONFIG_EDIT_S2;
        }

        if (backFlag != null) {
            return "redirect:" + getBaseRequestMappingUrl() + UserConfiguratonControllerS1.URL_USERCONFIG_EDIT_S1;
        }

        // save user
        User user = getValidatedRelevantUser(userId);
        user.setName(command.getName());
        user.setEmail(command.getEmail());
        user.setForename(command.getForename());
        user.setSurname(command.getSurname());
        if (!StringUtils.isEmpty(command.getPassword())) {
            user.setPassword(UserSecurityService.crypt(command.getPassword()));
        }
        // user.setGroups(getGroups());
        // user.setRole(getRole());
        _userDao.makePersistent(user);

        // save user's configuration
        _userConfigurationDao.saveOrUpdateConfiguration(user, command.getUserConfiguration());
        if (command.isSyncMediaPahtes()) {
            // Build here a separate thread that runs the configuration?
            _mediaSyncService.syncMediaPathes(user, command.getUserConfiguration().getRootMediaPathes());
        }
        
        status.setComplete();
        return "redirect:" + getBaseRequestMappingUrl() + UserConfiguratonControllerS1.URL_USERCONFIG;
    }

    public static String getBaseRequestMappingUrl() {
        return UserConfiguratonControllerS2.class.getAnnotation(RequestMapping.class).value()[0];
    }
}

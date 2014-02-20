package de.wehner.mediamagpie.conductor.webapp.controller.configuration;

import java.io.IOException;

import javax.validation.Valid;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.StringArrayPropertyEditor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.support.SessionStatus;

import de.wehner.mediamagpie.conductor.webapp.controller.commands.UserConfigurationCommand;
import de.wehner.mediamagpie.conductor.webapp.services.ImageService;
import de.wehner.mediamagpie.conductor.webapp.services.MediaSyncService;
import de.wehner.mediamagpie.conductor.webapp.services.UserSecurityService;
import de.wehner.mediamagpie.conductor.webapp.validator.UserConfigurationValidator;
import de.wehner.mediamagpie.persistence.dao.UserDao;
import de.wehner.mediamagpie.persistence.entity.User;
import de.wehner.mediamagpie.persistence.service.ConfigurationProvider;

@Controller
public class UserConfiguratonControllerS2 extends UserConfigurationController {

    private static final Logger LOG = LoggerFactory.getLogger(UserConfiguratonControllerS2.class);

    public static final String URL_USERCONFIG_EDIT_S2 = "/edit2";
    public static final String VIEW_USERCONFIG_EDIT_S2 = "config/user/edit_userconfigurationS2";

    private final UserDao _userDao;
    private final MediaSyncService _mediaSyncService;

    @Autowired
    public UserConfiguratonControllerS2(ConfigurationProvider configurationProvider, UserDao userDao, ImageService imageService,
            MediaSyncService mediaSyncService) {
        super(configurationProvider, userDao);
        _userDao = userDao;
        _mediaSyncService = mediaSyncService;
    }

    @InitBinder
    public void initBinder(WebDataBinder webDataBinder) {
        webDataBinder.registerCustomEditor(String[].class, new StringArrayPropertyEditor("\n", false, true));
    }

    @RequestMapping(method = RequestMethod.GET, value = URL_USERCONFIG_EDIT_S2)
    public String showEditUserConfigurationStep2(UserConfigurationCommand command, Model model,
            @RequestParam(value = "userId", required = false) Long userId) {
        User user = getValidatedRelevantUser(userId);
        if (command == null || command.getId() == null) {
            command = UserConfigurationCommand.createCommand(user, _configurationProvider.getUserConfiguration(user));
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
            return "redirect:" + UserConfigurationController.getBaseRequestMappingUrl() + UserConfiguratonControllerS1.URL_USERCONFIG_EDIT_S1;
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
        _configurationProvider.saveOrUpdateUserConfiguration(user, command.getUserConfiguration());
        if (command.isSyncMediaPahtes()) {
            // Build here a separate thread that runs the configuration?
            _mediaSyncService.syncMediaPathes(user, command.getUserConfiguration().getRootMediaPathes());
        }

        status.setComplete();
        return "redirect:" + UserConfigurationController.getBaseRequestMappingUrl() + UserConfiguratonControllerS1.URL_USERCONFIG;
    }

}

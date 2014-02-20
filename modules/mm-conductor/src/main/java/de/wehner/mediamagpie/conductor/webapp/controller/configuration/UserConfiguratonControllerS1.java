package de.wehner.mediamagpie.conductor.webapp.controller.configuration;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import de.wehner.mediamagpie.conductor.webapp.controller.commands.UserConfigurationCommand;
import de.wehner.mediamagpie.conductor.webapp.services.ImageService;
import de.wehner.mediamagpie.conductor.webapp.validator.PasswordConfirmValidator;
import de.wehner.mediamagpie.persistence.dao.UserDao;
import de.wehner.mediamagpie.persistence.entity.User;
import de.wehner.mediamagpie.persistence.service.ConfigurationProvider;

@Controller
public class UserConfiguratonControllerS1 extends UserConfigurationController {

    private static final Logger LOG = LoggerFactory.getLogger(UserConfiguratonControllerS1.class);

    public static final String URL_USERCONFIG = "/";
    public static final String VIEW_USERCONFIG = "config/user/show_userconfiguration";
    public static final String URL_USERCONFIG_EDIT_S1 = "/edit";
    public static final String VIEW_USERCONFIG_EDIT_S1 = "config/user/edit_userconfigurationS1";

    @Autowired
    public UserConfiguratonControllerS1(ConfigurationProvider configurationProvider, UserDao userDao, ImageService imageService) {
        super(configurationProvider, userDao);
    }

    @RequestMapping(method = RequestMethod.GET, value = URL_USERCONFIG)
    public String showUserConfiguration(Model model, @RequestParam(value = "userId", required = false) Long userId, HttpSession session) {
        User user = getValidatedRelevantUser(userId);
        model.addAttribute("conf", _configurationProvider.getUserConfiguration(user));
        model.addAttribute("user", user);
        model.asMap().remove(COMMAND);
        session.removeAttribute(COMMAND);
        return VIEW_USERCONFIG;
    }

    @RequestMapping(method = RequestMethod.GET, value = URL_USERCONFIG_EDIT_S1)
    public String showEditUserConfiguration(Model model, @RequestParam(value = "userId", required = false) Long userId) {
        UserConfigurationCommand command = retrieveUserConfigurationCommandFromUserId(model, userId);
        model.addAttribute(command);
        return VIEW_USERCONFIG_EDIT_S1;
    }

    @RequestMapping(method = RequestMethod.POST, value = URL_USERCONFIG_EDIT_S1)
    public String submitConfiguration(@Valid UserConfigurationCommand command, BindingResult result, Model model,
            @RequestParam(value = "userId", required = false) Long userId) {
        new PasswordConfirmValidator().validate(command, result);
        if (result.hasErrors()) {
            LOG.info(result.toString());
            return VIEW_USERCONFIG_EDIT_S1;
        }
        return "redirect:" + getBaseRequestMappingUrl() + UserConfiguratonControllerS2.URL_USERCONFIG_EDIT_S2;
    }

    @RequestMapping(method = RequestMethod.GET, value = URL_USERCONFIG_CANCEL)
    public String cancel(Model model) {
        model.asMap().remove(COMMAND);
        return "redirect:" + getBaseRequestMappingUrl() + UserConfiguratonControllerS1.URL_USERCONFIG;
    }

}

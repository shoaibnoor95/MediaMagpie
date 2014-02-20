package de.wehner.mediamagpie.conductor.webapp.controller.configuration;

import org.springframework.beans.propertyeditors.StringTrimmerEditor;
import org.springframework.ui.Model;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.SessionAttributes;

import de.wehner.mediamagpie.conductor.webapp.controller.AbstractConfigurationSupportController;
import de.wehner.mediamagpie.conductor.webapp.controller.commands.UserConfigurationCommand;
import de.wehner.mediamagpie.persistence.dao.UserDao;
import de.wehner.mediamagpie.persistence.entity.User;
import de.wehner.mediamagpie.persistence.service.ConfigurationProvider;

@RequestMapping("/config/user")
@SessionAttributes({ "userConfigurationCommand" })
public class UserConfigurationController extends AbstractConfigurationSupportController {

    public static final String COMMAND = "userConfigurationCommand";

    public static final String URL_USERCONFIG_CANCEL = "/cancel";

    public UserConfigurationController(ConfigurationProvider configurationProvider, UserDao userDao) {
        super(configurationProvider, userDao);
    }

    @InitBinder
    public void initBinder(WebDataBinder webDataBinder) {
        webDataBinder.registerCustomEditor(String.class, new StringTrimmerEditor(false));
    }

    protected UserConfigurationCommand retrieveUserConfigurationCommandFromUserId(Model model, Long userId) {
        User user = getValidatedRelevantUser(userId);
        UserConfigurationCommand command = (UserConfigurationCommand) model.asMap().get("userConfigurationCommand");
        if (command == null || command.getId() == null) {
            // create fresh command
            command = UserConfigurationCommand.createCommand(user, _configurationProvider.getUserConfiguration(user));
        }
        return command;
    }

    public static String getBaseRequestMappingUrl() {
        return UserConfigurationController.class.getAnnotation(RequestMapping.class).value()[0];
    }

}
package de.wehner.mediamagpie.conductor.webapp.controller.configuration;

import java.io.IOException;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.StringTrimmerEditor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import de.wehner.mediamagpie.common.persistence.entity.User;
import de.wehner.mediamagpie.common.persistence.entity.properties.S3Configuration;
import de.wehner.mediamagpie.conductor.persistence.dao.UserConfigurationDao;
import de.wehner.mediamagpie.conductor.persistence.dao.UserDao;
import de.wehner.mediamagpie.conductor.webapp.controller.AbstractConfigurationSupportController;
import de.wehner.mediamagpie.conductor.webapp.controller.commands.config.S3ConfigurationCommand;

@Controller
@RequestMapping("/config/aws/s3")
public class AwsConfigurationController extends AbstractConfigurationSupportController {

    private static final Logger LOG = LoggerFactory.getLogger(AwsConfigurationController.class);

    public static final String URL_S3CONFIG = "/";
    public static final String VIEW_S3CONFIG = "config/aws/s3/show_s3configuration";

    public static final String URL_S3CONFIG_EDIT = "/edit";
    public static final String VIEW_S3CONFIG_EDIT = "config/aws/s3/edit_s3configuration";

    @Autowired
    public AwsConfigurationController(UserConfigurationDao userConfigurationDao, UserDao userDao) {
        super(null, userConfigurationDao, userDao);
    }

    @InitBinder
    public void initBinder(WebDataBinder webDataBinder) {
        webDataBinder.registerCustomEditor(String.class, new StringTrimmerEditor(false));
    }

    @RequestMapping(method = RequestMethod.GET, value = URL_S3CONFIG)
    public String showAwsS3Configuration(Model model, @RequestParam(value = "userId", required = false) Long userId, HttpSession session) {
        S3ConfigurationCommand s3ConfigurationCommand = createS3ConfigurationCommandFromUser(userId);
        model.addAttribute("conf", s3ConfigurationCommand);
        // session.removeAttribute("userConfigurationCommand");
        return VIEW_S3CONFIG;
    }

    @RequestMapping(method = RequestMethod.GET, value = URL_S3CONFIG_EDIT)
    public String editAwsS3Configuration(Model model, @RequestParam(value = "userId", required = false) Long userId, HttpSession session) {
        S3ConfigurationCommand s3ConfigurationCommand = createS3ConfigurationCommandFromUser(userId);
        model.addAttribute("conf", s3ConfigurationCommand);
        // session.removeAttribute("userConfigurationCommand");
        return VIEW_S3CONFIG_EDIT;
    }

    @RequestMapping(method = RequestMethod.POST, value = URL_S3CONFIG_EDIT)
    public String submitConfiguration(@Valid @ModelAttribute("conf") S3ConfigurationCommand command, BindingResult result, Model model,
            @RequestParam(value = "userId", required = false) Long userId) throws IOException {

        if (result.hasErrors()) {
            LOG.info(result.toString());
            return VIEW_S3CONFIG_EDIT;
        }

        // update existing configuration
        User user = getValidatedRelevantUser(userId);
        S3Configuration existingS3Configuration = _userConfigurationDao.getConfiguration(user, S3Configuration.class);
        existingS3Configuration.setAccessKey(command.getAccessKey());
        if (!StringUtils.isEmpty(command.getSecretKey()) && !command.getSecretKey().equals(existingS3Configuration.getSecretKey())) {
            existingS3Configuration.setSecretKey(command.getSecretKey());
        }
        _userConfigurationDao.saveOrUpdateConfiguration(user, existingS3Configuration);

        return "redirect:" + getBaseRequestMappingUrl() + URL_S3CONFIG;
    }

    private S3ConfigurationCommand createS3ConfigurationCommandFromUser(Long userId) {
        User user = getValidatedRelevantUser(userId);
        S3Configuration s3Configuration = _userConfigurationDao.getConfiguration(user, S3Configuration.class);
        S3ConfigurationCommand s3ConfigurationCommand = S3ConfigurationCommand.createCommand(s3Configuration);
        return s3ConfigurationCommand;
    }

    public static String getBaseRequestMappingUrl() {
        return AwsConfigurationController.class.getAnnotation(RequestMapping.class).value()[0];
    }
}

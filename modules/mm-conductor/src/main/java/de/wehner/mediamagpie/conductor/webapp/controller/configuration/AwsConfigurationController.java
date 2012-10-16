package de.wehner.mediamagpie.conductor.webapp.controller.configuration;

import java.io.IOException;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.StringTrimmerEditor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.support.SessionStatus;

import de.wehner.mediamagpie.common.persistence.entity.User;
import de.wehner.mediamagpie.common.persistence.entity.properties.S3Configuration;
import de.wehner.mediamagpie.conductor.persistence.dao.UserConfigurationDao;
import de.wehner.mediamagpie.conductor.persistence.dao.UserDao;
import de.wehner.mediamagpie.conductor.webapp.controller.AbstractConfigurationSupportController;

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
        User user = getValidatedRelevantUser(userId);
        if (user != null) {
            model.addAttribute("conf", _userConfigurationDao.getConfiguration(user, S3Configuration.class));
        }
        // session.removeAttribute("userConfigurationCommand");
        return VIEW_S3CONFIG;
    }

    @RequestMapping(method = RequestMethod.GET, value = URL_S3CONFIG_EDIT)
    public String editAwsS3Configuration(Model model, @RequestParam(value = "userId", required = false) Long userId, HttpSession session) {
        User user = getValidatedRelevantUser(userId);
        model.addAttribute("conf", _userConfigurationDao.getConfiguration(user, S3Configuration.class));
        // session.removeAttribute("userConfigurationCommand");
        return VIEW_S3CONFIG_EDIT;
    }

    @RequestMapping(method = RequestMethod.POST, value = URL_S3CONFIG_EDIT)
    public String submitConfiguration(@Valid S3Configuration command, BindingResult result, Model model,
            @RequestParam(value = "userId", required = false) Long userId, final SessionStatus status) throws IOException {

        // TODO rwe: add validation of configuration here
        // new UserConfigurationValidator().validate(command.getUserConfiguration(), result);
        if (result.hasErrors()) {
            LOG.info(result.toString());
            return VIEW_S3CONFIG_EDIT;
        }

        // save configuration
        User user = getValidatedRelevantUser(userId);
        // TODO rwe: only replace secret key, if user sets a new one
        _userConfigurationDao.saveOrUpdateConfiguration(user, command);

        status.setComplete();
        return "redirect:" + getBaseRequestMappingUrl() + URL_S3CONFIG;
    }

    public static String getBaseRequestMappingUrl() {
        return AwsConfigurationController.class.getAnnotation(RequestMapping.class).value()[0];
    }
}

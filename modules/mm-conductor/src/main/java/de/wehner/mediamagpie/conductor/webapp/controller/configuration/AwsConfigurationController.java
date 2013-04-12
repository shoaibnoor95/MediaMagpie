package de.wehner.mediamagpie.conductor.webapp.controller.configuration;

import java.io.IOException;

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

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.BasicAWSCredentials;

import de.wehner.mediamagpie.aws.s3.S3ClientFacade;
import de.wehner.mediamagpie.aws.s3.service.S3SyncService;
import de.wehner.mediamagpie.common.persistence.dao.UserConfigurationDao;
import de.wehner.mediamagpie.common.persistence.dao.UserDao;
import de.wehner.mediamagpie.common.persistence.entity.User;
import de.wehner.mediamagpie.common.persistence.entity.properties.S3Configuration;
import de.wehner.mediamagpie.conductor.webapp.controller.AbstractConfigurationSupportController;
import de.wehner.mediamagpie.conductor.webapp.controller.commands.CheckResultCommand;
import de.wehner.mediamagpie.conductor.webapp.controller.commands.config.S3ConfigurationCommand;
import de.wehner.mediamagpie.conductor.webapp.util.security.SecurityUtil;
import de.wehner.mediamagpie.core.util.Pair;

@Controller
@RequestMapping("/config/aws/s3")
public class AwsConfigurationController extends AbstractConfigurationSupportController {

    private static final Logger LOG = LoggerFactory.getLogger(AwsConfigurationController.class);

    public static final String URL_S3CONFIG = "/";
    public static final String VIEW_S3CONFIG = "config/aws/s3/show_s3configuration";

    public static final String URL_S3CONFIG_EDIT = "/edit";
    public static final String VIEW_S3CONFIG_EDIT = "config/aws/s3/edit_s3configuration";

    public static final String URL_TEST_SETTINGS = "/test";

    private final S3SyncService _s3SyncService;

    @Autowired
    public AwsConfigurationController(UserConfigurationDao userConfigurationDao, UserDao userDao, S3SyncService s3SyncService) {
        super(null, userConfigurationDao, userDao);
        _s3SyncService = s3SyncService;
    }

    @InitBinder
    public void initBinder(WebDataBinder webDataBinder) {
        webDataBinder.registerCustomEditor(String.class, new StringTrimmerEditor(false));
    }

    @RequestMapping(method = RequestMethod.GET, value = URL_S3CONFIG)
    public String showAwsS3Configuration(Model model, @RequestParam(value = "userId", required = false) Long userId) {
        addConfigurationIntoModel(model, userId);
        return VIEW_S3CONFIG;
    }

    private void addConfigurationIntoModel(Model model, Long userId) {
        S3ConfigurationCommand s3ConfigurationCommand = createS3ConfigurationCommandFromUser(userId);
        model.addAttribute("conf", s3ConfigurationCommand);
    }

    @RequestMapping(method = RequestMethod.GET, value = URL_S3CONFIG_EDIT)
    public String editAwsS3Configuration(Model model, @RequestParam(value = "userId", required = false) Long userId) {
        addConfigurationIntoModel(model, userId);
        // session.removeAttribute("userConfigurationCommand");
        return VIEW_S3CONFIG_EDIT;
    }

    @RequestMapping(method = RequestMethod.GET, value = URL_TEST_SETTINGS)
    public String testAwsS3Configuration(Model model, @RequestParam(value = "userId", required = false) Long userId) {
        User user = SecurityUtil.getCurrentUser(false);
        if (user == null) {
            return VIEW_S3CONFIG;
        }

        S3Configuration s3Configuration = _userConfigurationDao.getConfiguration(user, S3Configuration.class);
        AWSCredentials credentials = new BasicAWSCredentials(s3Configuration.getAccessKey(), s3Configuration.getSecretKey());
        S3ClientFacade s3ClientFacade = new S3ClientFacade(credentials);
        Pair<Boolean, String> result = s3ClientFacade.testConnection();
        if (result.getFirst()) {
            model.addAttribute(new CheckResultCommand(true, "awsConfigurationController.s3settings.ok"));
        } else {
            model.addAttribute(new CheckResultCommand(false, "awsConfigurationController.s3settings.invalid", result.getSecond()));
        }
        addConfigurationIntoModel(model, userId);
        return VIEW_S3CONFIG;
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

        // add a s3 sync job if there is a transition from non-sync to sync
        if (!existingS3Configuration.isSyncToS3() && command.isSyncToS3()) {
            _s3SyncService.syncS3Bucket(user);
        }
        existingS3Configuration.setSyncToS3(command.isSyncToS3());
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

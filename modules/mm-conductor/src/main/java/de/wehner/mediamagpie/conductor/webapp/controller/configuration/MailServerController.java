package de.wehner.mediamagpie.conductor.webapp.controller.configuration;

import java.io.UnsupportedEncodingException;
import java.util.Locale;

import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import de.wehner.mediamagpie.common.persistence.entity.User;
import de.wehner.mediamagpie.common.persistence.entity.User.Role;
import de.wehner.mediamagpie.common.persistence.entity.properties.MailServerConfiguration;
import de.wehner.mediamagpie.conductor.mail.MailSenderFacade;
import de.wehner.mediamagpie.conductor.persistence.dao.ConfigurationDao;
import de.wehner.mediamagpie.conductor.webapp.controller.commands.MailServerConfigurationCommand;
import de.wehner.mediamagpie.conductor.webapp.controller.commands.SendTestMailCommand;
import de.wehner.mediamagpie.conductor.webapp.services.MailerService;
import de.wehner.mediamagpie.core.util.ExceptionUtil;

@Controller
@RequestMapping("/config/admin/mailserver")
public class MailServerController {

    private static final Logger LOG = LoggerFactory.getLogger(MailServerController.class);

    public static final String URL_MAILCONFIG = "/configuration";
    public static final String VIEW_MAILCONFIG = "config/admin/mailServer/show_mailserverconfiguration";
    public static final String URL_MAILCONFIG_EDIT = "/configuration/edit";
    public static final String VIEW_MAILCONFIG_EDIT = "config/admin/mailServer/edit_mailserverconfiguration";
    public static final String URL_SEND_TEST_MAIL = "/sendTestMail";
    public static final String VIEW_SEND_TEST_MAIL = "config/admin/mailServer/sendTestMail";

    private final ConfigurationDao _configurationDao;
    private final MailSenderFacade _mailSenderFacade;
    private final MailerService _mailerService;

    @Autowired
    public MailServerController(ConfigurationDao configurationDao, MailSenderFacade mailerSenderFacade, MailerService mailerService) {
        super();
        _configurationDao = configurationDao;
        _mailSenderFacade = mailerSenderFacade;
        _mailerService = mailerService;
    }

    @RequestMapping(method = RequestMethod.GET, value = URL_MAILCONFIG)
    public String showConfiguration(Model model) {
        MailServerConfiguration command = _configurationDao.getConfiguration(MailServerConfiguration.class);
        model.addAttribute("conf", command);
        return VIEW_MAILCONFIG;
    }

    @RequestMapping(method = RequestMethod.GET, value = URL_MAILCONFIG_EDIT)
    public String editMailConfiguration(Model model) {
        MailServerConfigurationCommand command = _configurationDao.getConfiguration(MailServerConfigurationCommand.class);
        command.setPasswordConfirm(command.getPassword());
        model.addAttribute("conf", command);
        return VIEW_MAILCONFIG_EDIT;
    }

    @RequestMapping(method = RequestMethod.POST, value = URL_MAILCONFIG_EDIT)
    public String submitMailServerConfiguration(@Valid @ModelAttribute("conf") MailServerConfigurationCommand conf, BindingResult result, Model model) {
        if (conf.getPassword() != null && !conf.getPassword().equals(conf.getPasswordConfirm())) {
            result.rejectValue("password", "passwordConfirm.differs");
            result.rejectValue("passwordConfirm", "passwordConfirm.differs");
        }
        if (result.hasErrors()) {
            LOG.info(result.toString());
            return VIEW_MAILCONFIG_EDIT;
        }
        _configurationDao.saveOrUpdateConfiguration(conf);

        // reinitialize the _mailSenderFacade with new configuration
        try {
            _mailSenderFacade.initialize(conf);
        } catch (UnsupportedEncodingException e) {
            throw ExceptionUtil.convertToRuntimeException(e);
        }

        return "redirect:" + getBaseRequestMappingUrl() + URL_MAILCONFIG;
    }

    @RequestMapping(method = RequestMethod.GET, value = URL_SEND_TEST_MAIL)
    public String showSendTestMail(Model model) {
        SendTestMailCommand command = new SendTestMailCommand();
        model.addAttribute(command);
        return VIEW_SEND_TEST_MAIL;
    }

    @RequestMapping(method = RequestMethod.POST, value = URL_SEND_TEST_MAIL)
    public String submit(Model model, @Valid SendTestMailCommand command, BindingResult result) {
        if (result.hasErrors()) {
            LOG.info(result.toString());
            return VIEW_SEND_TEST_MAIL;
        }

        Locale locale = Locale.GERMAN;
        MailServerConfiguration mailServerConfiguration = _configurationDao.getConfiguration(MailServerConfiguration.class);
        try {
            User toUser = new User("", command.getTo(), Role.ADMIN);
            _mailerService.sendTestMail(toUser, locale, command.getSubject(), command.getMessage(), mailServerConfiguration.getSenderName(),
                    mailServerConfiguration.getHostName(), mailServerConfiguration.getPort().toString(), mailServerConfiguration.getUserName(),
                    mailServerConfiguration.getPassword());
            LOG.info("Successfully send email to '" + toUser.getEmail() + "'.");
        } catch (Exception ex) {
            result.rejectValue("message", null, ex.getMessage());
            return VIEW_SEND_TEST_MAIL;
        }
        return "redirect:" + getBaseRequestMappingUrl() + URL_MAILCONFIG;
    }

    public static String getBaseRequestMappingUrl() {
        return MailServerController.class.getAnnotation(RequestMapping.class).value()[0];
    }
}

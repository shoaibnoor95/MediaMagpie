package de.wehner.mediamagpie.conductor.webapp.controller.configuration;

import java.io.IOException;

import javax.validation.Valid;

import ma.glasnost.orika.MapperFacade;
import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.impl.DefaultMapperFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.StringArrayPropertyEditor;
import org.springframework.beans.propertyeditors.StringTrimmerEditor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import de.wehner.mediamagpie.conductor.webapp.controller.commands.MainConfigurationCommand;
import de.wehner.mediamagpie.conductor.webapp.services.ImageService;
import de.wehner.mediamagpie.conductor.webapp.services.MediaSyncService;
import de.wehner.mediamagpie.conductor.webapp.services.SetupVerificationService;
import de.wehner.mediamagpie.conductor.webapp.validator.MainConfigurationValidator;
import de.wehner.mediamagpie.persistence.entity.properties.SetupTask;
import de.wehner.mediamagpie.persistence.service.ConfigurationProvider;

@Controller
@RequestMapping("/config/admin")
public class AdministrationController {

    private static final Logger LOG = LoggerFactory.getLogger(AdministrationController.class);

    public static final String URL_MAINCONFIG = "/mainconfiguration";
    public static final String VIEW_MAINCONFIG = "config/admin/show_mainconfiguration";
    public static final String URL_MAINCONFIG_EDIT = "/mainconfiguration/edit";
    public static final String VIEW_MAINCONFIG_EDIT = "config/admin/edit_mainconfiguration";

    private final ConfigurationProvider _configurationProvider;
    private final MediaSyncService _mediaSyncService;
    private final SetupVerificationService _setupVerificationService;
    private final MapperFactory _mapperFactory;

    @Autowired
    public AdministrationController(ConfigurationProvider configurationProvider, ImageService imageService, MediaSyncService mediaSyncService,
            SetupVerificationService setupVerificationService) {
        super();
        _configurationProvider = configurationProvider;
        _mediaSyncService = mediaSyncService;
        _setupVerificationService = setupVerificationService;
        _mapperFactory = new DefaultMapperFactory.Builder().build();
    }

    @InitBinder
    public void initBinder(WebDataBinder webDataBinder) {
        webDataBinder.registerCustomEditor(String.class, new StringTrimmerEditor(false));
        webDataBinder.registerCustomEditor(String[].class, new StringArrayPropertyEditor("\n", false, true));
    }

    @RequestMapping(method = RequestMethod.GET, value = URL_MAINCONFIG)
    public String showConfiguration(Model model) {
        setMainConfigurationCommandIntoModel(model);
        return VIEW_MAINCONFIG;
    }

    @RequestMapping(method = RequestMethod.GET, value = URL_MAINCONFIG_EDIT)
    public String editConfiguration(Model model) {
        setMainConfigurationCommandIntoModel(model);
        return VIEW_MAINCONFIG_EDIT;
    }

    @RequestMapping(method = RequestMethod.POST, value = URL_MAINCONFIG_EDIT)
    public String submitConfiguration(@Valid @ModelAttribute("conf") MainConfigurationCommand conf, BindingResult result, Model model) throws IOException {
        if (conf.isCreateDirectories()) {
            conf.prepareDirectories(result);
        }
        new MainConfigurationValidator().validate(conf, result);
        if (result.hasErrors()) {
            LOG.info(result.toString());
            return VIEW_MAINCONFIG_EDIT;
        }

        _configurationProvider.saveOrUpdateMainConfiguration(conf);
        _setupVerificationService.clearSetupTask(SetupTask.CONFIGURE_SYSTEM_DIRS);

        _mediaSyncService.execute();
        return "redirect:" + getBaseRequestMappingUrl() + URL_MAINCONFIG;
    }

    private void setMainConfigurationCommandIntoModel(Model model) {
        MapperFacade mapper = _mapperFactory.getMapperFacade();
        MainConfigurationCommand mainConfigurationCommand = mapper.map(_configurationProvider.getMainConfiguration(), MainConfigurationCommand.class);
        model.addAttribute("conf", mainConfigurationCommand);
    }

    public static String getBaseRequestMappingUrl() {
        return AdministrationController.class.getAnnotation(RequestMapping.class).value()[0];
    }
}

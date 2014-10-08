package de.wehner.mediamagpie.conductor.webapp.controller;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mobile.device.Device;
import org.springframework.mobile.device.DeviceUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import de.wehner.mediamagpie.conductor.webapp.controller.commands.MediaThumbCommand;
import de.wehner.mediamagpie.conductor.webapp.controller.configuration.AdministrationController;
import de.wehner.mediamagpie.conductor.webapp.services.ImageService;
import de.wehner.mediamagpie.conductor.webapp.util.security.SecurityUtil;
import de.wehner.mediamagpie.persistence.dao.MediaDao;
import de.wehner.mediamagpie.persistence.entity.Media;
import de.wehner.mediamagpie.persistence.entity.Visibility;
import de.wehner.mediamagpie.persistence.entity.properties.SetupTask;
import de.wehner.mediamagpie.persistence.service.ConfigurationProvider;

@Controller
public class WelcomeController {

    private static final Logger LOG = LoggerFactory.getLogger(WelcomeController.class);

    public static final String WELCOME_URL = "/welcome";
    public static final String WELCOME_VIEW = "public/welcome";
    public static final String MOCK_URL = "/mock";
    public static final String MOCK_VIEW = "public/mock/mock";
    public static final String MOCKSTATIC_URL = "/mockstatic";
    public static final String MOCKSTATIC_VIEW = "public/mock/mockstatic";

    private final MediaDao _mediaDao;
    private final ImageService _imageSerivce;
    private final ConfigurationProvider _configurationProvider;

    @Autowired
    public WelcomeController(MediaDao mediaDao, ImageService imageService, ConfigurationProvider configurationProvider) {
        super();
        _mediaDao = mediaDao;
        _imageSerivce = imageService;
        _configurationProvider = configurationProvider;
    }

    @RequestMapping(method = { RequestMethod.GET, RequestMethod.HEAD }, value = WELCOME_URL)
    public String welcome(Device device, ModelMap model, @RequestParam(value = "start", required = false) Integer start, HttpServletRequest request) {

        LOG.info("Detected device {}", device);

        String redirectUrl = nextRequiredSetupLink(request);
        if (!StringUtils.isEmpty(redirectUrl)) {
            LOG.warn("Found setup task and offer new redirect url {}.", redirectUrl);
            return "redirect:" + redirectUrl;
        }

        List<MediaThumbCommand> mediaThumbCommands = new ArrayList<MediaThumbCommand>();
        Visibility minVisibility = (SecurityUtil.getCurrentUser(false) != null) ? Visibility.USERS : Visibility.PUBLIC;
        List<Media> someMedias = _mediaDao.getAllLastAddedPublicMedias(minVisibility, 100);
        for (Media media : someMedias) {
            mediaThumbCommands.add(_imageSerivce.createMediaThumbCommand(media, _configurationProvider.getMainConfiguration(), null, request));
        }

        model.addAttribute(mediaThumbCommands);
        return WELCOME_VIEW;
    }

    @RequestMapping("/env")
    public void env(HttpServletResponse response, HttpServletRequest servletRequest) throws IOException {
        Device device = DeviceUtils.getCurrentDevice(servletRequest);
        response.setContentType("text/plain");
        PrintWriter out = response.getWriter();
        out.println("System Environment:");
        out.println("device: " + device);
        for (Map.Entry<String, String> envvar : System.getenv().entrySet()) {
            out.println(envvar.getKey() + ": " + envvar.getValue());
        }
    }

    private String nextRequiredSetupLink(HttpServletRequest request) {
        Set<SetupTask> setupTasks = _configurationProvider.getRequiredSetupTasks().getSetupTasks();
        if (setupTasks.contains(SetupTask.CONFIGURE_SYSTEM_DIRS)) {
            return AdministrationController.getBaseRequestMappingUrl() + AdministrationController.URL_MAINCONFIG_EDIT;
        }
        return null;
    }

    @RequestMapping(method = { RequestMethod.GET, RequestMethod.HEAD }, value = MOCK_URL)
    public String showMock() {
        return MOCK_VIEW;
    }

    @RequestMapping(method = { RequestMethod.GET, RequestMethod.HEAD }, value = MOCKSTATIC_URL)
    public String showMockStatic() {
        return MOCKSTATIC_VIEW;
    }
}

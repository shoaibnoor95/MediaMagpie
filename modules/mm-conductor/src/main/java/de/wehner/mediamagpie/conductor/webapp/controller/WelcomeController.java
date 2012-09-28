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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import de.wehner.mediamagpie.common.persistence.entity.Media;
import de.wehner.mediamagpie.common.persistence.entity.Visibility;
import de.wehner.mediamagpie.common.persistence.entity.properties.SetupTask;
import de.wehner.mediamagpie.conductor.configuration.ConfigurationProvider;
import de.wehner.mediamagpie.conductor.persistence.dao.MediaDao;
import de.wehner.mediamagpie.conductor.webapp.controller.commands.MediaThumbCommand;
import de.wehner.mediamagpie.conductor.webapp.controller.configuration.AdministrationController;
import de.wehner.mediamagpie.conductor.webapp.services.ImageService;
import de.wehner.mediamagpie.conductor.webapp.util.security.SecurityUtil;

@Controller
public class WelcomeController {

    public static final String WELCOME_URL = "/welcome";
    public static final String WELCOME_VIEW = "public/welcome";
    public static final String MOCK_URL = "/mock";
    public static final String MOCK_VIEW = "public/mock";

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
    public String welcome(ModelMap model, @RequestParam(value = "start", required = false) Integer start, HttpServletRequest request) {

        String redirectUrl = nextRequiredSetupLink(request);
        if (!StringUtils.isEmpty(redirectUrl)) {
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
    public void env(HttpServletResponse response) throws IOException {
        response.setContentType("text/plain");
        PrintWriter out = response.getWriter();
        out.println("System Environment:");
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
}

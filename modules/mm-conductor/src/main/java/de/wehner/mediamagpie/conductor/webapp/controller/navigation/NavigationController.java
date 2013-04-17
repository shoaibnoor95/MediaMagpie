package de.wehner.mediamagpie.conductor.webapp.controller.navigation;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import de.wehner.mediamagpie.persistence.entity.properties.RequiredSetupTasks;
import de.wehner.mediamagpie.persistence.service.ConfigurationProvider;

@Controller
// @RequestMapping("/navigation")
public class NavigationController {

    private final String MAIN_NAVIGATION_URL = "/mainNavigation";
    private final String MAIN_NAVIGATION_VIEW = "/navigation/mainNavi";

    private final String SUB_NAVIGATION_DASHBOARD_URL = "/subNaviDashboard";
    private final String SUB_NAVIGATION_DASHBOARD_VIEW = "/navigation/subNaviDashboard";
    private final String SUB_NAVIGATION_MEDIA_URL = "/subNaviMedia";
    private final String SUB_NAVIGATION_MEDIA_VIEW = "/navigation/subNaviMedia";
    private final String SUB_NAVIGATION_CONFIG_URL = "/subNaviConfiguration";
    private final String SUB_NAVIGATION_CONFIG_VIEW = "/navigation/subNaviConfiguration";

    private final ConfigurationProvider _configurationController;

    @Autowired
    public NavigationController(ConfigurationProvider configurationProvider) {
        _configurationController = configurationProvider;
    }

    @RequestMapping(method = { RequestMethod.HEAD, RequestMethod.GET, RequestMethod.POST }, value = MAIN_NAVIGATION_URL)
    public String showMainNavigation(Model model) {
        RequiredSetupTasks requiredSetupTasks = _configurationController.getRequiredSetupTasks();

        if (requiredSetupTasks != null) {
            model.addAttribute(requiredSetupTasks);
        }
        return MAIN_NAVIGATION_VIEW;
    }

    @RequestMapping(method = { RequestMethod.HEAD, RequestMethod.GET, RequestMethod.POST }, value = SUB_NAVIGATION_DASHBOARD_URL)
    public String showSubNavigationDashboard() {
        return SUB_NAVIGATION_DASHBOARD_VIEW;
    }

    @RequestMapping(method = { RequestMethod.HEAD, RequestMethod.GET, RequestMethod.POST }, value = SUB_NAVIGATION_MEDIA_URL)
    public String showSubNavigationMedia() {
        return SUB_NAVIGATION_MEDIA_VIEW;
    }

    @RequestMapping(method = { RequestMethod.HEAD, RequestMethod.GET, RequestMethod.POST }, value = SUB_NAVIGATION_CONFIG_URL)
    public String showSubNavigationAdministration(Model model) {
        RequiredSetupTasks requiredSetupTasks = _configurationController.getRequiredSetupTasks();

        if (requiredSetupTasks != null) {
            model.addAttribute(requiredSetupTasks);
        }
        return SUB_NAVIGATION_CONFIG_VIEW;
    }

}

package de.wehner.mediamagpie.conductor.webapp.controller.navigation;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import de.wehner.mediamagpie.common.core.util.ManifestMetaData;
import de.wehner.mediamagpie.conductor.webapp.util.Env;


@Controller
public class NavigationController {

    private final String HEADER_URL = "/header";
    private final String HEADER_URL5 = "/header5";
    private final String HEADER_VIEW = "/general/header";
    private final String HEADER_VIEW5 = "/general/header5";
    private final String FOOTER_URL = "/footer";
    private final String FOOTER_VIEW = "/general/footer";

    private final String MAIN_NAVIGATION_URL = "/mainNavi";
    private final String MAIN_NAVIGATION_VIEW = "/navigation/mainNavi";
    private final String SUB_NAVIGATION_DASHBOARD_URL = "/subNaviDashboard";
    private final String SUB_NAVIGATION_DASHBOARD_VIEW = "/navigation/subNaviDashboard";
    private final String SUB_NAVIGATION_MEDIA_URL = "/subNaviMedia";
    private final String SUB_NAVIGATION_MEDIA_VIEW = "/navigation/subNaviMedia";
    private final String SUB_NAVIGATION_CONFIG_URL = "/subNaviConfiguration";
    private final String SUB_NAVIGATION_CONFIG_VIEW = "/navigation/subNaviConfiguration";

    private final String CROSSDOMAIN_URL = "/crossdomain.xml";
    private final String CROSSDOMAIN_VIEW = "/rss/crossdomain";

    @RequestMapping(method = { RequestMethod.HEAD, RequestMethod.GET, RequestMethod.POST }, value = CROSSDOMAIN_URL)
    public String getCrossdomainXml() {
        return CROSSDOMAIN_VIEW;
    }

    @RequestMapping(method = { RequestMethod.HEAD, RequestMethod.GET, RequestMethod.POST }, value = HEADER_URL)
    public String showHeader() {
        return HEADER_VIEW;
    }

    @RequestMapping(method = { RequestMethod.HEAD, RequestMethod.GET, RequestMethod.POST }, value = HEADER_URL5)
    public String showHeader5() {
        return HEADER_VIEW5;
    }

    @RequestMapping(method = { RequestMethod.HEAD, RequestMethod.GET, RequestMethod.POST }, value = MAIN_NAVIGATION_URL)
    public String showMainNavigation() {
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
    public String showSubNavigationAdministration() {
        return SUB_NAVIGATION_CONFIG_VIEW;
    }

    @RequestMapping(method = { RequestMethod.HEAD, RequestMethod.GET, RequestMethod.POST }, value = FOOTER_URL)
    public String showFooter(ModelMap model) {
        model.addAttribute("version", ManifestMetaData.getVersion());
        model.addAttribute("revision", ManifestMetaData.getRevision());
        model.addAttribute("time", ManifestMetaData.getCompileTime());
        model.addAttribute("deployMode", System.getProperty(Env.DEPLOY_MODE_KEY));
        return FOOTER_VIEW;
    }
}

package de.wehner.mediamagpie.conductor.webapp.controller.navigation;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import de.wehner.mediamagpie.conductor.util.Env;
import de.wehner.mediamagpie.conductor.util.ManifestMetaData;

@Controller
public class GeneralController {

    private final String FOOTER_URL = "/footer";
    private final String FOOTER_VIEW = "/general/footer";

    private final String CROSSDOMAIN_URL = "/crossdomain.xml";
    private final String CROSSDOMAIN_VIEW = "/rss/crossdomain";

    @RequestMapping(method = { RequestMethod.HEAD, RequestMethod.GET, RequestMethod.POST }, value = CROSSDOMAIN_URL)
    public String getCrossdomainXml() {
        return CROSSDOMAIN_VIEW;
    }

    @RequestMapping(method = { RequestMethod.HEAD, RequestMethod.GET, RequestMethod.POST }, value = FOOTER_URL)
    public String showFooter(ModelMap model) {
        ManifestMetaData manifestMetaData = new ManifestMetaData();
        model.addAttribute("version", manifestMetaData.getVersion());
        model.addAttribute("buildTime", manifestMetaData.getBuildTime());
        model.addAttribute("deployMode", System.getProperty(Env.DEPLOY_MODE_KEY));
        return FOOTER_VIEW;
    }
}

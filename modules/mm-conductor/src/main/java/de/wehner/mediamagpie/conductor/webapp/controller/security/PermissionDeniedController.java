package de.wehner.mediamagpie.conductor.webapp.controller.security;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class PermissionDeniedController {

    public static final String URL = "/general/permissionDenied";
    public static final String VIEW = "general/permissionDenied";

    @RequestMapping(method = { RequestMethod.GET, RequestMethod.POST }, value = URL)
    public String permissionDenied(Model model, @RequestParam(value = "site", required = false) String site) {
        model.addAttribute("site", site);
        return VIEW;
    }

}

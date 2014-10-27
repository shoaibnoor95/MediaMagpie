package de.wehner.mediamagpie.conductor.webapp.controller.security;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class LoginController {

    public static final String LOGIN_URL = "/login";
    public static final String LOGIN_VIEW = "/public/login";

    /*
     * @RequestMapping(method = RequestMethod.GET, value = LOGIN_URL) public String setupView(ModelMap model) { return LOGIN_VIEW; }
     */

    /**
     * both "normal login" and "login for update" shared this form.
     * 
     */
    @RequestMapping(method = RequestMethod.GET, value = LOGIN_URL)
    public String login(ModelMap model, @RequestParam(value = "error", required = false) String error,
            @RequestParam(value = "logout", required = false) String logout, HttpServletRequest request) {

        if (error != null) {
            model.addAttribute("error", "Invalid username and password!");

            // login form for update page
            // if login error, get the targetUrl from session again.
            String targetUrl = getRememberMeTargetUrlFromSession(request);
            System.out.println(targetUrl);
            if (!StringUtils.isEmpty(targetUrl)) {
                model.addAttribute("targetUrl", targetUrl);
                model.addAttribute("loginUpdate", true);
            }

        }

        if (logout != null) {
            model.addAttribute("msg", "You've been logged out successfully.");
        }

        return LOGIN_VIEW;
    }

    /**
     * get targetURL from session
     */
    private String getRememberMeTargetUrlFromSession(HttpServletRequest request) {
        String targetUrl = "";
        HttpSession session = request.getSession(false);
        if (session != null) {
            targetUrl = session.getAttribute("targetUrl") == null ? "" : session.getAttribute("targetUrl").toString();
        }
        return targetUrl;
    }
}

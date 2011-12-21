package de.wehner.mediamagpie.conductor.webapp.controller.security;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
public class LoginController {

	public static final String LOGIN_URL = "/login";
	public static final String LOGIN_VIEW = "/public/login";
	
	@RequestMapping(method = RequestMethod.GET, value = LOGIN_URL)
	public String setupView(ModelMap model) {
		return LOGIN_VIEW;
	}
}

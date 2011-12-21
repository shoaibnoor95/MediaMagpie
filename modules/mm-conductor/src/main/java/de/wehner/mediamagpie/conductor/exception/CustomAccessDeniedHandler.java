package de.wehner.mediamagpie.conductor.exception;

import java.io.IOException;
import java.net.URLDecoder;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import de.wehner.mediamagpie.conductor.webapp.controller.security.PermissionDeniedController;

@Component(value = "accessDeniedHandler")
public class CustomAccessDeniedHandler implements AccessDeniedHandler {

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException exception) throws IOException, ServletException {
        String contextPath = request.getContextPath();
        String requestURI = request.getRequestURI();
        requestURI = URLDecoder.decode(requestURI, "UTF-8");
        // StringEscapeUtils.escapeHtml(requestURI);
        response.sendRedirect(contextPath + PermissionDeniedController.URL + "?site=" + requestURI);
    }
}
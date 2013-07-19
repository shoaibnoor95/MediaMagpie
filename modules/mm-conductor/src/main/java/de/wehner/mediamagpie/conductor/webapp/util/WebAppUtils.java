package de.wehner.mediamagpie.conductor.webapp.util;

import java.util.Locale;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.PageContext;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.support.RequestContextUtils;

import de.wehner.mediamagpie.conductor.ApplicationConstants;

public class WebAppUtils {

    public static Locale getCurrentLocale(HttpServletRequest request) {
        LocaleResolver localeResolver = RequestContextUtils.getLocaleResolver(request);
        if (localeResolver == null) {
            throw new IllegalStateException("No LocaleResolver found: not in a DispatcherServlet request?");
        }
        Locale locale = localeResolver.resolveLocale(request);
        return locale;
    }

    /**
     * Extracts the protocol, server name with port and the context path from a client request. The result is something like
     * 'http://localhost:8087/mediamagpie'.
     * 
     * @param request
     *            The request.
     * @return The first part of url used from user's browser request.
     */
    public static String getRequestUrlUpToContextPath(HttpServletRequest request) {
        String scheme = request.getScheme();
        int serverPort = request.getServerPort();
        return buildRequestBasedOnServletRequest(request, scheme, serverPort);
    }

    public static String buildHttpRequestBasedOnServletRequest(HttpServletRequest request, PageContext pc) {
        Integer httpPort = (Integer) pc.getAttribute(ApplicationConstants.WEB_APP_PORT_HTTP, PageContext.APPLICATION_SCOPE);

        return buildRequestBasedOnServletRequest(request, "http", httpPort);
    }

    static String buildRequestBasedOnServletRequest(HttpServletRequest request, String scheme, int serverPort) {
        String serverName = request.getServerName();
        String contextPath = request.getContextPath();
        StringBuilder builder = new StringBuilder();
        builder.append(scheme).append("://");
        builder.append(serverName);
        if (serverPort != 80) {
            builder.append(':').append(serverPort);
        }
        builder.append(contextPath);
        return builder.toString();
    }

    public static String getBaseRequestMappingUrl(Class<?> controllerClass) {
        return controllerClass.getAnnotation(RequestMapping.class).value()[0];
    }

    public static String redirect(Object controller, String targetUrlPart) {
        return "redirect:" + getBaseRequestMappingUrl(controller.getClass()) + targetUrlPart;
    }
}

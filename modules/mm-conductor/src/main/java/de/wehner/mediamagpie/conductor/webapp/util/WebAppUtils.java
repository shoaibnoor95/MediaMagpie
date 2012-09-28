package de.wehner.mediamagpie.conductor.webapp.util;

import java.util.Locale;

import javax.servlet.http.HttpServletRequest;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.support.RequestContextUtils;

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
     * Extracts the protocoll, server name with port and the context path from a client request. The result is something like
     * 'http://localhost:8087/mediamagpie'.
     * 
     * @param request
     *            The request.
     * @return The first part of url used from user's browser request.
     */
    public static String getRequestUrlUpToContextPath(HttpServletRequest request) {
        String scheme = request.getScheme();
        String serverName = request.getServerName();
        int serverPort = request.getServerPort();
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

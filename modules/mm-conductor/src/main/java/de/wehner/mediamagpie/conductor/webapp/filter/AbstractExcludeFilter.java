package de.wehner.mediamagpie.conductor.webapp.filter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

public abstract class AbstractExcludeFilter implements Filter {

    private static final Logger LOG = LoggerFactory.getLogger(AbstractExcludeFilter.class);

    protected ApplicationContext _applicationContext;

    private List<String> _excludeEndings = new ArrayList<String>();

    private List<String> _excludeBeginnings = new ArrayList<String>();

    public AbstractExcludeFilter() {
        super();
    }

    public AbstractExcludeFilter(EnumSet<Exclude> excludes) {
        setupExcludeLists(excludes);
    }

    public final void init(FilterConfig filterConfig) throws ServletException {
        _applicationContext = WebApplicationContextUtils.getWebApplicationContext(filterConfig.getServletContext());
        onInit(filterConfig);
    }

    protected abstract void onInit(FilterConfig filterConfig);

    public final void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;
        String requestURI = request.getRequestURI();
        if (!isExcluded(requestURI)) {
            LOG.trace(this.getClass().getSimpleName() + ": filter " + request.getRequestURI());
            onDoFilter(request, response, request.getSession(), chain);
        } else {
            LOG.trace(this.getClass().getSimpleName() + ": skip filter " + request.getRequestURI());
            chain.doFilter(request, response);
        }
    }

    private boolean isExcluded(String requestURI) {
        for (String ending : _excludeEndings) {
            if (requestURI.endsWith(ending)) {
                return true;
            }
        }
        for (String begining : _excludeBeginnings) {
            if (requestURI.startsWith(begining)) {
                return true;
            }
        }
        return false;
    }

    protected abstract void onDoFilter(HttpServletRequest request, HttpServletResponse response, HttpSession session, FilterChain chain) throws IOException, ServletException;

    protected Object loadBean(String beanId) {
        Object bean = _applicationContext.getBean(beanId);
        if (bean == null) {
            throw new IllegalStateException("no bean with id '" + beanId + "' configured");
        }
        return bean;
    }

    public void destroy() {
        // can be overwritten
    }

    private void setupExcludeLists(EnumSet<Exclude> excludes) {
        for (Exclude exclude : excludes) {
            String[] excludesStrings = exclude.getExcludes();
            for (String excludeString : excludesStrings) {
                if (exclude.isBeginnings()) {
                    _excludeBeginnings.add(excludeString);
                } else {
                    _excludeEndings.add(excludeString);
                }
            }
        }
    }

    // protected static EnumSet<Exclude> EXCLUDE_NO_DATABASE = EnumSet.of(Exclude.RESOURCES,
    // Exclude.NAVIGATION);

    protected static EnumSet<Exclude> EXCLUDE_INCLUDES = EnumSet.of(Exclude.RESOURCES, Exclude.NAVIGATION);

    protected static enum Exclude {

        RESOURCES(".gif", ".jpg", ".png", ".ico", ".htc", ".css", ".js"),

        NAVIGATION(true, "/navigation");

        private final String[] _excludes;
        private final boolean _beginnings;

        private Exclude(String... excludes) {
            this(false, excludes);
        }

        private Exclude(boolean beginnings, String... excludes) {
            _beginnings = beginnings;
            _excludes = excludes;
        }

        public String[] getExcludes() {
            return _excludes;
        }

        public boolean isBeginnings() {
            return _beginnings;
        }
    }
}

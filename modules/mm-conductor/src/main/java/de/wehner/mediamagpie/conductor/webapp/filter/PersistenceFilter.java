package de.wehner.mediamagpie.conductor.webapp.filter;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import de.wehner.mediamagpie.persistence.PersistenceService;


public class PersistenceFilter implements Filter {

    private static final Logger LOG = LoggerFactory.getLogger(PersistenceFilter.class);
    private PersistenceService _persistenceService;

    public void destroy() {
    }

    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        try {
            LOG.trace("begin transaction");
            _persistenceService.beginTransaction();

            // ensure that we do a commit before a redirect is send back to the browser. If we don't
            // do this it can happen that the redirected page is opened before the transaction is
            // commited and this page might then show old content.
            chain.doFilter(request, new HttpServletResponseWrapper((HttpServletResponse) response) {

                @Override
                public void sendRedirect(String location) throws IOException {
                    commit();
                    LOG.debug("Sending redirect to " + location);
                    super.sendRedirect(location);
                }
            });

            commit();
        } catch (Throwable t) {
            LOG.error("error while transaction handling. do a rollback", t);
            _persistenceService.rollbackTransaction();
            if (t instanceof RuntimeException) {
                throw (RuntimeException) t;
            }
            if (t instanceof ServletException) {
                throw (ServletException) t;
            }
            throw new ServletException(t);
        }
    }

    private void commit() {
        LOG.trace("commit transaction");
        _persistenceService.commitTransaction();
    }

    public void init(FilterConfig filterConfig) throws ServletException {
        ApplicationContext applicationContext = WebApplicationContextUtils.getWebApplicationContext(filterConfig.getServletContext());
        _persistenceService = applicationContext.getBean(PersistenceService.class);
    }
}

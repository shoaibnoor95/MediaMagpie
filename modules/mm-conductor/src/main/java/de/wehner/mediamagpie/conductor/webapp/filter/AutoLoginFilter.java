package de.wehner.mediamagpie.conductor.webapp.filter;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

import de.wehner.mediamagpie.common.persistence.entity.User;
import de.wehner.mediamagpie.common.util.StringUtil;
import de.wehner.mediamagpie.conductor.persistence.TransactionHandler;
import de.wehner.mediamagpie.conductor.persistence.dao.UserDao;
import de.wehner.mediamagpie.conductor.webapp.services.UserSecurityService;
import de.wehner.mediamagpie.conductor.webapp.util.Env;
import de.wehner.mediamagpie.conductor.webapp.util.security.SecurityUtil;




/**
 * TODO rwe: can we remove this?
 * @author ralfwehner
 * @Deprecated
 */
public class AutoLoginFilter extends AbstractExcludeFilter {

    private static Logger LOG = LoggerFactory.getLogger(AutoLoginFilter.class);

    private final boolean _runsInLocalMode;
    
    public AutoLoginFilter(){
        super(EXCLUDE_INCLUDES);
        _runsInLocalMode = System.getProperty(Env.DEPLOY_MODE_KEY, "local").equals("local");
    }
    
    @Override
    protected void onInit(FilterConfig filterConfig) {
        // do nothing
    }

    @Override
    protected void onDoFilter(HttpServletRequest request, HttpServletResponse response, HttpSession session, FilterChain chain) throws IOException, ServletException {
        if(_runsInLocalMode){
            // auto-login admin user
            UserDetails user = SecurityUtil.getCurrentPrinzipal(false);
            if (user == null) {
              TransactionHandler transactionHandler = _applicationContext.getBean(TransactionHandler.class);
              final UserDao userDao = _applicationContext.getBean(UserDao.class);
              transactionHandler.executeInTransaction(new Runnable(){

                  @Override
                  public void run() {
                      User user = userDao.getByName("admin");
                      
                      LOG.warn("Auto-login with 'admin' user duo to "+Env.DEPLOY_MODE_KEY+"=local is set.");
                      
                      UserDetails userDetails = UserSecurityService.createUserDetails(user);
                      if(!StringUtil.isEmpty(user.getPassword())){
                          SecurityUtil.setCurrentPassword(user.getPassword());
                      }
                      Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
                      Object credentials = null;
                      if (authentication != null) {
                          credentials = authentication.getCredentials();
                      }
//                      SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken(userDetails, credentials));

                  }});
            }
        }

        chain.doFilter(request, response);
    }

}

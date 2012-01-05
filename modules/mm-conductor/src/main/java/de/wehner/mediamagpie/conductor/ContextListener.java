package de.wehner.mediamagpie.conductor;

import java.lang.Thread.UncaughtExceptionHandler;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.context.ContextLoaderListener;

import de.wehner.mediamagpie.conductor.spring.deploy.impl.DynamicPropertiesConfigurer;

public class ContextListener extends ContextLoaderListener {

    protected static Logger LOG = LoggerFactory.getLogger(ContextListener.class);

    static {
        Thread.setDefaultUncaughtExceptionHandler(new UncaughtExceptionHandler() {
            public void uncaughtException(Thread t, Throwable e) {
                LOG.error("uncaught exception from thread " + t.getName(), e);
            }
        });
    }

    @Override
    public void contextInitialized(ServletContextEvent event) {
        LOG.info("initializing project...");
        DynamicPropertiesConfigurer.setupDeployModeAndSpringProfile();

        super.contextInitialized(event);

        // access beans through
        // WebApplicationContext applicationContext =
        // WebApplicationContextUtils.getWebApplicationContext(event.getServletContext());
        LOG.info("initializing project done");
//        String deployMode = System.getProperty(Env.DEPLOY_MODE_KEY);
//        
//        if(deployMode == null || deployMode.equals("local")){
//            // auto-login with an admin user...
//            User currentUser = SecurityUtil.getCurrentUser(false);
//            
//            if(currentUser == null){
//                WebApplicationContext applicationContext = WebApplicationContextUtils.getWebApplicationContext(event.getServletContext());
//                
//                TransactionHandler transactionHandler = applicationContext.getBean(TransactionHandler.class);
//                final UserDao userDao = applicationContext.getBean(UserDao.class);
//                transactionHandler.executeInTransaction(new Runnable(){
//
//                    @Override
//                    public void run() {
//                        User user = userDao.getByName("admin");
//                        
//                        LOG.warn("Auto-login with 'admin' user duo to deploy.mode=local is set.");
//                        
//                        UserDetails userDetails = UserSecurityService.createUserDetails(user);
//                        if(!StringUtil.isEmpty(user.getPassword())){
//                            SecurityUtil.setCurrentPassword(user.getPassword());
//                        }
////                        SecurityUtil.setCurrentUser(userDetails);
//                        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//                        Object credentials = null;
//                        if (authentication != null) {
//                            credentials = authentication.getCredentials();
//                        }
//                        SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken(userDetails, credentials));
//
//                    }});
//            }
//        }
    }

}

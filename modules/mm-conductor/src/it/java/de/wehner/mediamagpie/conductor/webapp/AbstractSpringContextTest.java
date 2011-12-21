package de.wehner.mediamagpie.conductor.webapp;

import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import de.wehner.mediamagpie.conductor.webapp.util.Env;


@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration( { "/spring/web-application.xml" })
public abstract class AbstractSpringContextTest {

    @Autowired
    protected ApplicationContext _applicationContext;

    protected AbstractSpringContextTest() {
        String deployMode = System.getProperty(Env.DEPLOY_MODE_KEY);
        if (deployMode == null || deployMode.equals("local")) {
            System.setProperty(Env.DEPLOY_MODE_KEY, "test");
        }
    }
}

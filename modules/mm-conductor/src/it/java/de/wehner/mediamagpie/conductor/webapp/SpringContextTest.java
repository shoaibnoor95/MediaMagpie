package de.wehner.mediamagpie.conductor.webapp;

import static org.junit.Assert.*;

import org.junit.Test;

import de.wehner.mediamagpie.common.persistence.dao.UserDao;


public class SpringContextTest extends AbstractSpringContextTest {
    
    @Test
    public void testWiring() {
        UserDao userDao = _applicationContext.getBean(UserDao.class);
        
        assertNotNull(userDao);
    }
}

package de.wehner.mediamagpie.conductor.persistence.dao;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import de.wehner.mediamagpie.conductor.persistence.dao.UserDaoWithTransaction;
import de.wehner.mediamagpie.conductor.webapp.AbstractSpringContextTest;
import de.wehner.mediamagpie.persistence.PersistenceService;
import de.wehner.mediamagpie.persistence.entity.User;
import de.wehner.mediamagpie.persistence.entity.User.Role;
import de.wehner.mediamagpie.persistence.testsupport.PersistenceTestUtil;


/**
 * This works only when transaction is activated in springs context configuration. Eg, you need:
 * <pre>
     &lt;context:component-scan base-package="wehner.workshop.common.entity" /&gt;
     &lt;context:component-scan base-package="wehner.workshop.webapp.persistence" //&gt;

     &lt;tx:annotation-driven //&gt;

     &lt;bean id="transactionManager" class="org.springframework.orm.jpa.JpaTransactionManager"
        p:entityManagerFactory-ref="entityManagerFactory" //&gt;

     &lt;bean id="entityManagerFactory"
        class="org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean"/&gt;
         &lt;property name="jpaVendorAdapter"/&gt;
             &lt;bean class="org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter" //&gt;
         &lt;/property/&gt;
         &lt;property name="persistenceUnitName" value="${db.mode}" //&gt;
     &lt;/bean/&gt;
 * </pre>
 * @author ralfwehner
 */
@Ignore("Works only, if annotation driven transaction is configured. Read text above.")
public class UserDaoWithTransactionTest extends AbstractSpringContextTest {

    @Before
    public void setUp() {
        PersistenceTestUtil.deleteAll(_applicationContext.getBean(PersistenceService.class));
    }

    @Test
    public void testSave() {
        UserDaoWithTransaction dao = getDao();

        User user = new User("Ralf", "ralfwehner@web.de", Role.ADMIN);
        Long id = dao.save(user).getId();
        User userFromDb = dao.getById(id);

        assertNotNull(userFromDb);
        assertEquals(user.getName(), userFromDb.getName());
    }

    @Test
    public void testFindByName() {
        UserDaoWithTransaction dao = getDao();

        User user = new User("Ralf", "ralfwehner@web.de", Role.ADMIN);
        dao.save(user).getId();
        User userFromDb = dao.findByName("Ralf");

        assertNotNull(userFromDb);
        assertEquals(user.getName(), userFromDb.getName());
    }

    private UserDaoWithTransaction getDao() {
        UserDaoWithTransaction userDao = _applicationContext.getBean(UserDaoWithTransaction.class);

        return userDao;
    }
}

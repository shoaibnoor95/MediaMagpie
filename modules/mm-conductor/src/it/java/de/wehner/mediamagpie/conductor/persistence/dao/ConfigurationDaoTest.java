package de.wehner.mediamagpie.conductor.persistence.dao;

import static org.junit.Assert.*;

import static org.mockito.Mockito.*;

import org.junit.Ignore;
import org.junit.Test;

import de.wehner.mediamagpie.core.util.properties.PropertiesUtil;
import de.wehner.mediamagpie.persistence.dao.ConfigurationDao;
import de.wehner.mediamagpie.persistence.dao.PersistenceService;
import de.wehner.mediamagpie.persistence.entity.properties.AdminConfiguration;
import de.wehner.mediamagpie.persistence.entity.properties.Property;
import de.wehner.mediamagpie.persistence.util.CipherServiceImpl;


public class ConfigurationDaoTest extends AbstractDaoTest<ConfigurationDao> {

    @Override
    protected ConfigurationDao createDao(PersistenceService persistenceService) {
        return new ConfigurationDao(persistenceService, mock(CipherServiceImpl.class));
    }

    @Test
    public void testSaveLoad() throws Exception {
        ConfigurationDao dao = getDao();
        AdminConfiguration configuration = new AdminConfiguration();
        configuration.setEmail("admin@datameer.com");
        configuration.setPassword("admin");

        dao.saveConfiguration(configuration);
        assertEquals(2, (long) dao.countAll());

        flipTransaction();
        checkAdminConfig(configuration, dao);
    }

    @Test
    @Ignore("constrain doesn't work any more if user_fk is <null>")
    public void testDuplicatedSave() throws Exception {
        ConfigurationDao dao = getDao();
        AdminConfiguration configuration1 = new AdminConfiguration();
        configuration1.setEmail("admin@datameer.com");
        configuration1.setPassword("admin");
        AdminConfiguration configuration2 = new AdminConfiguration();
        configuration2.setEmail("admin2@datameer.com");
        configuration2.setPassword("admin2");

        dao.saveConfiguration(configuration1);
        flipTransaction();
        try {
            dao.saveConfiguration(configuration2);
            fail("should throw exception");
        } catch (Exception e) {
            rollbackTransaction();
            // expected
        }
    }

    @Test
    public void testSaveOrUpdate() throws Exception {
        ConfigurationDao dao = getDao();
        AdminConfiguration configuration = new AdminConfiguration();
        configuration.setEmail("admin@datameer.com");
        configuration.setPassword("admin");

        // duplicate safe without change
        dao.saveOrUpdateConfiguration(configuration);
        dao.saveOrUpdateConfiguration(configuration);
        checkAdminConfig(configuration, dao);

        // duplicate safe with change
        configuration.setPassword("admin2");
        dao.saveOrUpdateConfiguration(configuration);
        checkAdminConfig(configuration, dao);
        assertEquals(2, (long) dao.countAll());

        // remove obsolete property
        dao.makePersistent(new Property(PropertiesUtil.getPrefix(AdminConfiguration.class) + ".obsolete", "aValue"));
        dao.saveOrUpdateConfiguration(configuration);
        checkAdminConfig(configuration, dao);
        assertEquals(2, (long) dao.countAll());
    }

    private void checkAdminConfig(AdminConfiguration configuration, ConfigurationDao dao) {
        flipTransaction();
        AdminConfiguration loadedConfiguration = dao.getConfiguration(AdminConfiguration.class);
        assertEquals(configuration.getEmail(), loadedConfiguration.getEmail());
        assertEquals(configuration.getPassword(), loadedConfiguration.getPassword());
    }

}

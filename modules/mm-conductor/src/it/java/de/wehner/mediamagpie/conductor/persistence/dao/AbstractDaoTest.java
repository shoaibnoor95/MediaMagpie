package de.wehner.mediamagpie.conductor.persistence.dao;

import javax.persistence.EntityManagerFactory;

import org.junit.After;
import org.junit.Before;

import de.wehner.mediamagpie.common.persistence.dao.Dao;
import de.wehner.mediamagpie.common.persistence.testsupport.PersistenceTestUtil;
import de.wehner.mediamagpie.persistence.PersistenceService;


@SuppressWarnings({ "unchecked", "rawtypes" })
public abstract class AbstractDaoTest<T extends Dao> {

    protected PersistenceService _persistenceService;
    
    private EntityManagerFactory _entityManagerFactory;

    private T _dao;

    @Before
    public void setUp() {
        _entityManagerFactory = PersistenceTestUtil.createEntityManagerFactory();
        _persistenceService = new PersistenceService(_entityManagerFactory);
        _dao = createDao(_persistenceService);
        cleanDb();
        _persistenceService.beginTransaction();
    }

    private void cleanDb() {
        PersistenceTestUtil.deleteAll(_persistenceService);
        PersistenceTestUtil.deleteAllEntities(getDao());
    }

    @After
    public void tearDown() throws Exception {
        cleanDb();
        _entityManagerFactory.close();
    }

    protected T getDao() {
        return _dao;
    }

    protected T createDao() {
        return createDao(_persistenceService);
    }

    protected abstract T createDao(PersistenceService persistenceService);

    protected void beginTransaction() {
        _persistenceService.beginTransaction();
    }

    protected void commitTransaction() {
        _persistenceService.commitTransaction();
    }

    protected void flipTransaction() {
        _persistenceService.flipTransaction();
    }

    protected void rollbackTransaction() {
        _persistenceService.rollbackTransaction();
    }

}

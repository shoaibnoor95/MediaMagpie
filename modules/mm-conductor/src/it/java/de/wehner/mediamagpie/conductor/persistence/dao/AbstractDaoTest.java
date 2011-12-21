package de.wehner.mediamagpie.conductor.persistence.dao;

import org.junit.After;
import org.junit.Before;

import de.wehner.mediamagpie.common.testsupport.PersistenceTestUtil;
import de.wehner.mediamagpie.conductor.persistence.PersistenceService;
import de.wehner.mediamagpie.conductor.persistence.dao.Dao;


@SuppressWarnings({ "unchecked", "rawtypes" })
public abstract class AbstractDaoTest<T extends Dao> {

    public PersistenceService _persistenceService;
    private T _dao;

    @Before
    public void setUp() {
        _persistenceService = PersistenceTestUtil.createPersistenceService();
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

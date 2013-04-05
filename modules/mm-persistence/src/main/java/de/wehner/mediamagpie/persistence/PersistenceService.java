package de.wehner.mediamagpie.persistence;

import java.io.Serializable;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Query;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.search.jpa.FullTextEntityManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import de.wehner.mediamagpie.common.persistence.entity.Base;
import de.wehner.mediamagpie.common.persistence.entity.User;


@Service
public class PersistenceService {
    
    private final EntityManagerFactory _entityManagerFactory;
    private final ThreadLocal<EntityManager> _threadLocal = new ThreadLocal<EntityManager>();

    @Autowired
    public PersistenceService(EntityManagerFactory entityManagerFactory) {
        _entityManagerFactory = entityManagerFactory;
    }

    public Query createQuery(String query) {
        EntityManager entityManager = getEntityManagerWithActiveTransaction();
        return entityManager.createQuery(query);
    }

    public Query createNativeQuery(String query) {
        EntityManager entityManager = getEntityManagerWithActiveTransaction();
        return entityManager.createNativeQuery(query);
    }

    public void beginTransaction() {
        EntityManager entityManager = getOrCreateEntityManager();
        EntityTransaction transaction = entityManager.getTransaction();
        if (!transaction.isActive()) {
            transaction.begin();
        }
    }

    protected EntityManager getOrCreateEntityManager() {
        EntityManager entityManager = getEntityManager(false);
        if (entityManager == null) {
            entityManager = _entityManagerFactory.createEntityManager();
            _threadLocal.set(entityManager);
        }
        return entityManager;
    }

    /**
     * Commits the current transaction. Does nothing if no transaction was started, yet.
     */
    public void commitTransaction() {
        EntityManager entityManager = getEntityManager(false);
        if (entityManager == null) {
            return;
        }
        EntityTransaction transaction = entityManager.getTransaction();
        transaction.commit();
        close();
    }

    /**
     * Rolls back the current transaction. Does nothing if no transaction was started, yet.
     */
    public void rollbackTransaction() {
        EntityManager entityManager = getEntityManager(false);
        if (entityManager == null) {
            return;
        }
        EntityTransaction transaction = entityManager.getTransaction();
        if (transaction.isActive()) {
            transaction.rollback();
        }
        close();
    }

    protected void close() {
        EntityManager entityManager = getEntityManager(false);
        if (entityManager != null) {
            _threadLocal.remove();
            entityManager.close();
        }
    }

    protected EntityManager getEntityManager(boolean failIfNotExists) {
        EntityManager entityManager = _threadLocal.get();
        if (failIfNotExists) {
            if (entityManager == null) {
                throw new IllegalStateException("No entity manager (did you call beginTransaction)?");
            }
        }
        return entityManager;
    }

    public void persist(Object object) {
        EntityManager entityManager = getEntityManagerWithActiveTransaction();
        entityManager.persist(object);
    }

    public EntityManager getEntityManagerWithActiveTransaction() {
        return getEntityManager(true);
    }

    public void remove(Object object) {
        EntityManager entityManager = getEntityManagerWithActiveTransaction();
        entityManager.remove(object);
    }

    @SuppressWarnings("unchecked")
    public <T> List<T> getAll(Class<T> clazz) {
        return createQuery("select t from " + clazz.getName() + " as t").getResultList();
    }

    public Criteria createCriteria(Class<?> persistentClass) {
        EntityManager entityManager = getEntityManagerWithActiveTransaction();
        Session session = (Session) entityManager.getDelegate();
        return session.createCriteria(persistentClass);
    }

    public FullTextEntityManager getFullTextEntityManager() {
        FullTextEntityManager fullTextEntityManager = org.hibernate.search.jpa.Search.getFullTextEntityManager(getEntityManagerWithActiveTransaction());
        return fullTextEntityManager;
    }

    public void flipTransaction() {
        commitTransaction();
        beginTransaction();
    }

    public Query createNamedQuery(String name) {
        EntityManager entityManager = getEntityManagerWithActiveTransaction();
        return entityManager.createNamedQuery(name);
    }

    public void flush() {
        EntityManager entityManager = getEntityManager(true);
        entityManager.flush();
    }

    public <T> T getById(Class<T> clazz, Serializable id) {
        EntityManager entityManager = getEntityManagerWithActiveTransaction();
        return entityManager.find(clazz, id);
    }

    public User merge(User user) {
        EntityManager entityManager = getEntityManagerWithActiveTransaction();
        return entityManager.merge(user);
    }

    public boolean isTransactionActive() {
        EntityManager entityManager = getEntityManager(false);
        if (entityManager == null) {
            return false;
        }

        return true;
    }

    @SuppressWarnings("unchecked")
    public <T extends Base> T reload(T entity) {
        return (T) getById(entity.getClass(), entity.getId());
    }
}

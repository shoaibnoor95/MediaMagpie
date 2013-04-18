package de.wehner.mediamagpie.persistence.dao;

import java.util.List;

import javax.persistence.NoResultException;
import javax.persistence.Query;

import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;

import de.wehner.mediamagpie.persistence.entity.UniqueBaseName;


public abstract class UniqueBaseNameDao<T extends UniqueBaseName> extends Dao<T> {

    public UniqueBaseNameDao(Class<T> clazz, PersistenceService persistenceService) {
        super(clazz, persistenceService);
    }

    @SuppressWarnings("unchecked")
    public boolean exists(String name) {
        Query createQuery = _persistenceService.createQuery("select t from " + _clazz.getSimpleName() + " as t where t._name = ?1");
        createQuery.setParameter(1, name);
        List<T> result = createQuery.getResultList();
        return !result.isEmpty();
    }

    @SuppressWarnings("unchecked")
    public T getByName(String name) {
        Criteria criteria = createCriteria();
        criteria.add(Restrictions.eq("_name", name));
        return (T) criteria.uniqueResult();
    }

    public T getByName(String name, boolean hasToExist) {
        T t = null;
        try {
            t = getByName(name);
        } catch (NoResultException e) {
            if (hasToExist) {
                throw e;
            }
        }
        return t;
    }

    @SuppressWarnings("unchecked")
    public List<String> getAllNames() {
        Query query = _persistenceService.createQuery("select t._name from " + _clazz.getSimpleName() + " as t");
        return query.getResultList();
    }

}

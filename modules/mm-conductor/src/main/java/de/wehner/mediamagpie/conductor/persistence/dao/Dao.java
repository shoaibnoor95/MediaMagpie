package de.wehner.mediamagpie.conductor.persistence.dao;

import java.util.Iterator;
import java.util.List;

import javax.persistence.NoResultException;
import javax.persistence.Query;

import org.hibernate.Criteria;
import org.hibernate.ScrollableResults;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Order;

import de.wehner.mediamagpie.common.persistence.entity.Base;
import de.wehner.mediamagpie.conductor.persistence.PersistenceService;

public abstract class Dao<T extends Base> {

    protected final Class<T> _clazz;

    protected final PersistenceService _persistenceService;

    public Dao(Class<T> clazz, PersistenceService persistenceService) {
        _clazz = clazz;
        _persistenceService = persistenceService;
    }

    public void makePersistent(T t) {
        _persistenceService.persist(t);
    }

    public void makeTransient(T t) {
        _persistenceService.remove(t);
    }

    public void flush() {
        _persistenceService.flush();
    }

    public List<T> getAll() {
        return _persistenceService.getAll(_clazz);
    }

    public List<T> getAll(Order order, int maxResults) {
        return getAll(order, 0, maxResults);
    }

    @SuppressWarnings("unchecked")
    public List<T> getAll(Order order, int start, int maxResults) {
        Query query = _persistenceService.createQuery("select t from " + _clazz.getName() + " as t order by " + order);
        query.setFirstResult(start);
        query.setMaxResults(maxResults);
        return query.getResultList();
    }

    public Long countAll() {
        Query query = _persistenceService.createQuery("select count(t) from " + _clazz.getName() + " as t");
        return (Long) query.getSingleResult();
    }

    public PersistenceService getPersistenceService() {
        return _persistenceService;
    }

    protected Criteria createCriteria() {
        return _persistenceService.createCriteria(_clazz);
    }

    protected final Criteria createCriteria(Criterion... criterion) {
        Criteria crit = _persistenceService.createCriteria(_clazz);
        for (Criterion c : criterion) {
            crit.add(c);
        }
        return crit;
    }

    public T getById(Long id) {
        return _persistenceService.getById(_clazz, id);
    }

    public T getById(Long id, boolean hasToExist) {
        T t = null;
        try {
            t = getById(id);
        } catch (NoResultException e) {
            if (hasToExist) {
                throw e;
            }
        } catch (IllegalArgumentException e) {
            if (hasToExist) {
                throw e;
            }
        }
        return t;
    }

    public final Iterator<T> iterate(Criteria criteria) {
        final ScrollableResults scrollableResults = criteria.scroll();

        return new Iterator<T>() {

            private T _next;

            public boolean hasNext() {
                if (_next == null) {
                    gotoNext();
                }
                return _next != null;
            }

            @SuppressWarnings("unchecked")
            private void gotoNext() {
                if (scrollableResults.next()) {
                    _next = (T) scrollableResults.get()[0];
                }
            }

            public T next() {
                if (_next == null) {
                    gotoNext();
                }
                try {
                    return _next;
                } finally {
                    _next = null;
                }
            }

            public void remove() {
                throw new UnsupportedOperationException();
            }
        };
    }
}

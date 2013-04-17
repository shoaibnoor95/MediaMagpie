package de.wehner.mediamagpie.persistence;

import de.wehner.mediamagpie.persistence.entity.CreationDateBase;

public abstract class CreationDateBaseDao<T extends CreationDateBase> extends Dao<T> {

    public CreationDateBaseDao(Class<T> clazz, PersistenceService persistenceService) {
        super(clazz, persistenceService);
    }

//    @SuppressWarnings("unchecked")
//    public boolean exists(String name) {
//        Query createQuery = _persistenceService.createQuery("select t from " + _clazz.getSimpleName() + " as t where t._name = ?1");
//        createQuery.setParameter(1, name);
//        List<T> result = createQuery.getResultList();
//        return !result.isEmpty();
//    }
//
//    @SuppressWarnings("unchecked")
//    public T getByName(String name) {
//        Criteria criteria = createCriteria();
//        criteria.add(Restrictions.eq("_name", name));
//        return (T) criteria.uniqueResult();
//    }
//
//    public T getByName(String name, boolean hasToExist) {
//        T t = null;
//        try {
//            t = getByName(name);
//        } catch (NoResultException e) {
//            if (hasToExist) {
//                throw e;
//            }
//        }
//        return t;
//    }
//
//    @SuppressWarnings("unchecked")
//    public List<String> getAllNames() {
//        Query query = _persistenceService.createQuery("select t._name from " + _clazz.getSimpleName() + " as t");
//        return query.getResultList();
//    }

}

package de.wehner.mediamagpie.conductor.persistence.dao;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import de.wehner.mediamagpie.persistence.entity.User;


@Repository
@Transactional(readOnly = true)
public class UserDaoWithTransaction {

    private EntityManager em = null;

    /**
     * Sets the entity manager.
     */
    @PersistenceContext
    public void setEntityManager(EntityManager em) {
        this.em = em;
    }

    public User getById(Long id){
        return em.find(User.class, id);    }
    
    /**
     * Saves person.
     */
    @Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
    public User save(User person) {
        return em.merge(person);
    }

    /**
     * Deletes person.
     */
    @Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
    public void delete(User person) {
        em.remove(em.merge(person));
    }

    public User findByName(String name) {
        Criteria criteria = createCriteria();
        criteria.add(Restrictions.eq("_name", name));
        return (User) criteria.uniqueResult();
    }

    protected Criteria createCriteria() {
        return createCriteria(User.class);
    }

    public Criteria createCriteria(Class<?> persistentClass) {
        Session session = (Session) em.getDelegate();
        return session.createCriteria(persistentClass);
    }
}

package de.wehner.mediamagpie.persistence;

import java.util.List;

import javax.persistence.Query;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import de.wehner.mediamagpie.persistence.entity.User;

@Repository
public class UserDao extends UniqueBaseNameDao<User> {

    @Autowired
    public UserDao(PersistenceService persistenceService) {
        super(User.class, persistenceService);
    }

    @SuppressWarnings("unchecked")
    public List<User> getUserLikeName(String name) {
        Query query = _persistenceService.createNamedQuery("getUserLikeName");
        query.setParameter("name", name.toLowerCase() + "%");
        return query.getResultList();
    }

    public User getByEmail(String email) {
        Query query = _persistenceService.createNamedQuery("getUserByEmail");
        query.setParameter("email", email.toLowerCase());
        @SuppressWarnings("unchecked")
        List<User> users = query.getResultList();

        if (users != null && users.size() == 1) {
            return users.get(0);
        }
        return null;
    }
}

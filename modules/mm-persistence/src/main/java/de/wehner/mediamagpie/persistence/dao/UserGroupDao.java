package de.wehner.mediamagpie.persistence.dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import de.wehner.mediamagpie.persistence.entity.UserGroup;


@Repository
public class UserGroupDao extends UniqueBaseNameDao<UserGroup>{
    
    @Autowired
    public UserGroupDao(PersistenceService persistenceService) {
        super(UserGroup.class, persistenceService);
    }
    
}

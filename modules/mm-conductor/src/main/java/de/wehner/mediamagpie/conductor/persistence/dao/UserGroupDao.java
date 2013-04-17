package de.wehner.mediamagpie.conductor.persistence.dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import de.wehner.mediamagpie.persistence.PersistenceService;
import de.wehner.mediamagpie.persistence.UniqueBaseNameDao;
import de.wehner.mediamagpie.persistence.entity.UserGroup;


@Repository
public class UserGroupDao extends UniqueBaseNameDao<UserGroup>{
    
    @Autowired
    public UserGroupDao(PersistenceService persistenceService) {
        super(UserGroup.class, persistenceService);
    }
    
}

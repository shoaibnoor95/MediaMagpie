package de.wehner.mediamagpie.conductor.persistence.dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import de.wehner.mediamagpie.common.persistence.entity.UserGroup;
import de.wehner.mediamagpie.conductor.persistence.PersistenceService;


@Repository
public class UserGroupDao extends UniqueBaseNameDao<UserGroup>{
    
    @Autowired
    public UserGroupDao(PersistenceService persistenceService) {
        super(UserGroup.class, persistenceService);
    }
    
}

package de.wehner.mediamagpie.conductor.persistence.dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import de.wehner.mediamagpie.common.persistence.entity.Registration;
import de.wehner.mediamagpie.conductor.persistence.PersistenceService;

@Repository
public class RegistrationDao extends CreationDateBaseDao<Registration> {

    @Autowired
    public RegistrationDao(PersistenceService persistenceService) {
        super(Registration.class, persistenceService);
    }

}

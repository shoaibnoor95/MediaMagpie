package de.wehner.mediamagpie.conductor.persistence.dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import de.wehner.mediamagpie.persistence.CreationDateBaseDao;
import de.wehner.mediamagpie.persistence.PersistenceService;
import de.wehner.mediamagpie.persistence.entity.Registration;

@Repository
public class RegistrationDao extends CreationDateBaseDao<Registration> {

    @Autowired
    public RegistrationDao(PersistenceService persistenceService) {
        super(Registration.class, persistenceService);
    }

}

package de.wehner.mediamagpie.persistence.dao;

import de.wehner.mediamagpie.persistence.entity.Base;

public class PersistenceServiceMock extends PersistenceService {

    public PersistenceServiceMock() {
        super(null);
    }

    @Override
    public <T extends Base> T reload(T entity) {
        return entity;
    }

}

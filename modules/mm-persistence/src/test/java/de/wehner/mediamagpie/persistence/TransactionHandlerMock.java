package de.wehner.mediamagpie.persistence;

import static org.mockito.Mockito.*;

import de.wehner.mediamagpie.persistence.dao.PersistenceService;
import de.wehner.mediamagpie.persistence.dao.TransactionHandler;
import de.wehner.mediamagpie.persistence.entity.Base;

public class TransactionHandlerMock extends TransactionHandler {

    private final PersistenceService _persistenceService;

    public TransactionHandlerMock() {
        this(mock(PersistenceService.class));
    }

    private TransactionHandlerMock(PersistenceService persistenceService) {
        super(persistenceService);
        _persistenceService = persistenceService;
    }

    public PersistenceService getPersistenceService() {
        return _persistenceService;
    }

    @Override
    public <T extends Base> T reload(T entity) {
        return entity;
    }

}

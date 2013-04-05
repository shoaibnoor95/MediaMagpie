package de.wehner.mediamagpie.persistence;

import static org.mockito.Mockito.*;

import de.wehner.mediamagpie.persistence.PersistenceService;
import de.wehner.mediamagpie.persistence.TransactionHandler;

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
}

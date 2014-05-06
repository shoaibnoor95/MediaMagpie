package de.wehner.mediamagpie.persistence;

import static org.mockito.Mockito.*;

import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import de.wehner.mediamagpie.persistence.dao.PersistenceService;
import de.wehner.mediamagpie.persistence.dao.TransactionHandler;
import de.wehner.mediamagpie.persistence.entity.Base;

public class TransactionHandlerMock extends TransactionHandler {

    
    public TransactionHandlerMock() {
        this(mock(PersistenceService.class));
        doAnswer(new Answer<Base>() {

            @Override
            public Base answer(InvocationOnMock invocation) throws Throwable {
                return (Base) invocation.getArguments()[0];
            }
            
        }).when(_persistenceService).reload(any(Base.class));
    }

    private TransactionHandlerMock(PersistenceService persistenceService) {
        super(persistenceService);
    }

    @Override
    public <T extends Base> T reload(T entity) {
        return entity;
    }

}

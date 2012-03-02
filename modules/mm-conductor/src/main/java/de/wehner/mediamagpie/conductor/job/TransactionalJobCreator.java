package de.wehner.mediamagpie.conductor.job;

import java.util.concurrent.Callable;

import org.springframework.beans.factory.annotation.Autowired;

import de.wehner.mediamagpie.common.persistence.entity.JobExecution;
import de.wehner.mediamagpie.conductor.performingjob.JobCreator;
import de.wehner.mediamagpie.conductor.performingjob.PerformingJob;
import de.wehner.mediamagpie.conductor.persistence.PersistenceService;
import de.wehner.mediamagpie.conductor.persistence.TransactionHandler;


public abstract class TransactionalJobCreator<T extends PerformingJob> implements JobCreator {

    private final TransactionHandler _transactionHandler;
    private PersistenceService _persistenceService;

    @Autowired
    public TransactionalJobCreator(TransactionHandler transactionHandler, PersistenceService persistenceService) {
        _transactionHandler = transactionHandler;
        _persistenceService = persistenceService;
    }

    @Override
    public T create(/*final DapJobConfiguration configuration,*/ final JobExecution execution) {
        return _transactionHandler.executeInTransaction(new Callable<T>() {
            @Override
            public T call() throws Exception {
                return createInTransaction(/*configuration,*/ _persistenceService.reload(execution));
            }
        });
    }

    protected abstract T createInTransaction(/*DapJobConfiguration configuration,*/ JobExecution execution) throws Exception;
}
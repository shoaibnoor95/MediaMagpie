package de.wehner.mediamagpie.conductor.job;

import java.util.concurrent.Callable;

import org.springframework.beans.factory.annotation.Autowired;

import de.wehner.mediamagpie.conductor.performingjob.JobCreator;
import de.wehner.mediamagpie.conductor.performingjob.PerformingJob;
import de.wehner.mediamagpie.persistence.dao.PersistenceService;
import de.wehner.mediamagpie.persistence.dao.TransactionHandler;
import de.wehner.mediamagpie.persistence.entity.JobExecution;

public abstract class TransactionalJobCreator <T extends PerformingJob> implements JobCreator {

    /**
     * The intention is to use the transactionHandler only in create() method.
     */
    protected final TransactionHandler _transactionHandler;
    protected PersistenceService _persistenceService;

    @Autowired
    public TransactionalJobCreator(TransactionHandler transactionHandler, PersistenceService persistenceService) {
        _transactionHandler = transactionHandler;
        _persistenceService = persistenceService;
    }

    @Override
    public T create(final JobExecution execution) {
        return (T) _transactionHandler.executeInTransaction(new Callable<PerformingJob>() {
            @Override
            public T call() throws Exception {
                return createInTransaction(/* configuration, */_persistenceService.reload(execution));
            }
        });
    }

    /**
     * This method will be called when <code>JobFactory.createPerformingJob(JobExecution)</code> is called due to executing the JobExceution
     * instance. It is called within a database transaction.
     * 
     * @param execution
     *            The JobExecution which should be processed.
     * @return The performing job object that do really the work.
     * @throws Exception
     */
    protected abstract T createInTransaction(/* DapJobConfiguration configuration, */JobExecution execution) throws Exception;
}

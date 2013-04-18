package de.wehner.mediamagpie.conductor.job;

import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

import de.wehner.mediamagpie.core.concurrent.SingleThreadedController;
import de.wehner.mediamagpie.persistence.dao.TransactionHandler;


public abstract class SingleThreadedTransactionController extends SingleThreadedController {

    private TransactionHandler _transactionHandler;

    public SingleThreadedTransactionController(TransactionHandler transactionHandler) {
        _transactionHandler = transactionHandler;
    }

    public SingleThreadedTransactionController(TransactionHandler transactionHandler, TimeUnit timeUnit, long waitTime) {
        super(timeUnit, waitTime);
        _transactionHandler = transactionHandler;
    }

    @Override
    public final boolean execute() {
        return _transactionHandler.executeInTransaction(new Callable<Boolean>() {

            @Override
            public Boolean call() throws Exception {
                return executeInTransaction();
            }
        });
    }

    public TransactionHandler getTransactionHandler() {
        return _transactionHandler;
    }

    /**
     * @return <code>true</code> when next call must be done immediately, <code>false</code> when the scheduler better sleept before next
     *         call.
     */
    protected abstract boolean executeInTransaction();
}

package de.wehner.mediamagpie.conductor.spring.deploy.impl;

import de.wehner.mediamagpie.conductor.spring.deploy.DataInjector;
import de.wehner.mediamagpie.persistence.TransactionHandler;

public abstract class AbstractDataInjector implements DataInjector {

    private TransactionHandler _transactionHandler;

    protected AbstractDataInjector(TransactionHandler transactionHandler) {
        _transactionHandler = transactionHandler;
    }

    @Override
    public void injectData() {
        _transactionHandler.executeInTransaction(new Runnable() {
            @Override
            public void run() {
                injectInTransaction();
            }
        });
    }

    private void injectInTransaction() {
    }

    public TransactionHandler getTransactionHandler() {
        return _transactionHandler;
    }
}

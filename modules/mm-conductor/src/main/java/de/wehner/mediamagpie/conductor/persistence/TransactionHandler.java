package de.wehner.mediamagpie.conductor.persistence;

import java.util.concurrent.Callable;
import java.util.concurrent.Executors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import de.wehner.mediamagpie.common.util.ExceptionUtil;


@Service
public class TransactionHandler {

    private final PersistenceService _persistenceService;

    @Autowired
    public TransactionHandler(PersistenceService persistenceService) {
        _persistenceService = persistenceService;
    }

    public <K> K executeInTransaction(Callable<K> callable) {
        // TODO rwe: Check, if we really need and want this block. I think, it makes no sense to
        // do something without begin() and commit().
        if (_persistenceService.isTransactionActive()) {
            try {
                assert (false);
                return callable.call();
            } catch (Exception e) {
                throw ExceptionUtil.convertToRuntimeException(e);
            }
        }

        _persistenceService.beginTransaction();
        boolean success = false;
        K result = null;
        try {
            result = callable.call();
            success = true;
        } catch (Exception e) {
            throw ExceptionUtil.convertToRuntimeException(e);
        } finally {
            boolean transactionActive = _persistenceService.isTransactionActive();
            if (success) {
                if (transactionActive) {
                    try {
                        _persistenceService.commitTransaction();
                    } catch (RuntimeException e) {
                        _persistenceService.close();
                        throw e;
                    }
                }
            } else {
                if (transactionActive) {
                    _persistenceService.rollbackTransaction();
                }
            }
        }
        return result;
    }

    public void executeInTransaction(Runnable runnable) {
        executeInTransaction(Executors.callable(runnable));
    }
}

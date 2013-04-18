package de.wehner.mediamagpie.persistence.dao;

import java.util.concurrent.Callable;
import java.util.concurrent.Executors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import de.wehner.mediamagpie.core.util.ExceptionUtil;
import de.wehner.mediamagpie.persistence.entity.Base;

@Service
public class TransactionHandler {

    private final Logger LOG = LoggerFactory.getLogger(TransactionHandler.class);

    private final PersistenceService _persistenceService;

    @Autowired
    public TransactionHandler(PersistenceService persistenceService) {
        _persistenceService = persistenceService;
    }

    public <K> K executeInTransaction(Callable<K> callable) {
        // Is a transaction opened before?
        if (_persistenceService.isTransactionActive()) {
            // We are running with a transaction that was opened before. Just use this and don't commit/rollback
            try {
                LOG.debug("using current opened transaction");
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

    public <T extends Base> T reload(T entity) {
        return _persistenceService.reload(entity);
    }

    public PersistenceService getPersistenceService() {
        return _persistenceService;
    }
}

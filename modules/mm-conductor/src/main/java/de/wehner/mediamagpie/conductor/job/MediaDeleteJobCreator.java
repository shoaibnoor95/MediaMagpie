package de.wehner.mediamagpie.conductor.job;

import java.net.URI;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import de.wehner.mediamagpie.common.fslayer.IFSLayer;
import de.wehner.mediamagpie.common.persistence.entity.JobExecution;
import de.wehner.mediamagpie.common.persistence.entity.MediaDeleteJobExecution;
import de.wehner.mediamagpie.conductor.performingjob.MediaDeleteJob;
import de.wehner.mediamagpie.conductor.persistence.PersistenceService;
import de.wehner.mediamagpie.conductor.persistence.TransactionHandler;
import de.wehner.mediamagpie.conductor.persistence.dao.MediaDao;

@Component
public class MediaDeleteJobCreator extends TransactionalJobCreator<MediaDeleteJob> {

    private final MediaDao _mediaDao;
    private final IFSLayer _fsLayer;

    @Autowired
    public MediaDeleteJobCreator(MediaDao mediaDao, IFSLayer fsLayer, TransactionHandler transactionHandler, PersistenceService persistenceService) {
        super(transactionHandler, persistenceService);
        _mediaDao = mediaDao;
        _fsLayer = fsLayer;
    }

    @Override
    protected MediaDeleteJob createInTransaction(JobExecution execution) throws Exception {
        MediaDeleteJobExecution mediaDeleteJobExecution = (MediaDeleteJobExecution) execution;
        long mediaId = mediaDeleteJobExecution.getMediaId();
        URI uri = new URI(_mediaDao.getById(mediaId).getUri());
        return new MediaDeleteJob(_mediaDao, mediaId, uri, _fsLayer);
    }

    @Override
    public Class<? extends JobExecution> getJobExecutionClass() {
        return MediaDeleteJobExecution.class;
    }
}

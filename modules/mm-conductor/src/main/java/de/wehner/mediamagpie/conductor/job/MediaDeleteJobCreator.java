package de.wehner.mediamagpie.conductor.job;

import java.net.URI;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import de.wehner.mediamagpie.conductor.performingjob.MediaDeleteJob;
import de.wehner.mediamagpie.persistence.dao.MediaDao;
import de.wehner.mediamagpie.persistence.dao.TransactionHandler;
import de.wehner.mediamagpie.persistence.entity.JobExecution;
import de.wehner.mediamagpie.persistence.entity.MediaDeleteJobExecution;

@Component
public class MediaDeleteJobCreator extends TransactionalJobCreator<MediaDeleteJob> {

    private final MediaDao _mediaDao;

    @Autowired
    public MediaDeleteJobCreator(MediaDao mediaDao, TransactionHandler transactionHandler) {
        super(transactionHandler);
        _mediaDao = mediaDao;
    }

    @Override
    protected MediaDeleteJob createInTransaction(JobExecution execution) throws Exception {
        MediaDeleteJobExecution mediaDeleteJobExecution = (MediaDeleteJobExecution) execution;
        long mediaId = mediaDeleteJobExecution.getMediaId();
        URI uri = new URI(_mediaDao.getById(mediaId).getUri());
        return new MediaDeleteJob(_mediaDao, mediaId, uri);
    }

    @Override
    public Class<? extends JobExecution> getJobExecutionClass() {
        return MediaDeleteJobExecution.class;
    }
}

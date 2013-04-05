package de.wehner.mediamagpie.conductor.job;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import de.wehner.mediamagpie.common.persistence.dao.MediaDao;
import de.wehner.mediamagpie.common.persistence.entity.ImageResizeJobExecution;
import de.wehner.mediamagpie.common.persistence.entity.JobExecution;
import de.wehner.mediamagpie.common.persistence.entity.Media;
import de.wehner.mediamagpie.conductor.performingjob.ImageResizeJob;
import de.wehner.mediamagpie.conductor.persistence.dao.ThumbImageDao;
import de.wehner.mediamagpie.persistence.PersistenceService;
import de.wehner.mediamagpie.persistence.TransactionHandler;


@Component
public class ImageResizeJobCreator extends TransactionalJobCreator<ImageResizeJob> {

    private final MediaDao _mediaDao;
    private final ThumbImageDao _thumbImageDao;

    @Autowired
    public ImageResizeJobCreator(MediaDao mediaDao, ThumbImageDao thumbImageDao, TransactionHandler transactionHandler, PersistenceService persistenceService) {
        super(transactionHandler, persistenceService);
        _mediaDao = mediaDao;
        _thumbImageDao = thumbImageDao;
    }

    @Override
    protected ImageResizeJob createInTransaction(JobExecution execution) {
        ImageResizeJobExecution resizeImageExecution = (ImageResizeJobExecution) execution;
        Media media = resizeImageExecution.getMedia();
        return new ImageResizeJob(_mediaDao, _thumbImageDao, media.getFileFromUri(), media.getId(), resizeImageExecution.getLabel(), media.getOrientation());
    }

    @Override
    public Class<? extends JobExecution> getJobExecutionClass() {
        return ImageResizeJobExecution.class;
    }
}

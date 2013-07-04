package de.wehner.mediamagpie.conductor.job;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import de.wehner.mediamagpie.conductor.performingjob.ImageResizeJob;
import de.wehner.mediamagpie.conductor.webapp.services.ImageService;
import de.wehner.mediamagpie.persistence.dao.MediaDao;
import de.wehner.mediamagpie.persistence.dao.PersistenceService;
import de.wehner.mediamagpie.persistence.dao.ThumbImageDao;
import de.wehner.mediamagpie.persistence.dao.TransactionHandler;
import de.wehner.mediamagpie.persistence.entity.ImageResizeJobExecution;
import de.wehner.mediamagpie.persistence.entity.JobExecution;
import de.wehner.mediamagpie.persistence.entity.Media;

@Component
public class ImageResizeJobCreator extends TransactionalJobCreator<ImageResizeJob> {

    private final MediaDao _mediaDao;
    private final ThumbImageDao _thumbImageDao;
    private final ImageService _imageService;

    @Autowired
    public ImageResizeJobCreator(MediaDao mediaDao, ThumbImageDao thumbImageDao, ImageService imageService, TransactionHandler transactionHandler,
            PersistenceService persistenceService) {
        super(transactionHandler, persistenceService);
        _mediaDao = mediaDao;
        _thumbImageDao = thumbImageDao;
        _imageService = imageService;
    }

    @Override
    protected ImageResizeJob createInTransaction(JobExecution execution) {
        ImageResizeJobExecution resizeImageExecution = (ImageResizeJobExecution) execution;
        Media media = resizeImageExecution.getMedia();
        return new ImageResizeJob(_mediaDao, _thumbImageDao, _imageService, media.getFileFromUri(), media.getId(), resizeImageExecution.getLabel(),
                media.getOrientation());
    }

    @Override
    public Class<? extends JobExecution> getJobExecutionClass() {
        return ImageResizeJobExecution.class;
    }
}

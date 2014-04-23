package de.wehner.mediamagpie.conductor.job;

import org.apache.commons.lang3.StringUtils;
import org.apache.tika.Tika;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import de.wehner.mediamagpie.conductor.performingjob.ImageResizeJob;
import de.wehner.mediamagpie.conductor.webapp.services.ImageService;
import de.wehner.mediamagpie.conductor.webapp.services.VideoService;
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
    private final VideoService _videoService;

    @Autowired
    public ImageResizeJobCreator(MediaDao mediaDao, ThumbImageDao thumbImageDao, ImageService imageService, VideoService videoService,
            TransactionHandler transactionHandler, PersistenceService persistenceService) {
        super(transactionHandler, persistenceService);
        _mediaDao = mediaDao;
        _thumbImageDao = thumbImageDao;
        _imageService = imageService;
        _videoService = videoService;
    }

    @Override
    protected ImageResizeJob createInTransaction(JobExecution execution) throws Exception {
        ImageResizeJobExecution resizeImageExecution = (ImageResizeJobExecution) execution;
        Media media = resizeImageExecution.getMedia();

        // try to find out which type of media we have to process
        String mediaType = media.getMediaType();
        if (StringUtils.isEmpty(mediaType)) {
            Tika tika = new Tika();
            mediaType = tika.detect(media.getFileFromUri()); // something like: 'image/jpeg' or 'video/quicktime' etc.
        }

        return new ImageResizeJob(_mediaDao, _thumbImageDao, _imageService, _videoService, media.getFileFromUri(), media.getId(),
                resizeImageExecution.getLabel(), media.getOrientation(), media.getMediaType().startsWith(Media.VIDEO_PREFIX));
    }

    @Override
    public Class<? extends JobExecution> getJobExecutionClass() {
        return ImageResizeJobExecution.class;
    }
}

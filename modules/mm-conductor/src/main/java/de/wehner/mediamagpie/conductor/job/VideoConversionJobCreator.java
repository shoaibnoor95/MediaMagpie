package de.wehner.mediamagpie.conductor.job;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import de.wehner.mediamagpie.conductor.performingjob.VideoConversionJob;
import de.wehner.mediamagpie.conductor.webapp.services.VideoService;
import de.wehner.mediamagpie.conductor.webapp.services.VideoService.VideoFormat;
import de.wehner.mediamagpie.persistence.dao.ConvertedVideoDao;
import de.wehner.mediamagpie.persistence.dao.MediaDao;
import de.wehner.mediamagpie.persistence.dao.TransactionHandler;
import de.wehner.mediamagpie.persistence.entity.JobExecution;
import de.wehner.mediamagpie.persistence.entity.Media;
import de.wehner.mediamagpie.persistence.entity.VideoConversionJobExecution;

@Component
public class VideoConversionJobCreator extends TransactionalJobCreator<VideoConversionJob> {

    private final static Logger LOG = LoggerFactory.getLogger(VideoConversionJobCreator.class);

    private final MediaDao _mediaDao;
    private final ConvertedVideoDao _convertedVideoDao;
    private final VideoService _videoService;

    @Autowired
    public VideoConversionJobCreator(TransactionHandler transactionHandler, MediaDao mediaDao, ConvertedVideoDao convertedVideoDao,
            VideoService videoService) {
        super(transactionHandler);
        _mediaDao = mediaDao;
        _convertedVideoDao = convertedVideoDao;
        _videoService = videoService;
    }

    @Override
    public Class<? extends JobExecution> getJobExecutionClass() {
        return VideoConversionJobExecution.class;
    }

    @Override
    protected VideoConversionJob createInTransaction(JobExecution execution) throws Exception {
        VideoConversionJobExecution jobExecution = (VideoConversionJobExecution) execution;
        Long mediaId = jobExecution.getMediaId();
        Media media = _mediaDao.getById(mediaId);

        if (media == null) {
            // this is possible in case of the media is deleted by a MediaDeleteJob before
            LOG.warn("Can not find media with id {}. Maybe the media was deleted before this job was executed.", mediaId);
            return null;
        }

        VideoService.VideoFormat destFormat = VideoFormat.valueOf(jobExecution.getDestFormat());
        return new VideoConversionJob(_mediaDao, _convertedVideoDao, _videoService, media, destFormat, jobExecution.getWidthOrHeight());
    }

}

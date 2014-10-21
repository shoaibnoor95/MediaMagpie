package de.wehner.mediamagpie.conductor.performingjob;

import java.io.File;
import java.net.URI;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.wehner.mediamagpie.conductor.webapp.services.VideoService;
import de.wehner.mediamagpie.conductor.webapp.services.VideoService.VideoFormat;
import de.wehner.mediamagpie.persistence.dao.ConvertedVideoDao;
import de.wehner.mediamagpie.persistence.dao.MediaDao;
import de.wehner.mediamagpie.persistence.entity.ConvertedVideo;
import de.wehner.mediamagpie.persistence.entity.Media;

public class VideoConversionJob extends AbstractJob {

    private static final Logger LOG = LoggerFactory.getLogger(VideoConversionJob.class);

    private final MediaDao _mediaDao;
    private final ConvertedVideoDao _convertedVideoDao;
    private final VideoService _videoService;
    private final long _mediaId;
    private final File _srcVideo;
    private final VideoFormat _destFormat;
    private final Integer _destWidth;

    public VideoConversionJob(MediaDao mediaDao, ConvertedVideoDao convertedVideoDao, VideoService videoService, Media media, VideoFormat destFormat,
            Integer destWidth) {
        super();
        _mediaDao = mediaDao;
        _convertedVideoDao = convertedVideoDao;
        _videoService = videoService;
        _mediaId = media.getId();
        _srcVideo = media.getFileFromUri();
        _destFormat = destFormat;
        _destWidth = destWidth;
    }

    @Override
    public JobCallable prepare() throws Exception {
        return new AbstractJobCallable() {

            @Override
            public URI internalCall() throws Exception {
                LOG.debug("Start video conversion job for Media {}, file {} to format '{}'.", _mediaId, _srcVideo.getName(), _destFormat);
                File destPath = getPerformingJobContext().getConvertedVideoPath();
                File destVideo = _videoService.convertVideo(_srcVideo, _destFormat, _destWidth, destPath);
                return (destVideo != null) ? destVideo.toURI() : null;
            }

            @Override
            public int getProgress() {
                return 0;
            }

            @Override
            public void cancel() throws Exception {
                LOG.info("cancel called....");
            }

            @Override
            public void handleResult(URI result) {
                Media media = _mediaDao.getById(_mediaId);
                if (media != null) {
                    String pathToResult = new File(result).getPath();
                    ConvertedVideo convertedVideo = new ConvertedVideo(media, VideoService.createLabel(_destWidth), _destFormat.getExtension(),
                            pathToResult);
                    _convertedVideoDao.makePersistent(convertedVideo);
                } else {
                    // delete the result of conversion process
                    LOG.warn("The media seems to be deleted during the conversion process. Delete the result of conversion now.");
                    new File(result).delete();
                }
            }
        };
    }

    @Override
    public String toString() {
        return ReflectionToStringBuilder.toString(this, ToStringStyle.SHORT_PREFIX_STYLE);
    }

}

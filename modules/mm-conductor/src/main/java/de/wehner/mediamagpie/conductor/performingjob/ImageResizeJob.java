package de.wehner.mediamagpie.conductor.performingjob;

import java.io.File;
import java.net.URI;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.wehner.mediamagpie.conductor.webapp.services.ImageService;
import de.wehner.mediamagpie.conductor.webapp.services.VideoService;
import de.wehner.mediamagpie.persistence.dao.MediaDao;
import de.wehner.mediamagpie.persistence.dao.ThumbImageDao;
import de.wehner.mediamagpie.persistence.entity.Media;
import de.wehner.mediamagpie.persistence.entity.Orientation;
import de.wehner.mediamagpie.persistence.entity.ThumbImage;

public class ImageResizeJob extends AbstractJob {

    private static final Logger LOG = LoggerFactory.getLogger(ImageResizeJob.class);

    private final MediaDao _mediaDao;
    private final ThumbImageDao _thumbImageDao;
    private final ImageService _imageService;
    private final VideoService _videoService;
    private final File _originImage;
    private final long _mediaId;
    private final String _widthOrHeight;
    private final Orientation _originOrientation;
    private final boolean _createImageFromVideo;

    public ImageResizeJob(MediaDao mediaDao, ThumbImageDao thumbImageDao, ImageService imageService, VideoService videoService, File originImage,
            long mediaId, String widthOrHeight, Orientation originOrientation, boolean createImageFromVideo) {
        _mediaDao = mediaDao;
        _thumbImageDao = thumbImageDao;
        _imageService = imageService;
        _videoService = videoService;
        _originImage = originImage;
        _mediaId = mediaId;
        if (widthOrHeight == null) {
            throw new IllegalArgumentException("The parameter widthOrHeight must not be null!");
        }
        _widthOrHeight = widthOrHeight;
        _originOrientation = (originOrientation != null) ? originOrientation : Orientation.UNKNOWN;
        _createImageFromVideo = createImageFromVideo;
    }

    @Override
    public JobCallable prepare() throws Exception {
        return new JobCallable() {

            @Override
            public URI call() throws Exception {
                LOG.debug("create the thumb image ...");
                File temporaryImage = null;
                if (_createImageFromVideo) {
                    LOG.debug("First extract image from video");
                    temporaryImage = _videoService.createImageFromVideo(_originImage, getPerformingJobContext().getTempMediaPath());
                }
                try {
                    int widthOrHeight = Integer.parseInt(_widthOrHeight);
                    File resizedImage = _imageService.resizeImage((temporaryImage != null) ? temporaryImage : _originImage, _mediaId,
                            getPerformingJobContext().getTempMediaPath(), widthOrHeight, widthOrHeight, _originOrientation.getNecessaryRotation());
                    return (resizedImage != null) ? resizedImage.toURI() : null;
                } catch (NumberFormatException e) {
                    return null;
                } finally {
                    if (temporaryImage != null) {
                        temporaryImage.delete();
                    }
                }
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
                String pathToImage = new File(result).getPath();
                ThumbImage thumbImage = new ThumbImage(media, "" + _widthOrHeight, pathToImage);
                _thumbImageDao.makePersistent(thumbImage);
            }
        };
    }

}

package de.wehner.mediamagpie.conductor.performingjob;

import java.io.File;
import java.net.URI;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.wehner.mediamagpie.common.fs.ApplicationFS;
import de.wehner.mediamagpie.common.persistence.entity.Media;
import de.wehner.mediamagpie.common.persistence.entity.Orientation;
import de.wehner.mediamagpie.common.persistence.entity.ThumbImage;
import de.wehner.mediamagpie.conductor.persistence.dao.MediaDao;
import de.wehner.mediamagpie.conductor.persistence.dao.ThumbImageDao;
import de.wehner.mediamagpie.conductor.webapp.services.ImageService;

public class ImageResizeJob extends AbstractJob {

    private static final Logger LOG = LoggerFactory.getLogger(ImageResizeJob.class);

    private final MediaDao _mediaDao;
    private final ThumbImageDao _thumbImageDao;
    private final File _originImage;
    private final long _mediaId;
    private final String _widthOrHeight;
    private final Orientation _originOrientation;

    public ImageResizeJob(MediaDao mediaDao, ThumbImageDao thumbImageDao, File originImage, long mediaId, String widthOrHeight,
            Orientation originOrientation) {
        _mediaDao = mediaDao;
        _thumbImageDao = thumbImageDao;
        _originImage = originImage;
        _mediaId = mediaId;
        if (widthOrHeight == null) {
            throw new IllegalArgumentException("The parameter widthOrHeight must not be null!");
        }
        _widthOrHeight = widthOrHeight;
        _originOrientation = (originOrientation != null) ? originOrientation : Orientation.UNKNOWN;
    }

    @Override
    public JobCallable prepare() throws Exception {
        return new JobCallable() {

            @Override
            public URI call() throws Exception {
                LOG.info("create the thumb image here...");
                try {
                    int widthOrHeight = Integer.parseInt(_widthOrHeight);
                    ApplicationFS applicationFS = getPerformingJobContext().getApplicationFS();
                    File resizedImage = ImageService.resizeImageInQueue(_originImage, _mediaId, applicationFS.getTempMediaPath(), widthOrHeight,
                            widthOrHeight, _originOrientation.getNecessaryRotation());
                    return (resizedImage != null) ? resizedImage.toURI() : null;
                } catch (NumberFormatException e) {
                    return null;
                }
            }

            @Override
            public int getProgress() {
                // TODO Auto-generated method stub
                return 0;
            }

            @Override
            public void cancel() throws Exception {
                // TODO Auto-generated method stub
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

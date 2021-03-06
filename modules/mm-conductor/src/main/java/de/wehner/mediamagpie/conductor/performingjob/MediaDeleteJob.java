package de.wehner.mediamagpie.conductor.performingjob;

import java.io.File;
import java.net.URI;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.wehner.mediamagpie.persistence.dao.MediaDao;
import de.wehner.mediamagpie.persistence.entity.Media;

public class MediaDeleteJob extends AbstractJob {

    // private static final long serialVersionUID = ManifestMetaData.SERIAL_VERSION_UID;
    private static final Logger LOG = LoggerFactory.getLogger(MediaDeleteJob.class);

    private final MediaDao _mediaDao;
    private final long _mediaId;
    private final URI _uriMedia;

    public MediaDeleteJob(MediaDao mediaDao, long mediaId, URI uriMedia) {
        super();
        _mediaDao = mediaDao;
        _mediaId = mediaId;
        _uriMedia = uriMedia;
    }

    @Override
    public JobCallable prepare() throws Exception {
        return new AbstractJobCallable() {

            @Override
            public URI internalCall() throws Exception {
                LOG.info("Delete media {} and file '{}'.", _mediaId, _uriMedia);
                String pathToFile = _uriMedia.getRawPath();
                if (pathToFile != null) {
                    File file = new File(pathToFile);
                    if (file.exists()) {
                        file.delete();
                    }
                } else {
                    LOG.warn("internal error. No valid file name.");
                }
                return _uriMedia;
            }

            @Override
            public int getProgress() {
                return 0;
            }

            @Override
            public void cancel() throws Exception {
            }

            @Override
            public void handleResult(URI result) {
                Media media = _mediaDao.getById(_mediaId);
                LOG.info("Delete Media entity {} with URI '{}' from database.", media.getId(), media.getUri());
                _mediaDao.makeTransient(media);
            }
        };
    }

}

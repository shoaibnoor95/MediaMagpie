package de.wehner.mediamagpie.conductor.performingjob;

import java.net.URI;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.wehner.mediamagpie.common.fslayer.IFSLayer;
import de.wehner.mediamagpie.common.fslayer.IFile;
import de.wehner.mediamagpie.common.persistence.entity.Media;
import de.wehner.mediamagpie.conductor.persistence.dao.MediaDao;

public class MediaDeleteJob extends AbstractJob {

    // private static final long serialVersionUID = ManifestMetaData.SERIAL_VERSION_UID;
    private static final Logger LOG = LoggerFactory.getLogger(ImageResizeJob.class);

    private final MediaDao _mediaDao;
    private final long _mediaId;
    private final URI _uriMedia;
    private final IFSLayer _fsLayer;

    public MediaDeleteJob(MediaDao mediaDao, long mediaId, URI uriMedia, IFSLayer fsLayer) {
        super();
        _mediaDao = mediaDao;
        _mediaId = mediaId;
        _uriMedia = uriMedia;
        _fsLayer = fsLayer;
    }

    @Override
    public JobCallable prepare() throws Exception {
        return new JobCallable() {

            @Override
            public URI call() throws Exception {
                LOG.info("Delete media file with URI '" + _uriMedia + "'...");
                String pathToFile = _uriMedia.getRawPath();
                if (pathToFile != null) {
                    IFile file = _fsLayer.createFile(pathToFile);
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
                LOG.info("Delete Media entity with URI '" + media.getUri() + "' from database.");
                _mediaDao.makeTransient(media);
            }
        };
    }

}
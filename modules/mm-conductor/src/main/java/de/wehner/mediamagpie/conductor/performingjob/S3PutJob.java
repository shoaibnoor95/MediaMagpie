package de.wehner.mediamagpie.conductor.performingjob;

import java.net.URI;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.amazonaws.auth.AWSCredentials;

import de.wehner.mediamagpie.api.MediaExport;
import de.wehner.mediamagpie.api.MediaExportRepository;
import de.wehner.mediamagpie.api.MediaExportResults;
import de.wehner.mediamagpie.aws.s3.S3MediaExportRepository;
import de.wehner.mediamagpie.common.persistence.dao.MediaDao;
import de.wehner.mediamagpie.common.persistence.entity.Media;

public class S3PutJob extends AbstractJob {

    private static final Logger LOG = LoggerFactory.getLogger(S3PutJob.class);

    private final String _user;
    private final MediaExport _mediaExport;
    protected final MediaExportRepository _s3MediaRepositiory;
    private final MediaDao _mediaDao;

    public S3PutJob(String user, AWSCredentials credentials, MediaExport mediaExport, MediaDao mediaDao) {
        super();
        _user = user;
        _mediaExport = mediaExport;
        _s3MediaRepositiory = new S3MediaExportRepository(credentials);
        _mediaDao =mediaDao;
    }

    @Override
    public JobCallable prepare() throws Exception {
        return new JobCallable() {

            @Override
            public URI call() throws Exception {
                LOG.info("try to upload media to S3 bucket ...");
                MediaExportResults exportResults = _s3MediaRepositiory.addMedia(_user, _mediaExport);
                // the uri shoud be something like 'https://s3.amazonaws.com/mediamagpie-photo/rwe/PHOTO/ID5/IMG_0154-mmcounter-2.JPG'
                return exportResults.getUri();
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
                // mark media to be sucessfully exported to S3
                Media media = _mediaDao.getById(Long.parseLong(_mediaExport.getMediaId()));
                media.setExportedToS3(true);
                LOG.info("Mark Media entity with URI '" + media.getUri() + "' to be sucessfully exported to S3.");
                _mediaDao.makePersistent(media);
            }
        };
    }

}

package de.wehner.mediamagpie.conductor.performingjob;

import java.net.URI;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.amazonaws.auth.AWSCredentials;

import de.wehner.mediamagpie.api.MediaExport;
import de.wehner.mediamagpie.api.MediaExportRepository;
import de.wehner.mediamagpie.aws.s3.S3MediaExportRepository;

public class S3PutJob extends AbstractJob {

    private static final Logger LOG = LoggerFactory.getLogger(S3PutJob.class);

    private final String _user;
    private final MediaExport _mediaExport;
    protected final MediaExportRepository _s3MediaRepositiory;

    public S3PutJob(String user, AWSCredentials credentials, MediaExport mediaExport) {
        super();
        _user = user;
        _mediaExport = mediaExport;
        _s3MediaRepositiory = new S3MediaExportRepository(credentials);
    }

    @Override
    public JobCallable prepare() throws Exception {
        return new JobCallable() {

            @Override
            public URI call() throws Exception {
                LOG.info("try to upload media to S3 bucket ...");
                _s3MediaRepositiory.addMedia(_user, _mediaExport);
                return null;
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
                LOG.info("Handle result() ...");
            }
        };
    }

}

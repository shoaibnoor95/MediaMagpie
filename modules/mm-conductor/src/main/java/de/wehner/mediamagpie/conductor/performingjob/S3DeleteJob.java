package de.wehner.mediamagpie.conductor.performingjob;

import java.net.URI;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.wehner.mediamagpie.api.MediaExportRepository;
import de.wehner.mediamagpie.aws.s3.S3MediaExportRepository;

public class S3DeleteJob extends AbstractJob {

    private static final Logger LOG = LoggerFactory.getLogger(S3DeleteJob.class);

    protected final String _bucketName;
    protected final String _exportStoragePath;
    protected final MediaExportRepository _s3MediaRepositiory;

    public S3DeleteJob(String bucketName, String exportStoragePath, S3MediaExportRepository s3MediaExportRepository) {
        super();
        _bucketName = bucketName;
        _s3MediaRepositiory = s3MediaExportRepository;
        _exportStoragePath = exportStoragePath;
    }

    @Override
    public JobCallable prepare() throws Exception {
        return new JobCallable() {

            @Override
            public URI call() throws Exception {
                LOG.debug("delete media on S3 '" + _bucketName + "/" + _exportStoragePath + "'...");
                _s3MediaRepositiory.deleteMediaStoragePath(_bucketName, _exportStoragePath);
                LOG.debug("delete media on S3 '" + _bucketName + "/" + _exportStoragePath + "'...DONE");
                return null;
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
            }
        };
    }
}

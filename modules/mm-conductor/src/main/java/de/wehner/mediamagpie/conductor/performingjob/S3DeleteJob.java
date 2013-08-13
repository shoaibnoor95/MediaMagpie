package de.wehner.mediamagpie.conductor.performingjob;

import java.net.URI;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.wehner.mediamagpie.api.MediaExportRepository;
import de.wehner.mediamagpie.aws.s3.S3MediaExportRepository;

public class S3DeleteJob extends AbstractJob {

    private static final Logger LOG = LoggerFactory.getLogger(S3DeleteJob.class);

    protected final String _bucketName;
    protected final String _exportStoragePath;
    protected final String _exportStorageMetaPath;
    protected final MediaExportRepository _s3MediaRepositiory;

    public S3DeleteJob(String bucketName, String exportStoragePath, String exportStorageMetaPath, S3MediaExportRepository s3MediaExportRepository) {
        super();
        _bucketName = bucketName;
        _s3MediaRepositiory = s3MediaExportRepository;
        _exportStoragePath = exportStoragePath;
        _exportStorageMetaPath = exportStorageMetaPath;
    }

    @Override
    public JobCallable prepare() throws Exception {
        return new JobCallable() {

            @Override
            public URI call() throws Exception {
                deleteFileOnS3(_exportStoragePath);
                if (!StringUtils.isEmpty(_exportStorageMetaPath)) {
                    deleteFileOnS3(_exportStorageMetaPath);
                }
                return null;
            }

            private void deleteFileOnS3(String pathInBucket) {
                LOG.debug("delete media on S3 '{}/{}'...", _bucketName, pathInBucket);
                _s3MediaRepositiory.deleteMediaStoragePath(_bucketName, pathInBucket);
                LOG.debug("delete media on S3 '{}/{}'...DONE", _bucketName, pathInBucket);
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

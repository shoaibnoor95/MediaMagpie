package de.wehner.mediamagpie.aws.s3;

import java.io.InputStream;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.AmazonS3Exception;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.Owner;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.PutObjectResult;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.transfer.TransferManager;
import com.amazonaws.services.s3.transfer.Upload;

import de.wehner.mediamagpie.aws.s3.in.S3ObjectIterator;
import de.wehner.mediamagpie.core.util.Pair;

/**
 * This class provides some convenient methods to use the aws API.
 * 
 * @author ralfwehner
 * 
 */
public class S3ClientFacade {

    private static final Logger LOG = LoggerFactory.getLogger(S3ClientFacade.class);

    public static final String HASH_OF_DATA = "hash-of-data";

    public static class FileNameInfo {

        private final String _nameObject;

        private String _nameMetadata;

        public FileNameInfo(String nameObject, String nameMetadata) {
            super();
            _nameObject = nameObject;
            _nameMetadata = nameMetadata;
        }

        public String getNameObject() {
            return _nameObject;
        }

        public String getNameMetadata() {
            return _nameMetadata;
        }

        public void setNameMetadata(String nameMetadata) {
            _nameMetadata = nameMetadata;
        }
    }

    private final AmazonS3 _s3;

    public S3ClientFacade(AWSCredentials credentials) {
        _s3 = new AmazonS3Client(credentials);
    }

    AmazonS3 getS3() {
        return _s3;
    }

    public S3Object getObjectIfExists(String bucketName, String fileName) {
        S3Object object = null;
        try {
            object = _s3.getObject(new GetObjectRequest(bucketName, fileName));
        } catch (AmazonServiceException e) {
        }
        return object;
    }

    public void createBucketIfNotExists(String bucketName) {
        if (!_s3.doesBucketExist(bucketName)) {
            _s3.createBucket(bucketName);
            LOG.info("Created new bucket '" + bucketName + "'.");
        }
    }

    public PutObjectResult putObject(String existingBucketName, String keyName, InputStream is, ObjectMetadata metadata) {
        // metadata.setContentEncoding("UTF8");
        final PutObjectRequest putObjectRequest = new PutObjectRequest(existingBucketName, keyName, is, metadata);
        PutObjectResult putObject = _s3.putObject(putObjectRequest);
        LOG.debug("uploaded object '" + existingBucketName + "'#'" + keyName + "' to S3");
        return putObject;
    }

    public void transferMultipart(String existingBucketName, String keyName, InputStream is, ObjectMetadata metadata) {

        final PutObjectRequest putObjectRequest = new PutObjectRequest(existingBucketName, keyName, is, metadata);

        TransferManager tm = new TransferManager(_s3);

        // TransferManager processes all transfers asynchronously, so this call will return immediately.
        Upload upload = tm.upload(putObjectRequest);

        try {
            // Or you can block and wait for the upload to finish
            upload.waitForCompletion();
            LOG.debug("upload object '" + existingBucketName + "', '" + keyName + "' with multipart upload to S3");
        } catch (AmazonClientException amazonClientException) {
            LOG.warn("Unable to upload file, upload was aborted.");
            amazonClientException.printStackTrace();
            abortTransferMultipart(existingBucketName);
        } catch (InterruptedException e) {
            abortTransferMultipart(existingBucketName);
        }
    }

    public void abortTransferMultipart(String existingBucketName) {
        TransferManager tm = new TransferManager(_s3);
        int sevenDays = 1000 * 60 * 60 * 24 * 7;
        Date oneWeekAgo = new Date(System.currentTimeMillis() - sevenDays);

        try {
            tm.abortMultipartUploads(existingBucketName, oneWeekAgo);
            LOG.info("abort multipart transfer of bucket '" + existingBucketName + "'");
        } catch (AmazonClientException amazonClientException) {
            System.out.println("Unable to upload file, upload was aborted.");
            amazonClientException.printStackTrace();
        }
    }

    S3ObjectIterator iterator(String bucketName, String prefix) {
        return new S3ObjectIterator(_s3, bucketName, prefix);
    }

    public Pair<Boolean, String> testConnection() {
        try {
            Owner s3AccountOwner = _s3.getS3AccountOwner();
            LOG.debug("found owner: " + s3AccountOwner);
            return new Pair<Boolean, String>((s3AccountOwner != null), null);
        } catch (AmazonS3Exception e) {
            LOG.debug("Can not connecto to S3.", e);
            return new Pair<Boolean, String>(false, e.getMessage());
        }
    }
}

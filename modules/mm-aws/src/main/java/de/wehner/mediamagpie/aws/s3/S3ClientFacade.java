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
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.PutObjectResult;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.transfer.TransferManager;
import com.amazonaws.services.s3.transfer.Upload;

public class S3ClientFacade {

    private static final Logger LOG = LoggerFactory.getLogger(S3ClientFacade.class);

    public static final String HASH_OF_DATA = "hash-of-data";

    private final AmazonS3 _s3;

    // an alternative:
    // private final RestS3Service;

    public S3ClientFacade(AWSCredentials credentials) {
        _s3 = new AmazonS3Client(credentials);
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
        }
    }

    public PutObjectResult putObject(String existingBucketName, String keyName, InputStream is, ObjectMetadata metadata) {
        final PutObjectRequest putObjectRequest = new PutObjectRequest(existingBucketName, keyName, is, metadata);
        PutObjectResult putObject = _s3.putObject(putObjectRequest);
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
        } catch (AmazonClientException amazonClientException) {
            System.out.println("Unable to upload file, upload was aborted.");
            amazonClientException.printStackTrace();
        }
    }
}

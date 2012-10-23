package de.wehner.mediamagpie.aws.s3;

import java.util.Iterator;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectResult;
import com.amazonaws.services.s3.model.S3Object;

import de.wehner.mediamagpie.api.ExportStatus;
import de.wehner.mediamagpie.api.MediaExport;

public class S3MediaRepository {

    private static final Logger LOG = LoggerFactory.getLogger(S3MediaRepository.class);

    public static final String HASH_OF_DATA = "hash-of-data";

    private final S3ClientFacade _s3Facade;

    public S3MediaRepository(AWSCredentials credentials) {
        this(new S3ClientFacade(credentials));
    }

    public S3MediaRepository(S3ClientFacade s3facde) {
        _s3Facade = s3facde;
    }

    /** export functionality */

    public void addMedia(String user, MediaExport mediaExport) {
        // build file name and bucket name
        String fileName = buildFileName(user, mediaExport);
        String bucketName = getBucketName(mediaExport);

        _s3Facade.createBucketIfNotExists(bucketName);

        // test file already exists in bucket
        S3Object object = _s3Facade.getObjectIfExists(bucketName, fileName);

        String hashValueInS3 = null;
        if (object != null) {
            ObjectMetadata metadata = object.getObjectMetadata();
            // Map<String, Object> rawMetadata = metadata.getRawMetadata();
            Map<String, String> userMetadata = metadata.getUserMetadata();

            // TODO rwe: remove logging?
            LOG.info("Content-Type: " + object.getObjectMetadata().getContentType());
            // displayTextInputStream(object.getObjectContent());
            hashValueInS3 = userMetadata.get(HASH_OF_DATA);
        }
        // does we need to overwrite?
        boolean write = ((hashValueInS3 == null) || hashValueInS3 != mediaExport.getHashValue());

        if (!write) {
            LOG.info("Media with key '" + fileName + "' will not uploaded to S3, because this object already exists.");
            return;
        }

        // upload file to s3
        PutObjectResult putObject = _s3Facade.putObject(bucketName, fileName, mediaExport.getInputStream(), createObjectMetadata(mediaExport));
        LOG.info("Upload of media with key '" + fileName + "' sucessfully finished. Got version: " + putObject.getVersionId());
    }

    public ExportStatus getStatus(MediaExport mediaExport) {
        return ExportStatus.UNDEFINED;
    }

    /** import functionality */

    public Iterator<MediaExport> iteratorPhotos() {
        return null;
    }

    public Iterator<MediaExport> iteratorVideos() {
        return null;
    }

    private ObjectMetadata createObjectMetadata(MediaExport mediaExport) {
        ObjectMetadata objectMetadata = new ObjectMetadata();
        if (!StringUtils.isEmpty(mediaExport.getMimeType())) {
            objectMetadata.setContentType(mediaExport.getMimeType());
        }
        objectMetadata.addUserMetadata(HASH_OF_DATA, mediaExport.getHashValue());
        if (mediaExport.getLength() != null) {
            objectMetadata.setContentLength(mediaExport.getLength());
        }
        return objectMetadata;
    }

    private String getBucketName(MediaExport mediaExport) {
        switch (mediaExport.getType()) {
        case UNKNOWN:
            return "mediamagpie";
        case PHOTO:
            return "mediamagpiePhoto";
        case STREAM:
            return "mediamagpieStream";
        }
        throw new RuntimeException("Undefined media type: " + mediaExport.getType());
    }

    /**
     * Creates a specific file name for the media in form of: <code>&lt;user&gt;_ID&lt;media id&gt;_&lt;original file name&gt;</code><br/>
     * EG: <code>rwe_ID17_IMG_1795.jpg</code>
     * 
     * @param user
     * @param mediaExport
     * @return
     */
    private String buildFileName(String user, MediaExport mediaExport) {
        StringBuilder builder = new StringBuilder();
        builder.append(user).append('_');
        builder.append(mediaExport.getType()).append('_');
        builder.append("ID").append(mediaExport.getMediaId()).append('_');
        builder.append(mediaExport.getOriginalFileName());
        return builder.toString();
    }
}

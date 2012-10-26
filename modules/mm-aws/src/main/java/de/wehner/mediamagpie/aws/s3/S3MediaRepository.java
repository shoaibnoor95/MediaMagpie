package de.wehner.mediamagpie.aws.s3;

import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectResult;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectSummary;
import com.sun.xml.internal.messaging.saaj.packaging.mime.internet.MimeUtility;

import de.wehner.mediamagpie.api.ExportStatus;
import de.wehner.mediamagpie.api.MediaExport;
import de.wehner.mediamagpie.api.MediaType;
import de.wehner.mediamagpie.common.util.ExceptionUtil;
import de.wehner.mediamagpie.common.util.MMTransformIterator;

public class S3MediaRepository {

    private static final Logger LOG = LoggerFactory.getLogger(S3MediaRepository.class);

    public static final String KEY_DELIMITER = "/";

    public static final String META_HASH_OF_DATA = "hash-of-data";
    public static final String META_NAME = "name";
    public static final String META_DESCRIPTION = "description";
    public static final String META_CREATION_DATE = "creation-date";
    public static final String META_ORIGINAL_FILE_NAME = "original-file-name";
    public static final String META_MEDIA_TYPE = "media-type";
    public static final String META_TAGS = "media-tags";

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
        String fileName = getKeyName(user, mediaExport);
        String bucketName = getBucketName(mediaExport.getType());

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
            hashValueInS3 = userMetadata.get(META_HASH_OF_DATA);
        }
        // does we need to overwrite?
        boolean write = ((hashValueInS3 == null) || !hashValueInS3.equals(mediaExport.getHashValue()));

        if (!write) {
            LOG.debug("Media with key '" + fileName + "' will not uploaded to S3, because this object already exists.");
            return;
        }

        // upload file to s3
        ObjectMetadata objectMetadata = createObjectMetadata(mediaExport);
        PutObjectResult putObject = _s3Facade.putObject(bucketName, fileName, mediaExport.getInputStream(), objectMetadata);
    }

    public ExportStatus getStatus(MediaExport mediaExport) {
        return ExportStatus.UNDEFINED;
    }

    /** import functionality */

    public Iterator<MediaExport> iteratorPhotos(String user) {
        String prefix = getKeyNamePrefixForUserAndType(user, MediaType.PHOTO).toString();

        // get iterator for S3ObjectSummary objects
        S3ObjectIterator iterator = _s3Facade.iterator(getBucketName(MediaType.PHOTO), prefix);

        // create a transformer (a kind of factory) which loads a concreate object from S3 and creates a MediaExport object
        S3ObjectSummary2MediaExportTransformer transformer = new S3ObjectSummary2MediaExportTransformer(_s3Facade.getS3());

        // wrap transformer within a new iterator
        return new MMTransformIterator<S3ObjectSummary, MediaExport>(iterator, transformer);
    }

    public Iterator<MediaExport> iteratorVideos() {
        return null;
    }

    private ObjectMetadata createObjectMetadata(MediaExport mediaExport) {
        ObjectMetadata objectMetadata = new ObjectMetadata();
        // mime type
        if (!StringUtils.isEmpty(mediaExport.getMimeType())) {
            objectMetadata.setContentType(mediaExport.getMimeType());
        }
        // name
        addStringIntoUserMetadata(META_NAME, mediaExport.getName(), objectMetadata);
        // description (TODO rwe: compress?)
        addStringIntoUserMetadata(META_DESCRIPTION, mediaExport.getDescription(), objectMetadata);
        // creation date
        addStringIntoUserMetadata(META_CREATION_DATE, mediaExport.getCreationDate(), objectMetadata);
        // hash
        objectMetadata.addUserMetadata(META_HASH_OF_DATA, mediaExport.getHashValue());
        // length
        if (mediaExport.getLength() != null) {
            objectMetadata.setContentLength(mediaExport.getLength());
        }
        // original file name
        addStringIntoUserMetadata(META_ORIGINAL_FILE_NAME, mediaExport.getOriginalFileName(), objectMetadata);
        // media type
        objectMetadata.addUserMetadata(META_MEDIA_TYPE, mediaExport.getType().name());
        // tags
        addStringIntoUserMetadata(META_TAGS, mediaExport.getTags(), objectMetadata);

        return objectMetadata;
    }

    @SuppressWarnings("unchecked")
    private void addStringIntoUserMetadata(String key, Object value, ObjectMetadata objectMetadata) {
        String strValue = null;
        if (value instanceof String) {
            strValue = (String) value;
        } else if (value instanceof Date) {
            strValue = ((Date) value).getTime() + "";
        } else if (value instanceof List<?>) {
            strValue = StringUtils.join(((List<String>)value), ',');
        }
        if (!StringUtils.isEmpty(strValue)) {
            String encodedValue = strValue;
            try {
                encodedValue = MimeUtility.encodeText(strValue);
            } catch (UnsupportedEncodingException e) {
                ExceptionUtil.convertToRuntimeException(e);
            }
            objectMetadata.addUserMetadata(key, encodedValue);
        }
    }

    private String getBucketName(MediaType type) {
        switch (type) {
        case PHOTO:
            return "mediamagpie-photo";
        case STREAM:
            return "mediamagpie-stream";
        default:
            throw new RuntimeException("Undefined media type: " + type);
        }
    }

    /**
     * Creates a specific file name for the media in form of: <code>&lt;user&gt;_ID&lt;media id&gt;_&lt;original file name&gt;</code><br/>
     * EG: <code>rwe_ID17_IMG_1795.jpg</code>
     * 
     * @param user
     * @param mediaExport
     * @return
     */
    private String getKeyName(String user, MediaExport mediaExport) {
        StringBuilder builder = getKeyNamePrefixForUserAndType(user, mediaExport.getType());
        builder.append("ID").append(mediaExport.getMediaId()).append(KEY_DELIMITER);
        if (!StringUtils.isEmpty(mediaExport.getOriginalFileName())) {
            builder.append(mediaExport.getOriginalFileName());
        } else {
            builder.append("media.data");
        }
        return builder.toString();
    }

    private StringBuilder getKeyNamePrefixForUserAndType(String user, MediaType type) {
        StringBuilder builder = new StringBuilder();
        builder.append(user).append(KEY_DELIMITER);
        builder.append(type).append(KEY_DELIMITER);
        return builder;
    }
}

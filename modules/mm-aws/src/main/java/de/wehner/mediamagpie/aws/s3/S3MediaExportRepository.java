package de.wehner.mediamagpie.aws.s3;

import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Iterator;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectResult;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectSummary;

import de.wehner.mediamagpie.api.MediaExport;
import de.wehner.mediamagpie.api.MediaExportRepository;
import de.wehner.mediamagpie.api.MediaExportResult;
import de.wehner.mediamagpie.api.MediaExportResult.ExportStatus;
import de.wehner.mediamagpie.api.MediaType;
import de.wehner.mediamagpie.common.util.ExceptionUtil;
import de.wehner.mediamagpie.common.util.MMTransformIterator;

public class S3MediaExportRepository implements MediaExportRepository {

    private static final Logger LOG = LoggerFactory.getLogger(S3MediaExportRepository.class);

    public static final String KEY_DELIMITER = "/";

    public static final String META_HASH_OF_DATA = "hash-of-data";
    public static final String META_NAME = "name";
    public static final String META_DESCRIPTION = "description";
    public static final String META_CREATION_DATE = "creation-date";
    public static final String META_ORIGINAL_FILE_NAME = "original-file-name";
    public static final String META_MEDIA_TYPE = "media-type";
    public static final String META_TAGS = "media-tags";

    private final S3ClientFacade _s3Facade;

    public S3MediaExportRepository(AWSCredentials credentials) {
        this(new S3ClientFacade(credentials));
    }

    public S3MediaExportRepository(S3ClientFacade s3facde) {
        _s3Facade = s3facde;
    }

    /*
     * (non-Javadoc)
     * 
     * @see de.wehner.mediamagpie.aws.s3.MediaExportRepository#addMedia(java.lang.String, de.wehner.mediamagpie.api.MediaExport)
     */

    @Override
    public MediaExportResult addMedia(String user, MediaExport mediaExport) {
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
            return createMediaExportResult(MediaExportResult.ExportStatus.ALREADY_EXPORTED, bucketName, fileName);
        }

        // upload file to s3
        MediaExport2S3ObjectMetadataTransformer metadataTransformer = new MediaExport2S3ObjectMetadataTransformer();
        ObjectMetadata objectMetadata = metadataTransformer.transform(mediaExport);
        PutObjectResult putObjectResult = _s3Facade.putObject(bucketName, fileName, mediaExport.getInputStream(), objectMetadata);
        LOG.debug("eTag: " + putObjectResult.getETag());
        return createMediaExportResult(MediaExportResult.ExportStatus.SUCESS, bucketName, fileName);
    }

    private MediaExportResult createMediaExportResult(ExportStatus exportStatus, String bucketName, String fileName) {
        try {
            URL url = new URL("https", "s3.amazonaws.com", bucketName + "/" + fileName);
            return new MediaExportResult(exportStatus, url.toURI());
        } catch (MalformedURLException e) {
            ExceptionUtil.convertToRuntimeException(e);
        } catch (URISyntaxException e) {
            ExceptionUtil.convertToRuntimeException(e);
        }
        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see de.wehner.mediamagpie.aws.s3.MediaExportRepository#iteratorPhotos(java.lang.String)
     */

    @Override
    public Iterator<MediaExport> iteratorPhotos(String user) {
        String prefix = getKeyNamePrefixForUserAndType(user, MediaType.PHOTO).toString();

        // get iterator for S3ObjectSummary objects
        S3ObjectIterator iterator = _s3Facade.iterator(getBucketName(MediaType.PHOTO), prefix);

        // create a transformer (a kind of factory) which loads a concreate object from S3 and creates a MediaExport object
        S3ObjectSummary2MediaExportTransformer transformer = new S3ObjectSummary2MediaExportTransformer(_s3Facade.getS3());

        // wrap transformer within a new iterator
        return new MMTransformIterator<S3ObjectSummary, MediaExport>(iterator, transformer);
    }

    /*
     * (non-Javadoc)
     * 
     * @see de.wehner.mediamagpie.aws.s3.MediaExportRepository#iteratorVideos()
     */
    @Override
    public Iterator<MediaExport> iteratorVideos() {
        return null;
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

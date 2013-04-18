package de.wehner.mediamagpie.aws.s3;

import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
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

import de.wehner.mediamagpie.api.MediaExport;
import de.wehner.mediamagpie.api.MediaExportMetadata;
import de.wehner.mediamagpie.api.MediaExportRepository;
import de.wehner.mediamagpie.api.MediaExportResult;
import de.wehner.mediamagpie.api.MediaExportResult.ExportStatus;
import de.wehner.mediamagpie.api.MediaExportResults;
import de.wehner.mediamagpie.api.MediaType;
import de.wehner.mediamagpie.aws.s3.S3ClientFacade.FileNameInfo;
import de.wehner.mediamagpie.aws.s3.in.S3ObjectIterator;
import de.wehner.mediamagpie.aws.s3.in.S3ObjectTuple2MediaExportTransformer;
import de.wehner.mediamagpie.aws.s3.in.S3ObjectTupleIterator;
import de.wehner.mediamagpie.aws.s3.out.MediaExport2S3ObjectMetadataTransformer;
import de.wehner.mediamagpie.aws.s3.out.MediaExportMetaData2S3ObjectMetadataTransformer;
import de.wehner.mediamagpie.core.util.DigestUtil;
import de.wehner.mediamagpie.core.util.ExceptionUtil;

public class S3MediaExportRepository implements MediaExportRepository {

    public static final String METADATA_FILE_EXTENSION = ".METADATA";

    private static final Logger LOG = LoggerFactory.getLogger(S3MediaExportRepository.class);

    public static final String KEY_DELIMITER = "/";

    public static final String META_MEDIA_ID = "id";
    public static final String META_HASH_OF_DATA = "hash-of-data";
    public static final String META_NAME = "name";
    public static final String META_CREATION_DATE = "creation-date";
    /**
     * @deprecated: use a separate object now
     */
    public static final String META_ORIGINAL_FILE_NAME = "original-file-name";
    public static final String META_MEDIA_TYPE = "media-type";

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
    public MediaExportResults addMedia(String user, MediaExport mediaExport) {
        // build file name and bucket name
        FileNameInfo fileNameInfo = getKeyNames(user, mediaExport);
        String bucketName = getBucketName(mediaExport.getType());
        List<MediaExportResult> result = new ArrayList<MediaExportResult>();

        _s3Facade.createBucketIfNotExists(bucketName);

        // 1) Does we need to upload the media file?
        if (!isUploadNecessary(bucketName, fileNameInfo.getNameObject(), mediaExport.getHashValue())) {
            LOG.debug("Media with key '" + fileNameInfo + "' will not uploaded to S3, because this object already exists and doesn't changed.");
            result.add(createMediaExportResult(MediaExportResult.ExportStatus.ALREADY_EXPORTED, bucketName, fileNameInfo.getNameObject()));
        } else {
            // upload the raw data and its meta data to s3
            MediaExport2S3ObjectMetadataTransformer metadataTransformer = new MediaExport2S3ObjectMetadataTransformer();
            ObjectMetadata objectMetadata = metadataTransformer.transform(mediaExport);
            PutObjectResult putObjectResult = _s3Facade.putObject(bucketName, fileNameInfo.getNameObject(), mediaExport.getInputStream(), objectMetadata);
            LOG.debug("eTag for '" + fileNameInfo.getNameObject() + "': " + putObjectResult.getETag());
            result.add(createMediaExportResult(MediaExportResult.ExportStatus.SUCCESS, bucketName, fileNameInfo.getNameObject()));
        }

        // 2) Does we need to upload the media metadata file?
        MediaExportMetadata mediaExportMetaData = mediaExport.createMediaExportMetadata();
        InputStream is = mediaExportMetaData.createInputStream();
        String hashValueMetaData = DigestUtil.computeSha1AsHexString(is);
        if (!isUploadNecessary(bucketName, fileNameInfo.getNameMetadata(), hashValueMetaData)) {
            LOG.debug("Metadata with key '" + fileNameInfo + "' will not uploaded to S3, because this object already exists and doesn't changed.");
            result.add(createMediaExportResult(MediaExportResult.ExportStatus.ALREADY_EXPORTED, bucketName, fileNameInfo.getNameMetadata()));
        } else {
            // upload media's meta data file to s3
            MediaExportMetaData2S3ObjectMetadataTransformer metadataTransformer = new MediaExportMetaData2S3ObjectMetadataTransformer();
            ObjectMetadata objectMetadata = metadataTransformer.transform(mediaExportMetaData);
            PutObjectResult putObjectResult = _s3Facade.putObject(bucketName, fileNameInfo.getNameMetadata(), mediaExportMetaData.createInputStream(),
                    objectMetadata);
            LOG.debug("eTag for '" + fileNameInfo.getNameMetadata() + "': " + putObjectResult.getETag());
            result.add(createMediaExportResult(MediaExportResult.ExportStatus.SUCCESS, bucketName, fileNameInfo.getNameMetadata()));
        }

        return new MediaExportResults(result);
    }

    private boolean isUploadNecessary(String bucketName, String fileName, String hashValue) {
        // a) test object file already exists in bucket
        S3Object object = _s3Facade.getObjectIfExists(bucketName, fileName);

        String hashValueInS3 = null;
        if (object != null) {
            ObjectMetadata metadata = object.getObjectMetadata();
            // Map<String, Object> rawMetadata = metadata.getRawMetadata();
            Map<String, String> userMetadata = metadata.getUserMetadata();

            LOG.debug("Content-Type for '" + fileName + "': " + object.getObjectMetadata().getContentType());
            hashValueInS3 = userMetadata.get(META_HASH_OF_DATA);
        }
        // does we need to overwrite?
        return ((hashValueInS3 == null) || !hashValueInS3.equals(hashValue));
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

        // wrap iterator to retrieve a tuple of data and meta data S3ObjectSummary objects
        final S3ObjectTupleIterator s3ObjectTupleIterator = new S3ObjectTupleIterator(iterator);

        // create a transformer (a kind of factory) which loads a concrete object from S3 and creates a MediaExport object
        final S3ObjectTuple2MediaExportTransformer transformer = new S3ObjectTuple2MediaExportTransformer(_s3Facade.getS3());

        Iterator<MediaExport> iterator2 = new Iterator<MediaExport>() {

            @Override
            public boolean hasNext() {
                return s3ObjectTupleIterator.hasNext();
            }

            @Override
            public MediaExport next() {
                S3ObjectTuple s3ObjectTuple = s3ObjectTupleIterator.next();
                return transformer.transform(s3ObjectTuple);
            }

            @Override
            public void remove() {
                throw new RuntimeException("not supported operation");
            }
        };

        return iterator2;
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
     * Creates a specific file name for the media object and its metadata in form of:
     * <ul>
     * <li> <code>&lt;user.getName()&gt;/&lt;PHOTO or MEDIA&gt;/ID&lt;media id&gt;/&lt;original file name&gt;</code><br/>
     * EG: <code>rwe/PHOTO/ID17/IMG_1795.JPG</code></li>
     * <li> <code>&lt;user.getName()&gt;/&lt;PHOTO or MEDIA&gt;/ID&lt;media id&gt;/&lt;original file name&gt;.METADATA</code><br/>
     * EG: <code>rwe/PHOTO/ID17/IMG_1795.JPG.METADATA</code></li>
     * </ul>
     * 
     * @param user
     * @param mediaExport
     * @return an object containing both names
     */
    private FileNameInfo getKeyNames(String user, MediaExport mediaExport) {
        StringBuilder builder = getKeyNamePrefixForUserAndType(user, mediaExport.getType());
        builder.append("ID").append(mediaExport.getMediaId()).append(KEY_DELIMITER);
        if (!StringUtils.isEmpty(mediaExport.getOriginalFileName())) {
            builder.append(mediaExport.getOriginalFileName());
        } else {
            builder.append("media.data");
        }
        return new FileNameInfo(builder.toString(), builder.append(METADATA_FILE_EXTENSION).toString());
    }

    private StringBuilder getKeyNamePrefixForUserAndType(String user, MediaType type) {
        StringBuilder builder = new StringBuilder();
        builder.append(user).append(KEY_DELIMITER);
        builder.append(type).append(KEY_DELIMITER);
        return builder;
    }
}

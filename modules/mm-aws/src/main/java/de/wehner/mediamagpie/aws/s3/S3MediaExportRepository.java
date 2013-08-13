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

import de.wehner.mediamagpie.api.FileNameInfo;
import de.wehner.mediamagpie.api.MediaExport;
import de.wehner.mediamagpie.api.MediaExportMetadata;
import de.wehner.mediamagpie.api.MediaExportRepository;
import de.wehner.mediamagpie.api.MediaExportResult;
import de.wehner.mediamagpie.api.MediaExportResult.ExportStatus;
import de.wehner.mediamagpie.api.MediaExportResults;
import de.wehner.mediamagpie.api.MediaType;
import de.wehner.mediamagpie.api.util.DigestUtil;
import de.wehner.mediamagpie.aws.s3.in.S3ObjectIterator;
import de.wehner.mediamagpie.aws.s3.in.S3ObjectTuple2MediaExportTransformer;
import de.wehner.mediamagpie.aws.s3.in.S3ObjectTupleIterator;
import de.wehner.mediamagpie.aws.s3.out.MediaExport2S3ObjectMetadataTransformer;
import de.wehner.mediamagpie.aws.s3.out.MediaExportMetaData2S3ObjectMetadataTransformer;
import de.wehner.mediamagpie.core.util.ExceptionUtil;

public class S3MediaExportRepository implements MediaExportRepository {

    public static final String METADATA_FILE_EXTENSION = ".METADATA";

    private static final Logger LOG = LoggerFactory.getLogger(S3MediaExportRepository.class);

    public static final String KEY_DELIMITER = "/";

    public static final String META_MEDIA_ID = "media-id";
    public static final String META_HASH_OF_DATA = "hash-of-data";
    public static final String META_CREATION_DATE = "creation-date";
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
     * @see de.wehner.mediamagpie.api.MediaExportRepository#addMedia(java.lang.String, de.wehner.mediamagpie.api.MediaExport)
     */
    @Override
    public MediaExportResults addMedia(String user, MediaExport mediaExport) {
        // build file name and bucket name
        FileNameInfo fileNameInfo = getKeyNames(user, mediaExport.getType(), mediaExport.getHashValue(), mediaExport.getOriginalFileName());
        String bucketName = buildBucketTypeName(mediaExport.getType());
        List<MediaExportResult> result = new ArrayList<MediaExportResult>();

        _s3Facade.createBucketIfNotExists(bucketName);

        // 1) Does we need to upload the media file?
        if (!isUploadNecessary(bucketName, fileNameInfo.getNameObject(), mediaExport.getHashValue())) {
            LOG.trace("Media with key '" + fileNameInfo + "' will not uploaded to S3, because this object already exists and doesn't changed.");
            result.add(createMediaExportResult(MediaExportResult.ExportStatus.ALREADY_EXPORTED, bucketName, fileNameInfo.getNameObject()));
        } else {
            // upload the raw data and its meta data to s3
            MediaExport2S3ObjectMetadataTransformer metadataTransformer = new MediaExport2S3ObjectMetadataTransformer();
            ObjectMetadata objectMetadata = metadataTransformer.transform(mediaExport);
            PutObjectResult putObjectResult = _s3Facade.putObject(bucketName, fileNameInfo.getNameObject(), mediaExport.getInputStream(), objectMetadata);

            result.add(createMediaExportResult(
                    (putObjectResult != null) ? MediaExportResult.ExportStatus.SUCCESS : MediaExportResult.ExportStatus.FAILURE, bucketName,
                    fileNameInfo.getNameObject()));
        }

        // 2) Does we need to upload the media metadata file?
        MediaExportMetadata mediaExportMetaData = mediaExport.createMediaExportMetadata();
        InputStream is = mediaExportMetaData.createInputStream();
        String hashValueMetaData = DigestUtil.computeSha1AsHexString(is);
        if (!isUploadNecessary(bucketName, fileNameInfo.getNameMetadata(), hashValueMetaData)) {
            LOG.trace("Metadata with key '" + fileNameInfo + "' will not uploaded to S3, because this object already exists and doesn't changed.");
            result.add(createMediaExportResult(MediaExportResult.ExportStatus.ALREADY_EXPORTED, bucketName, fileNameInfo.getNameMetadata()));
        } else {
            // upload media's meta data file to s3
            MediaExportMetaData2S3ObjectMetadataTransformer metadataTransformer = new MediaExportMetaData2S3ObjectMetadataTransformer();
            ObjectMetadata objectMetadata = metadataTransformer.transform(mediaExportMetaData);
            PutObjectResult putObjectResult = _s3Facade.putObject(bucketName, fileNameInfo.getNameMetadata(), mediaExportMetaData.createInputStream(),
                    objectMetadata);
            result.add(createMediaExportResult(
                    (putObjectResult != null) ? MediaExportResult.ExportStatus.SUCCESS : MediaExportResult.ExportStatus.FAILURE, bucketName,
                    fileNameInfo.getNameMetadata()));
        }

        return new MediaExportResults(result);
    }

    /**
     * @param userLoginId
     *            The user's name used for login (equivalent to <code>User.getName()</code>)
     * @param mediaType
     *            The media type (photo or video)
     * @param sha1Hash
     *            The media's hash value
     * @return The path used to store the media and its metadata file on external systems. (EG:
     *         <code>test-user/PHOTO/SHA1-14eed328269944441c66fa362eb461516e203172/</code>)
     */
    private String buildMediaStoragePath(String userLoginId, MediaType mediaType, String sha1Hash) {
        StringBuilder builder = new StringBuilder();
        builder.append(userLoginId).append(KEY_DELIMITER);
        builder.append(mediaType).append(KEY_DELIMITER);
        builder.append("SHA1-").append(sha1Hash).append(KEY_DELIMITER);
        return builder.toString();
    }

    /**
     * Creates a specific file name for the media object and its metadata object in form of:
     * <ul>
     * <li> <code>&lt;user.getName()&gt;/&lt;PHOTO or MEDIA&gt;/ID&lt;media id&gt;/&lt;original file name&gt;</code><br/>
     * EG: <code>rwe/PHOTO/ID17/IMG_1795.JPG</code></li>
     * <li> <code>&lt;user.getName()&gt;/&lt;PHOTO or MEDIA&gt;/ID&lt;media id&gt;/&lt;original file name&gt;.METADATA</code><br/>
     * EG: <code>rwe/PHOTO/ID17/IMG_1795.JPG.METADATA</code></li>
     * </ul>
     * 
     * @param userLoginId
     * @param mediaType
     * @param hashValue
     * @param originalFileName
     * @return an object containing the storage path of media object and its metadata object
     */
    @Override
    public FileNameInfo getKeyNames(String userLoginId, MediaType mediaType, String hashValue, String originalFileName) {
        // build path
        StringBuilder builder = new StringBuilder(buildMediaStoragePath(userLoginId, mediaType, hashValue));

        // build path for media
        if (!StringUtils.isEmpty(originalFileName)) {
            builder.append(originalFileName);
        } else {
            builder.append("media.data");
        }

        // build path for media metadata and add to return object
        return new FileNameInfo(builder.toString(), builder.append(METADATA_FILE_EXTENSION).toString());
    }

    @Override
    public void deleteMediaStoragePath(String bucketName, String mediaStoragePath) {
        _s3Facade.deletePath(bucketName, mediaStoragePath);
    }

    @Override
    public Iterator<MediaExport> iteratorPhotos(String user) {
        String prefix = getKeyNamePrefixForUserAndType(user, MediaType.PHOTO).toString();

        // get iterator for S3ObjectSummary objects
        S3ObjectIterator iterator = _s3Facade.iterator(buildBucketTypeName(MediaType.PHOTO), prefix);

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

    private boolean isUploadNecessary(String bucketName, String fileName, String hashValue) {
        // a) test object file already exists in bucket
        S3Object object = _s3Facade.getObjectIfExists(bucketName, fileName);

        String hashValueInS3 = null;
        if (object != null) {
            ObjectMetadata metadata = object.getObjectMetadata();
            // Map<String, Object> rawMetadata = metadata.getRawMetadata();
            Map<String, String> userMetadata = metadata.getUserMetadata();

            LOG.trace("Content-Type for '" + fileName + "': " + object.getObjectMetadata().getContentType());
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

    public String buildBucketTypeName(MediaType type) {
        switch (type) {
        case PHOTO:
            return "mediamagpie-photo";
        case STREAM:
            return "mediamagpie-stream";
        default:
            throw new RuntimeException("Undefined media type: " + type);
        }
    }

    private StringBuilder getKeyNamePrefixForUserAndType(String user, MediaType type) {
        StringBuilder builder = new StringBuilder();
        builder.append(user).append(KEY_DELIMITER);
        builder.append(type).append(KEY_DELIMITER);
        return builder;
    }
}

package de.wehner.mediamagpie.aws.s3.in;

import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectInputStream;
import com.amazonaws.services.s3.model.S3ObjectSummary;

import de.wehner.mediamagpie.api.MediaExport;
import de.wehner.mediamagpie.api.MediaExportMetadata;
import de.wehner.mediamagpie.api.MediaType;
import de.wehner.mediamagpie.aws.s3.S3MediaExportRepository;
import de.wehner.mediamagpie.aws.s3.S3ObjectTuple;
import de.wehner.mediamagpie.core.util.MMTransformer;
import de.wehner.mediamagpie.core.util.StringUtil;

public class S3ObjectTuple2MediaExportTransformer implements MMTransformer<S3ObjectTuple, MediaExport> {

    private static final Logger LOG = LoggerFactory.getLogger(S3ObjectTuple2MediaExportTransformer.class);

    /**
     * Due to 'java.net.SocketTimeoutException: Read timed out' errors i've found out that it is better to use a 'proxy' when loading the
     * binary content from the <code>S3Object</code>. So this objects just reloads the <code>S3Object</code> before the InputStream will be
     * accessed.
     */
    public static class InputStreamDelegate extends InputStream {

        private final AmazonS3 _s3;
        private final String _bucketName;
        private final String _key;
        private S3ObjectInputStream objectContent;

        public InputStreamDelegate(AmazonS3 s3, String bucketName, String key) {
            super();
            _s3 = s3;
            _bucketName = bucketName;
            _key = key;
        }

        @Override
        public int read() throws IOException {
            if (objectContent == null) {
                LOG.info("Get object from S3 (" + _bucketName + "/" + _key + ") for download.");
                S3Object s3object = _s3.getObject(_bucketName, _key);
                objectContent = s3object.getObjectContent();
            }
            return objectContent.read();
        }
    }

    private final AmazonS3 _s3;

    public S3ObjectTuple2MediaExportTransformer(AmazonS3 s3) {
        super();
        _s3 = s3;
    }

    @Override
    public MediaExport transform(S3ObjectTuple objectSummary) {

        S3ObjectSummary dataObject = objectSummary.getDataObject();
        // a) get Data file object (S3Object)
        final S3Object s3objectData = loadS3Object(dataObject);
        if (s3objectData == null) {
            return null;
        }

        // b) get the information from Metadata file object
        MediaExportMetadata exportMetadata = null;
        S3Object s3ObjectMetadata = null;
        try {
            s3ObjectMetadata = loadS3Object(objectSummary.getMetaObject());
            if (s3ObjectMetadata != null) {
                exportMetadata = MediaExportMetadata.createInstance(s3ObjectMetadata.getObjectContent());
            }
        } finally {
            IOUtils.closeQuietly(s3objectData);
            IOUtils.closeQuietly(s3ObjectMetadata);
        }

        // a) initialize a new MediaExport with Data file information
        // See also de.wehner.mediamagpie.aws.s3.export.MediaExport2S3ObjectMetadataTransformer.transform(MediaExport)
        final ObjectMetadata objectMetadata = s3objectData.getObjectMetadata();
        final Map<String, String> userMetadata = objectMetadata.getUserMetadata();

        // name
        final MediaExport mediaExport = new MediaExport((exportMetadata != null) ? exportMetadata.getName() : null);
        // mediaId (That was the original Media.id attribute that has only information character. A new Media will create it's own id!)
        if (!StringUtils.isEmpty(userMetadata.get(S3MediaExportRepository.META_MEDIA_ID))) {
            mediaExport.setMediaId(userMetadata.get(S3MediaExportRepository.META_MEDIA_ID));
        }
        // creation date
        if (!StringUtils.isEmpty(userMetadata.get(S3MediaExportRepository.META_CREATION_DATE))) {
            long time = Long.parseLong(userMetadata.get(S3MediaExportRepository.META_CREATION_DATE));
            mediaExport.setCreationDate(new Date(time));
        }
        // hash value
        if (!StringUtils.isEmpty(userMetadata.get(S3MediaExportRepository.META_HASH_OF_DATA))) {
            mediaExport.setHashValue(userMetadata.get(S3MediaExportRepository.META_HASH_OF_DATA));
        }
        // size
        mediaExport.setLength(dataObject.getSize());
        // MediaType
        if (!StringUtils.isEmpty(userMetadata.get(S3MediaExportRepository.META_MEDIA_TYPE))) {
            mediaExport.setType(MediaType.valueOf(userMetadata.get(S3MediaExportRepository.META_MEDIA_TYPE)));
        }
        // content
        mediaExport.setInputStream(new InputStreamDelegate(_s3, dataObject.getBucketName(), dataObject.getKey()));

        // b) try to read the meta data part of object
        // see also de.wehner.mediamagpie.api.MediaExportMetadata.createInputStream()
        if (exportMetadata != null) {
            mediaExport.setOriginalFileName(exportMetadata.getOriginalFileName());
            mediaExport.setDescription(exportMetadata.getDescription());
            mediaExport.setTags(exportMetadata.getTags());
        }
        return mediaExport;
    }

    private S3Object loadS3Object(S3ObjectSummary s3ObjectSummary) {
        if (s3ObjectSummary == null) {
            return null;
        }
        LOG.debug("Try loading S3Object for name '{}/{}' and size {}...", s3ObjectSummary.getBucketName(), s3ObjectSummary.getKey(),
                StringUtil.formatBytesToHumanReadableRepresentation(s3ObjectSummary.getSize()));
        S3Object s3object = _s3.getObject(s3ObjectSummary.getBucketName(), s3ObjectSummary.getKey());
        LOG.debug("Try loading S3Object for name '{}/{}' and size {}...DONE", s3ObjectSummary.getBucketName(), s3ObjectSummary.getKey(),
                StringUtil.formatBytesToHumanReadableRepresentation(s3ObjectSummary.getSize()));
        if (s3object == null) {
            LOG.warn("Can not load bucket '" + s3ObjectSummary.getBucketName() + "' with key '" + s3ObjectSummary.getKey() + "'.");
            return null;
        }
        return s3object;
    }

}
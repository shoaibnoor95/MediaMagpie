package de.wehner.mediamagpie.aws.s3;

import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.util.Map;

import javax.mail.internet.MimeUtility;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectSummary;

import de.wehner.mediamagpie.api.MediaExport;
import de.wehner.mediamagpie.api.MediaExportMetadata;
import de.wehner.mediamagpie.api.MediaType;
import de.wehner.mediamagpie.common.util.ExceptionUtil;
import de.wehner.mediamagpie.common.util.MMTransformer;
import de.wehner.mediamagpie.common.util.StringUtil;

public class S3ObjectTuple2MediaExportTransformer implements MMTransformer<S3ObjectTuple, MediaExport> {

    private static final Logger LOG = LoggerFactory.getLogger(S3ObjectTuple2MediaExportTransformer.class);

    private final AmazonS3 _s3;

    public S3ObjectTuple2MediaExportTransformer(AmazonS3 s3) {
        super();
        _s3 = s3;
    }

    @Override
    public MediaExport transform(S3ObjectTuple objectSummary) {

        // a) try to read the data part of MediaExport
        // See also de.wehner.mediamagpie.aws.s3.export.MediaExport2S3ObjectMetadataTransformer.transform(MediaExport)
        final S3Object s3objectData = loadS3Object(objectSummary.getDataObject());
        final ObjectMetadata objectMetadata = s3objectData.getObjectMetadata();

        // name
        final MediaExport mediaExport = new MediaExport(getNameByStrategie(objectMetadata, objectSummary.getDataObject()));
        // mime type
        if (!StringUtils.isEmpty(objectMetadata.getContentType())) {
            mediaExport.setMimeType(objectMetadata.getContentType());
        }
        // creation date
        Map<String, String> userMetadata = objectMetadata.getUserMetadata();
        if (!StringUtils.isEmpty(userMetadata.get(S3MediaExportRepository.META_CREATION_DATE))) {
            long time = Long.parseLong(userMetadata.get(S3MediaExportRepository.META_CREATION_DATE));
            mediaExport.setCreationDate(new Date(time));
        }
        // hash value
        if (!StringUtils.isEmpty(userMetadata.get(S3MediaExportRepository.META_HASH_OF_DATA))) {
            mediaExport.setHashValue(userMetadata.get(S3MediaExportRepository.META_HASH_OF_DATA));
        }
        // size
        mediaExport.setLength(objectSummary.getDataObject().getSize());
        // MediaType
        if (!StringUtils.isEmpty(userMetadata.get(S3MediaExportRepository.META_MEDIA_TYPE))) {
            mediaExport.setType(MediaType.valueOf(userMetadata.get(S3MediaExportRepository.META_MEDIA_TYPE)));
        }
        // content
        mediaExport.setInputStream(s3objectData.getObjectContent());

        // b) try to read the meta data part of object
        // see also de.wehner.mediamagpie.api.MediaExportMetadata.createInputStream()
        final S3Object s3ObjectMetadata = loadS3Object(objectSummary.getMetaObject());
        MediaExportMetadata exportMetadata = MediaExportMetadata.createInstance(s3ObjectMetadata.getObjectContent());

        mediaExport.setOriginalFileName(exportMetadata.getOriginalFileName());
        mediaExport.setDescription(exportMetadata.getDescription());
        mediaExport.setTags(exportMetadata.getTags());

        return mediaExport;
    }

    private S3Object loadS3Object(S3ObjectSummary s3ObjectSummary) {
        if (s3ObjectSummary == null) {
            return null;
        }
        final S3Object s3object = _s3.getObject(s3ObjectSummary.getBucketName(), s3ObjectSummary.getKey());
        if (s3object == null) {
            LOG.warn("Can not load bucket '" + s3ObjectSummary.getBucketName() + "' with key '" + s3ObjectSummary.getKey() + "'.");
            return null;
        }
        LOG.debug(String.format("Load object with name '%s' and size '%s'.", s3ObjectSummary.getBucketName(),
                StringUtil.humanReadableByteCount(s3ObjectSummary.getSize(), true)));
        return s3object;
    }

    private String getNameByStrategie(ObjectMetadata objectMetadata, S3ObjectSummary objectSummary) {
        String name = getValueFromUserMetadata(S3MediaExportRepository.META_NAME, objectMetadata);
        if (!StringUtils.isEmpty(name)) {
            return name;
        }
        return objectSummary.getKey();

    }

    private String getValueFromUserMetadata(String key, ObjectMetadata objectMetadata) {
        Map<String, String> userMetadata = objectMetadata.getUserMetadata();
        String value = userMetadata.get(key);
        if (!StringUtils.isEmpty(value)) {
            try {
                return MimeUtility.decodeText(value);
            } catch (UnsupportedEncodingException e) {
                ExceptionUtil.convertToRuntimeException(e);
            }
        }
        return null;
    }
}
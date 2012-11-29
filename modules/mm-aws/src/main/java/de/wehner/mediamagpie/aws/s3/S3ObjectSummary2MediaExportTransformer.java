package de.wehner.mediamagpie.aws.s3;

import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.Date;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectSummary;
import com.sun.xml.internal.messaging.saaj.packaging.mime.internet.MimeUtility;

import de.wehner.mediamagpie.api.MediaExport;
import de.wehner.mediamagpie.api.MediaType;
import de.wehner.mediamagpie.common.util.ExceptionUtil;
import de.wehner.mediamagpie.common.util.MMTransformer;

public class S3ObjectSummary2MediaExportTransformer implements MMTransformer<S3ObjectSummary, MediaExport> {

    private static final Logger LOG = LoggerFactory.getLogger(S3ObjectSummary2MediaExportTransformer.class);

    private final AmazonS3 _s3;

    public S3ObjectSummary2MediaExportTransformer(AmazonS3 s3) {
        super();
        _s3 = s3;
    }

    @Override
    public MediaExport transform(S3ObjectSummary objectSummary) {
        final S3Object s3object = _s3.getObject(objectSummary.getBucketName(), objectSummary.getKey());
        if (s3object == null) {
            LOG.warn("Can not load bucket '" + objectSummary.getBucketName() + "' with key '" + objectSummary.getKey() + "'.");
            return null;
        }
        LOG.debug("Load object with name '' and size ''.");
        ObjectMetadata objectMetadata = s3object.getObjectMetadata();

        // name
        MediaExport mediaExport = new MediaExport(getNameByStrategie(objectMetadata, objectSummary));
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
        // content
        mediaExport.setInputStream(s3object.getObjectContent());
        // original file name
        mediaExport.setOriginalFileName(getValueFromUserMetadata(S3MediaExportRepository.META_ORIGINAL_FILE_NAME, objectMetadata));
        // size
        mediaExport.setLength(objectSummary.getSize());
        // MediaType
        if (!StringUtils.isEmpty(userMetadata.get(S3MediaExportRepository.META_MEDIA_TYPE))) {
            mediaExport.setType(MediaType.valueOf(userMetadata.get(S3MediaExportRepository.META_MEDIA_TYPE)));
        }
        // tags
        String tags = getValueFromUserMetadata(S3MediaExportRepository.META_TAGS, objectMetadata);
        if (!StringUtils.isEmpty(tags)) {
            String[] tagArray = StringUtils.split(tags, ',');
            mediaExport.setTags(Arrays.asList(tagArray));
        }
        // description
        mediaExport.setDescription(getValueFromUserMetadata(S3MediaExportRepository.META_DESCRIPTION, objectMetadata));
        // mime type
        mediaExport.setMimeType(objectMetadata.getContentType());
        
        return mediaExport;
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
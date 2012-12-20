package de.wehner.mediamagpie.aws.s3;

import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.util.List;

import javax.mail.internet.MimeUtility;

import org.apache.commons.lang3.StringUtils;

import com.amazonaws.services.s3.model.ObjectMetadata;

import de.wehner.mediamagpie.api.MediaExport;
import de.wehner.mediamagpie.common.util.ExceptionUtil;
import de.wehner.mediamagpie.common.util.MMTransformer;

public class MediaExport2S3ObjectMetadataTransformer implements MMTransformer<MediaExport, ObjectMetadata> {

    @Override
    public ObjectMetadata transform(MediaExport mediaExport) {
        ObjectMetadata objectMetadata = new ObjectMetadata();
        // mime type
        if (!StringUtils.isEmpty(mediaExport.getMimeType())) {
            objectMetadata.setContentType(mediaExport.getMimeType());
        }
        // name
        addStringIntoUserMetadata(S3MediaExportRepository.META_NAME, mediaExport.getName(), objectMetadata);
        // description (TODO rwe: compress?)
        addStringIntoUserMetadata(S3MediaExportRepository.META_DESCRIPTION, mediaExport.getDescription(), objectMetadata);
        // creation date
        addStringIntoUserMetadata(S3MediaExportRepository.META_CREATION_DATE, mediaExport.getCreationDate(), objectMetadata);
        // hash
        objectMetadata.addUserMetadata(S3MediaExportRepository.META_HASH_OF_DATA, mediaExport.getHashValue());
        // length
        if (mediaExport.getLength() != null) {
            objectMetadata.setContentLength(mediaExport.getLength());
        }
        // original file name
        addStringIntoUserMetadata(S3MediaExportRepository.META_ORIGINAL_FILE_NAME, mediaExport.getOriginalFileName(), objectMetadata);
        // media type
        objectMetadata.addUserMetadata(S3MediaExportRepository.META_MEDIA_TYPE, mediaExport.getType().name());
        // tags
        addStringIntoUserMetadata(S3MediaExportRepository.META_TAGS, mediaExport.getTags(), objectMetadata);

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
            strValue = StringUtils.join(((List<String>) value), ',');
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
}

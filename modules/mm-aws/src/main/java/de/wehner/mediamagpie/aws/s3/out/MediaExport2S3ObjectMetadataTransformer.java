package de.wehner.mediamagpie.aws.s3.out;

import org.apache.commons.lang3.StringUtils;

import com.amazonaws.services.s3.model.ObjectMetadata;

import de.wehner.mediamagpie.api.MediaExport;
import de.wehner.mediamagpie.aws.s3.BaseMMTransformer;
import de.wehner.mediamagpie.aws.s3.S3MediaExportRepository;
import de.wehner.mediamagpie.common.util.MMTransformer;

/**
 * Creates a S3 <code>ObjectMetadata</code> which contains some "short" metadata of a media. This raw binary data stream of a media will be
 * stored parallel to the ObjectMetadata.<br/>
 * Because some Metadata of an <code>Media</code> (lke the description ) can exceed the capacaty of an ObjectMetadata, the new object
 * {@linkplain MediaExportMetadata} is introduced to the metadata in json format as binary stream.
 * 
 * @author ralfwehner
 * @see de.wehner.mediamagpie.api.MediaExportMetadata
 */
public class MediaExport2S3ObjectMetadataTransformer extends BaseMMTransformer implements MMTransformer<MediaExport, ObjectMetadata> {

    @Override
    public ObjectMetadata transform(MediaExport mediaExport) {
        ObjectMetadata objectMetadata = new ObjectMetadata();
        // mime type
        if (!StringUtils.isEmpty(mediaExport.getMimeType())) {
            objectMetadata.setContentType(mediaExport.getMimeType());
        }
        // name
        addStringIntoUserMetadata(S3MediaExportRepository.META_NAME, mediaExport.getName(), objectMetadata);
        // creation date
        addStringIntoUserMetadata(S3MediaExportRepository.META_CREATION_DATE, mediaExport.getCreationDate(), objectMetadata);
        // hash
        objectMetadata.addUserMetadata(S3MediaExportRepository.META_HASH_OF_DATA, mediaExport.getHashValue());
        // length
        if (mediaExport.getLength() != null) {
            objectMetadata.setContentLength(mediaExport.getLength());
        }
        // media type
        objectMetadata.addUserMetadata(S3MediaExportRepository.META_MEDIA_TYPE, mediaExport.getType().name());

        return objectMetadata;
    }

}

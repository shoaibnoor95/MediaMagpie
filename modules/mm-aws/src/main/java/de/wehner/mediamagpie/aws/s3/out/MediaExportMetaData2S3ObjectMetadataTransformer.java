package de.wehner.mediamagpie.aws.s3.out;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import org.apache.commons.io.IOUtils;

import com.amazonaws.services.s3.model.ObjectMetadata;

import de.wehner.mediamagpie.api.MediaExportMetadata;
import de.wehner.mediamagpie.aws.s3.BaseMMTransformer;
import de.wehner.mediamagpie.aws.s3.S3MediaExportRepository;
import de.wehner.mediamagpie.core.util.DigestUtil;
import de.wehner.mediamagpie.core.util.ExceptionUtil;
import de.wehner.mediamagpie.core.util.MMTransformer;

/**
 * Creates the S3 <code>ObjectMetadata</code> to the json metadata objects which only contains the hash value of its raw S3Object.
 * 
 * @author ralfwehner
 * 
 */
public class MediaExportMetaData2S3ObjectMetadataTransformer extends BaseMMTransformer implements MMTransformer<MediaExportMetadata, ObjectMetadata> {

    @Override
    public ObjectMetadata transform(MediaExportMetadata mediaExportMetaData) {
        ObjectMetadata objectMetadata = new ObjectMetadata();
        try {
            byte[] content = IOUtils.toByteArray(mediaExportMetaData.createInputStream());
            // hash
            String hash = DigestUtil.computeSha1AsHexString(new ByteArrayInputStream(content));
            objectMetadata.addUserMetadata(S3MediaExportRepository.META_HASH_OF_DATA, hash);
            // length
            objectMetadata.setContentLength(content.length);
        } catch (IOException e) {
            throw ExceptionUtil.convertToRuntimeException(e);
        }

        return objectMetadata;
    }

}

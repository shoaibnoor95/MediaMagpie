package de.wehner.mediamagpie.aws.s3;

import static org.fest.assertions.Assertions.*;

import static org.mockito.Mockito.*;

import java.io.IOException;
import java.util.Arrays;
import java.util.Date;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectSummary;

import de.wehner.mediamagpie.api.MediaExport;
import de.wehner.mediamagpie.api.MediaType;

public class S3ObjectSummary2MediaExportTransformerTest {

    private static final String BUCKET_NAME = "bucket-name";
    private static final String MEDIA_NAME = "my media name with äöü";
    private static final Date CREATION_DATE = new Date(54321);

    private static final String KEY = "key";

    @Mock
    private AmazonS3 _s3;

    private S3ObjectSummary2MediaExportTransformer _transformer;

    private byte[] _content;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        _transformer = new S3ObjectSummary2MediaExportTransformer(_s3);

        // setup a mock S3Object
        S3Object s3Object = new S3Object();
        ObjectMetadata objectMetadata = s3Object.getObjectMetadata();
        Map<String, String> userMetadata = objectMetadata.getUserMetadata();
        userMetadata.put(S3MediaRepository.META_NAME, MEDIA_NAME);
        userMetadata.put(S3MediaRepository.META_CREATION_DATE, "" + CREATION_DATE.getTime());
        String random = RandomStringUtils.random(128);
        _content = random.getBytes();

        // link test object to S3Client
        when(_s3.getObject(BUCKET_NAME, KEY)).thenReturn(s3Object);
    }

    // TODO rwe: complete test next time or remove !
    @Ignore("currently this test ist not complete")
    @Test
    public void test_transform() throws IOException {
        S3ObjectSummary s3ObjectSummary = new S3ObjectSummary();
        s3ObjectSummary.setBucketName(BUCKET_NAME);
        s3ObjectSummary.setKey(KEY);

        MediaExport mediaExport = _transformer.transform(s3ObjectSummary);

        assertThat(mediaExport.getName()).isEqualTo(MEDIA_NAME);
        assertThat(mediaExport.getCreationDate()).isEqualTo(CREATION_DATE);

        assertThat(mediaExport.getDescription()).isEqualTo("description äüö?ß\"");
        assertThat(mediaExport.getHashValue()).isEqualTo("description äüö?ß\"");
        assertThat(IOUtils.toByteArray(mediaExport.getInputStream())).isEqualTo(_content);
        assertThat(mediaExport.getLength()).isEqualTo(_content.length);
        assertThat(mediaExport.getMediaId()).isEqualTo("123");
        assertThat(mediaExport.getMimeType()).isEqualTo("description äüö?ß\"");
        assertThat(mediaExport.getOriginalFileName()).isEqualTo("orig file name");
        assertThat(mediaExport.getTags()).containsOnly(Arrays.asList("tag 1", "tag 2"));
        assertThat(mediaExport.getType()).isEqualTo(MediaType.PHOTO);
    }

    @Test
    public void test_transform_ButCantLoadObject() throws IOException {
        S3ObjectSummary s3ObjectSummary = new S3ObjectSummary();

        MediaExport mediaExport = _transformer.transform(s3ObjectSummary);
        assertThat(mediaExport).isNull();
    }
}

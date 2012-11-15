package de.wehner.mediamagpie.aws.s3;

import static org.fest.assertions.Assertions.*;

import static org.mockito.Mockito.*;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectSummary;
import com.sun.xml.internal.messaging.saaj.packaging.mime.internet.MimeUtility;

import de.wehner.mediamagpie.api.MediaExport;
import de.wehner.mediamagpie.api.MediaType;
import de.wehner.mediamagpie.common.core.util.DigestUtil;

public class S3ObjectSummary2MediaExportTransformerTest {

    private static final String BUCKET_NAME = "bucket-name";
    private static final String KEY = "key";
    private static final String MEDIA_NAME = "my media name with äöü";
    private static final String MEDIA_DESC = "This is a long description text, containing some special html characters\nlike <li/> or <b/>. Addionally, there are some chars like äöüß€ etc.";
    private static final String MEDIA_ORIG_FILE_NAME = "original file name.png";
    private static final String MIME_TYPE = "the mime type";
    private static final Date CREATION_DATE = new Date(54321);
    private static final List<String> MEDIA_TAGS = Arrays.asList("tag 1", "tag 3", "tag 2");

    @Mock
    private AmazonS3 _s3;

    private S3ObjectSummary2MediaExportTransformer _transformer;

    private byte[] _content;
    private Map<String, String> _userMetadata;

    @Before
    public void setUp() throws UnsupportedEncodingException {
        MockitoAnnotations.initMocks(this);
        _transformer = new S3ObjectSummary2MediaExportTransformer(_s3);

        // setup a mock S3Object
        S3Object s3Object = new S3Object();
        ObjectMetadata objectMetadata = s3Object.getObjectMetadata();
        _userMetadata = objectMetadata.getUserMetadata();
        _userMetadata.put(S3MediaRepository.META_NAME, MimeUtility.encodeText(MEDIA_NAME));
        _userMetadata.put(S3MediaRepository.META_DESCRIPTION, MimeUtility.encodeText(MEDIA_DESC));
        _userMetadata.put(S3MediaRepository.META_CREATION_DATE, "" + CREATION_DATE.getTime());
        String random = RandomStringUtils.random(128);
        _content = random.getBytes();
        s3Object.setObjectContent(new ByteArrayInputStream(_content));
        _userMetadata.put(S3MediaRepository.META_HASH_OF_DATA, DigestUtil.computeSha1AsHexString(new ByteArrayInputStream(_content)));
        _userMetadata.put(S3MediaRepository.META_ORIGINAL_FILE_NAME, MimeUtility.encodeText(MEDIA_ORIG_FILE_NAME));
        objectMetadata.setContentType(MIME_TYPE);
        _userMetadata.put(S3MediaRepository.META_TAGS, MimeUtility.encodeText(StringUtils.join(MEDIA_TAGS, ',')));
        _userMetadata.put(S3MediaRepository.META_MEDIA_TYPE, MediaType.PHOTO.toString());

        // link test object to S3Client
        when(_s3.getObject(BUCKET_NAME, KEY)).thenReturn(s3Object);
    }

    @Test
    public void test_transform_HashValueGiven() throws IOException {
        S3ObjectSummary s3ObjectSummary = new S3ObjectSummary();
        s3ObjectSummary.setBucketName(BUCKET_NAME);
        s3ObjectSummary.setKey(KEY);
        s3ObjectSummary.setSize(_content.length);

        MediaExport mediaExport = _transformer.transform(s3ObjectSummary);

        assertThat(mediaExport.getName()).isEqualTo(MEDIA_NAME);
        assertThat(mediaExport.getCreationDate()).isEqualTo(CREATION_DATE);

        assertThat(mediaExport.getDescription()).isEqualTo(MEDIA_DESC);
        assertThat(mediaExport.getHashValue()).isEqualTo(DigestUtil.computeSha1AsHexString(new ByteArrayInputStream(_content)));
        assertThat(IOUtils.toByteArray(mediaExport.getInputStream())).isEqualTo(_content);
        assertThat(mediaExport.getLength()).isEqualTo(_content.length);
        // assertThat(mediaExport.getMediaId()).isEqualTo("123");
        assertThat(mediaExport.getMimeType()).isEqualTo(MIME_TYPE);
        assertThat(mediaExport.getOriginalFileName()).isEqualTo(MEDIA_ORIG_FILE_NAME);
        assertThat(mediaExport.getTags()).isEqualTo(MEDIA_TAGS);
        assertThat(mediaExport.getType()).isEqualTo(MediaType.PHOTO);
    }

    @Test
    public void test_transform_ButCantLoadObject() throws IOException {
        S3ObjectSummary s3ObjectSummary = new S3ObjectSummary();

        MediaExport mediaExport = _transformer.transform(s3ObjectSummary);
        assertThat(mediaExport).isNull();
    }
}

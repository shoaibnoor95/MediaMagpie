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

import javax.mail.internet.MimeUtility;
import javax.mail.internet.ParseException;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectSummary;

import de.wehner.mediamagpie.api.MediaExport;
import de.wehner.mediamagpie.api.MediaExportMetadata;
import de.wehner.mediamagpie.api.MediaType;
import de.wehner.mediamagpie.aws.s3.in.S3ObjectTuple2MediaExportTransformer;
import de.wehner.mediamagpie.core.util.DigestUtil;

public class S3ObjectSummary2MediaExportTransformerTest {

    private static final String BUCKET_NAME = "bucket-name";
    private static final String KEY_DATA = "mediamagpie-photo/jimmy/PHOTO/id123/media.data";
    private static final String KEY_METADATA = KEY_DATA + S3MediaExportRepository.METADATA_FILE_EXTENSION;
    private static final String MEDIA_ID = "123abc";
    private static final String MEDIA_NAME = "my media name with äöü";
    private static final String MEDIA_DESC = "This is a long description text, containing some special html characters\nlike <li/> or <b/>. Addionally, there are some chars like äöüß€ etc.";
    private static final String MEDIA_ORIG_FILE_NAME = "original file name.png";
    private static final Date CREATION_DATE = new Date(54321);
    private static final List<String> MEDIA_TAGS = Arrays.asList("tag 1", "tag 3", "tag 2");

    @Mock
    private AmazonS3 _s3;

    private S3ObjectTuple2MediaExportTransformer _transformer;

    private byte[] _contentS3DataObject;

    @Before
    public void setUp() throws UnsupportedEncodingException {
        MockitoAnnotations.initMocks(this);
        _transformer = new S3ObjectTuple2MediaExportTransformer(_s3);

        // setup a data mock S3Object
        S3Object s3DataObject = new S3Object();
        ObjectMetadata objectMetadata_Data = s3DataObject.getObjectMetadata();
        Map<String, String> map = objectMetadata_Data.getUserMetadata();
        map.put(S3MediaExportRepository.META_MEDIA_ID, MEDIA_ID);
        map.put(S3MediaExportRepository.META_NAME, MimeUtility.encodeText(MEDIA_NAME));
        map.put(S3MediaExportRepository.META_CREATION_DATE, "" + CREATION_DATE.getTime());
        String random = RandomStringUtils.random(128);
        _contentS3DataObject = random.getBytes();
        s3DataObject.setObjectContent(new ByteArrayInputStream(_contentS3DataObject));
        map.put(S3MediaExportRepository.META_HASH_OF_DATA, DigestUtil.computeSha1AsHexString(new ByteArrayInputStream(_contentS3DataObject)));
        map.put(S3MediaExportRepository.META_MEDIA_TYPE, MediaType.PHOTO.toString());

        // link test object to S3Client
        when(_s3.getObject(BUCKET_NAME, KEY_DATA)).thenReturn(s3DataObject);

        // setup a meta data mock S3Object
        S3Object s3DataObjectMetadata = new S3Object();
        MediaExportMetadata mediaExportMetadata = new MediaExportMetadata(MEDIA_NAME);
        mediaExportMetadata.setDescription(MEDIA_DESC);
        mediaExportMetadata.setOriginalFileName(MEDIA_ORIG_FILE_NAME);
        mediaExportMetadata.setTags(MEDIA_TAGS);
        s3DataObjectMetadata.setObjectContent(mediaExportMetadata.createInputStream());

        // link test object to S3Client
        when(_s3.getObject(BUCKET_NAME, KEY_METADATA)).thenReturn(s3DataObjectMetadata);
    }

    @Test
    public void test_RFC2047_RoundRobin() throws UnsupportedEncodingException, ParseException {

        String encoded = MimeUtility.encodeText(MEDIA_DESC);
        String decoded = MimeUtility.decodeText(encoded);
        assertThat(decoded).isEqualTo(MEDIA_DESC);
    }

    @Test
    public void test_transform_OnlyDataObjectIsAvailable() throws IOException {
        S3ObjectSummary s3ObjectSummary = new S3ObjectSummary();
        s3ObjectSummary.setBucketName(BUCKET_NAME);
        s3ObjectSummary.setKey(KEY_DATA);
        s3ObjectSummary.setSize(_contentS3DataObject.length);
        S3ObjectTuple s3ObjectTuple = new S3ObjectTuple(s3ObjectSummary, null);

        MediaExport mediaExport = _transformer.transform(s3ObjectTuple);

        assertThat(mediaExport.getName()).isEqualTo(MEDIA_NAME);
        assertThat(mediaExport.getMediaId()).isEqualTo(MEDIA_ID);
        assertThat(mediaExport.getCreationDate()).isEqualTo(CREATION_DATE);

        assertThat(mediaExport.getHashValue()).isEqualTo(DigestUtil.computeSha1AsHexString(new ByteArrayInputStream(_contentS3DataObject)));
        assertThat(IOUtils.toByteArray(mediaExport.getInputStream())).isEqualTo(_contentS3DataObject);
        assertThat(mediaExport.getLength()).isEqualTo(_contentS3DataObject.length);
        assertThat(mediaExport.getMediaId()).isEqualTo(MEDIA_ID);
        assertThat(mediaExport.getType()).isEqualTo(MediaType.PHOTO);

        assertThat(mediaExport.getOriginalFileName()).isNull();
        assertThat(mediaExport.getDescription()).isNull();
        assertThat(mediaExport.getTags()).isEmpty();
    }

    @Test
    public void test_transform_DataObjectAndMetaDataObjectIsAvailable() throws IOException {
        S3ObjectSummary s3ObjectSummary = new S3ObjectSummary();
        s3ObjectSummary.setBucketName(BUCKET_NAME);
        s3ObjectSummary.setKey(KEY_DATA);
        s3ObjectSummary.setSize(_contentS3DataObject.length);
        S3ObjectSummary s3ObjectMetadataSummary = new S3ObjectSummary();
        s3ObjectMetadataSummary.setBucketName(BUCKET_NAME);
        s3ObjectMetadataSummary.setKey(KEY_METADATA);
        s3ObjectMetadataSummary.setSize(0);/* not relevant for this test */
        S3ObjectTuple s3ObjectTuple = new S3ObjectTuple(s3ObjectSummary, s3ObjectMetadataSummary);

        MediaExport mediaExport = _transformer.transform(s3ObjectTuple);

        assertThat(mediaExport.getName()).isEqualTo(MEDIA_NAME);
        assertThat(mediaExport.getMediaId()).isEqualTo(MEDIA_ID);
        assertThat(mediaExport.getCreationDate()).isEqualTo(CREATION_DATE);

        assertThat(mediaExport.getDescription()).isEqualTo(MEDIA_DESC);
        assertThat(mediaExport.getHashValue()).isEqualTo(DigestUtil.computeSha1AsHexString(new ByteArrayInputStream(_contentS3DataObject)));
        assertThat(IOUtils.toByteArray(mediaExport.getInputStream())).isEqualTo(_contentS3DataObject);
        assertThat(mediaExport.getLength()).isEqualTo(_contentS3DataObject.length);
        assertThat(mediaExport.getMediaId()).isEqualTo(MEDIA_ID);
        assertThat(mediaExport.getOriginalFileName()).isEqualTo(MEDIA_ORIG_FILE_NAME);
        assertThat(mediaExport.getTags()).isEqualTo(MEDIA_TAGS);
        assertThat(mediaExport.getType()).isEqualTo(MediaType.PHOTO);
    }

    @Test
    public void test_transform_ButCantLoadDataObject() throws IOException {
        S3ObjectSummary s3ObjectSummary = new S3ObjectSummary();
        S3ObjectTuple s3ObjectTuple = new S3ObjectTuple(s3ObjectSummary, null);

        MediaExport mediaExport = _transformer.transform(s3ObjectTuple);
        assertThat(mediaExport).isNull();
    }
}

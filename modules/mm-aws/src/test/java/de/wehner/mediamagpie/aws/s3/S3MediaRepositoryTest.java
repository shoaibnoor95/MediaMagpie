package de.wehner.mediamagpie.aws.s3;

import static org.fest.assertions.Assertions.*;

import static org.mockito.Matchers.*;

import static org.mockito.Mockito.*;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.io.IOUtils;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectResult;

import de.wehner.mediamagpie.api.MediaExport;
import de.wehner.mediamagpie.api.MediaExportRepository;
import de.wehner.mediamagpie.api.testsupport.MediaExportFixture;

public class S3MediaRepositoryTest {

    private static final File SRC_TEST_JPG = new File("../mm-conductor/src/test/resources/images/IMG_1414.JPG");

    @Mock
    private S3ClientFacade _s3ClientFacade;

    private MediaExport _mediaExport;

    private MediaExportRepository _s3MediaRepository;

    @Before
    public void setUp() throws IOException {
        MockitoAnnotations.initMocks(this);
        when(_s3ClientFacade.putObject(anyString(), anyString(), any(InputStream.class), any(ObjectMetadata.class))).thenReturn(new PutObjectResult());
        _s3MediaRepository = new S3MediaExportRepository(_s3ClientFacade);

        _mediaExport = MediaExportFixture.createMediaExportTestObject(123, "media1", SRC_TEST_JPG);
    }

    @Test
    public void test_addMedia() throws IOException {
        String s3ObjectFilePath = "test-user/PHOTO/SHA1-" + _mediaExport.getHashValue();
        _mediaExport.setOriginalFileName("my difficultFileNameäö.jpg");
        
        _s3MediaRepository.addMedia("test-user", _mediaExport);

        verify(_s3ClientFacade, times(1)).createBucketIfNotExists("mediamagpie-photo");
        // the media data object
        verify(_s3ClientFacade, times(1)).getObjectIfExists("mediamagpie-photo", s3ObjectFilePath + "/media.data");
        verify(_s3ClientFacade, times(1)).putObject(eq("mediamagpie-photo"), eq(s3ObjectFilePath + "/media.data"), same(_mediaExport.getInputStream()),
                any(ObjectMetadata.class));
        // media's meta data
        ArgumentCaptor<InputStream> captor = ArgumentCaptor.forClass(InputStream.class);
        verify(_s3ClientFacade, times(1)).getObjectIfExists("mediamagpie-photo", s3ObjectFilePath + "/media.data.METADATA");
        verify(_s3ClientFacade, times(1)).putObject(eq("mediamagpie-photo"), eq(s3ObjectFilePath + "/media.data.METADATA"), captor.capture(),
                any(ObjectMetadata.class));
        InputStream inputStream = captor.getValue();
        assertThat(IOUtils.contentEquals(inputStream, _mediaExport.createMediaExportMetadata().createInputStream())).isTrue();
    }

    @Test
    public void test_addMedia_Which_has_no_originalFileName() throws IOException {
        String s3ObjectFilePath = "test-user/PHOTO/SHA1-" + _mediaExport.getHashValue();
        _mediaExport.setOriginalFileName(null);
        
        _s3MediaRepository.addMedia("test-user", _mediaExport);

        verify(_s3ClientFacade, times(1)).createBucketIfNotExists("mediamagpie-photo");
        // the media data object
        verify(_s3ClientFacade, times(1)).getObjectIfExists("mediamagpie-photo", s3ObjectFilePath + "/media.data");
        verify(_s3ClientFacade, times(1)).putObject(eq("mediamagpie-photo"), eq(s3ObjectFilePath + "/media.data"), same(_mediaExport.getInputStream()),
                any(ObjectMetadata.class));
        // media's meta data
        ArgumentCaptor<InputStream> captor = ArgumentCaptor.forClass(InputStream.class);
        verify(_s3ClientFacade, times(1)).getObjectIfExists("mediamagpie-photo", s3ObjectFilePath + "/media.data.METADATA");
        verify(_s3ClientFacade, times(1)).putObject(eq("mediamagpie-photo"), eq(s3ObjectFilePath + "/media.data.METADATA"), captor.capture(),
                any(ObjectMetadata.class));
        InputStream inputStream = captor.getValue();
        assertThat(IOUtils.contentEquals(inputStream, _mediaExport.createMediaExportMetadata().createInputStream())).isTrue();
    }
}

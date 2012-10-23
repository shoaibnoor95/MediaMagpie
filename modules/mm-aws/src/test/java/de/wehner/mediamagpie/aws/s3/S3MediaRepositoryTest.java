package de.wehner.mediamagpie.aws.s3;

import static org.mockito.Matchers.*;

import static org.mockito.Mockito.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectResult;

import de.wehner.mediamagpie.api.MediaExport;

public class S3MediaRepositoryTest {

    private static final Date CREATION_DATE = new Date(123456);

    // private static final File SRC_TEST_PNG = new File("../mm-conductor/src/test/resources/images/image1.png");

    private static final File SRC_TEST_JPG = new File("../mm-conductor/src/test/resources/images/IMG_1414.JPG");

    @Mock
    private S3ClientFacade _s3ClientFacade;

    MediaExport _mediaExport;

    private S3MediaRepository _s3MediaRepository;

    @Before
    public void setUp() throws IOException {
        MockitoAnnotations.initMocks(this);
        when(_s3ClientFacade.putObject(anyString(), anyString(), any(InputStream.class), any(ObjectMetadata.class))).thenReturn(new PutObjectResult());
        _s3MediaRepository = new S3MediaRepository(_s3ClientFacade);

        _mediaExport = new MediaExport("media1");
        _mediaExport.setHashValue("pseudo-hash-value");
        _mediaExport.setInputStream(new FileInputStream(SRC_TEST_JPG));
        _mediaExport.setMediaId("mediaID");
        _mediaExport.setOriginalFileName("origFileName");
    }

    @Test
    public void test_addMedia() throws FileNotFoundException {

        _s3MediaRepository.addMedia("test-user", _mediaExport);

        verify(_s3ClientFacade, times(1)).createBucketIfNotExists("mediamagpie");
        verify(_s3ClientFacade, times(1)).getObjectIfExists("mediamagpie", "test-user_UNKNOWN_IDmediaID_origFileName");
        verify(_s3ClientFacade, times(1)).putObject(eq("mediamagpie"), eq("test-user_UNKNOWN_IDmediaID_origFileName"),
                same(_mediaExport.getInputStream()), any(ObjectMetadata.class));
    }
}

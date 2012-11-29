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
import de.wehner.mediamagpie.api.MediaExportRepository;
import de.wehner.mediamagpie.api.MediaType;

public class S3MediaRepositoryTest {

    private static final Date CREATION_DATE = new Date(123456);

    // private static final File SRC_TEST_PNG = new File("../mm-conductor/src/test/resources/images/image1.png");

    private static final File SRC_TEST_JPG = new File("../mm-conductor/src/test/resources/images/IMG_1414.JPG");

    @Mock
    private S3ClientFacade _s3ClientFacade;

    MediaExport _mediaExport;

    private MediaExportRepository _s3MediaRepository;

    @Before
    public void setUp() throws IOException {
        MockitoAnnotations.initMocks(this);
        when(_s3ClientFacade.putObject(anyString(), anyString(), any(InputStream.class), any(ObjectMetadata.class))).thenReturn(new PutObjectResult());
        _s3MediaRepository = new S3MediaExportRepository(_s3ClientFacade);

        _mediaExport = new MediaExport("media1");
        _mediaExport.setHashValue("pseudo-hash-value");
        _mediaExport.setInputStream(new FileInputStream(SRC_TEST_JPG));
        _mediaExport.setMediaId("mediaID");
        _mediaExport.setOriginalFileName("origFileName");
        _mediaExport.setType(MediaType.PHOTO);
    }

    @Test
    public void test_addMedia() throws FileNotFoundException {

        _s3MediaRepository.addMedia("test-user", _mediaExport);

        verify(_s3ClientFacade, times(1)).createBucketIfNotExists("mediamagpie-photo");
        verify(_s3ClientFacade, times(1)).getObjectIfExists("mediamagpie-photo", "test-user/PHOTO/IDmediaID/origFileName");
        verify(_s3ClientFacade, times(1)).putObject(eq("mediamagpie-photo"), eq("test-user/PHOTO/IDmediaID/origFileName"),
                same(_mediaExport.getInputStream()), any(ObjectMetadata.class));
    }

    // @Test
    // public void testXmlBuilder() throws ParserConfigurationException, FactoryConfigurationError, TransformerException {
    // XMLBuilder builder = XMLBuilder.create("CompleteMultipartUpload").a("xmlns", Constants.XML_NAMESPACE);
    // builder.e("Part").e("PartNumber").t("" + 12).up().e("ETag").t("ETag");
    // Properties outputProperties = new Properties();
    // // Pretty-print the XML output (doesn't work in all cases)
    // outputProperties.put(javax.xml.transform.OutputKeys.INDENT, "yes");
    // // Get 2-space indenting when using the Apache transformer
    // outputProperties.put("{http://xml.apache.org/xslt}indent-amount", "2");
    //
    // System.out.println(builder.asString(outputProperties));
    // }
}

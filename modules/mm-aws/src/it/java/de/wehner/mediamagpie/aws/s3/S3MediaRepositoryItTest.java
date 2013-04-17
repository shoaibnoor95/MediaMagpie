package de.wehner.mediamagpie.aws.s3;

import static org.fest.assertions.Assertions.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.runner.RunWith;

import de.wehner.mediamagpie.api.MediaExport;
import de.wehner.mediamagpie.api.MediaExportRepository;
import de.wehner.mediamagpie.api.testsupport.MediaExportFixture;
import de.wehner.mediamagpie.aws.test.util.S3TestEnvironment;
import de.wehner.mediamagpie.core.util.OrderedJUnit4ClassRunner;

@RunWith(OrderedJUnit4ClassRunner.class)
public class S3MediaRepositoryItTest {

    private static final String TEST_NAME = "test-_/mediaöäüß\"19";

    private static final String USER = "test-user";

    @ClassRule
    public static S3TestEnvironment _s3TestEnvironment = new S3TestEnvironment();

    private static final File SRC_TEST_JPG = new File("../mm-conductor/src/test/resources/images/accept.png");

    private static final File SRC_TEST_JPG2 = new File("../mm-conductor/src/test/resources/images/1600x4.jpg");

    @Test
    public void test_addMedia() throws FileNotFoundException {
        org.junit.Assume.assumeTrue(_s3TestEnvironment.getS3Credentials() != null);

        MediaExportRepository repository = new S3MediaExportRepository(_s3TestEnvironment.getS3Credentials());

        MediaExport mediaExport = MediaExportFixture.createMediaExportTestObject(123, TEST_NAME, SRC_TEST_JPG);

        repository.addMedia(USER, mediaExport);

        mediaExport = MediaExportFixture.createMediaExportTestObject(124, "test-name-2", SRC_TEST_JPG2);

        repository.addMedia(USER, mediaExport);
    }

    @Test
    public void test_iteratorPhotos_readObjectFromPreviousTest() throws IOException {
        org.junit.Assume.assumeTrue(_s3TestEnvironment.getS3Credentials() != null);

        MediaExportRepository repository = new S3MediaExportRepository(_s3TestEnvironment.getS3Credentials());

        Iterator<MediaExport> it = repository.iteratorPhotos(USER);

        List<MediaExport> mediaExportsFromS3 = new ArrayList<MediaExport>();
        while (it.hasNext()) {
            MediaExport mediaExport = it.next();
            System.out.println(" found: '" + mediaExport.getName() + "', size: " + mediaExport.getLength());
            mediaExportsFromS3.add(mediaExport);
        }
        assertThat(mediaExportsFromS3).hasSize(2);
        MediaExport mediaExportFromS3 = mediaExportsFromS3.get(0);
        MediaExport mediaExport = MediaExportFixture.createMediaExportTestObject(123, TEST_NAME, SRC_TEST_JPG);
        assertThat(mediaExportFromS3.getCreationDate()).isEqualTo(mediaExport.getCreationDate());
        assertThat(mediaExportFromS3.getDescription()).isEqualTo(mediaExport.getDescription());
        assertThat(mediaExportFromS3.getHashValue()).isEqualTo(mediaExport.getHashValue());
        assertThat(IOUtils.toByteArray(mediaExportFromS3.getInputStream())).isEqualTo(IOUtils.toByteArray(mediaExport.getInputStream()));
        assertThat(mediaExportFromS3.getLength()).isEqualTo(SRC_TEST_JPG.length());
        assertThat(mediaExportFromS3.getName()).isEqualTo(mediaExport.getName());
        assertThat(mediaExportFromS3.getOriginalFileName()).isEqualTo(mediaExport.getOriginalFileName());
        assertThat(mediaExportFromS3.getType()).isEqualTo(mediaExport.getType());
        assertThat(mediaExportFromS3.getTags()).isEqualTo(mediaExport.getTags());
        assertThat(mediaExportFromS3.getCreationDate()).isEqualTo(MediaExportFixture.CREATION_DATE);
    }

}

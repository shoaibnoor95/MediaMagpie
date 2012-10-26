package de.wehner.mediamagpie.aws.s3;

import static org.fest.assertions.Assertions.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.junit.Rule;
import org.junit.Test;

import de.wehner.mediamagpie.api.MediaExport;
import de.wehner.mediamagpie.api.MediaType;
import de.wehner.mediamagpie.aws.test.util.S3TestEnvironment;

public class S3MediaRepositoryItTest {

    private static final String TEST_NAME = "test-_/mediaöäüß\"19";

    private static final String USER = "test-user";

    @Rule
    public S3TestEnvironment _s3TestEnvironment = new S3TestEnvironment();

    // private final TestEnvironment _testEnvironment = new TestEnvironment(getClass());

    private static final Date CREATION_DATE = new Date(123456);

    // private static final File SRC_TEST_PNG = new File("../mm-conductor/src/test/resources/images/image1.png");

    private static final File SRC_TEST_JPG = new File("../mm-conductor/src/test/resources/images/accept.png");

    @Test
    public void test_addMedia() throws FileNotFoundException {
        org.junit.Assume.assumeTrue(_s3TestEnvironment.getS3Credentials() != null);

        S3MediaRepository repository = new S3MediaRepository(_s3TestEnvironment.getS3Credentials());

        MediaExport mediaExport = createTestMediaExport(TEST_NAME);

        repository.addMedia(USER, mediaExport);
    }

    @Test
    public void test_iteratorPhotos_readObjectFromPreviousTest() throws IOException {
        org.junit.Assume.assumeTrue(_s3TestEnvironment.getS3Credentials() != null);

        S3MediaRepository repository = new S3MediaRepository(_s3TestEnvironment.getS3Credentials());

        Iterator<MediaExport> it = repository.iteratorPhotos(USER);

        List<MediaExport> mediaExportsFromS3 = new ArrayList<MediaExport>();
        while (it.hasNext()) {
            MediaExport mediaExport = it.next();
            System.out.println(" found: '" + mediaExport.getName() + "', size: " + mediaExport.getLength());
            mediaExportsFromS3.add(mediaExport);
        }
        assertThat(mediaExportsFromS3).hasSize(1);
        MediaExport mediaExportFromS3 = mediaExportsFromS3.get(0);
        MediaExport mediaExport = createTestMediaExport(TEST_NAME);
        assertThat(mediaExportFromS3.getCreationDate()).isEqualTo(mediaExport.getCreationDate());
        assertThat(mediaExportFromS3.getDescription()).isEqualTo(mediaExport.getDescription());
        assertThat(mediaExportFromS3.getHashValue()).isEqualTo(mediaExport.getHashValue());
        assertThat(IOUtils.toByteArray(mediaExportFromS3.getInputStream())).isEqualTo(IOUtils.toByteArray(mediaExport.getInputStream()));
        assertThat(mediaExportFromS3.getLength()).isEqualTo(SRC_TEST_JPG.length());
        assertThat(mediaExportFromS3.getName()).isEqualTo(mediaExport.getName());
        assertThat(mediaExportFromS3.getOriginalFileName()).isEqualTo(mediaExport.getOriginalFileName());
        assertThat(mediaExportFromS3.getType()).isEqualTo(mediaExport.getType());
        assertThat(mediaExportFromS3.getTags()).isEqualTo(mediaExport.getTags());
    }

    private MediaExport createTestMediaExport(String name) throws FileNotFoundException {
        MediaExport mediaExport = new MediaExport(name);
        mediaExport.setMediaId("123");
        // TODO rwe: mediaExport.setDescription("This is a picture\n i've taken <b>long</b> time ago.\n\t YES");
        mediaExport.setType(MediaType.PHOTO);
        mediaExport.setHashValue("pseudo-hash-value");
        mediaExport.setInputStream(new FileInputStream(SRC_TEST_JPG));
        mediaExport.setTags(Arrays.asList("Tag AB C", "Schöner Knipsen", "california"));
        mediaExport.setDescription("This is a sample for a description text, \nwhich contains some extra characters like äöü #'ß4§€ and \t blah.");
        return mediaExport;
    }

}

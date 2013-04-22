package de.wehner.mediamagpie.aws.s3;

import static org.fest.assertions.Assertions.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;

import de.wehner.mediamagpie.api.MediaExport;
import de.wehner.mediamagpie.api.MediaExportRepository;
import de.wehner.mediamagpie.api.testsupport.MediaExportFixture;
import de.wehner.mediamagpie.aws.test.util.S3TestEnvironment;
import de.wehner.mediamagpie.core.testsupport.TestEnvironment;
import de.wehner.mediamagpie.core.util.OrderedJUnit4ClassRunner;

@RunWith(OrderedJUnit4ClassRunner.class)
public class S3MediaRepositoryItTest {

    private static final String TEST_NAME = "test-_/mediaöäüß\"19";

    private static final String USER = "test-user";

    @ClassRule
    public static S3TestEnvironment _s3TestEnvironment = new S3TestEnvironment();

    private TestEnvironment _testEnvironment = new TestEnvironment(getClass());

    private static final File SRC_TEST_JPG = new File("../mm-conductor/src/test/resources/images/accept.png");

    private static final File SRC_TEST_JPG2 = new File("../mm-conductor/src/test/resources/images/1600x4.jpg");

    private static final File IMG_1 = new File("../mm-conductor/src/test/resources/images/IMG_1414.JPG");

    private static final File IMG_2 = new File("../mm-conductor/src/test/resources/images/IMG_0013.JPG");

    private MediaExportRepository _repository;

    @Before
    public void setUp() {
        org.junit.Assume.assumeTrue(_s3TestEnvironment.getS3Credentials() != null);
        _repository = new S3MediaExportRepository(_s3TestEnvironment.getS3Credentials());
        _testEnvironment.cleanWorkingDir();
    }

    @Test
    public void test_addMedia() throws FileNotFoundException {
        MediaExport mediaExport = MediaExportFixture.createMediaExportTestObject(123, TEST_NAME, SRC_TEST_JPG);

        _repository.addMedia(USER, mediaExport);

        mediaExport = MediaExportFixture.createMediaExportTestObject(124, "test-name-2", SRC_TEST_JPG2);

        _repository.addMedia(USER, mediaExport);
    }

    @Test
    public void test_iteratorPhotos_readObjectFromPreviousTest() throws IOException {
        Iterator<MediaExport> it = _repository.iteratorPhotos(USER);

        List<MediaExport> mediaExportsFromS3 = new ArrayList<MediaExport>();
        while (it.hasNext()) {
            MediaExport mediaExport = it.next();
            System.out.println(" found: '" + mediaExport.getName() + "', size: " + mediaExport.getLength());
            mediaExportsFromS3.add(mediaExport);
        }
//        assertThat(mediaExportsFromS3).hasSize(2);
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

    @Test
    public void push2MediasToS3PullAndVerifyContent() throws IOException {
        MediaExport mediaExport1 = MediaExportFixture.createMediaExportTestObject(1, "image01", IMG_1);
        MediaExport mediaExport2 = MediaExportFixture.createMediaExportTestObject(2, "image02", IMG_2);

        _repository.addMedia(USER, mediaExport1);
        _repository.addMedia(USER, mediaExport2);

        Iterator<MediaExport> it = _repository.iteratorPhotos(USER);
        List<MediaExport> mediaExportsFromS3 = new ArrayList<MediaExport>();
        while (it.hasNext()) {
            MediaExport mediaExport = it.next();
            System.out.println(" found: '" + mediaExport.getName() + "', hash: " + mediaExport.getHashValue() + ", size: " + mediaExport.getLength());
            mediaExportsFromS3.add(mediaExport);
        }

        // dump input streams from MediaExport to file
        int i = 0;
        List<File> dumpedFiles = new ArrayList<File>();
        for (MediaExport mediaExport : mediaExportsFromS3) {
            File dumpFile = new File(_testEnvironment.getWorkingDir(), "dump" + (i++) + ".jpg");
            System.out.println("write file " + dumpFile.getPath());
            FileOutputStream fos = new FileOutputStream(dumpFile);
            int bytesCopied = IOUtils.copy(mediaExport.getInputStream(), fos);
            System.out.println(" copied " + bytesCopied + " to output stream for media " + mediaExport.getHashValue());
            IOUtils.closeQuietly(fos);
            IOUtils.closeQuietly(mediaExport.getInputStream());
            dumpedFiles.add(dumpFile);
        }

        assertThat(IMG_1).hasSameContentAs(dumpedFiles.get(0));
        assertThat(IMG_2).hasSameContentAs(dumpedFiles.get(3));
    }

}

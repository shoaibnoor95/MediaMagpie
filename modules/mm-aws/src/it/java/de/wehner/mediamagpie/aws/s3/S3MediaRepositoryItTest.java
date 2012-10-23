package de.wehner.mediamagpie.aws.s3;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.Date;

import org.junit.Rule;
import org.junit.Test;

import de.wehner.mediamagpie.api.MediaExport;
import de.wehner.mediamagpie.aws.test.util.S3TestEnvironment;

public class S3MediaRepositoryItTest {

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

        MediaExport mediaExport = new MediaExport("media1");
        mediaExport.setHashValue("pseudo-hash-value");
        mediaExport.setInputStream(new FileInputStream(SRC_TEST_JPG));
        
        repository.addMedia("test-user", mediaExport);
    }

}

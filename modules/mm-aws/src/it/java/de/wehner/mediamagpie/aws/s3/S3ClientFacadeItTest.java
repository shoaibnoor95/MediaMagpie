package de.wehner.mediamagpie.aws.s3;

import static org.fest.assertions.Assertions.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;

import com.amazonaws.services.s3.model.ObjectListing;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectInputStream;
import com.amazonaws.services.s3.model.S3ObjectSummary;

import de.wehner.mediamagpie.aws.s3.in.S3ObjectIterator;
import de.wehner.mediamagpie.aws.test.util.S3TestEnvironment;
import de.wehner.mediamagpie.common.test.util.TestEnvironment;

public class S3ClientFacadeItTest {

    @Rule
    public S3TestEnvironment _s3TestEnvironment = new S3TestEnvironment();

    private TestEnvironment _testEnvironment = new TestEnvironment(getClass());

    private static final String JUNIT_BUCKET_NAME = "junitbuckettest";

    private static final String JUNIT_KEY_NAME = "junit/key/name";

    private static final Date CREATION_DATE = new Date(123456);

    private static final File SRC_TEST_JPG = new File("../mm-conductor/src/test/resources/images/accept.png");

    private S3ClientFacade s3ClientFacade;

    private ObjectMetadata metadata;

    @Before
    public void setup() {
        org.junit.Assume.assumeTrue(_s3TestEnvironment.getS3Credentials() != null);

        s3ClientFacade = new S3ClientFacade(_s3TestEnvironment.getS3Credentials());
        metadata = new ObjectMetadata();
        metadata.setContentLength(SRC_TEST_JPG.length());

        _testEnvironment.cleanWorkingDir();
    }

    @Test
    public void test_createBucketIfNotExists() throws FileNotFoundException {
        org.junit.Assume.assumeTrue(_s3TestEnvironment.getS3Credentials() != null);

        // create new one
        s3ClientFacade.createBucketIfNotExists(JUNIT_BUCKET_NAME);

        // be sure no exception arised although the bucket is present
        s3ClientFacade.createBucketIfNotExists(JUNIT_BUCKET_NAME);
    }

    @Test
    public void test_putObject() throws IOException {
        org.junit.Assume.assumeTrue(_s3TestEnvironment.getS3Credentials() != null);

        InputStream is = new FileInputStream(SRC_TEST_JPG);
        try {
            s3ClientFacade.putObject(JUNIT_BUCKET_NAME, JUNIT_KEY_NAME, is, metadata);
        } finally {
            IOUtils.closeQuietly(is);
        }

        S3Object objectRead = s3ClientFacade.getObjectIfExists(JUNIT_BUCKET_NAME, JUNIT_KEY_NAME);

        assertThat(objectRead).isNotNull();
        S3ObjectInputStream objectContent = objectRead.getObjectContent();
        byte[] dataRead = IOUtils.toByteArray(objectContent);
        assertThat(FileUtils.readFileToByteArray(SRC_TEST_JPG)).isEqualTo(dataRead);
    }

    @Test
    public void test_transferMultipart() throws IOException {
        org.junit.Assume.assumeTrue(_s3TestEnvironment.getS3Credentials() != null);

        InputStream is = new FileInputStream(SRC_TEST_JPG);
        try {
            s3ClientFacade.transferMultipart(JUNIT_BUCKET_NAME, JUNIT_KEY_NAME, is, metadata);
        } finally {
            IOUtils.closeQuietly(is);
        }

        S3Object objectRead = s3ClientFacade.getObjectIfExists(JUNIT_BUCKET_NAME, JUNIT_KEY_NAME);

        assertThat(objectRead).isNotNull();
        S3ObjectInputStream objectContent = objectRead.getObjectContent();
        byte[] dataRead = IOUtils.toByteArray(objectContent);
        assertThat(FileUtils.readFileToByteArray(SRC_TEST_JPG)).isEqualTo(dataRead);
    }

    @Test
    @Ignore("just to get in touch with listObject()...")
    public void test_listObjects() throws IOException {
        org.junit.Assume.assumeTrue(_s3TestEnvironment.getS3Credentials() != null);

        // put test files into s3
        File fileA = new File(_testEnvironment.getWorkingDir(), "a.txt");
        File fileB = new File(_testEnvironment.getWorkingDir(), "b.txt");
        FileUtils.writeStringToFile(fileA, "File A contains some text.");
        FileUtils.writeStringToFile(fileB, "File B contains some text, too.");
        s3ClientFacade.getS3().putObject(JUNIT_BUCKET_NAME, "junit/testlistObjects/a", fileA);
        s3ClientFacade.getS3().putObject(JUNIT_BUCKET_NAME, "junit/testlistObjects/sub/b", fileB);

        ObjectListing listObjects = s3ClientFacade.getS3().listObjects(JUNIT_BUCKET_NAME);
        for (S3ObjectSummary objectSummary : listObjects.getObjectSummaries()) {
            System.out.println("all - " + objectSummary.getKey() + "  " + "(size = " + objectSummary.getSize() + ")");
        }
        System.out.println();

        listObjects = s3ClientFacade.getS3().listObjects(JUNIT_BUCKET_NAME, "junit/testlistObjects/");
        for (S3ObjectSummary objectSummary : listObjects.getObjectSummaries()) {
            System.out.println("prefix - " + objectSummary.getKey() + "  " + "(size = " + objectSummary.getSize() + ")");
        }
    }

    @Test
    public void test_iterate() throws IOException {
        org.junit.Assume.assumeTrue(_s3TestEnvironment.getS3Credentials() != null);

        // put test files into s3
        File fileA = new File(_testEnvironment.getWorkingDir(), "a.txt");
        File fileB = new File(_testEnvironment.getWorkingDir(), "b.txt");
        File fileC = new File(_testEnvironment.getWorkingDir(), "b.txt");
        FileUtils.writeStringToFile(fileA, "File A contains some text.");
        FileUtils.writeStringToFile(fileB, "File B contains some text, too.");
        FileUtils.writeStringToFile(fileB, "File C contains some text, too.");
        s3ClientFacade.getS3().putObject(JUNIT_BUCKET_NAME, "junit/testlistObjects/a", fileA);
        s3ClientFacade.getS3().putObject(JUNIT_BUCKET_NAME, "junit/testlistObjects/sub/b", fileB);
        s3ClientFacade.getS3().putObject(JUNIT_BUCKET_NAME, "junit/testlistObjects/sub/c", fileC);

        ObjectListing listObjects = s3ClientFacade.getS3().listObjects(JUNIT_BUCKET_NAME);
        for (S3ObjectSummary objectSummary : listObjects.getObjectSummaries()) {
            System.out.println("all - " + objectSummary.getKey() + "  " + "(size = " + objectSummary.getSize() + ")");
        }
        System.out.println();

        S3ObjectIterator iterator = s3ClientFacade.iterator(JUNIT_BUCKET_NAME, "junit/testlistObjects/");
        int counter = 0;
        while (iterator.hasNext()) {
            S3ObjectSummary objectSummary = iterator.next();
            System.out.println("prefix - " + objectSummary.getKey() + "  " + "(size = " + objectSummary.getSize() + ")");
            counter++;
        }
        assertThat(counter).isEqualTo(3);
    }
}

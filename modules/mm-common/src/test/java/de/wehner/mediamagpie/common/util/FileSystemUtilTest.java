package de.wehner.mediamagpie.common.util;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import de.wehner.mediamagpie.common.test.util.TestEnvironment;

public class FileSystemUtilTest {

    @Rule
    public TestEnvironment _testEnvironment = new TestEnvironment(getClass());

    // private FileSystemUtil _fsUtil;

    @Before
    public void setUp() {
        // _fsUtil = new FileSystemUtil();
    }

    @Test
    public void testGetNextUniqueFilename_withoutNumber() throws IOException {
        File existingFile = new File(_testEnvironment.getWorkingDir(), "myMedia.png");
        existingFile.createNewFile();

        File nextUniqueFilename = FileSystemUtil.getNextUniqueFilename(existingFile);
        assertEquals("myMedia" + FileSystemUtil.FILE_COUNTER_SEPARATOR + "1.png", nextUniqueFilename.getName());
    }

    @Test
    public void testGetNextUniqueFilename_withNumber() throws IOException {
        File existingFile = new File(_testEnvironment.getWorkingDir(), "myMedia" + FileSystemUtil.FILE_COUNTER_SEPARATOR + "1.png");
        existingFile.createNewFile();

        File nextUniqueFilename = FileSystemUtil.getNextUniqueFilename(existingFile);
        assertEquals("myMedia" + FileSystemUtil.FILE_COUNTER_SEPARATOR + "2.png", nextUniqueFilename.getName());
    }

    @Test
    public void testGetNextUniqueFilename_withNumberNumber() throws IOException {
        File existingFile = new File(_testEnvironment.getWorkingDir(), "IMG_5149.jpg");
        existingFile.createNewFile();

        File nextUniqueFilename = FileSystemUtil.getNextUniqueFilename(existingFile);
        assertEquals("IMG_5149" + FileSystemUtil.FILE_COUNTER_SEPARATOR + "1.jpg", nextUniqueFilename.getName());
    }

    @Test
    public void testGetNextUniqueFilename_withNumberNumber_TwoAttempts() throws IOException {
        File existingFile = new File(_testEnvironment.getWorkingDir(), "IMG_5149.jpg");
        existingFile.createNewFile();
        File existingFile2 = new File(_testEnvironment.getWorkingDir(), "IMG_5149" + FileSystemUtil.FILE_COUNTER_SEPARATOR + "1.jpg");
        existingFile2.createNewFile();

        File nextUniqueFilename = FileSystemUtil.getNextUniqueFilename(existingFile);
        assertEquals("IMG_5149" + FileSystemUtil.FILE_COUNTER_SEPARATOR + "2.jpg", nextUniqueFilename.getName());
    }
}

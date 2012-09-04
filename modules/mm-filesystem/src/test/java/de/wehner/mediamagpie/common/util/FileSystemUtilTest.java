package de.wehner.mediamagpie.common.util;

import static org.junit.Assert.assertEquals;

import java.io.IOException;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import de.wehner.mediamagpie.common.fslayer.IFile;
import de.wehner.mediamagpie.common.fslayer.LocalFSLayer;
import de.wehner.mediamagpie.common.test.util.TestEnvironment;

public class FileSystemUtilTest {

    @Rule
    public TestEnvironment _testEnvironment = new TestEnvironment(getClass());

    private LocalFSLayer _fsLayer;

    @Before
    public void setUp() {
        _fsLayer = new LocalFSLayer();
    }

//    @Test
//    public void testGetNextUniqueFilename_withoutNumber() throws IOException {
//        IFile existingFile = _fsLayer.createFile(_testEnvironment.getWorkingDir(), "myMedia.png");
//        existingFile.createNewFile();
//
//        IFile nextUniqueFilename = FileSystemUtil.getNextUniqueFilename(_fsLayer, existingFile);
//        assertEquals("myMedia" + FileSystemUtil.FILE_COUNTER_SEPARATOR + "1.png", nextUniqueFilename.getName());
//    }
//
//    @Test
//    public void testGetNextUniqueFilename_withNumber() throws IOException {
//        IFile existingFile = _fsLayer.createFile(_testEnvironment.getWorkingDir(), "myMedia" + FileSystemUtil.FILE_COUNTER_SEPARATOR + "1.png");
//        existingFile.createNewFile();
//
//        IFile nextUniqueFilename = FileSystemUtil.getNextUniqueFilename(_fsLayer, existingFile);
//        assertEquals("myMedia" + FileSystemUtil.FILE_COUNTER_SEPARATOR + "2.png", nextUniqueFilename.getName());
//    }
//
//    @Test
//    public void testGetNextUniqueFilename_withNumberNumber() throws IOException {
//        IFile existingFile = _fsLayer.createFile(_testEnvironment.getWorkingDir(), "IMG_5149.jpg");
//        existingFile.createNewFile();
//
//        IFile nextUniqueFilename = FileSystemUtil.getNextUniqueFilename(_fsLayer, existingFile);
//        assertEquals("IMG_5149" + FileSystemUtil.FILE_COUNTER_SEPARATOR + "1.jpg", nextUniqueFilename.getName());
//    }
//
//    @Test
//    public void testGetNextUniqueFilename_withNumberNumber_TwoAttempts() throws IOException {
//        IFile existingFile = _fsLayer.createFile(_testEnvironment.getWorkingDir(), "IMG_5149.jpg");
//        existingFile.createNewFile();
//        IFile existingFile2 = _fsLayer.createFile(_testEnvironment.getWorkingDir(), "IMG_5149" + FileSystemUtil.FILE_COUNTER_SEPARATOR + "1.jpg");
//        existingFile2.createNewFile();
//
//        IFile nextUniqueFilename = FileSystemUtil.getNextUniqueFilename(_fsLayer, existingFile);
//        assertEquals("IMG_5149" + FileSystemUtil.FILE_COUNTER_SEPARATOR + "2.jpg", nextUniqueFilename.getName());
//    }
}

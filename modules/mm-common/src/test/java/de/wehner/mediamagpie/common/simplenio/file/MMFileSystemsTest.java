package de.wehner.mediamagpie.common.simplenio.file;

import static org.fest.assertions.Assertions.assertThat;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Random;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.junit.Before;
import org.junit.Test;

import de.wehner.mediamagpie.common.test.util.TestEnvironment;

public class MMFileSystemsTest {

    private final TestEnvironment _testEnvironment = new TestEnvironment(getClass());

    @Before
    public void setUp() {
        _testEnvironment.cleanWorkingDir();
    }

    @Test
    public void testGetPath() {
        MMPath path = MMFileSystems.getDefault().getPath("myPath", "subPathA", "subPathB");
        System.out.printf("Created path (%s) of type %s%n", path, path.getClass().getName());
    }

    @Test
    public void testCreateFile() throws IOException {
        MMPath path = MMPaths.get(_testEnvironment.getWorkingDir().getPath(), "fileA.txt");
        MMFiles.createFile(path);

        assertThat(MMFiles.exists(path)).isTrue();
    }

    @Test
    public void testDeleteFile() throws IOException {
        MMPath path = MMPaths.get(_testEnvironment.getWorkingDir().getPath(), "fileB.txt");
        MMFiles.createFile(path);
        assertThat(MMFiles.exists(path)).isTrue();

        MMFiles.delete(path);
        assertThat(MMFiles.exists(path)).isFalse();
    }

    @Test
    public void testWriteIntoOutputStream() throws IOException {
        MMPath path = MMPaths.get(_testEnvironment.getWorkingDir().getPath(), "foo.txt");

        OutputStream os = MMFiles.newOutputStream(path);
        IOUtils.write("blah", os);
        IOUtils.closeQuietly(os);

        assertThat(FileUtils.readFileToString(new File(_testEnvironment.getWorkingDir(), "foo.txt"))).isEqualTo("blah");
    }

    @Test
    public void testWriteIntoOutputStream_BigData() throws IOException {
        File testFile = new File(_testEnvironment.getWorkingDir().getPath(), "read.test");
        MMPath path = MMPaths.get(testFile.getParentFile().getPath(), testFile.getName());

        OutputStream os = MMFiles.newOutputStream(path);
        byte[] data = createRandomBytes(12 * 1024 * 1024);
        IOUtils.write(data, os);
        IOUtils.closeQuietly(os);

        // verify, file contains exactly the same bytes as data
        assertThat(FileUtils.readFileToByteArray(testFile)).isEqualTo(data);
    }

    @Test
    public void testReadFromInputStream() throws IOException {
        File testFile = new File(_testEnvironment.getWorkingDir().getPath(), "read.test");
        String TEST_STRING = "This is my special file content with some extra characters like 'äöüß'.";
        FileUtils.writeStringToFile(testFile, TEST_STRING);
        MMPath path = MMPaths.get(testFile.getParentFile().getPath(), testFile.getName());

        InputStream is = MMFiles.newInputStream(path);
        ByteArrayOutputStream contentFromFile = new ByteArrayOutputStream();
        IOUtils.copy(is, contentFromFile);

        assertThat(contentFromFile.toByteArray()).isEqualTo(FileUtils.readFileToByteArray(testFile));
    }

    @Test
    public void testReadFromInputStream_BigData() throws IOException {
        File testFile = new File(_testEnvironment.getWorkingDir().getPath(), "read.test");
        byte[] data = createRandomBytes(12 * 1024 * 1024);
        FileUtils.writeByteArrayToFile(testFile, data);
        assertThat(data).isEqualTo(FileUtils.readFileToByteArray(testFile));

        // copy content form input stream to ByteArrayOutputStream
        MMPath path = MMPaths.get(testFile.getParentFile().getPath(), testFile.getName());
        InputStream is = MMFiles.newInputStream(path);
        ByteArrayOutputStream contentFromFile = new ByteArrayOutputStream();
        IOUtils.copy(is, contentFromFile);

        assertThat(contentFromFile.toByteArray()).isEqualTo(data);
    }

    @Test
    public void testMoveFile() throws IOException {
        File srcFile = new File(_testEnvironment.getWorkingDir().getPath(), "src.test");
        File targetFile = new File(_testEnvironment.getWorkingDir().getPath(), "target.test");
        String TEST_STRING = "Some content of file";
        FileUtils.writeStringToFile(srcFile, TEST_STRING);
        // rwe: equals to: MMUnixFileSystem.getPath();
        MMPath path = MMPaths.get(srcFile.getParentFile().getPath(), srcFile.getName());
        MMPath pathTarget = MMPaths.get(targetFile.getParentFile().getPath(), targetFile.getName());

        MMFiles.move(path, pathTarget);

        assertThat(srcFile).doesNotExist();
        assertThat(targetFile).exists();
        assertThat(FileUtils.readFileToString(targetFile)).isEqualTo(TEST_STRING);
    }

    @Test
    public void testSize() throws IOException {
        File testFile = new File(_testEnvironment.getWorkingDir().getPath(), "read.test");
        String TEST_STRING = "Some content of file";
        FileUtils.writeStringToFile(testFile, TEST_STRING);
        MMPath path = MMPaths.get(testFile.getParentFile().getPath(), testFile.getName());

        assertThat(MMFiles.size(path)).isEqualTo(20);
    }

    @Test
    public void testMkDir() throws IOException {
        File dirFile = new File(_testEnvironment.getWorkingDir().getPath(), "newSubDir");
        MMPath dir = MMPaths.get(dirFile.getPath());
        MMFiles.createDirectory(dir);
        
        assertThat(dirFile).isDirectory();
    }

    private byte[] createRandomBytes(int length) {
        Random random = new Random();
        byte[] testBytes = new byte[length];
        random.nextBytes(testBytes);
        return testBytes;
    }

}

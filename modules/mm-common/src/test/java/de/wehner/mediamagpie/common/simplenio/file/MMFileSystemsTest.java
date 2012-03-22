package de.wehner.mediamagpie.common.simplenio.file;

import static org.fest.assertions.Assertions.assertThat;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;

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
    public void testWriteIntoOutputStream() throws IOException{
        MMPath path = MMPaths.get(_testEnvironment.getWorkingDir().getPath(), "foo.txt");

        OutputStream os = MMFiles.newOutputStream(path);
        IOUtils.write("blah", os);

        assertThat(FileUtils.readFileToString(new File(_testEnvironment.getWorkingDir(), "foo.txt"))).isEqualTo("blah");

    }
    @Test
    public void testCopyFile() {
    }

    @Test
    public void testMoveFile() {
    }

    @Test
    public void testWriteFile() {
    }

    @Test
    public void testReadFile() {
    }

    @Test
    public void testgetLength() {
    }

    @Test
    public void testMkDir() {
    }

}

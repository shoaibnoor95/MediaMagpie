package de.wehner.mediamagpie.common.fslayer;

import static org.fest.assertions.Assertions.assertThat;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.junit.Before;
import org.junit.Test;

import de.wehner.mediamagpie.common.test.util.TestEnvironment;

public class LocalFSLayerTest {

    private TestEnvironment _testEnvironment = new TestEnvironment(getClass());
    private LocalFSLayer _localFsLayer = new LocalFSLayer();

    @Before
    public void setUp() {
        _testEnvironment.cleanWorkingDir();
    }

    @Test
    public void testCreateFile_1() {
        File expectedFile = new File(_testEnvironment.getWorkingDir(), "foo.txt");

        IFile createFile = _localFsLayer.createFile(expectedFile.getPath());

        assertThat(createFile.getPath()).isEqualTo(expectedFile.getPath());
    }

    @Test
    public void testGetOutputStream() throws IOException {
        File expectedFile = new File(_testEnvironment.getWorkingDir(), "foo.txt");
        IFile createFile = _localFsLayer.createFile(expectedFile.getPath());

        OutputStream os = createFile.getOutputStream();
        IOUtils.write("blah", os);

        assertThat(FileUtils.readFileToString(expectedFile)).isEqualTo("blah");
    }

    @Test
    public void testGetOutputStream_GetInputStream() throws IOException {
        File expectedFile = new File(_testEnvironment.getWorkingDir(), "foo.txt");
        IFile iFile = _localFsLayer.createFile(expectedFile.getPath());

        OutputStream os = iFile.getOutputStream();
        IOUtils.write("blah", os);
        os.close();

        IFile fileFromFs = _localFsLayer.createFile(iFile.getPath());
        assertThat(IOUtils.toString(fileFromFs.getInputStream())).isEqualTo("blah");
    }

}

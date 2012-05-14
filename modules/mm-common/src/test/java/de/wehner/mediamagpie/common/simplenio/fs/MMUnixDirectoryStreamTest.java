package de.wehner.mediamagpie.common.simplenio.fs;

import static org.fest.assertions.Assertions.assertThat;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.junit.Before;
import org.junit.Test;

import de.wehner.mediamagpie.common.simplenio.file.MMDirectoryStream;
import de.wehner.mediamagpie.common.simplenio.file.MMDirectoryStream.Filter;
import de.wehner.mediamagpie.common.simplenio.file.MMFiles;
import de.wehner.mediamagpie.common.simplenio.file.MMPath;
import de.wehner.mediamagpie.common.simplenio.file.MMPaths;
import de.wehner.mediamagpie.common.test.util.TestEnvironment;

public class MMUnixDirectoryStreamTest {

    private final TestEnvironment _testEnvironment = new TestEnvironment(getClass());

    @Before
    public void setUp() {
        _testEnvironment.cleanWorkingDir();
    }

    @Test
    public void testNewDirectoryStream() throws IOException {
        File testSubDir = new File(_testEnvironment.getWorkingDir().getPath(), "mySubDir");
        testSubDir.mkdir();
        File testSubDir2 = new File(_testEnvironment.getWorkingDir().getPath(), "mySubDir2");
        testSubDir2.mkdir();
        MMPath path = MMPaths.get(testSubDir.getParentFile().getPath());

        MMDirectoryStream<MMPath> directoryStream = MMFiles.newDirectoryStream(path);

        List<MMPath> asList = new ArrayList<MMPath>();
        for (MMPath mmPath : directoryStream) {
            asList.add(mmPath);
        }

        assertThat(asList.size()).isEqualTo(2);
    }

    @Test
    public void testNewDirectoryStream_ListFilesWithSpecialChars() throws IOException {
        File testSubDir = new File(_testEnvironment.getWorkingDir().getPath(), "mySubDir");
        testSubDir.mkdir();
        File testFile = new File(testSubDir, "Datei mit Sonderzeichen š Š Ÿ §.txt");
        FileUtils.writeStringToFile(testFile, "foo content");
        MMPath path = MMPaths.get(testSubDir.getPath());

        MMDirectoryStream<MMPath> directoryStream = MMFiles.newDirectoryStream(path);

        List<MMPath> asList = new ArrayList<MMPath>();
        for (MMPath mmPath : directoryStream) {
            asList.add(mmPath);
        }

        assertThat(asList.size()).isEqualTo(1);
        assertThat(asList.get(0).toString()).isEqualTo(testFile.getPath());
    }

    @Test
    public void testNewDirectoryStream_WithFilter() throws IOException {
        File testSubDir = new File(_testEnvironment.getWorkingDir().getPath(), "mySubDir");
        testSubDir.mkdir();
        File testSubDir2 = new File(_testEnvironment.getWorkingDir().getPath(), "mySubDir2");
        testSubDir2.mkdir();
        Filter<MMPath> filter = new MMDirectoryStream.Filter<MMPath>() {

            @Override
            public boolean accept(MMPath entry) throws IOException {
                String name = new File(entry.toString()).getName();
                return name.endsWith("2");
            }

        };
        MMPath path = MMPaths.get(testSubDir.getParentFile().getPath());

        MMDirectoryStream<MMPath> directoryStream = MMFiles.newDirectoryStream(path, filter);

        List<MMPath> asList = new ArrayList<MMPath>();
        for (MMPath mmPath : directoryStream) {
            asList.add(mmPath);
        }

        assertThat(asList.size()).isEqualTo(1);
    }

}

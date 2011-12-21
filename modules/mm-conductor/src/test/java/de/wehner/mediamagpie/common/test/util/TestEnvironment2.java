package de.wehner.mediamagpie.common.test.util;

import static org.junit.Assert.*;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.junit.Ignore;
import org.junit.rules.ExternalResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

//TODO rwe: better use TestEnvironment and remove this one
@Deprecated
@Ignore
public class TestEnvironment2 extends ExternalResource {

    private static final Logger LOG = LoggerFactory.getLogger(TestEnvironment2.class);

    public static final String BASE_JUNIT_TEST_DIR = "target/junit_data";

    private File _testDirWithFiles;
    private File _targetTestDir;

    public TestEnvironment2(Class<?> testClass) {
        this(new File("target/junittest", testClass.getSimpleName()));
    }

    public TestEnvironment2(File targetTestDir) {
        this(null, targetTestDir);
    }

    public TestEnvironment2(File testDirWithFiles, File targetTestDir) {
        super();
        _testDirWithFiles = testDirWithFiles;
        _targetTestDir = targetTestDir;
    }

    @Override
    protected void after() {
        super.after();
    }

    @Override
    protected void before() throws Throwable {
        super.before();
        if (_targetTestDir != null) {
            cleanTestDir(_targetTestDir);
        }
        if (_testDirWithFiles != null) {
            copyTestFilesIntoCleanTestDir(_testDirWithFiles, _targetTestDir);
        }
    }

    public static void copyTestFilesIntoCleanTestDir(File testDirWithFiles, File targetTestDir) throws IOException {
        cleanTestDir(targetTestDir);
        synchronized (testDirWithFiles) {
            LOG.info("Copy testdata from '" + testDirWithFiles + "' to '" + targetTestDir + "'.");
            FileUtils.copyDirectory(testDirWithFiles, targetTestDir);
        }
    }

    public static void cleanTestDir(File targetTestDir) throws IOException {
        synchronized (targetTestDir) {
            if (targetTestDir.exists()) {
                LOG.info("Clean target test directory '" + targetTestDir + "'.");
                FileUtils.cleanDirectory(targetTestDir);
            }
            LOG.info("Create target test directory '" + targetTestDir + "'.");
            targetTestDir.mkdirs();
        }
    }

    public static boolean isTempSystemFile(String fileName) {
        return fileName.equals(".DS_Store");
    }

    public static File[] listFiles(File dir, final String wildcardMatcher) {
        return dir.listFiles(new FilenameFilter() {

            @Override
            public boolean accept(File dir, String name) {
                boolean matches = true;

                if (wildcardMatcher != null) {
                    matches = FilenameUtils.wildcardMatch(name, wildcardMatcher);
                }
                if (matches) {
                    return !TestEnvironment2.isTempSystemFile(name);
                }
                return matches;
            }
        });

    }

    public static void verifyUnorderedLines(String[] expected, List<String> given) {
        assertEquals(expected.length, given.size());
        Set<String> givenSet = new HashSet<String>(given);
        for (String expectedLine : expected) {
            if (!givenSet.contains(expectedLine)) {
                fail("expected '" + expectedLine + "' is not available.");
            }
        }
    }

    public File getTargetTestDir() {
        return _targetTestDir;
    }

}

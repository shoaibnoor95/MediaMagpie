package de.wehner.mediamagpie.common.test.util;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang.builder.ReflectionToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
import org.junit.Ignore;
import org.junit.rules.ExternalResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Helper class for junit tests which provides a working directory for each test case.
 * <p>
 * Usage:<br/>
 * Add a new attribute of this class into your junit test class with {@linkplain @Rule} annotation.
 * 
 * <pre>
 * public class MyTest {
 *   ...
 *   &#064;Rule
 *   public TestEnvironment _testEnvironment = new TestEnvironment(getClass());
 *   ...
 *   &#064;Before
 *   public void setUp() {
 *      // clean or create if necessary the working dir
 *      _testEnvironment.cleanWorkingDir();
 *   }
 *   
 *   &#064;Test
 *   public void test() {
 *      File myNewTestFile = new File(_testEnvironment.getWorkingDir(), "newFile.txt");
 *      ...
 *   }
 * </pre>
 * 
 * </p>
 * 
 * @author rwe-extern
 * 
 */
@Ignore
public class TestEnvironment extends ExternalResource {

    private static final Logger LOG = LoggerFactory.getLogger(TestEnvironment.class);

    /**
     * The directory which will be used for test output
     */
    private File _workingDir;

    public TestEnvironment(Class<?> testClass) {
        this(testClass.getSimpleName());
    }

    public TestEnvironment(String testName) {
        _workingDir = new File("target/junit", testName);
    }

    @Override
    protected void after() {
        super.after();
    }

    @Override
    protected void before() throws Throwable {
        super.before();
        if (_workingDir != null) {
            cleanWorkingDir();
        }
    }

    public File getWorkingDir() {
        return _workingDir;
    }

    public static void cleanDir(File dirToClean) throws IOException {
        synchronized (dirToClean) {
            if (dirToClean.exists()) {
                LOG.info("Clean directory '" + dirToClean + "'.");
                FileUtils.cleanDirectory(dirToClean);
            }
            LOG.info("Create directory '" + dirToClean + "'.");
            dirToClean.mkdirs();
        }
    }

    public void cleanWorkingDir() {
        try {
            cleanDir(_workingDir);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void copyFilesIntoWorkingDir(File... testFiles) throws IOException {
        synchronized (_workingDir) {
            for (File testFile : testFiles) {
                LOG.info("Copy test file '" + testFile.getPath() + "' into target dir '" + _workingDir.getPath() + "'.");
                FileUtils.copyFileToDirectory(testFile, _workingDir);
            }
        }
    }

    public File copyFileIntoWorkingDir(File srcFile, String destFileName) throws IOException {
        synchronized (_workingDir) {
            LOG.info("Copy test file '" + srcFile.getPath() + "' into target dir '" + _workingDir.getPath() + "' as '" + destFileName + "'.");
            File destFile = new File(_workingDir, destFileName);
            FileUtils.copyFile(srcFile, destFile);
            return destFile;
        }
    }

    public static boolean isTempSystemFile(String fileName) {
        return fileName.equals(".DS_Store");
    }

    public File[] listFilesInWorkingDir(String wildcardMatcher) {
        return listFiles(getWorkingDir(), wildcardMatcher);
    }

    public static File[] listFiles(File folder, final String wildcardMatcher) {
        File[] files = folder.listFiles(new FilenameFilter() {

            @Override
            public boolean accept(File dir, String name) {
                boolean matches = true;

                if (wildcardMatcher != null) {
                    matches = FilenameUtils.wildcardMatch(name, wildcardMatcher);
                }
                if (matches) {
                    return !TestEnvironment.isTempSystemFile(name);
                }
                return matches;
            }
        });
        if (files == null) {
            LOG.info("Directory '" + folder.getPath() + "' does not exists.");
            return new File[0];
        }
        return files;
    }

    @Override
    public String toString() {
        return ReflectionToStringBuilder.toString(this, ToStringStyle.SHORT_PREFIX_STYLE);
    }

}

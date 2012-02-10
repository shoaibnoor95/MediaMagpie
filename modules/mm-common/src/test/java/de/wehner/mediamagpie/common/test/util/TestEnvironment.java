package de.wehner.mediamagpie.common.test.util;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOCase;
import org.apache.commons.io.filefilter.AbstractFileFilter;
import org.apache.commons.io.filefilter.IOFileFilter;
import org.apache.commons.io.filefilter.TrueFileFilter;
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
        return listFiles(getWorkingDir(), wildcardMatcher, IOCase.SENSITIVE);
    }

    /**
     * Lists all files within a directory using a wildcard pattern.
     * <p>
     * If you need to search for files recursively, try this solution:
     * 
     * <pre>
     * FileUtils.listFiles(directory, new WildcardFileFilter(&quot;*.txt&quot;), TrueFileFilter.INSTANCE)
     * </pre>
     * 
     * </p>
     * 
     * @param folder
     *            The folder to search within
     * @param wildcardMatcher
     *            The wildcard pattern
     * @param ioCase
     *            The search mode.
     * @return A list of files within given directory
     */
    public static File[] listFiles(File folder, final String wildcardMatcher, final IOCase ioCase) {
        List<File> files = listFiles(folder, wildcardMatcher, ioCase, false);
        Collections.sort(files);
        return files.toArray(new File[0]);
    }

    public static List<File> listFilesRecursive(File folder, final String wildcardMatcher) {
        return listFiles(folder, wildcardMatcher, IOCase.INSENSITIVE, true);
    }

    public static List<File> listFiles(File folder, final String wildcardMatcher, final IOCase ioCase, boolean recursive) {
        if (!folder.exists()) {
            LOG.info("Directory '" + folder.getPath() + "' does not exists.");
            return null;//Collections.emptyList();
        }

        IOFileFilter fileFilter = new AbstractFileFilter() {

            @Override
            public boolean accept(File file) {
                if (file.isDirectory()) {
                    return false;
                }
                boolean matches = true;
                if (wildcardMatcher != null) {
                    matches = FilenameUtils.wildcardMatch(file.getName(), wildcardMatcher, ioCase);
                }
                if (matches) {
                    return !TestEnvironment.isTempSystemFile(file.getName());
                }
                return matches;
            }

        };
        List<File> allFiles = new ArrayList<File>(FileUtils.listFiles(folder, fileFilter, recursive ? TrueFileFilter.INSTANCE : null));
        return allFiles;
    }

    @Override
    public String toString() {
        return ReflectionToStringBuilder.toString(this, ToStringStyle.SHORT_PREFIX_STYLE);
    }

}

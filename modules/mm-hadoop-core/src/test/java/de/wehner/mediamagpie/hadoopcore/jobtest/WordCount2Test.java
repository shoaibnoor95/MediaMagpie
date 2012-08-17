package de.wehner.mediamagpie.hadoopcore.jobtest;

import static org.fest.assertions.Assertions.*;

import java.io.File;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.mapred.JobConf;
import org.junit.Before;
import org.junit.Test;

import de.wehner.mediamagpie.common.test.util.TestEnvironment;

public class WordCount2Test {

    private final TestEnvironment _testEnvironment = new TestEnvironment(getClass());

    @Before
    public void setUp() {
        _testEnvironment.cleanWorkingDir();
    }

    @Test
    public void testWordCount2() throws Exception {
        File input = new File("src/test/resources/input");
        File output = new File(_testEnvironment.getWorkingDir(), "output");

        WordCount2.main(new String[] { input.getPath(), output.getPath() });

        assertThat(new File(output, "part-00000")).exists();
    }

    @Test
    public void testWordCount2_UseDistributedCache() throws Exception {
        File input = new File("src/test/resources/input");
        File output = new File(_testEnvironment.getWorkingDir(), "output");
        File patterns = new File("src/test/resources/patterns.txt");
        WordCount2.main(new String[] { input.getPath(), output.getPath(), "-skip", patterns.getPath() });

        assertThat(new File(output, "part-00000")).exists();
    }

    @Test
    public void testWordCount2_UseJobConf() throws Exception {
        File input = new File("src/test/resources/input");
        File output = new File(_testEnvironment.getWorkingDir(), "output");
        Configuration jobConf = new JobConf();
        jobConf.setBoolean("wordcount.case.sensitive", false);
        WordCount2.runWithConfiguration(jobConf, new String[] { input.getPath(), output.getPath() });

        assertThat(new File(output, "part-00000")).exists();
    }
}

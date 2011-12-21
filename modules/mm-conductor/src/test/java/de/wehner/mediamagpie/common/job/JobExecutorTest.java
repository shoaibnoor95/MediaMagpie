package de.wehner.mediamagpie.common.job;

import static org.junit.Assert.*;

import static org.mockito.Matchers.*;

import static org.mockito.Mockito.*;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import de.wehner.mediamagpie.common.persistence.entity.JobExecution;
import de.wehner.mediamagpie.common.persistence.entity.properties.MainConfiguration;
import de.wehner.mediamagpie.common.test.util.UnitTestEnvironment;
import de.wehner.mediamagpie.conductor.performingjob.JobCallable;
import de.wehner.mediamagpie.conductor.performingjob.JobExecutor;
import de.wehner.mediamagpie.conductor.performingjob.PerformingJob;
import de.wehner.mediamagpie.conductor.performingjob.PerformingJobContext;


public class JobExecutorTest {

    @Rule
    public UnitTestEnvironment _unitTestEnvironment = new UnitTestEnvironment();

    private JobExecutor _jobExecutor;
    //private ExecutionEngine _executionEngine = new HadoopExecutionEngine(HadoopUtil.createConf());
    @Mock
    private PerformingJob _dapJob;
    @Mock
    private JobCallable _dapJobCallable;
    private JobExecution _dapJobExecution;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        when(_dapJob.prepare()).thenReturn(_dapJobCallable);
        _jobExecutor = _unitTestEnvironment.createJobExecutor();
        _dapJobExecution = _unitTestEnvironment.createDapJobExecution(_dapJob);
    }

    @Test
    public void testExecute() throws Exception {
        _jobExecutor.execute(_unitTestEnvironment.getConfigurationDaoWithMainConfiguration().getConfiguration(MainConfiguration.class),  _dapJobExecution);

        InOrder inOrder = inOrder(_dapJob, _dapJobCallable);
        inOrder.verify(_dapJob).init(any(PerformingJobContext.class));
        inOrder.verify(_dapJob).prepare();
        inOrder.verify(_dapJobCallable).call();
    }

//    @Test
//    public void testCleanTmpFolder() throws Exception {
//        ExecutionEngine spiedExecutionEngine = spy(_executionEngine);
//        FileSystem fileSystem = mock(FileSystem.class);
//        when(spiedExecutionEngine.getFileSystem()).thenReturn(fileSystem);
//        _jobExecutor.execute(spiedExecutionEngine, _dapJobExecution);
//
//        InOrder inOrder = inOrder(_dapJob, _dapJobCallable, fileSystem);
//        inOrder.verify(fileSystem).delete(any(Path.class), eq(true));
//        inOrder.verify(_dapJob).init(any(DapJobContext.class));
//        inOrder.verify(_dapJob).prepare();
//        inOrder.verify(_dapJobCallable).call();
//        inOrder.verify(fileSystem).delete(any(Path.class), eq(true));
//    }

//    @Test
//    public void testLoggingIntoHdfs() throws Exception {
//        _jobExecutor.execute(_executionEngine, _dapJobExecution);
//
//        List<String> lines = readLines(0);
//        assertEquals(1, lines.size());
//    }

    @Test
    public void testLoggingErrors() throws Exception {
        doThrow(new RuntimeException("test exception")).when(_dapJob).init(any(PerformingJobContext.class));

        try {
            _jobExecutor.execute(_unitTestEnvironment.getConfigurationDaoWithMainConfiguration().getConfiguration(MainConfiguration.class), _dapJobExecution);
            fail();
        } catch (RuntimeException e) {
            // expected
        }

//        List<String> lines = readLines(0);
//        assertTrue(StringUtil.join(lines, "\n").contains("test exception"));
    }

//    private LineIterator openLineIterator(long jobId) throws IOException {
//        Path logFile = new Path(_unitTestEnvironment.createDapFileSystem().getJobLogFile(jobId));
//        FileSystem fileSystem = logFile.getFileSystem(new Configuration());
//        FSDataInputStream inputStream = fileSystem.open(logFile);
//        return new LineIterator(new InputStreamReader(inputStream));
//    }
//
//    @SuppressWarnings("unchecked")
//    private List<String> readLines(long jobId) throws IOException {
//        LineIterator lineIterator = openLineIterator(jobId);
//        try {
//            return IteratorUtils.toList(lineIterator);
//        } finally {
//            lineIterator.close();
//        }
//    }
}

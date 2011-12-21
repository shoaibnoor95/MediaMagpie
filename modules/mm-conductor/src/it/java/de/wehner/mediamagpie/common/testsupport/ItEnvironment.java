package de.wehner.mediamagpie.common.testsupport;

import static org.mockito.Matchers.*;

import static org.mockito.Mockito.*;

import java.io.File;
import java.io.IOException;

import org.junit.rules.ExternalResource;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.wehner.mediamagpie.common.persistence.entity.JobExecution;
import de.wehner.mediamagpie.common.persistence.entity.properties.MainConfiguration;
import de.wehner.mediamagpie.common.persistence.entity.properties.UserConfiguration;
import de.wehner.mediamagpie.common.test.util.TestEnvironment2;
import de.wehner.mediamagpie.conductor.performingjob.JobExecutor;
import de.wehner.mediamagpie.conductor.performingjob.JobFactory;
import de.wehner.mediamagpie.conductor.performingjob.PerformingJob;
import de.wehner.mediamagpie.conductor.persistence.TransactionHandlerMock;
import de.wehner.mediamagpie.conductor.persistence.dao.ConfigurationDao;


/**
 * Gives access to the 'environment' needed for executing dap job. Depending on the configuration it gives you a <code>Configuration</code>
 * for a distributed or a local hadoop. For a remote database or a local database and so on...
 * 
 * <p>
 * Need to set sytem property {@link #SYSTEM_PROPERTY_EC2_TESTS_ENABLED} to true in order to run a test on a distributed hadoop (ec2).
 */
public abstract class ItEnvironment extends ExternalResource {

    private static final Logger LOG = LoggerFactory.getLogger(ItEnvironment.class);

    public static final String SYSTEM_PROPERTY_EC2_TESTS_ENABLED = "ec2.tests.enabled";

    @Mock
    private ConfigurationDao _configuraitonDao;
    @Mock
    private UserConfiguration _userConfiguration;

    public enum CleanFolderInstruction {
        BEFORE_CLASS, BEFORE;
    }

    public enum ItMode {
        LOCAL_OR_DISTRIBUTED_HADOOP, EMR, SELENIUM;
    }

    private final CleanFolderInstruction _cleanInstruction;
    // protected final Configuration _hadoopConf;
    protected final File _rootTmpDirectory;
    private final JobFactory _dapJobFactory;
    private final JobExecutor _jobExecutor;

    // private int _dataSourceCounter;
    //
    // public ItEnvironment(CleanFolderInstruction cleanInstruction, Configuration hadoopConf) {
    // _cleanInstruction = cleanInstruction;
    // _hadoopConf = hadoopConf;
    // _rootTmpDirectory = new File("build", "IntTestEnvironment");
    // _rootTmpDirectory.mkdirs();
    // _dapJobFactory = mock(DapJobFactory.class);
    // _jobExecutor = new JobExecutor(_dapJobFactory, createDapFilesystemProvider());
    // if (cleanInstruction == CleanFolderInstruction.BEFORE_CLASS) {
    // HadoopUtil.deletePath(new Path(_rootTmpDirectory.getPath()), _hadoopConf);
    // }
    // }
    //
    @Override
    protected void before() throws Throwable {
        MockitoAnnotations.initMocks(this);
    };

    public ItEnvironment(CleanFolderInstruction cleanInstruction) {
        _cleanInstruction = cleanInstruction;
        // _hadoopConf = hadoopConf;
        _rootTmpDirectory = new File("target", "IntTestEnvironment");
        _rootTmpDirectory.mkdirs();
        _dapJobFactory = mock(JobFactory.class);
        _jobExecutor = new JobExecutor(_dapJobFactory, new TransactionHandlerMock());
        if (cleanInstruction == CleanFolderInstruction.BEFORE_CLASS) {
            // HadoopUtil.deletePath(new Path(_rootTmpDirectory.getPath()), _hadoopConf);
            try {
                TestEnvironment2.cleanTestDir(_rootTmpDirectory);
            } catch (IOException e) {
            }
        }
    }

    public ConfigurationDao getConfigurationDaoWithMainConfiguration() {
        MainConfiguration mainConfiguration = new MainConfiguration();
        mainConfiguration.setTempMediaPath(getTmpFile("tempMediaPath").getPath());
        when(_configuraitonDao.getConfiguration(MainConfiguration.class)).thenReturn(mainConfiguration);
        return _configuraitonDao;
    }

    // public DapFilesystemProvider createDapFilesystemProvider() {
    // return new ConstantFilesystemProvider(createDapFilesystem());
    // }
    //
    // @Override
    // protected void before() throws Throwable {
    // if (_cleanInstruction == CleanFolderInstruction.BEFORE) {
    // HadoopUtil.deletePath(new Path(_rootTmpDirectory.getPath()), _hadoopConf);
    // }
    // }
    //
    // public abstract Path getDfsTmpFile(String name) throws IOException;
    //
    // public abstract FileSystem getFileSystem() throws IOException;
    //
    // public abstract boolean isClusteredEnvironment() throws IOException;
    //
    // protected abstract void setFileConnectionPassword(FileConnection fileConnection);
    //
    // protected abstract String getImportFileUrlPrefix() throws Exception;
    //
    // protected abstract String getDbUrl(DbType dbType) throws Exception;
    //
    // /**
    // *
    // * @param localFile
    // * @return absolute file path
    // * @throws Exception
    // */
    // protected abstract String copyFileToImportableFileLocation(File localFile) throws Exception;
    //
    // /**
    // *
    // * @param filename
    // * to be located in the dap data folder (either locally in
    // * modules/dap-conductor/src/data) or in ec2 under... or in s3 under ...
    // * @return
    // * @throws Exception
    // */
    // public abstract String getAbsoluteDataPath(String filename) throws Exception;
    //
    // protected abstract FileDataAccess getSrcDataAccess();
    //
    // public Configuration createHadoopConf() {
    // Configuration configuration = new Configuration(_hadoopConf);
    // initConfiguration(configuration);
    // return configuration;
    // }
    //
    // protected abstract void initConfiguration(Configuration configuration);
    //
    public JobExecutor getJobExecutor() {
        return _jobExecutor;
    }

    //
    // public DapContext createDapContext() {
    // return new DapContext(createDapFilesystem());
    // }
    //
    // public DapFilesystem createDapFilesystem() {
    // return new DapFilesystem(_rootTmpDirectory.getPath(), _rootTmpDirectory.getPath());
    // }
    //
    public File getTmpFile(String name) {
        return new File(_rootTmpDirectory, name);
    }

    //
    public JobExecution createJobExecutionMock(PerformingJob performingJob) {
        JobExecution jobExecution = mock(JobExecution.class);
        when(_dapJobFactory.createDapJob(any(JobExecution.class))).thenReturn(performingJob);
        when(jobExecution.getId()).thenReturn(0L);
        return jobExecution;
    }

    //
    // public FileConnection createFileConnection() throws Exception {
    // FileConnection fileConnection = new FileConnection("connection", "", getImportFileUrlPrefix(), "", "");
    // fileConnection.setPassword("");// for local file since pwd i not nullable
    // setFileConnectionPassword(fileConnection);
    // return fileConnection;
    // }
    //
    // public FileDataSourceConfiguration createExampleDataRowsDatasource() throws Exception {
    //
    // return createExampleDataRowsDatasource(getSrcDataAccess());
    // }
    //
    // public FileDataSourceConfiguration createExampleDataRowsDatasource(FileDataAccess fileDataAccess) throws Exception {
    // URI uri = fileDataAccess.createInputUri("data_rows.txt");
    // // String uri = getAbsoluteDataPath("data_rows.txt");
    // FileConnection fileConnection = createFileConnection();
    // fileConnection.setUri(uri.toString());
    // if (fileDataAccess.getPassword() != null) {
    // fileConnection.setPassword(fileDataAccess.getPassword());
    // }
    //
    // String parsePattern = "([^\\t]+)\\t([^\\t]+)\\t([^\\t]+)\\t([^\\t]+)\\t([^\\t]+)\\t([^\\t]+)";
    // FileDataSourceConfiguration fileDataSource = new FileDataSourceConfiguration("dataRows", "", uri.getPath(),
    // ValidationStrategy.DROP_IMPORT, FileType.TEXT_FILE);
    // fileDataSource.setConnection(fileConnection);
    // fileDataSource.getFields().add(new Field("time", "0", FieldType.DATE));
    // fileDataSource.getFields().add(new Field("user", "1", FieldType.STRING));
    // fileDataSource.getFields().add(new Field("product", "2", FieldType.STRING));
    // fileDataSource.getFields().add(new Field("amount", "3", FieldType.INTEGER));
    // fileDataSource.getFields().add(new Field("payed", "4", FieldType.INTEGER));
    // fileDataSource.getFields().add(new Field("float", "5", FieldType.FLOAT));
    // fileDataSource.setParsePattern(parsePattern);
    //
    // Pattern.compile(parsePattern);
    // return fileDataSource;
    // }
    //
    // public FileDataSourceConfiguration createExampleDataRowsDatasourceJoinable() throws Exception {
    // String uri = getAbsoluteDataPath("data_rows_joinable.txt");
    // FileConnection fileConnection = createFileConnection();
    // setFileConnectionPassword(fileConnection);
    //
    // String parsePattern = "([^\\t]+)\\t([^\\t]+)\\t([^\\t]+)\\t([^\\t]+)";
    // FileDataSourceConfiguration fileDataSource = new FileDataSourceConfiguration("dataRowsJoinable", "", uri,
    // ValidationStrategy.DROP_IMPORT, FileType.TEXT_FILE);
    // fileDataSource.setConnection(fileConnection);
    // fileDataSource.getFields().add(new Field("product", "0", FieldType.STRING));
    // fileDataSource.getFields().add(new Field("manufacturer", "1", FieldType.STRING));
    // fileDataSource.getFields().add(new Field("city", "2", FieldType.STRING));
    // fileDataSource.getFields().add(new Field("creationDate", "3", FieldType.DATE));
    // fileDataSource.setParsePattern(parsePattern);
    //
    // Pattern.compile(parsePattern);
    // return fileDataSource;
    // }
    //
    // public FileDataSourceConfiguration createFileDatasource(FileType fileType, FileDataAccess fileDataAccess, String relativeDataFile)
    // throws Exception {
    // return createFileDatasource(fileType, fileDataAccess, fileDataAccess.createInputUri(relativeDataFile));
    // }
    //
    // public FileDataSourceConfiguration createFileDatasource(FileType fileType, FileDataAccess fileDataAccess, URI uri) throws Exception {
    // String delimiter = null;
    // String parsePattern = null;
    // String escapeCharacter = null;
    // String quoteCharacter = null;
    // boolean strictQuotes = false;
    // switch (fileType) {
    // case CSV:
    // delimiter = ",";
    // escapeCharacter = "\\";
    // quoteCharacter = "\"";
    // strictQuotes = false;
    // break;
    // case APACHE_LOG:
    // parsePattern = "%h %l %u %t \"%r\" %>s %b";
    // break;
    // case TEXT_FILE:
    // parsePattern = "(\\S+) (\\S+) (\\S+) (\\S+) (\\S+)";
    // break;
    // default:
    // throw new IllegalStateException("type " + fileType + " not supported yet");
    // }
    //
    // FileConnection fileConnection = createFileConnection();
    // fileConnection.setUri(uri.toString());
    // fileConnection.setPassword(fileDataAccess.getPassword());
    // FileDataSourceConfiguration fileDataSource = new FileDataSourceConfiguration("file data source", "...", uri.getPath(),
    // ValidationStrategy.DROP_RECORD, fileType);
    // fileDataSource.setConnection(fileConnection);
    // fileDataSource.setDelimiter(delimiter);
    // fileDataSource.setEscapeCharacter(escapeCharacter);
    // fileDataSource.setQuoteCharacter(quoteCharacter);
    // fileDataSource.setStrictQuotes(strictQuotes);
    //
    // fileDataSource.setParsePattern(parsePattern);
    // detectAndSetFields(fileDataSource, uri);
    // return fileDataSource;
    // }
    //
    // private void detectAndSetFields(FileDataSourceConfiguration fileDataSource, URI uri) throws IOException {
    // String originalFile = fileDataSource.getFile();
    // String originalUri = fileDataSource.getConnection().getUri();
    //
    // // String absoluteLocalFile = FileDataLocation.LOCAL.getTestDataFile(relativeDataFile);
    // fileDataSource.setFile(uri.getPath().toString());
    // fileDataSource.getConnection().setUri(uri.toString());
    //
    // ImportFormat<?> importFormat = fileDataSource.createImportFormat(HadoopUtil.createConf());
    // Field[] fields = importFormat.createAnalyzedSample(createDapContext(), 10).getGuessedFields();
    // fileDataSource.getFields().addAll(Arrays.asList(fields));
    //
    // fileDataSource.setFile(originalFile);
    // fileDataSource.getConnection().setUri(originalUri);
    // }
    //
    // public Connection createJdbcConnection(String tableName, DbType dbType) throws Exception {
    // return JdbcUtil.createJdbcConnection(createDatabaseDatasource(tableName, dbType));
    // }
    //
    // public DataBaseDataSourceConfiguration createDatabaseDatasource(String tableName, DbType dbType) throws Exception {
    // return createDatabaseDatasource(tableName, new Field[0], dbType);
    // }
    //
    // public DataBaseDataSourceConfiguration createDatabaseDatasource(String tableName, Field[] fields, DbType dbType) throws Exception {
    // DataBaseDataSourceConfiguration dataSource;
    // DataBaseConnection dataBaseConnection = createDbConnection(dbType);
    // if (dbType.isSame(BuiltInDbType.HSQL_FILE) || dbType.isSame(BuiltInDbType.HSQL_HTTP)) {
    // dataSource = new DataBaseDataSourceConfiguration("dap-integration hsql", "database data source for integration testing with hsql",
    // tableName, ValidationStrategy.DROP_RECORD);
    // } else if (dbType.isSame(BuiltInDbType.MYSQL)) {
    // dataSource = new DataBaseDataSourceConfiguration("dap-integration mysql", "database data source for integration testing with mysql",
    // tableName, ValidationStrategy.DROP_RECORD);
    // } else if (dbType.isSame(MsSqlDb.DB_TYPE)) {
    // dataSource = new DataBaseDataSourceConfiguration("dap-integration mssql", "database data source for integration testing with mssql",
    // tableName, ValidationStrategy.DROP_RECORD);
    // } else if (dbType.isSame(DB2Db.DB_TYPE)) {
    // dataSource = new DataBaseDataSourceConfiguration("dap-integration db2", "database data source for integration testing with db2",
    // tableName, ValidationStrategy.DROP_RECORD);
    // } else {
    // throw ExceptionUtil.convertToRuntimeException(new UnsupportedDataTypeException("datasource db type '" + dbType + "' not supported"));
    // }
    // dataSource.setConnection(dataBaseConnection);
    // for (Field field : fields) {
    // dataSource.getFields().add(field);
    // }
    // return dataSource;
    // }
    //
    // public DataBaseConnection createDbConnection(BuiltInDbType dbType) throws Exception {
    // return createDbConnection(new DbType(dbType));
    // }
    //
    // public DataBaseConnection createDbConnection(DbType dbType) throws Exception {
    // DataBaseConnection dataBaseConnection = new DataBaseConnection();
    // dataBaseConnection.setDbType(dbType);
    // if (dbType.isSame(BuiltInDbType.HSQL_FILE) || dbType.isSame(BuiltInDbType.HSQL_HTTP)) {
    // dataBaseConnection.setUserName("sa");
    // dataBaseConnection.setPassword("");
    // dataBaseConnection.setUri(getDbUrl(dbType));
    // } else if (dbType.isSame(BuiltInDbType.MYSQL)) {
    // dataBaseConnection.setUserName("dap");
    // dataBaseConnection.setPassword("dap");
    // dataBaseConnection.setUri(MySqlDb.url("localhost", 3306, "dap_integration"));
    // } else if (dbType.isSame(MsSqlDb.DB_TYPE)) {
    // // configured against local vmware
    // dataBaseConnection.setUserName("user2");
    // dataBaseConnection.setPassword("2resu23");
    // dataBaseConnection.setUri(MsSqlDb.url("192.168.221.128", 1433, "dap_integration"));
    // } else if (dbType.isSame(DB2Db.DB_TYPE)) {
    // dataBaseConnection.setUserName("db2inst1");// db2inst1
    // dataBaseConnection.setPassword("db2inst1");
    // dataBaseConnection.setUri(getDbUrl(dbType));
    // } else {
    // throw ExceptionUtil.convertToRuntimeException(new UnsupportedDataTypeException("datasource db-type '" + dbType + "' not supported"));
    // }
    // return dataBaseConnection;
    // }
    //
    // // TODO writeFileToImportableSource
    // /**
    // * @param name
    // * @param lines
    // * @return absolute file path
    // * @throws Exception
    // */
    // public String writeFile(String name, String... lines) throws Exception {
    // File tmpFile = getTmpFile(name);
    // IoTestUtil.writeFile(tmpFile, lines);
    // return copyFileToImportableFileLocation(tmpFile);
    // }
    //
    // public DataSourceData doImport(DataSourceConfiguration dataSource, int previewRecordCount, SamplingMethod samplingMethod) throws
    // IOException {
    // return doImport(new HadoopExecutionEngine(_hadoopConf), dataSource, previewRecordCount, samplingMethod);
    // }
    //
    // public DataSourceData doImport(ExecutionEngine executionEngine, DataSourceConfiguration dataSource, int previewRecordCount,
    // SamplingMethod samplingMethod) throws IOException {
    // String outputPath = _rootTmpDirectory.getPath() + "/" + _dataSourceCounter++;
    // ImportJob importJob = new ImportJob(dataSource, samplingMethod, previewRecordCount, outputPath);
    // DapJobExecution dapJobExecution = createDapJobExecution(importJob);
    // String tempDirectory = createDapFilesystem().getTempDirectory(dapJobExecution);
    // executionEngine.getFileSystem().delete(new Path(outputPath), true);
    // executionEngine.getFileSystem().delete(new Path(tempDirectory), true);
    // return (DataSourceData) _jobExecutor.execute(executionEngine, dapJobExecution).getData();
    // }
    //
    // public static ItEnvironment getItEnvironment(ItMode mode) {
    // return getItEnvironment(mode, CleanFolderInstruction.BEFORE);
    // }

    public static boolean isAwsEnabled() {
        return "true".equals(System.getProperty(SYSTEM_PROPERTY_EC2_TESTS_ENABLED));
    }

    public static void chechAwsEnabled() {
        if (!isAwsEnabled()) {
            throw new RuntimeException("aws tests not enabled - check -D" + SYSTEM_PROPERTY_EC2_TESTS_ENABLED);
        }
    }

    // public static ItEnvironment getItEnvironment(ItMode mode, CleanFolderInstruction cleanFolderInstruction) {
    // final ItEnvironment itEnvironment;
    // switch (mode) {
    // case LOCAL_OR_DISTRIBUTED_HADOOP:
    // if (isAwsEnabled()) {
    // LOG.info("using hadoop in distributed mode for testing");
    // itEnvironment = new Ec2ItEnvironment(cleanFolderInstruction);
    // } else {
    // LOG.info("using hadoop in local mode for testing");
    // itEnvironment = new LocalItEnvironment(cleanFolderInstruction);
    // }
    // break;
    // case EMR:
    // itEnvironment = new EmrItEnvironment(cleanFolderInstruction);
    // break;
    // case SELENIUM:
    // LOG.info("using hadoop in local mode and testing with Selenium");
    // itEnvironment = new SeleniumItEnvironment(cleanFolderInstruction);
    // break;
    // default:
    // throw new UnsupportedOperationException("unhandled mode: " + mode);
    // }
    // return itEnvironment;
    // }

}

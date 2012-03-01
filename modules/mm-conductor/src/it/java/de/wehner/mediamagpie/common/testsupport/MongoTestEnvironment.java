package de.wehner.mediamagpie.common.testsupport;

import java.io.File;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.builder.ReflectionToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
import org.apache.commons.lang3.StringUtils;
import org.junit.rules.ExternalResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mongodb.Mongo;

import de.wehner.mediamagpie.common.testsupport.ProcessWrapper.StdXXXLineListener;
import de.wehner.mediamagpie.common.util.SearchPathUtil;

/**
 * @author ralfwehner
 * 
 */
public class MongoTestEnvironment extends ExternalResource {

    private static final Logger LOG = LoggerFactory.getLogger(MongoTestEnvironment.class);

    /**
     * The Port mongoDB listens to. Default is
     * <code>28017<code>, but run tests parallel to the normal mongoDB, is makes more sense to select a different one.
     */
    private final int _port;
    private final File _mongoDbProgram;
    private final File _mongoDataPath = new File("target/it-mongodb/");
    private ProcessWrapper _mongoProcessWrapper;
    private Mongo _mongo;

    public MongoTestEnvironment() {
        this(28017);
    }

    public MongoTestEnvironment(int port) {
        _port = port;
        String programName = SearchPathUtil.findPath(new File(System.getProperty("user.home"), "programs/mongodb/bin/mongod").getPath(),
                "/usr/bin/mongod", "/bin/mongod");
        if (StringUtils.isEmpty(programName)) {
            programName = "mongodb";
        }
        _mongoDbProgram = new File(programName);
    }

    public Mongo getConnection() {
        return _mongo;
    }

    @Override
    protected void before() throws Throwable {
        if (!_mongoDataPath.exists()) {
            FileUtils.forceMkdir(_mongoDataPath);
        }

        final CountDownLatch dbIsRunning = new CountDownLatch(1);
        ProcessBuilder pb = new ProcessBuilder(_mongoDbProgram.getPath(), "--dbpath", _mongoDataPath.getPath(), "--port", "" + _port);
        _mongoProcessWrapper = new ProcessWrapper(pb);
        _mongoProcessWrapper.start(new StdXXXLineListener() {

            @Override
            public boolean fireNewLine(String line) {
                System.out.println(line);
                if (line.contains("waiting for connections on port " + _port)) {
                    dbIsRunning.countDown();
                }
                return true;
            }
        });

        dbIsRunning.await(5, TimeUnit.SECONDS);
        _mongo = new Mongo("localhost", _port);
        List<String> databaseNames = _mongo.getDatabaseNames();
        LOG.info("Test Mongo DB is up and running on port " + _port + ".");
        for (String name : databaseNames) {
            _mongo.dropDatabase(name);
        }
        super.before();
    }

    @Override
    protected void after() {
        if (_mongo != null) {
            _mongo.close();
        }
        if (_mongoProcessWrapper != null && _mongoProcessWrapper.isRunning()) {
            _mongoProcessWrapper.destroy();
        }
        super.after();
    }

    @Override
    public String toString() {
        return ReflectionToStringBuilder.toString(this, ToStringStyle.SHORT_PREFIX_STYLE);
    }
}

package de.wehner.mediamagpie.common.testsupport;

import java.io.File;
import java.io.IOException;
import java.net.ServerSocket;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import junit.framework.Assert;

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
        this(findFreeSocket(28017, 28050));
    }

    public MongoTestEnvironment(int port) {
        _port = port;
        String programName = null;
        try {
            programName = SearchPathUtil.findPath(new File(System.getProperty("user.home"), "programs/mongodb/bin/mongod").getPath(), "/usr/bin/mongod",
                    "/bin/mongod");
        } catch (IllegalArgumentException e) {
            LOG.info("Can not find executable for mongodb in test pathes.");
        }
        if (StringUtils.isEmpty(programName)) {
            programName = "mongod";
        }
        _mongoDbProgram = new File(programName);
    }

    public void beforeClass() {
        try {
            before();
        } catch (Throwable e) {
            Assert.fail("Unable to init MongoDB. " + e.getMessage());
        }
    }

    public void afterClass() {
        after();
    }

    @Override
    protected void before() throws Throwable {
        if (!_mongoDataPath.exists()) {
            FileUtils.forceMkdir(_mongoDataPath);
        }

        final CountDownLatch dbIsRunning = new CountDownLatch(1);
        ProcessBuilder pb = new ProcessBuilder(_mongoDbProgram.getPath(), "--dbpath", _mongoDataPath.getPath(), "--port", "" + _port);
        _mongoProcessWrapper = new ProcessWrapper(pb);
        try {
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
        } catch (IOException e) {
            LOG.warn("MongoDB cound not be started. Probably it is not installed on this system or it can not be found.");
            return;
        }

        // wait at least 5 seconds until mongo is start up and has send an
        // expected line to stdout (see lines above)
        dbIsRunning.await(5, TimeUnit.SECONDS);
        long count = dbIsRunning.getCount();
        if (count > 0) {
            // TODO rwe: verify if it makes sense to leave here...
            LOG.warn("MongoDB seems not to be started correctly.");
            // return;
        }
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

    public Mongo getConnection() {
        return _mongo;
    }

    private static int findFreeSocket(int startPort, int endPortRange) {
        if (startPort <= 0) {
            throw new IllegalArgumentException("The parameter 'startPort' must be greater than zero.");
        }
        if (endPortRange < startPort) {
            throw new IllegalArgumentException("The argument 'endPortRange' must be greater or equal to parameter 'startPort'.");
        }
        for (int testPort = startPort; testPort <= endPortRange; testPort++) {
            if (isSocketAvailable(testPort)) {
                return testPort;
            }
        }
        throw new RuntimeException("No port in range " + startPort + " - " + endPortRange + " is available.");
    }

    private static synchronized boolean isSocketAvailable(int port) {
        LOG.debug("trying to open port " + port);
        ServerSocket ss = null;
        try {
            ss = new ServerSocket(port);
            ss.setReuseAddress(true);
            return true;
        } catch (IOException e) {
            LOG.info("port " + port + " is unavailable");
        } finally {
            if (ss != null) {
                try {
                    ss.close();
                } catch (IOException e) {
                    LOG.error("Unable to close socket " + port);
                }
            }
        }
        return false;
    }

    @Override
    public String toString() {
        return ReflectionToStringBuilder.toString(this, ToStringStyle.SHORT_PREFIX_STYLE);
    }
}

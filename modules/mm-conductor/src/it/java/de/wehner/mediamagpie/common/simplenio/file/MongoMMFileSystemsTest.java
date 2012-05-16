package de.wehner.mediamagpie.common.simplenio.file;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.data.mongodb.core.MongoTemplate;

import com.mongodb.Mongo;

import de.wehner.mediamagpie.common.fslayer.mongodb.MongoDbFileDataDao;
import de.wehner.mediamagpie.common.testsupport.MongoTestEnvironment;

public class MongoMMFileSystemsTest {

    private static MongoTestEnvironment _mongoTestEnvironment;

    private MMFileSystemsTest mmFileSystemsTest = new MMFileSystemsTest();

    @BeforeClass
    public static void beforeClass() {
        _mongoTestEnvironment = new MongoTestEnvironment();
        _mongoTestEnvironment.beforeClass();
    }

    @AfterClass
    public static void afterClass() {
        _mongoTestEnvironment.afterClass();
    }

    @Before
    public void setUp() {
        mmFileSystemsTest.setUp();

        Mongo mongo = _mongoTestEnvironment.getConnection();
        // check condition, if mongo db is available on test system. Skip test if no present.
        org.junit.Assume.assumeTrue(mongo != null);

        if (mongo != null) {
            // The MongoTemplate comes normally from spring context
            MongoTemplate mongoTemplate = new MongoTemplate(mongo, "testdb");
            MongoDbFileDataDao mongoDbFileDataDao = new MongoDbFileDataDao(mongoTemplate);
            // MongoDbFileDescriptorDao mongoDbFileDescriptorDao = new MongoDbFileDescriptorDao(mongoTemplate, mongoDbFileDataDao);
            // _mongoDbFsLayer = new MongoDbFSLayer(mongoDbFileDescriptorDao);
        }
    }

    @Test
    public void testGetPath() {
        mmFileSystemsTest.testGetPath();
    }
}

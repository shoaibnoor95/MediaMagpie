package de.wehner.mediamagpie.common.simplenio.file;

import static org.fest.assertions.Assertions.*;

import java.io.IOException;
import java.net.URI;
import java.util.Arrays;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.data.mongodb.core.MongoTemplate;

import com.mongodb.Mongo;

import de.wehner.mediamagpie.common.fslayer.mongodb.MongoDbFileDataDao;
import de.wehner.mediamagpie.common.simplenio.fs.MMMongoFileSystemProvider;
import de.wehner.mediamagpie.common.testsupport.MongoTestEnvironment;

public class MongoMMFileSystemsTest {

    private static MongoTestEnvironment _mongoTestEnvironment;

    private MMFileSystemsTest mmFileSystemsTest = new MMFileSystemsTest();
    private URI MONGO_SCHEMA;

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
            MMMongoFileSystemProvider mmMongoFileSystemProvider = new MMMongoFileSystemProvider(mongoTemplate);
            MONGO_SCHEMA = URI.create(mmMongoFileSystemProvider.getScheme() + ":foo");
            new MMFileSystems(new MMFileSystemProviderFactory(Arrays.asList(mmMongoFileSystemProvider)));
        }
    }

    @Test
    public void testGetFileSystem() {
        MMPath path = MMFileSystems.getFileSystem(MONGO_SCHEMA).getPath("myPath", "subPathA", "subPathB");
        System.out.printf("Created path (%s) of type %s%n", path, path.getClass().getName());
    }

    // @Test
    // public void testCreateFile() throws IOException {
    // MMPath path = MMPaths.get(_testEnvironment.getWorkingDir().getPath(), "fileA.txt");
    // MMFiles.createFile(path);
    //
    // assertThat(MMFiles.exists(path)).isTrue();
    // }
}

package de.wehner.mediamagpie.common.fslayer;

import static org.fest.assertions.Assertions.assertThat;

import java.io.IOException;
import java.io.OutputStream;

import org.apache.commons.io.IOUtils;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.data.mongodb.core.MongoTemplate;

import com.mongodb.Mongo;

import de.wehner.mediamagpie.common.fslayer.mongodb.MongoDbFSLayer;
import de.wehner.mediamagpie.common.fslayer.mongodb.MongoDbFileDataDao;
import de.wehner.mediamagpie.common.fslayer.mongodb.MongoDbFileDescriptorDao;
import de.wehner.mediamagpie.common.testsupport.MongoTestEnvironment;

// TODO rwe: fix me
public class MongoDbFileTest {

    // @Rule
    // public MongoTestEnvironment _mongoTestEnvironment = new MongoTestEnvironment();
    // Made this static to startup and shutdown only once per junit test class
    private static MongoTestEnvironment _mongoTestEnvironment;

    private MongoDbFSLayer _mongoDbFsLayer;

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
        Mongo mongo = _mongoTestEnvironment.getConnection();
        // check condition, if mongo db is available on test system. Skip test if no present.
        org.junit.Assume.assumeTrue(mongo != null);

        if (mongo != null) {
            // The MongoTemplate comes normally from spring context
            MongoTemplate mongoTemplate = new MongoTemplate(mongo, "testdb");
            MongoDbFileDataDao mongoDbFileDataDao = new MongoDbFileDataDao(mongoTemplate);
//            MongoDbFileDescriptorDao mongoDbFileDescriptorDao = new MongoDbFileDescriptorDao(mongoTemplate, mongoDbFileDataDao);
//            _mongoDbFsLayer = new MongoDbFSLayer(mongoDbFileDescriptorDao);
        }
    }

    @Test
    public void testMongoStartup() {
        System.out.println("connection to mongodb: " + (_mongoTestEnvironment.getConnection() != null));
    }

    @Test
    @Ignore
    public void testGetOutputStream_And_ReplaceData() throws IOException {
        String expectedFile = "/path/to/existingFile";
        IFile iFile = _mongoDbFsLayer.createFile(expectedFile);

        // write 'blah' to file
        assertThat(iFile.exists()).isFalse();
        OutputStream os = iFile.getOutputStream();
        IOUtils.write("blah", os);
        os.close();

        IFile fileFromFs = _mongoDbFsLayer.createFile(iFile.getPath());
        assertThat(IOUtils.toString(fileFromFs.getInputStream())).isEqualTo("blah");
        
        // update content of file to 'blubber'
        os = iFile.getOutputStream();
        IOUtils.write("blubber", os);
        os.close();
        
        fileFromFs = _mongoDbFsLayer.createFile(iFile.getPath());
        assertThat(IOUtils.toString(fileFromFs.getInputStream())).isEqualTo("blubber");
    }

    // TODO rwe: add test for delete content
    // TODO rwe: add test for append content
}

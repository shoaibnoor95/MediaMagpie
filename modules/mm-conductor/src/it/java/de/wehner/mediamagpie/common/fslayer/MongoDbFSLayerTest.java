package de.wehner.mediamagpie.common.fslayer;

import static org.fest.assertions.Assertions.assertThat;

import java.io.IOException;
import java.io.OutputStream;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.springframework.data.mongodb.core.MongoTemplate;

import com.mongodb.Mongo;

import de.wehner.mediamagpie.common.fslayer.mongodb.MongoDbFSLayer;
import de.wehner.mediamagpie.common.testsupport.MongoTestEnvironment;

public class MongoDbFSLayerTest {

    @Rule
    public MongoTestEnvironment _mongoTestEnvironment = new MongoTestEnvironment();

    private MongoDbFSLayer _mongoDbFsLayer;

    @Before
    public void setUp() {
        Mongo mongo = _mongoTestEnvironment.getConnection();
        org.junit.Assume.assumeTrue(mongo != null);
        if (mongo != null) {
            // The MongoTemplate comes normally from spring context
            MongoTemplate mongoTemplate = new MongoTemplate(mongo, "testdb");
            _mongoDbFsLayer = new MongoDbFSLayer(mongoTemplate);
        }
    }

    @Test
    public void testMongoStartup() {
        System.out.println("connection to mongodb: " + (_mongoTestEnvironment.getConnection() != null));
    }

    @Test
    public void testCreateFile_1() {
        String expectedFile = "/path/to/my/foo.txt";

        IFile createFile = _mongoDbFsLayer.createFile(expectedFile);

        assertThat(createFile.getPath()).isEqualTo(expectedFile);
    }

    @Test
    public void testGetOutputStream_GetInputStream() throws IOException {
        String expectedFile = "/path/to/my/foo.txt";
        IFile iFile = _mongoDbFsLayer.createFile(expectedFile);

        OutputStream os = iFile.getOutputStream();
        IOUtils.write("blah", os);
        os.close();
        
        IFile fileFromFs = _mongoDbFsLayer.createFile(iFile.getPath());
        assertThat(IOUtils.toString(fileFromFs.getInputStream())).isEqualTo("blah");
    }

}

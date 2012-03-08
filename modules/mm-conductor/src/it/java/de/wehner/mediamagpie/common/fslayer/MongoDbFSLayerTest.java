package de.wehner.mediamagpie.common.fslayer;

import static org.fest.assertions.Assertions.assertThat;

import java.io.IOException;
import java.io.OutputStream;

import org.apache.commons.io.IOUtils;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.data.mongodb.core.MongoTemplate;

import com.mongodb.Mongo;

import de.wehner.mediamagpie.common.fslayer.mongodb.MongoDbFSLayer;
import de.wehner.mediamagpie.common.fslayer.mongodb.MongoDbFileDescriptorDao;
import de.wehner.mediamagpie.common.testsupport.MongoTestEnvironment;

public class MongoDbFSLayerTest {

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
            MongoDbFileDescriptorDao mongoDbFileDescriptorDao = new MongoDbFileDescriptorDao(mongoTemplate);
            _mongoDbFsLayer = new MongoDbFSLayer(mongoDbFileDescriptorDao);
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

    @Test
    public void testExists() throws IOException {
        String expectedFile = "/path/to/existingFile";
        IFile iFile = _mongoDbFsLayer.createFile(expectedFile);

        assertThat(iFile.exists()).isFalse();
        OutputStream os = iFile.getOutputStream();
        IOUtils.write("blah", os);
        os.close();

        assertThat(iFile.exists()).isTrue();
    }

    @Test
    public void testCreateNewFile() throws IOException {
        String expectedFile = "/path/to/existingFile";
        IFile iFile = _mongoDbFsLayer.createFile(expectedFile);

        iFile.createNewFile();

        assertThat(iFile.exists()).isTrue();
    }

    @Test
    public void testDelete() throws IOException {
        String expectedFile = "/path/to/delete/existingFile";
        IFile iFile = _mongoDbFsLayer.createFile(expectedFile);
        iFile.createNewFile();
        assertThat(iFile.exists()).isTrue();

        iFile.delete();
        assertThat(iFile.exists()).isFalse();
    }

    @Test
    public void testLength() throws IOException {
        String expectedFile = "/test/length";
        IFile iFile = _mongoDbFsLayer.createFile(expectedFile);

        assertThat(iFile.length()).isZero();

        iFile.createNewFile();

        assertThat(iFile.length()).isZero();

        OutputStream os = iFile.getOutputStream();
        IOUtils.write("1234567890", os);
        os.close();

        assertThat(iFile.length()).isEqualTo(10L);
    }

}

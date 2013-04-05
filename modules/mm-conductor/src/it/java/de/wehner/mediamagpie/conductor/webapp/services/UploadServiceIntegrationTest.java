package de.wehner.mediamagpie.conductor.webapp.services;

import static org.fest.assertions.Assertions.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.List;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import de.wehner.mediamagpie.common.persistence.dao.ImageResizeJobExecutionDao;
import de.wehner.mediamagpie.common.persistence.dao.MediaDao;
import de.wehner.mediamagpie.common.persistence.entity.Media;
import de.wehner.mediamagpie.common.persistence.entity.User;
import de.wehner.mediamagpie.common.persistence.entity.properties.MainConfiguration;
import de.wehner.mediamagpie.common.persistence.testsupport.DbTestEnvironment;
import de.wehner.mediamagpie.common.util.CipherServiceImpl;
import de.wehner.mediamagpie.conductor.persistence.dao.ConfigurationDao;
import de.wehner.mediamagpie.conductor.persistence.dao.MediaDeleteJobExecutionDao;
import de.wehner.mediamagpie.conductor.persistence.dao.ThumbImageDao;
import de.wehner.mediamagpie.core.testsupport.TestEnvironment;
import de.wehner.mediamagpie.core.util.Pair;
import de.wehner.mediamagpie.persistence.PersistenceService;

public class UploadServiceIntegrationTest {

    private static final File TEST_MEDIA = new File("src/test/resources/data/media/2010/07/12/resized_img_4556.jpg");
    private static final File TEST_MEDIA_NO_METADATA = new File("src/test/resources/images/accept.png");

    @Rule
    public DbTestEnvironment _dbTestEnvironment = new DbTestEnvironment(/* "mysql-it" */);
    @Rule
    public TestEnvironment _testEnvironment = new TestEnvironment(getClass());

    @Mock
    private CipherServiceImpl _cipherService;
    @Mock
    private ImageResizeJobExecutionDao _imageResizeJobExecutionDao;
    @Mock
    private MediaDeleteJobExecutionDao _mediaDeleteJobExecutionDao;

    private MediaDao _mediaDao;

    private PersistenceService _persistenceService;
    private ImageService _imageService;
    private MainConfiguration _mc;
    private UploadService _uploadService;
    private User _user;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        _dbTestEnvironment.cleanDb();
        _persistenceService = _dbTestEnvironment.getPersistenceService();
        ConfigurationDao configurationDao = new ConfigurationDao(_persistenceService, _cipherService);
        ThumbImageDao thumbImageDao = new ThumbImageDao(_persistenceService);
        _mediaDao = new MediaDao(_persistenceService);
        _imageService = new ImageService(thumbImageDao, _mediaDao, _imageResizeJobExecutionDao, _mediaDeleteJobExecutionDao);
        _uploadService = new UploadService(configurationDao, _mediaDao, _imageService, _persistenceService, thumbImageDao);
        _persistenceService.beginTransaction();
        _mc = new MainConfiguration();
        _mc.setBaseUploadPath(_testEnvironment.getWorkingDir() + "/baseUploadPath");
        configurationDao.saveConfiguration(_mc);
        _user = _dbTestEnvironment.getOrCreateTestUser();
        _persistenceService.flipTransaction();
    }

    @Test
    public void testHandleUploadStream() throws FileNotFoundException {
        FileInputStream inputStream = new FileInputStream(TEST_MEDIA);
        Pair<String, File> nameAndStoreFile = _uploadService.createUniqueUserStoreFile(_user, "fileB");

        _uploadService.handleUploadStream(_user, nameAndStoreFile.getSecond(), inputStream, 0);

        assertThat(nameAndStoreFile.getSecond()).exists();
        assertThat(nameAndStoreFile.getSecond()).hasSameContentAs(TEST_MEDIA);
        _persistenceService.flipTransaction();
        try {
            List<Media> all = _mediaDao.getAll();
            assertThat(all).hasSize(1);
            assertThat(all.get(0).getCameraMetaData()).isNotEmpty();
        } finally {
            _persistenceService.commitTransaction();
        }
    }

    @Test
    public void testHandleUploadStream_ButMediaHasNotMetadata() throws FileNotFoundException {
        FileInputStream inputStream = new FileInputStream(TEST_MEDIA_NO_METADATA);
        Pair<String, File> nameAndStoreFile = _uploadService.createUniqueUserStoreFile(_user, "aceppt.png");

        _uploadService.handleUploadStream(_user, nameAndStoreFile.getSecond(), inputStream, 0);

        assertThat(nameAndStoreFile.getSecond()).hasSameContentAs(TEST_MEDIA_NO_METADATA);
        _persistenceService.flipTransaction();
        try {
            List<Media> all = _mediaDao.getAll();
            assertThat(all).hasSize(1);
            assertThat(all.get(0).getCameraMetaData()).isNull();
        } finally {
            _persistenceService.commitTransaction();
        }
    }

    @Test
    public void test_createThumbImage() {
        // TODO rwe:

    }
}

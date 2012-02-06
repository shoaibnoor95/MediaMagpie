package de.wehner.mediamagpie.conductor.webapp.services;

import static org.fest.assertions.Assertions.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import de.wehner.mediamagpie.common.persistence.entity.User;
import de.wehner.mediamagpie.common.persistence.entity.properties.MainConfiguration;
import de.wehner.mediamagpie.common.test.util.TestEnvironment;
import de.wehner.mediamagpie.common.testsupport.DbTestEnvironment;
import de.wehner.mediamagpie.common.util.CipherService;
import de.wehner.mediamagpie.common.util.Pair;
import de.wehner.mediamagpie.conductor.fslayer.localfs.LocalFSLayer;
import de.wehner.mediamagpie.conductor.persistence.PersistenceService;
import de.wehner.mediamagpie.conductor.persistence.TransactionHandlerMock;
import de.wehner.mediamagpie.conductor.persistence.dao.ConfigurationDao;
import de.wehner.mediamagpie.conductor.persistence.dao.ImageResizeJobExecutionDao;
import de.wehner.mediamagpie.conductor.persistence.dao.MediaDao;
import de.wehner.mediamagpie.conductor.persistence.dao.MediaDeleteJobExecutionDao;
import de.wehner.mediamagpie.conductor.persistence.dao.ThumbImageDao;

public class UploadServiceIntegrationTest {

    private static final File TEST_MEDIA = new File("src/test/resources/data/media/2010/07/12/resized_img_4556.jpg");

    @Rule
    public DbTestEnvironment _dbTestEnvironment = new DbTestEnvironment(/* "mysql-it" */);
    @Rule
    public TestEnvironment _testEnvironment = new TestEnvironment(getClass());

    @Mock
    private CipherService _cipherService;
    @Mock
    private ImageResizeJobExecutionDao _imageResizeJobExecutionDao;
    @Mock
    private MediaDeleteJobExecutionDao _mediaDeleteJobExecutionDao;

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
        _imageService = new ImageService(new TransactionHandlerMock(), thumbImageDao, new MediaDao(_persistenceService), _imageResizeJobExecutionDao,
                _mediaDeleteJobExecutionDao);

        _uploadService = new UploadService(configurationDao, new MediaDao(_persistenceService), _imageService, _persistenceService, thumbImageDao, new LocalFSLayer());
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
        _persistenceService.commitTransaction();
    }
}

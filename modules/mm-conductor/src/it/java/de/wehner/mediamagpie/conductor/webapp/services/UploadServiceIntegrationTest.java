package de.wehner.mediamagpie.conductor.webapp.services;

import static org.fest.assertions.Assertions.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.Arrays;
import java.util.List;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import de.wehner.mediamagpie.conductor.webapp.processor.ImageProcessorFactory;
import de.wehner.mediamagpie.conductor.webapp.processor.ImageProcessorImageIOFactory;
import de.wehner.mediamagpie.conductor.webapp.processor.ImageProcessorJAIFactory;
import de.wehner.mediamagpie.core.testsupport.TestEnvironment;
import de.wehner.mediamagpie.core.util.Pair;
import de.wehner.mediamagpie.persistence.dao.ConvertedVideoDao;
import de.wehner.mediamagpie.persistence.dao.MediaDao;
import de.wehner.mediamagpie.persistence.dao.MediaDataProcessingJobExecutionDao;
import de.wehner.mediamagpie.persistence.dao.MediaDeleteJobExecutionDao;
import de.wehner.mediamagpie.persistence.dao.PersistenceService;
import de.wehner.mediamagpie.persistence.dao.ThumbImageDao;
import de.wehner.mediamagpie.persistence.entity.Media;
import de.wehner.mediamagpie.persistence.entity.User;
import de.wehner.mediamagpie.persistence.testsupport.DbTestEnvironment;

public class UploadServiceIntegrationTest {

    private static final File TEST_MEDIA = new File("src/test/resources/data/media/2010/07/12/resized_img_4556.jpg");
    private static final File TEST_VIDEO = new File("src/test/resources/videos/MVI_2627.MOV");
    private static final File TEST_MEDIA_NO_METADATA = new File("src/test/resources/images/accept.png");

    @Rule
    public DbTestEnvironment _dbTestEnvironment = new DbTestEnvironment(/* "mysql-it" */);

    @Rule
    public TestEnvironment _testEnvironment = new TestEnvironment(getClass());

    @Mock
    private MediaDataProcessingJobExecutionDao _imageResizeJobExecutionDao;
    @Mock
    private MediaDeleteJobExecutionDao _mediaDeleteJobExecutionDao;

    private MediaDao _mediaDao;

    private PersistenceService _persistenceService;
    private ImageService _imageService;
    private VideoService _videoService;
    private UploadService _uploadService;
    private User _user;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        _dbTestEnvironment.cleanDb();
        _persistenceService = _dbTestEnvironment.getPersistenceService();
        ThumbImageDao thumbImageDao = new ThumbImageDao(_persistenceService);
        _mediaDao = new MediaDao(_persistenceService);
        List<ImageProcessorFactory> imageProcessorFactories = Arrays.asList(new ImageProcessorImageIOFactory(), new ImageProcessorJAIFactory());
        _imageService = new ImageService(thumbImageDao, _mediaDao, _imageResizeJobExecutionDao, _mediaDeleteJobExecutionDao, imageProcessorFactories);
        _videoService = new VideoService(_dbTestEnvironment.createDao(ConvertedVideoDao.class),
                _dbTestEnvironment.createDao(MediaDataProcessingJobExecutionDao.class));
        _persistenceService.beginTransaction();
        _uploadService = new UploadService(_dbTestEnvironment.createConfigurationProvider(_testEnvironment.getWorkingDir()), _mediaDao, _imageService,
                _videoService, _persistenceService, thumbImageDao);
        _user = _dbTestEnvironment.getOrCreateTestUser();
        _persistenceService.flipTransaction();
    }

    @Test
    public void testHandleUploadStream() throws FileNotFoundException {
        FileInputStream inputStream = new FileInputStream(TEST_MEDIA);
        Pair<String, File> nameAndStoreFile = _uploadService.createUniqueUserStoreFile(_user, "photoXYZ.jpg");

        Media media = _uploadService.saveInputStreamToFileSystemAndCreateMedia(_user, nameAndStoreFile.getSecond(), inputStream);
        _persistenceService.persist(media);

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

        Media media = _uploadService.saveInputStreamToFileSystemAndCreateMedia(_user, nameAndStoreFile.getSecond(), inputStream);
        _persistenceService.persist(media);

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
    public void testHandleVideoUploadStream() throws FileNotFoundException {
        FileInputStream inputStream = new FileInputStream(TEST_VIDEO);
        Pair<String, File> nameAndStoreFile = _uploadService.createUniqueUserStoreFile(_user, "myVideo.mp4");

        Media media = _uploadService.saveInputStreamToFileSystemAndCreateMedia(_user, nameAndStoreFile.getSecond(), inputStream);
        _persistenceService.persist(media);

        assertThat(nameAndStoreFile.getSecond()).exists();
        assertThat(nameAndStoreFile.getSecond()).hasSameContentAs(TEST_VIDEO);
        _persistenceService.flipTransaction();
        try {
            List<Media> all = _mediaDao.getAll();
            assertThat(all).hasSize(1);
            assertThat(all.get(0).getCameraMetaData()).isNotEmpty();
        } finally {
            _persistenceService.commitTransaction();
        }
    }
}

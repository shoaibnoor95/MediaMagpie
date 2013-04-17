package de.wehner.mediamagpie.conductor.performingjob;

import java.io.File;
import java.io.FileNotFoundException;
import java.net.URI;
import java.util.Date;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.MockitoAnnotations;

import de.wehner.mediamagpie.aws.s3.S3MediaExportRepositoryMock;
import de.wehner.mediamagpie.conductor.webapp.services.ImageService;
import de.wehner.mediamagpie.conductor.webapp.services.UploadService;
import de.wehner.mediamagpie.core.testsupport.TestEnvironment;
import de.wehner.mediamagpie.persistence.ImageResizeJobExecutionDao;
import de.wehner.mediamagpie.persistence.MediaDao;
import de.wehner.mediamagpie.persistence.entity.Media;
import de.wehner.mediamagpie.persistence.entity.User;
import de.wehner.mediamagpie.persistence.service.ConfigurationProvider;
import de.wehner.mediamagpie.persistence.testsupport.DbTestEnvironment;

public class S3SyncJobIntTest {

    @Rule
    public DbTestEnvironment _dbTestEnvironment = new DbTestEnvironment();

    private TestEnvironment _testEnvironment = new TestEnvironment(getClass());

    private static final URI IMAGE_13 = new File("src/test/resources/images/IMG_0013.JPG").toURI();
    private static final URI IMAGE_14 = new File("src/test/resources/images/IMG_1414.JPG").toURI();
    private final Media m1;
    private final Media m2;

    private S3MediaExportRepositoryMock _s3MediaExportRepository = new S3MediaExportRepositoryMock();

    private ConfigurationProvider _configurationProvider;

    private User _user;
    private S3SyncJob _job;
    private MediaDao _mediaDao;

    public S3SyncJobIntTest() throws FileNotFoundException {
        m1 = Media.createWithHashValue(_user, "image-13", IMAGE_13, new Date());
        m2 = Media.createWithHashValue(_user, "image-14", IMAGE_14, new Date());
    }

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);

        _testEnvironment.cleanWorkingDir();
        _dbTestEnvironment.cleanDb();
        _mediaDao = new MediaDao(_dbTestEnvironment.getPersistenceService());
        _dbTestEnvironment.beginTransaction();
        _user = _dbTestEnvironment.getOrCreateTestUser();
        _configurationProvider = _dbTestEnvironment.createConfigurationProvider(_testEnvironment.getWorkingDir());
        ImageService imageService = new ImageService(null, _mediaDao, new ImageResizeJobExecutionDao(_dbTestEnvironment.getPersistenceService()), null);
        UploadService uploadService = new UploadService(_configurationProvider, _mediaDao, imageService, _dbTestEnvironment.getPersistenceService(), null);
        _job = new S3SyncJob(_s3MediaExportRepository, uploadService, _user, _configurationProvider, _dbTestEnvironment.createTransactionHandler(),
                _mediaDao);
        _dbTestEnvironment.flipTransaction();
    }

    @Test
    public void testPullFromS3() throws Exception {
        _s3MediaExportRepository.addMediaOnS3(m1);

        _dbTestEnvironment.beginTransaction();

        JobCallable prepare = _job.prepare();
        prepare.call();

        _dbTestEnvironment.commitTransaction();
    }
}

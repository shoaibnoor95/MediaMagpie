package de.wehner.mediamagpie.conductor.performingjob;

import static org.fest.assertions.Assertions.*;

import java.io.File;
import java.net.URI;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

import org.apache.commons.lang3.time.DateUtils;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.MockitoAnnotations;

import de.wehner.mediamagpie.api.MediaExport;
import de.wehner.mediamagpie.aws.s3.S3MediaExportRepositoryMock;
import de.wehner.mediamagpie.common.testsupport.ItEnvironment.CleanFolderInstruction;
import de.wehner.mediamagpie.common.testsupport.LocalItEnvironment;
import de.wehner.mediamagpie.conductor.webapp.processor.ImageProcessorFactory;
import de.wehner.mediamagpie.conductor.webapp.processor.ImageProcessorImageIOFactory;
import de.wehner.mediamagpie.conductor.webapp.processor.ImageProcessorJAIFactory;
import de.wehner.mediamagpie.conductor.webapp.services.ImageService;
import de.wehner.mediamagpie.conductor.webapp.services.MediaSyncService;
import de.wehner.mediamagpie.conductor.webapp.services.UploadService;
import de.wehner.mediamagpie.conductor.webapp.services.VideoService;
import de.wehner.mediamagpie.core.testsupport.TestEnvironment;
import de.wehner.mediamagpie.core.util.TimeUtil;
import de.wehner.mediamagpie.persistence.dao.ConvertedVideoDao;
import de.wehner.mediamagpie.persistence.dao.MediaDao;
import de.wehner.mediamagpie.persistence.dao.MediaDataProcessingJobExecutionDao;
import de.wehner.mediamagpie.persistence.entity.Media;
import de.wehner.mediamagpie.persistence.entity.MediaTag;
import de.wehner.mediamagpie.persistence.entity.User;
import de.wehner.mediamagpie.persistence.service.ConfigurationProvider;
import de.wehner.mediamagpie.persistence.testsupport.DbTestEnvironment;
import de.wehner.mediamagpie.persistence.util.TimeProvider;

public class S3SyncJobIntTest {

    @Rule
    public DbTestEnvironment _dbTestEnvironment = new DbTestEnvironment();

    @Rule
    public LocalItEnvironment _localItEnvironment = new LocalItEnvironment(CleanFolderInstruction.BEFORE);

    private TestEnvironment _testEnvironment = new TestEnvironment(getClass());

    private static final URI IMAGE_13 = new File("src/test/resources/images/IMG_0013.JPG").toURI();
    private static final URI IMAGE_14 = new File("src/test/resources/images/IMG_1414.JPG").toURI();
    /**
     * media for file IMAGE_13
     */
    private Media _m1;
    /**
     * media for file IMAGE_14
     */
    private Media _m2;

    private S3MediaExportRepositoryMock _s3MediaExportRepository = new S3MediaExportRepositoryMock();

    private ConfigurationProvider _configurationProvider;

    private User _user;
    private S3SyncJob _job;
    private MediaDao _mediaDao;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);

        _testEnvironment.cleanWorkingDir();
        _dbTestEnvironment.cleanDb();
        _mediaDao = _dbTestEnvironment.createDao(MediaDao.class);
        _dbTestEnvironment.beginTransaction();
        _user = _dbTestEnvironment.getOrCreateTestUser();
        _m1 = MediaSyncService.createMediaFromMediaFile(_user, IMAGE_13.toURL().toURI());
        _m1.setName("image-13");
        _m1.setCreationDate(TimeUtil.parseGermanDateAndTime("12.05.2005 17:05:00"));
        _m1.addTag(new MediaTag("family"));
        _m1.addTag(new MediaTag("tillmann"));
        _m1.setDescription("This is my description: äöüß@$§");
        _m2 = MediaSyncService.createMediaFromMediaFile(_user, IMAGE_14.toURL().toURI());
        _m2.setName("image-14");
        _m2.addTag(new MediaTag("family"));
        _configurationProvider = _dbTestEnvironment.createConfigurationProvider(_testEnvironment.getWorkingDir());
        List<ImageProcessorFactory> imageProcessorFactories = Arrays.asList(new ImageProcessorImageIOFactory(), new ImageProcessorJAIFactory());
        ImageService imageService = new ImageService(null, _mediaDao, new MediaDataProcessingJobExecutionDao(_dbTestEnvironment.getPersistenceService()),
                null, imageProcessorFactories);
        VideoService videoService = new VideoService(_dbTestEnvironment.createDao(ConvertedVideoDao.class),
                _dbTestEnvironment.createDao(MediaDataProcessingJobExecutionDao.class));
        UploadService uploadService = new UploadService(_configurationProvider, _mediaDao, imageService, videoService,
                _dbTestEnvironment.getPersistenceService(), null);
        _job = new S3SyncJob(_s3MediaExportRepository, uploadService, _user, _configurationProvider, _dbTestEnvironment.createTransactionHandler(),
                _mediaDao, new TimeProvider());

        // commit the transaction (close), because the S3SyncJob will be started without a transaction
        _dbTestEnvironment.commitTransaction();
    }

    @Test
    public void testPullFromS3() throws Exception {
        _s3MediaExportRepository.addMediaOnS3(_m1);

        JobCallable prepare = _job.prepare();
        prepare.call();

        _dbTestEnvironment.beginTransaction();
        List<Media> allMedias = _mediaDao.getAll();
        assertThat(allMedias).hasSize(1);
        Media mediaBasedFromS3 = allMedias.get(0);

        // compare with original file _m1 (IMAGE_13)
        assertThat(mediaBasedFromS3.getCameraMetaData()).isEqualTo(_m1.getCameraMetaData());
        assertThat(DateUtils.truncatedCompareTo(mediaBasedFromS3.getCreationDate(), _m1.getCreationDate(), Calendar.SECOND)).isZero();
        assertThat(mediaBasedFromS3.getDescription()).isEqualTo(_m1.getDescription());
        assertThat(mediaBasedFromS3.getHashValue()).isEqualTo(_m1.getHashValue());
        assertThat(mediaBasedFromS3.getImageType()).isEqualTo(_m1.getImageType());
        assertThat(mediaBasedFromS3.getName()).isEqualTo(_m1.getName());
        assertThat(mediaBasedFromS3.getOrientation()).isEqualTo(_m1.getOrientation());
        assertThat(mediaBasedFromS3.getOwner()).isEqualTo(_m1.getOwner());
        assertThat(mediaBasedFromS3.getTags().size()).isEqualTo(2);// family and tillmann
        assertThat(mediaBasedFromS3.getFileFromUri()).hasSameContentAs(new File(IMAGE_13));
        _dbTestEnvironment.commitTransaction();
    }

    @Test
    public void testPushToS3() throws Exception {
        _dbTestEnvironment.beginTransaction();
        _mediaDao.makePersistent(_m2);
        _dbTestEnvironment.commitTransaction();

        JobCallable prepare = _job.prepare();
        prepare.call();

        List<MediaExport> mediasPushedToS3 = _s3MediaExportRepository.getMediasPushedToS3();
        assertThat(mediasPushedToS3).hasSize(1);
    }
}

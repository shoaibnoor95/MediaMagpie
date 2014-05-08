package de.wehner.mediamagpie.conductor.performingjob;

import static org.fest.assertions.Assertions.*;

import java.io.File;
import java.net.URI;
import java.util.List;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.MockitoAnnotations;

import de.wehner.mediamagpie.common.testsupport.ItEnvironment.CleanFolderInstruction;
import de.wehner.mediamagpie.common.testsupport.LocalItEnvironment;
import de.wehner.mediamagpie.conductor.webapp.services.MediaSyncService;
import de.wehner.mediamagpie.conductor.webapp.services.VideoService;
import de.wehner.mediamagpie.conductor.webapp.services.VideoService.VideoFormat;
import de.wehner.mediamagpie.core.testsupport.TestEnvironment;
import de.wehner.mediamagpie.core.util.TimeUtil;
import de.wehner.mediamagpie.persistence.dao.ConvertedVideoDao;
import de.wehner.mediamagpie.persistence.dao.MediaDao;
import de.wehner.mediamagpie.persistence.dao.MediaDataProcessingJobExecutionDao;
import de.wehner.mediamagpie.persistence.entity.ConvertedVideo;
import de.wehner.mediamagpie.persistence.entity.Media;
import de.wehner.mediamagpie.persistence.entity.MediaTag;
import de.wehner.mediamagpie.persistence.entity.User;
import de.wehner.mediamagpie.persistence.service.ConfigurationProvider;
import de.wehner.mediamagpie.persistence.testsupport.DbTestEnvironment;

public class VideoConversionJobTest {

    @Rule
    public DbTestEnvironment _dbTestEnvironment = new DbTestEnvironment();

    @Rule
    public LocalItEnvironment _localItEnvironment = new LocalItEnvironment(CleanFolderInstruction.BEFORE);

    private TestEnvironment _testEnvironment = new TestEnvironment(getClass());

    private static final URI VIDEO_1 = new File("src/test/resources/videos/MVI_2627.MOV").toURI();

    private Media _m1;

    private ConfigurationProvider _configurationProvider;

    private User _user;
    private VideoConversionJob _job;
    private MediaDao _mediaDao;
    private ConvertedVideoDao _convertedVideoDao;
    private MediaDataProcessingJobExecutionDao _mediaDataProcessingJobExecutionDao;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);

        _testEnvironment.cleanWorkingDir();
        _dbTestEnvironment.cleanDb();
        _mediaDao = new MediaDao(_dbTestEnvironment.getPersistenceService());
        _mediaDataProcessingJobExecutionDao = new MediaDataProcessingJobExecutionDao(_dbTestEnvironment.getPersistenceService());
        _convertedVideoDao = new ConvertedVideoDao(_dbTestEnvironment.getPersistenceService());
        _dbTestEnvironment.beginTransaction();
        _user = _dbTestEnvironment.getOrCreateTestUser();
        _m1 = MediaSyncService.createMediaFromMediaFile(_user, VIDEO_1.toURL().toURI());
        _m1.setName("test-video");
        _m1.setCreationDate(TimeUtil.parseGermanDateAndTime("12.05.2005 17:05:00"));
        _m1.addTag(new MediaTag("holiday"));
        _m1.addTag(new MediaTag("mallorca"));
        _m1.setDescription("This is my description: äöüß@$§");
        _mediaDao.makePersistent(_m1);
        // _dbTestEnvironment.flipTransaction();
        _configurationProvider = _dbTestEnvironment.createConfigurationProvider(_testEnvironment.getWorkingDir());
        VideoService videoService = new VideoService(_convertedVideoDao, _mediaDataProcessingJobExecutionDao);
        _job = new VideoConversionJob(_mediaDao, _convertedVideoDao, videoService, _dbTestEnvironment.reload(_m1), VideoFormat.MP4_h264, 200);

        // commit the transaction (close), because the S3SyncJob will be started without a transaction
        _dbTestEnvironment.commitTransaction();
    }

    @Test
    public void test_prepare_sucessful() throws Exception {
        _dbTestEnvironment.beginTransaction();

        JobCallable prepare = _job.prepare();
        _job.init(new PerformingJobContext(_configurationProvider.getMainConfiguration()));
        URI uri = prepare.call();
        
        prepare.handleResult(uri);

        List<ConvertedVideo> allConversions = _convertedVideoDao.getAll();
        assertThat(allConversions).hasSize(1);
        _dbTestEnvironment.commitTransaction();
    }

}

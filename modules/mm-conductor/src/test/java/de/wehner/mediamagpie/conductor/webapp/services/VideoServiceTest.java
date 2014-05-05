package de.wehner.mediamagpie.conductor.webapp.services;

import static org.fest.assertions.Assertions.*;

import static org.mockito.Matchers.*;

import static org.mockito.Mockito.*;

import java.io.File;
import java.io.IOException;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;

import org.apache.tika.Tika;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.mobile.device.Device;

import de.wehner.mediamagpie.conductor.webapp.services.VideoService.VideoFormat;
import de.wehner.mediamagpie.core.testsupport.TestEnvironment;
import de.wehner.mediamagpie.persistence.dao.ConvertedVideoDao;
import de.wehner.mediamagpie.persistence.dao.MediaDataProcessingJobExecutionDao;
import de.wehner.mediamagpie.persistence.dao.MediaDao;
import de.wehner.mediamagpie.persistence.dao.MediaDeleteJobExecutionDao;
import de.wehner.mediamagpie.persistence.dao.ThumbImageDao;
import de.wehner.mediamagpie.persistence.entity.Media;
import de.wehner.mediamagpie.persistence.entity.Priority;
import de.wehner.mediamagpie.persistence.entity.VideoConversionJobExecution;

public class VideoServiceTest {

    private static final File TEST_VIDEO_QUICKTIME = new File("src/test/resources/videos/MVI_2627.MOV");

    private File _conversionPath;

    private VideoService _service;

    @Rule
    public TestEnvironment _testEnvironment = new TestEnvironment(getClass());

    private ImageService _imageService;
    @Mock
    private ThumbImageDao _thumbImageDao;
    @Mock
    private MediaDao _mediaDao;
    @Mock
    private MediaDataProcessingJobExecutionDao _imageResizeJobExecutionDao;
    @Mock
    private MediaDeleteJobExecutionDao _mediaDeleteJobExecutionDao;
    @Mock
    private ConvertedVideoDao _convertedVideoDao;

    @Mock
    private HttpServletRequest servletRequest;

    @Mock
    private Device device;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        _testEnvironment.cleanWorkingDir();
        _conversionPath = new File(_testEnvironment.getWorkingDir(), "conversion");
        _service = new VideoService(_convertedVideoDao, _imageResizeJobExecutionDao);
    }

    @Test
    public void test_createImageFromVideo() throws IOException {

        _service.createImageFromVideo(TEST_VIDEO_QUICKTIME, _conversionPath);

        assertThat(_conversionPath.listFiles()).isNotEmpty();

        Tika tika = new Tika();
        String fileType = tika.detect(_conversionPath.listFiles()[0]);
        assertThat(fileType).isEqualTo("image/jpeg");
    }

    @Test
    @Ignore
    public void test_convertVideo_WebM() throws IOException {

        File result = _service.convertVideo(TEST_VIDEO_QUICKTIME, VideoFormat.WebM_vp8, null, _conversionPath);

        assertThat(result).isNotNull();
        assertThat(_conversionPath.listFiles()).isNotEmpty();
        assertThat(_conversionPath.listFiles()[0].getAbsolutePath()).isEqualTo(result.getAbsolutePath());
        Tika tika = new Tika();
        String fileType = tika.detect(_conversionPath.listFiles()[0]);
        assertThat(fileType).isEqualTo("video/webm");
    }

    @Test
    @Ignore
    public void test_convertVideo_OGG_Theora() throws IOException {

        File result = _service.convertVideo(TEST_VIDEO_QUICKTIME, VideoFormat.OGG_Theora, null, _conversionPath);

        assertThat(result).isNotNull();
        assertThat(_conversionPath.listFiles()).isNotEmpty();
        assertThat(_conversionPath.listFiles()[0].getAbsolutePath()).isEqualTo(result.getAbsolutePath());
        Tika tika = new Tika();
        String fileType = tika.detect(_conversionPath.listFiles()[0]);
        assertThat(fileType).isEqualTo("video/ogg");
    }

    @Test
    @Ignore
    public void test_convertVideo_MP4_h264() throws IOException {

        File result = _service.convertVideo(TEST_VIDEO_QUICKTIME, VideoFormat.MP4_h264, null, _conversionPath);

        assertThat(result).isNotNull();
        assertThat(_conversionPath.listFiles()).isNotEmpty();
        assertThat(_conversionPath.listFiles()[0].getAbsolutePath()).isEqualTo(result.getAbsolutePath());
        Tika tika = new Tika();
        String fileType = tika.detect(_conversionPath.listFiles()[0]);
        assertThat(fileType).isEqualTo("video/mp4");
    }

    @Test
    public void testCreateLink() {
        Media media = new Media(null, "name", TEST_VIDEO_QUICKTIME.toURI(), new Date()/* , "video/quicktime" */);
        media.setId(1L);
        final String label = "200";

        String link = VideoService.createLink(media, label, VideoFormat.WebM_vp8, Priority.NORMAL);

        assertThat(link).isEqualTo("/content/videos/1/200.webm?priority=NORMAL");

        link = VideoService.createLink(media, null, VideoFormat.OGG_Theora, Priority.HIGH);

        assertThat(link).isEqualTo("/content/videos/1/original.ogv?priority=HIGH");
    }

    @Test
    public void test_getOrCreateVideoUrll_newVideoConversionJob_is_expected() {
        Media media = new Media(null, "name", TEST_VIDEO_QUICKTIME.toURI(), new Date()/* , "video/quicktime" */);
        media.setId(1L);
        when(servletRequest.getHeader("User-Agent")).thenReturn("Mozilla/5.0 (Macintosh; Intel Mac OS X 10.7; rv:29.0) Gecko/20100101 Firefox/29.0");

        String videoUrl = _service.getOrCreateVideoUrl(media, servletRequest, device, true, Priority.NORMAL);

        verify(_imageResizeJobExecutionDao).makePersistent(any(VideoConversionJobExecution.class));
    }
}

package de.wehner.mediamagpie.conductor.webapp.services;

import static org.fest.assertions.Assertions.*;

import java.io.File;
import java.io.IOException;

import org.apache.tika.Tika;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import de.wehner.mediamagpie.conductor.webapp.services.VideoService.VideoFormat;
import de.wehner.mediamagpie.core.testsupport.TestEnvironment;

public class VideoServiceTest {

    private static final File TEST_VIDEO_QUICKTIME = new File("src/test/resources/videos/MVI_2627.MOV");

    private TestEnvironment _testEnvironment = new TestEnvironment(getClass());

    private File _conversionPath;

    private VideoService _service;

    @Before
    public void setUp() {
        _testEnvironment.cleanWorkingDir();
        _conversionPath = new File(_testEnvironment.getWorkingDir(), "conversion");
        _service = new VideoService();
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
    public void test_convertVideo_MP4_h264() throws IOException {

        File result = _service.convertVideo(TEST_VIDEO_QUICKTIME, VideoFormat.MP4_h264, null, _conversionPath);

        assertThat(result).isNotNull();
        assertThat(_conversionPath.listFiles()).isNotEmpty();
        assertThat(_conversionPath.listFiles()[0].getAbsolutePath()).isEqualTo(result.getAbsolutePath());
        Tika tika = new Tika();
        String fileType = tika.detect(_conversionPath.listFiles()[0]);
        assertThat(fileType).isEqualTo("video/mp4");
    }
}

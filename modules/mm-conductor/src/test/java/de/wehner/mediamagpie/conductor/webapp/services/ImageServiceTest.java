package de.wehner.mediamagpie.conductor.webapp.services;

import static org.junit.Assert.*;

import static org.fest.assertions.Assertions.*;

import static org.mockito.Matchers.*;

import static org.mockito.Mockito.*;

import java.io.File;
import java.util.Date;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import de.wehner.mediamagpie.conductor.persistence.dao.MediaDeleteJobExecutionDao;
import de.wehner.mediamagpie.conductor.persistence.dao.ThumbImageDao;
import de.wehner.mediamagpie.core.testsupport.TestEnvironment;
import de.wehner.mediamagpie.persistence.ImageResizeJobExecutionDao;
import de.wehner.mediamagpie.persistence.MediaDao;
import de.wehner.mediamagpie.persistence.entity.ImageResizeJobExecution;
import de.wehner.mediamagpie.persistence.entity.Media;
import de.wehner.mediamagpie.persistence.entity.Orientation;
import de.wehner.mediamagpie.persistence.entity.Priority;

public class ImageServiceTest {

    private static final String SRC_BAD_IMAGE_IO_IMAGE = "src/test/resources/images/ralf_small.jpg";

    static final int RESIZE_W = 1500;

    static final int RESIZE_H = 600;

    @Rule
    public TestEnvironment _testEnvironment = new TestEnvironment(ImageServiceTest.class);

    private ImageService _imageService;
    @Mock
    private ThumbImageDao _thumbImageDao;
    @Mock
    private MediaDao _mediaDao;
    @Mock
    private ImageResizeJobExecutionDao _imageResizeJobExecutionDao;
    @Mock
    private MediaDeleteJobExecutionDao _mediaDeleteJobExecutionDao;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        _imageService = new ImageService(_thumbImageDao, _mediaDao, _imageResizeJobExecutionDao, _mediaDeleteJobExecutionDao);
    }

    @Test
    public void testGetOrCreateImageUrl_newImageResizeJob_wasCreated() {
        Media media = new Media(null, "name", new File("src/test/resources/images/1600x4.jpg").toURI(), new Date());
        media.setId(1L);
        _imageService.getOrCreateImageUrl(media, 300);

        verify(_imageResizeJobExecutionDao).makePersistent(any(ImageResizeJobExecution.class));
    }

    @Test
    public void testResizeImage() {
        File originMediaFile = new File("src/test/resources/images/IMG_0013.JPG");
        File resizedImageFile = ImageService.resizeImage(originMediaFile, 1L, _testEnvironment.getWorkingDir(), 2000, 2000);

        assertThat(resizedImageFile).exists();
    }

    @Test
    public void test_resizeImageInQueue() {
        File originMediaFile = new File("src/test/resources/images/IMG_0013.JPG");
        File resizedImageFile = ImageService.resizeImageInQueue(originMediaFile, 1L, _testEnvironment.getWorkingDir(), 2000, 2000,
                Orientation.LEFT_SIDE_TOP.getNecessaryRotation());

        assertThat(resizedImageFile).exists();
    }

    @Test
    public void test_resizeImageInQueue_ButImageIoFails() {
        File originMediaFile = new File(SRC_BAD_IMAGE_IO_IMAGE);
        File resizedImageFile = ImageService.resizeImageInQueue(originMediaFile, 1L, _testEnvironment.getWorkingDir(), 2000, 2000,
                Orientation.TOP_LEFT_SIDE.getNecessaryRotation());

        assertThat(resizedImageFile).exists();
    }

    @Test
    public void test_resizeImageInQueue_WithRotation_ButImageIoFails() {
        File originMediaFile = new File(SRC_BAD_IMAGE_IO_IMAGE);
        File resizedImageFile = ImageService.resizeImageInQueue(originMediaFile, 1L, _testEnvironment.getWorkingDir(), 2000, 2000,
                Orientation.LEFT_SIDE_TOP.getNecessaryRotation());

        assertThat(resizedImageFile).exists();
    }

    @Test
    public void testCreateLink() {
        Media media = new Media(null, "name", new File("src/test/resources/images/4x1600.jpg").toURI(), new Date());
        media.setId(1L);
        final String label = "200";
        String link = _imageService.createLink(media, label, Priority.NORMAL);

        assertEquals("/content/images/1/200.jpg?priority=NORMAL", link);

        link = _imageService.createLink(media, null, Priority.NORMAL);

        assertEquals("/content/images/1/original.jpg?priority=NORMAL", link);
    }

}

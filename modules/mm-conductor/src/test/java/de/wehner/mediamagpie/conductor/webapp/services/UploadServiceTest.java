package de.wehner.mediamagpie.conductor.webapp.services;

import static org.fest.assertions.Assertions.*;

import static org.mockito.Matchers.*;

import static org.mockito.Mockito.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import de.wehner.mediamagpie.core.testsupport.TestEnvironment;
import de.wehner.mediamagpie.core.util.FileSystemUtil;
import de.wehner.mediamagpie.core.util.Pair;
import de.wehner.mediamagpie.persistence.dao.MediaDao;
import de.wehner.mediamagpie.persistence.dao.PersistenceService;
import de.wehner.mediamagpie.persistence.dao.ThumbImageDao;
import de.wehner.mediamagpie.persistence.entity.Media;
import de.wehner.mediamagpie.persistence.entity.ThumbImage;
import de.wehner.mediamagpie.persistence.entity.User;
import de.wehner.mediamagpie.persistence.entity.User.Role;
import de.wehner.mediamagpie.persistence.entity.properties.MainConfiguration;
import de.wehner.mediamagpie.persistence.service.ConfigurationProvider;

public class UploadServiceTest {

    private static final File TEST_MEDIA = new File("src/test/resources/data/media/2010/07/12/resized_img_4556.jpg");
    private static final File TEST_MEDIA_NO_METADATA = new File("src/test/resources/images/accept.png");

    @Rule
    public TestEnvironment _testEnvironment = new TestEnvironment(getClass());

    private UploadService _uploadService;

    private User _user;

    private MainConfiguration mc;
    @Mock
    private ConfigurationProvider _configurationProvider;
    @Mock
    private MediaDao _mediaDao;
    @Mock
    private ImageService _imageService;
    @Mock
    private PersistenceService _persistenceService;
    @Mock
    private ThumbImageDao _thumbImageDao;

    @Before
    public void setUp() throws IOException {
        MockitoAnnotations.initMocks(this);
        _uploadService = new UploadService(_configurationProvider, _mediaDao, _imageService, _persistenceService, _thumbImageDao);
        _user = new User("Ralf", "rwe@localhost", Role.ADMIN);
        _user.setId(123L);
        mc = new MainConfiguration();
        mc.setBaseUploadPath(_testEnvironment.getWorkingDir() + "/baseUploadPath");
        when(_configurationProvider.getMainConfiguration()).thenReturn(mc);
        // new Media(_user, "pictureA", new File());
        ThumbImage thumbImage = new ThumbImage(null, UploadService.UPLOAD_PREVIEW_THUMB_LABEL, null);
        when(_thumbImageDao.getByMediaIdAndLabel(any(Long.class), eq(UploadService.UPLOAD_PREVIEW_THUMB_LABEL))).thenReturn(thumbImage);
    }

    @Test
    public void testCreateUserStoreFile() {
        assertThat(_uploadService.createUniqueUserStoreFile(_user, "fileA")).isEqualTo(
                new Pair<String, File>("fileA", new File(mc.getBaseUploadPath(), "user_000123/fileA")));
        assertThat(_uploadService.createUniqueUserStoreFile(_user, "file A")).isEqualTo(
                new Pair<String, File>("file A", new File(mc.getBaseUploadPath(), "user_000123/file A")));
        assertThat(_uploadService.createUniqueUserStoreFile(_user, "opera/file/with/path/fileA")).isEqualTo(
                new Pair<String, File>("opera/file/with/path/fileA", new File(mc.getBaseUploadPath(), "user_000123/opera/file/with/path/fileA")));
    }

    @Test
    public void testCreateUserStoreFile_GenerateNewUniqueFile() throws IOException {
        File existingFile = new File(mc.getBaseUploadPath(), "user_000123/fileA");
        FileUtils.forceMkdir(existingFile.getParentFile());
        existingFile.createNewFile();
        assertThat(_uploadService.createUniqueUserStoreFile(_user, "fileA")).isEqualTo(
                new Pair<String, File>("fileA", new File(mc.getBaseUploadPath(), "user_000123/fileA" + FileSystemUtil.FILE_COUNTER_SEPARATOR + "1")));
    }

    @Test
    public void testHandleUploadStream() throws FileNotFoundException {
        FileInputStream inputStream = new FileInputStream(TEST_MEDIA);
        Pair<String, File> origAndStoreFileName = _uploadService.createUniqueUserStoreFile(_user, "fileB");

        Media media = _uploadService.handleUploadStream(_user, origAndStoreFileName.getSecond(), inputStream);

        assertThat(media).isNotNull();
        assertThat(new File(mc.getBaseUploadPath(), "user_000123/fileB")).exists();
        assertThat(new File(mc.getBaseUploadPath(), "user_000123/fileB")).hasSameContentAs(TEST_MEDIA);
    }

    @Test
    public void testHandleUploadStream_ButMediaHasNotMetadata() throws FileNotFoundException {
        FileInputStream inputStream = new FileInputStream(TEST_MEDIA_NO_METADATA);
        Pair<String, File> origAndStoreFileName = _uploadService.createUniqueUserStoreFile(_user, "accept.png");

        Media media = _uploadService.handleUploadStream(_user, origAndStoreFileName.getSecond(), inputStream);

        assertThat(media).isNotNull();
        assertThat(new File(mc.getBaseUploadPath(), "user_000123/accept.png")).hasSameContentAs(TEST_MEDIA_NO_METADATA);
    }
}

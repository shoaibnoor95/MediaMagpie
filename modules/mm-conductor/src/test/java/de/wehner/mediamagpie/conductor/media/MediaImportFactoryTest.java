package de.wehner.mediamagpie.conductor.media;

import static org.fest.assertions.Assertions.*;

import static org.mockito.Matchers.*;

import static org.mockito.Mockito.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import de.wehner.mediamagpie.api.MediaExport;
import de.wehner.mediamagpie.api.testsupport.MediaExportFixture;
import de.wehner.mediamagpie.conductor.webapp.services.UploadService;
import de.wehner.mediamagpie.core.util.Pair;
import de.wehner.mediamagpie.persistence.dao.MediaDao;
import de.wehner.mediamagpie.persistence.dao.MediaTagDao;
import de.wehner.mediamagpie.persistence.entity.Media;
import de.wehner.mediamagpie.persistence.entity.MediaTag;
import de.wehner.mediamagpie.persistence.entity.User;
import de.wehner.mediamagpie.persistence.service.ConfigurationProvider;
import de.wehner.mediamagpie.persistence.util.TimeProvider;

public class MediaImportFactoryTest {

    private MediaImportFactory _mediaImportFactory;

    private static final File SRC_TEST_JPG = new File("../mm-conductor/src/test/resources/images/IMG_1414.JPG");

    @Mock
    private UploadService _uploadService;

    @Mock
    private User _user;

    @Mock
    private ConfigurationProvider _configurationProvider;

    @Mock
    private MediaDao _mediaDao;

    @Mock
    private MediaTagDao _mediaTagDao;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        Pair<String, File> pair = new Pair<String, File>("orig_file_name.bin", new File("myDir/orig_file_name_1.bin"));
        when(_uploadService.createUniqueUserStoreFile(any(User.class), anyString())).thenReturn(pair);

        Media media = new Media();
        media.setHashValue("pseudoHash");
        media.setUri(pair.getSecond().toURI().toString());
        when(_uploadService.saveInputStreamToFileSystemAndCreateMedia(any(User.class), eq(pair.getSecond()), any(InputStream.class))).thenReturn(media);
        MediaTag mediaTag = new MediaTag();
        when(_mediaTagDao.getByName(anyString())).thenReturn(mediaTag);
        _mediaImportFactory = new MediaImportFactory(_uploadService, _user, _configurationProvider, _mediaDao, _mediaTagDao, new TimeProvider());
    }

    @Test
    public void test_create_MediaFromMediaExport_ButOriginalFileNameIsNull() throws FileNotFoundException {
        MediaExport testMediaExport = MediaExportFixture.createMediaExportTestObject(123, "test name", SRC_TEST_JPG);
        testMediaExport.setOriginalFileName(null);

        Media newMedia = _mediaImportFactory.create(testMediaExport);

        assertThat(newMedia.getCreationDate()).isNotNull();
        assertThat(newMedia.getDescription()).isEqualTo(testMediaExport.getDescription());
        assertThat(newMedia.getHashValue()).isEqualTo("pseudoHash");
        assertThat(newMedia.getName()).isEqualTo(testMediaExport.getName());
        assertThat(newMedia.getOriginalFileName()).isNull();
        assertThat(newMedia.getTags()).hasSize(testMediaExport.getTags().size());
        assertThat(newMedia.getFileFromUri()).isNotNull();
    }
}

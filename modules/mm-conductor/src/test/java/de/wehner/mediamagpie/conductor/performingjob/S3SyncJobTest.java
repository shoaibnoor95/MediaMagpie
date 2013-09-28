package de.wehner.mediamagpie.conductor.performingjob;

import static org.fest.assertions.Assertions.*;

import static org.mockito.Matchers.*;

import static org.mockito.Mockito.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.net.URI;
import java.util.Arrays;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import de.wehner.mediamagpie.api.MediaExport;
import de.wehner.mediamagpie.aws.s3.S3MediaExportRepository;
import de.wehner.mediamagpie.conductor.webapp.services.UploadService;
import de.wehner.mediamagpie.core.testsupport.TestEnvironment;
import de.wehner.mediamagpie.core.util.Pair;
import de.wehner.mediamagpie.persistence.MediaExportFactory;
import de.wehner.mediamagpie.persistence.TransactionHandlerMock;
import de.wehner.mediamagpie.persistence.dao.MediaDao;
import de.wehner.mediamagpie.persistence.dao.PersistenceServiceMock;
import de.wehner.mediamagpie.persistence.dao.TransactionHandler;
import de.wehner.mediamagpie.persistence.entity.LifecyleStatus;
import de.wehner.mediamagpie.persistence.entity.Media;
import de.wehner.mediamagpie.persistence.entity.User;
import de.wehner.mediamagpie.persistence.service.ConfigurationProvider;
import de.wehner.mediamagpie.persistence.util.TimeProvider;

public class S3SyncJobTest {

    private static final URI IMAGE_RALF = new File("src/test/resources/images/ralf_small.jpg").toURI();
    private static final URI IMAGE_13 = new File("src/test/resources/images/IMG_0013.JPG").toURI();
    private static final URI IMAGE_14 = new File("src/test/resources/images/IMG_1414.JPG").toURI();
    private final Media m1;
    private final Media m2;
    private final Media m3;

    private TestEnvironment _testEnvironment = new TestEnvironment(getClass());

    private S3SyncJob _job;

    @Mock
    private S3MediaExportRepository _s3MediaExportRepository;

    @Mock
    private UploadService _uploadService;

    @Mock
    private ConfigurationProvider _configurationProvider;

    @Mock
    private User _user;

    private TransactionHandler _transactionHandler = new TransactionHandlerMock();

    @Mock
    private MediaDao _mediaDao;

    private JobCallable _prepare;

    /**
     * contains m1 and m2
     */
    private List<Media> _livingMedias;

    public S3SyncJobTest() throws FileNotFoundException {
        m1 = Media.createWithHashValue(_user, "ralf", IMAGE_RALF, new Date());
        m2 = Media.createWithHashValue(_user, "image-13", IMAGE_13, new Date());
        m3 = Media.createWithHashValue(_user, "image-14", IMAGE_14, new Date());
    }

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);

        _testEnvironment.cleanWorkingDir();

        when(_user.getName()).thenReturn("bob");

        _livingMedias = Arrays.asList(m1, m2);
        when(_mediaDao.getAllOfUser(_user, LifecyleStatus.Living)).thenReturn(_livingMedias);
        when(_mediaDao.getPersistenceService()).thenReturn(new PersistenceServiceMock());
        when(_uploadService.createUniqueUserStoreFile(eq(_user), any(String.class))).thenReturn(
                new Pair<String, File>("origFile.jpg", new File(_testEnvironment.getWorkingDir(), "mediax.jpg")));
        _job = new S3SyncJob(_s3MediaExportRepository, _uploadService, _user, _configurationProvider, _transactionHandler, _mediaDao, new TimeProvider());
        _prepare = _job.prepare();
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testMediaWillBePushed() throws Exception {
        MediaExportFactory mediaExportFactory = new MediaExportFactory();
        Iterator<MediaExport> iteratorPhotos = mock(Iterator.class);
        when(iteratorPhotos.hasNext()).thenReturn(true, false);
        when(iteratorPhotos.next()).thenReturn(mediaExportFactory.create(m1), (MediaExport) null);
        when(_s3MediaExportRepository.iteratorPhotos(_user.getName())).thenReturn(iteratorPhotos);

        _prepare.call();

        // verify, media will be pushed
        ArgumentCaptor<String> ownerNameCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<MediaExport> mediaExportCaptor = ArgumentCaptor.forClass(MediaExport.class);
        verify(_s3MediaExportRepository).addMedia(ownerNameCaptor.capture(), mediaExportCaptor.capture());
        assertThat(mediaExportCaptor.getValue().getName()).isEqualTo(m2.getName());
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testMediaWillBePulledAndPersisted() throws Exception {
        MediaExportFactory mediaExportFactory = new MediaExportFactory();
        Iterator<MediaExport> iteratorPhotos = mock(Iterator.class);
        when(iteratorPhotos.hasNext()).thenReturn(true, true, true, false);
        when(iteratorPhotos.next()).thenReturn(mediaExportFactory.create(m1), mediaExportFactory.create(m2), mediaExportFactory.create(m3), null);
        when(_s3MediaExportRepository.iteratorPhotos(_user.getName())).thenReturn(iteratorPhotos);
        when(_uploadService.saveInputStreamToFileSystemAndCreateMedia(any(User.class), any(File.class), any(InputStream.class))).thenReturn(
                Media.createWithHashValue(_user, m3.getName(), URI.create(m3.getUri()), null));

        _prepare.call();

        // verfiy one will be pulled (persisted)
        ArgumentCaptor<Media> mediaCaptor = ArgumentCaptor.forClass(Media.class);
        verify(_mediaDao, times(2)).makePersistent(mediaCaptor.capture());
        assertThat(mediaCaptor.getValue().getName()).isEqualTo(m3.getName());
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testMediaWillBePushedAndPulled() throws Exception {
        MediaExportFactory mediaExportFactory = new MediaExportFactory();
        Iterator<MediaExport> iteratorPhotos = mock(Iterator.class);
        when(iteratorPhotos.hasNext()).thenReturn(true, true, false);
        when(iteratorPhotos.next()).thenReturn(mediaExportFactory.create(m2), mediaExportFactory.create(m3), null);
        when(_s3MediaExportRepository.iteratorPhotos(_user.getName())).thenReturn(iteratorPhotos);
        when(_uploadService.saveInputStreamToFileSystemAndCreateMedia(any(User.class), any(File.class), any(InputStream.class))).thenReturn(
                Media.createWithHashValue(_user, m3.getName(), URI.create(m3.getUri()), null));

        _prepare.call();

        // verfiy one was pushed and one was pulled
        verify(_s3MediaExportRepository).addMedia(eq(_user.getName()), any(MediaExport.class));
        verify(_mediaDao, times(2)).makePersistent(any(Media.class));

        // verify pushed media
        ArgumentCaptor<String> ownerNameCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<MediaExport> mediaExportCaptor = ArgumentCaptor.forClass(MediaExport.class);
        verify(_s3MediaExportRepository).addMedia(ownerNameCaptor.capture(), mediaExportCaptor.capture());
        assertThat(mediaExportCaptor.getValue().getName()).isEqualTo(m1.getName());

        // verify the pulled media
        ArgumentCaptor<Media> mediaCaptor = ArgumentCaptor.forClass(Media.class);
        verify(_mediaDao, times(2)).makePersistent(mediaCaptor.capture());
        assertThat(mediaCaptor.getValue().getName()).isEqualTo(m3.getName());
    }
}

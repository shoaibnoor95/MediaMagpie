package de.wehner.mediamagpie.conductor.performingjob;

import static org.mockito.Mockito.*;

import java.io.File;
import java.net.URI;
import java.util.Arrays;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import de.wehner.mediamagpie.api.MediaExport;
import de.wehner.mediamagpie.aws.s3.S3MediaExportRepository;
import de.wehner.mediamagpie.common.persistence.MediaExportFactory;
import de.wehner.mediamagpie.common.persistence.dao.MediaDao;
import de.wehner.mediamagpie.common.persistence.entity.LifecyleStatus;
import de.wehner.mediamagpie.common.persistence.entity.Media;
import de.wehner.mediamagpie.common.persistence.entity.User;
import de.wehner.mediamagpie.common.persistence.entity.properties.UserConfiguration;
import de.wehner.mediamagpie.persistence.TransactionHandler;
import de.wehner.mediamagpie.persistence.TransactionHandlerMock;

public class S3SyncJobTest {

    private static final URI IMAGE_RALF = new File("src/test/resources/images/ralf_small.jpg").toURI();
    private static final URI IMAGE_13 = new File("src/test/resources/images/IMG_0013.JPG").toURI();
    private static final URI IMAGE_14 = new File("src/test/resources/images/IMG_1414.JPG").toURI();
    private final Media m1;
    private final Media m2;
    private final Media m3;

    private S3SyncJob _job;
    @Mock
    private S3MediaExportRepository _s3MediaExportRepository;
    @Mock
    private User _user;
    @Mock
    private UserConfiguration _userConfiguration;

    private TransactionHandler _transactionHandler = new TransactionHandlerMock();
    @Mock
    private MediaDao _mediaDao;

    private JobCallable _prepare;
    private List<Media> _livingMedias;

    public S3SyncJobTest() {
        m1 = new Media(_user, "ralf", IMAGE_RALF, new Date());
        m2 = new Media(_user, "image-13", IMAGE_13, new Date());
        m3 = new Media(_user, "image-14", IMAGE_14, new Date());
    }

    @Before
    @SuppressWarnings("unchecked")
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);

        when(_user.getName()).thenReturn("bob");

        _livingMedias = Arrays.asList(m1, m2);
        when(_mediaDao.getAllOfUser(_user, LifecyleStatus.Living)).thenReturn(_livingMedias);

        _job = new S3SyncJob(_s3MediaExportRepository, _user, _userConfiguration, _transactionHandler, _mediaDao);
        _prepare = _job.prepare();

        MediaExportFactory mediaExportFactory = new MediaExportFactory();

        Iterator<MediaExport> iteratorPhotos = mock(Iterator.class);
        when(iteratorPhotos.hasNext()).thenReturn(true, true, false);
        when(iteratorPhotos.next()).thenReturn(mediaExportFactory.create(m1), mediaExportFactory.create(m3), null);
        when(_s3MediaExportRepository.iteratorPhotos(_user.getName())).thenReturn(iteratorPhotos);
    }

    @Test
    public void test() throws Exception {
        _prepare.call();
    }
}

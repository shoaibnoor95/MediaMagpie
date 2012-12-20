package de.wehner.mediamagpie.conductor.webapp.services;

import static org.junit.Assert.*;

import static org.fest.assertions.Assertions.*;

import static org.mockito.Matchers.*;

import static org.mockito.Mockito.*;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.commons.io.FileUtils;
import org.hibernate.criterion.Order;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.internal.matchers.CapturingMatcher;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import de.wehner.mediamagpie.common.persistence.entity.Media;
import de.wehner.mediamagpie.common.persistence.entity.User;
import de.wehner.mediamagpie.common.persistence.entity.User.Role;
import de.wehner.mediamagpie.common.persistence.entity.properties.UserConfiguration;
import de.wehner.mediamagpie.common.test.util.TestEnvironment;
import de.wehner.mediamagpie.conductor.persistence.TransactionHandlerMock;
import de.wehner.mediamagpie.conductor.persistence.dao.MediaDao;
import de.wehner.mediamagpie.conductor.persistence.dao.UserConfigurationDao;
import de.wehner.mediamagpie.conductor.persistence.dao.UserDao;

public class MediaSyncServiceTest {

    private static final File SOURCE_TEST_DIR_MEDIA = new File("src/test/resources/data/media");

    @Mock
    private UserConfigurationDao _userConfigurationDao;
    @Mock
    private MediaDao _mediaDao;
    @Mock
    private UserDao _userDao;

    @Rule
    public TestEnvironment _testEnvironment = new TestEnvironment(getClass());

    private User _userA;
    private User _userB;
    private MediaSyncService _mediaSyncService;
    private String _testdataMediaDir;

    /**
     * 2010/07/10/resized_img_4358.jpg
     */
    private File _fileA;
    /**
     * 2010/07/12/resized_img_4556.jpg
     */
    private File _fileB;
    /**
     * 2010/07/12/resized_img_4636.jpg
     */
    @SuppressWarnings("unused")
    private File _fileC;
    private File _fileNotOnFs;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        _testdataMediaDir = new File(_testEnvironment.getWorkingDir(), "data/media").getPath();
        _fileA = new File(_testdataMediaDir, "2010/07/10/resized_img_4358.jpg");
        _fileB = new File(_testdataMediaDir, "2010/07/12/resized_img_4556.jpg");
        _fileC = new File(_testdataMediaDir, "2010/07/12/resized_img_4636.jpg");
        _fileNotOnFs = new File(_testdataMediaDir, "2010/07/10/this_simulates_deleted_file_on_fs.jpg");
        _mediaSyncService = new MediaSyncService(new TransactionHandlerMock(), _userDao, _mediaDao, _userConfigurationDao);
        UserConfiguration userConfiguration = new UserConfiguration();
        userConfiguration.simpleSetSingleRootMediaPath(_testdataMediaDir);
        _userA = new User("rwe", "rwe@localhost", Role.ADMIN);
        _userA.setId(1L);
        when(_userConfigurationDao.getConfiguration(_userA, UserConfiguration.class)).thenReturn(userConfiguration);
        _userB = new User("bb", "Berd.Blau@localhost", Role.USER);
        _userB.setId(2L);
        when(_userConfigurationDao.getConfiguration(_userB, UserConfiguration.class)).thenReturn(userConfiguration);
        when(_userDao.getAll(any(Order.class), anyInt())).thenReturn(Arrays.asList(_userA));
    }

    @Test
    public void syncDir_OnlyOneDirAndTestNoAddingNorRemovingOfMedia() throws IOException {
        // expected: just add the image from 2010/07/10 into DB
        UserConfiguration userConfiguration = _userConfigurationDao.getConfiguration(_userA, UserConfiguration.class);
        FileUtils.copyDirectory(SOURCE_TEST_DIR_MEDIA, new File(userConfiguration.getRootMediaPathes()[0]));
        // reset mainConfiguration.rootDir to 2010/07/10
        userConfiguration.simpleSetSingleRootMediaPath(new File(_testdataMediaDir, "2010/07/10").getPath());
        Media mediaA = new Media(null, null, _fileA.toURI(), new Date());
        when(_mediaDao.getAllByPathAndUri(_userA, _fileA.getParent(), Arrays.asList(_fileA.toURI().toString()), Integer.MAX_VALUE)).thenReturn(
                Arrays.asList(mediaA));

        boolean updatedDb = _mediaSyncService.execute();
        assertFalse(updatedDb);
        verify(_mediaDao, never()).makePersistent(any(Media.class));
        verify(_mediaDao, never()).makeTransient(any(Media.class));
    }

    @Test
    public void syncDir_OnlyOneDirAndTestAddingIntoDb() throws IOException {
        // expected: just add the image from 2010/07/10 into DB
        UserConfiguration userConfiguration = _userConfigurationDao.getConfiguration(_userA, UserConfiguration.class);
        FileUtils.copyDirectory(SOURCE_TEST_DIR_MEDIA, new File(userConfiguration.getRootMediaPathes()[0]));
        // reset mainConfiguration.rootDir to 2010/07/10
        userConfiguration.setRootMediaPathes(new String[] { new File(_testdataMediaDir, "2010/07/10").getPath() });

        boolean updatedDb = _mediaSyncService.execute();
        assertTrue(updatedDb);
        verify(_mediaDao).makePersistent(any(Media.class));
        verify(_mediaDao, never()).makeTransient(any(Media.class));
    }

    @SuppressWarnings("unchecked")
    @Test
    public void syncDir_WithDirectoryTreeAndTestAddingIntoDb() throws IOException {
        // expected: add the image from 2010/07/10 and one image from 2010/07/12 into DB
        UserConfiguration userConfiguration = _userConfigurationDao.getConfiguration(_userA, UserConfiguration.class);
        FileUtils.copyDirectory(SOURCE_TEST_DIR_MEDIA, new File(userConfiguration.getRootMediaPathes()[0]));
        Media mediaB = new Media(null, null, _fileB.toURI(), new Date());
        when(_mediaDao.getAllByPathAndUri(any(User.class), eq(_fileB.getParent()), (List<String>) anyObject(), eq(Integer.MAX_VALUE))).thenReturn(
                Arrays.asList(mediaB));

        boolean updatedDb = _mediaSyncService.execute();
        assertTrue(updatedDb);
        CapturingMatcher<Media> mediaCapturer = new CapturingMatcher<Media>();
        verify(_mediaDao, times(2)).makePersistent(argThat(mediaCapturer));
        List<Media> persistedMedias = mediaCapturer.getAllValues();
        Collections.sort(persistedMedias, new Comparator<Media>() {

            @Override
            public int compare(Media arg0, Media arg1) {
                return arg0.getUri().compareTo(arg1.getUri());
            }
        });
        assertTrue(persistedMedias.get(0).getUri().endsWith("/2010/07/10/resized_img_4358.jpg"));
        assertTrue(persistedMedias.get(1).getUri().endsWith("/2010/07/12/resized_img_4636.jpg"));
    }

    @Test
    public void syncDir_OnlyOneDirAndTestRemovingFromDb() throws IOException {
        // expected: just add the image from 2010/07/10 into DB
        UserConfiguration userConfiguration = _userConfigurationDao.getConfiguration(_userA, UserConfiguration.class);
        FileUtils.copyDirectory(SOURCE_TEST_DIR_MEDIA, new File(userConfiguration.getRootMediaPathes()[0]));
        // reset mainConfiguration.rootMediaPath to 2010/07/10
        userConfiguration.simpleSetSingleRootMediaPath(new File(_testdataMediaDir, "2010/07/10").getPath());
        Media mediaA = new Media(null, null, _fileA.toURI(), new Date());
        Media mediaOnlyInDb = new Media(null, null, _fileNotOnFs.toURI(), new Date());
        when(_mediaDao.getAllByPathAndUri(_userA, _fileA.getParent(), Arrays.asList(_fileA.toURI().toString()), Integer.MAX_VALUE)).thenReturn(
                Arrays.asList(mediaA));
        when(_mediaDao.getAllByPath(_userA, _fileNotOnFs.getParent(), Integer.MAX_VALUE)).thenReturn(Arrays.asList(mediaA, mediaOnlyInDb));
        when(_mediaDao.getByUri(_userA, _fileNotOnFs.toURI())).thenReturn(mediaOnlyInDb);

        boolean updatedDb = _mediaSyncService.execute();
        assertTrue(updatedDb);
        verify(_mediaDao, never()).makePersistent(any(Media.class));
        verify(_mediaDao).getByUri(_userA, _fileNotOnFs.toURI());
        verify(_mediaDao).makeTransient(mediaOnlyInDb);
    }

    @Test
    public void syncDir_TwoUsersUsingTheSameRootMediaPath() throws IOException {
        /**
         * Situation:<br/>
         * - rootMediaPath shows to '2010/07/10'<br/>
         * - on FS in rootMediaPath: _fileA<br/>
         * - userA: has Media for _fileA and _fileNotOnFs<br/>
         * - userB: has NO Media<br/>
         * Expected:<br/>
         * - userA: removes _fileNotOnFs<br/>
         * - userB: adds _fileA<br/>
         */
        UserConfiguration userConfiguration = _userConfigurationDao.getConfiguration(_userA, UserConfiguration.class);
        FileUtils.copyDirectory(SOURCE_TEST_DIR_MEDIA, new File(userConfiguration.getRootMediaPathes()[0]));
        // reset mainConfiguration.rootMediaPath to 2010/07/10
        userConfiguration.simpleSetSingleRootMediaPath(new File(_testdataMediaDir, "2010/07/10").getPath());
        when(_userDao.getAll(any(Order.class), anyInt())).thenReturn(Arrays.asList(_userA, _userB));
        // setup userA
        Media mediaA_FileA = new Media(_userA, null, _fileA.toURI(), new Date());
        Media mediaA_FileNotOnFs = new Media(_userA, null, _fileNotOnFs.toURI(), new Date());
        when(_mediaDao.getAllByPath(_userA, _fileA.getParent(), Integer.MAX_VALUE)).thenReturn(Arrays.asList(mediaA_FileA, mediaA_FileNotOnFs));
        when(_mediaDao.getAllByPathAndUri(_userA, _fileA.getParent(), MediaSyncService.uRIs2StringList(Arrays.asList(_fileA.toURI())), Integer.MAX_VALUE))
                .thenReturn(Arrays.asList(mediaA_FileA));
        when(_mediaDao.getByUri(_userA, _fileA.toURI())).thenReturn(mediaA_FileA);
        when(_mediaDao.getByUri(_userA, _fileNotOnFs.toURI())).thenReturn(mediaA_FileNotOnFs);

        // setup userB
        List<Media> emptyMediaList = Collections.emptyList();
        when(_mediaDao.getAllByPath(_userB, _fileA.getParent(), Integer.MAX_VALUE)).thenReturn(emptyMediaList);
        when(_mediaDao.getAllByPathAndUri(_userB, _fileA.getParent(), MediaSyncService.uRIs2StringList(Arrays.asList(_fileA.toURI())), Integer.MAX_VALUE))
                .thenReturn(emptyMediaList);

        boolean updatedDb = _mediaSyncService.execute();

        assertTrue(updatedDb);
        // userA removes only file _fileNotOnFs and userB adds only _fileA
        CapturingMatcher<Media> capturerMakeTransient = new CapturingMatcher<Media>();
        verify(_mediaDao, times(1)).makeTransient(argThat(capturerMakeTransient));
        assertThat(capturerMakeTransient.getAllValues().get(0).getOwner()).isEqualTo(_userA);
        assertThat(capturerMakeTransient.getAllValues().get(0).getUri()).endsWith(_fileNotOnFs.getPath());
        CapturingMatcher<Media> capturerMakePersistence = new CapturingMatcher<Media>();
        verify(_mediaDao, times(1)).makePersistent(argThat(capturerMakePersistence));
        assertThat(capturerMakePersistence.getAllValues().get(0).getOwner()).isEqualTo(_userB);
        assertThat(capturerMakePersistence.getAllValues().get(0).getUri()).endsWith(_fileA.getPath());
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testSyncMediaPathes() throws IOException {
        final UserConfiguration userConfiguration = _userConfigurationDao.getConfiguration(_userA, UserConfiguration.class);
        FileUtils.copyDirectory(SOURCE_TEST_DIR_MEDIA, new File(userConfiguration.getRootMediaPathes()[0]));
        userConfiguration.setRootMediaPathes(new String[] { new File(_testdataMediaDir, "2010/07/10").getPath() });
        final AtomicInteger processingCount = new AtomicInteger();
        when(_mediaDao.getAllByPathAndUri(any(User.class), any(String.class), any(List.class), any(Integer.class))).thenAnswer(new Answer<List<Media>>() {

            @Override
            public List<Media> answer(InvocationOnMock invocation) throws Throwable {
                int currentlyWaitingThreads = processingCount.incrementAndGet();
                Thread.sleep(100);
                assertThat(currentlyWaitingThreads).isEqualTo(1);
                processingCount.decrementAndGet();
                return new ArrayList<Media>();
            }
        });

        for (int i = 0; i < 2; i++) {
            Runnable syncThread = new Runnable() {

                @Override
                public void run() {
                    try {
                        _mediaSyncService.syncMediaPathes(_userA, userConfiguration.getRootMediaPathes()[0]);
                    } catch (IOException e) {
                    }
                }
            };
            syncThread.run();
        }
        verify(_mediaDao, times(2)).getAllByPathAndUri(any(User.class), any(String.class), any(List.class), any(Integer.class));
    }

    @Test
    public void test_resolveCreationDate() {

    }
}

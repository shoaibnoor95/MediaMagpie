package de.wehner.mediamagpie.conductor.persistence.dao;

import static org.junit.Assert.*;

import static org.fest.assertions.Assertions.*;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.List;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import de.wehner.mediamagpie.common.persistence.entity.Album;
import de.wehner.mediamagpie.common.persistence.entity.Media;
import de.wehner.mediamagpie.common.persistence.entity.User;
import de.wehner.mediamagpie.common.testsupport.DbTestEnvironment;
import de.wehner.mediamagpie.conductor.persistence.dao.AlbumDao;


public class AlbumDaoTest {

    @Rule
    public DbTestEnvironment _dbTestEnvironment = new DbTestEnvironment(/* "mysql-it" */);

    private Media _testMedia;
    private User _user;
    private AlbumDao _albumDao;

    @Before
    public void setUp() throws IOException {
        _dbTestEnvironment.cleanDb();
        _dbTestEnvironment.beginTransaction();
        _user = _dbTestEnvironment.getOrCreateTestUser();
        _testMedia = new Media(_user, "ralf", new File("/data/picture1.jpg").toURI(), new Date());
        _dbTestEnvironment.getPersistenceService().persist(_testMedia);
        _dbTestEnvironment.flipTransaction();
        _albumDao = new AlbumDao(_dbTestEnvironment.getPersistenceService());
    }

    @Test
    public void testInsertLoadDelete() {
        _testMedia = _dbTestEnvironment.reload(_testMedia);
        Album album = new Album(_user, "Kindergarten");
        album.addMedia(_testMedia);
        _albumDao.makePersistent(album);
        _dbTestEnvironment.flipTransaction();

        List<Album> albumFromDb = _albumDao.getAll();
        assertThat(albumFromDb).hasSize(1);
        assertThat(albumFromDb.get(0).getMedias()).hasSize(1);

        // delete album
        _albumDao.makeTransient(albumFromDb.get(0));
        _dbTestEnvironment.flipTransaction();
        assertThat(_albumDao.getAll()).isEmpty();
        List<Media> mediasFromDb = _dbTestEnvironment.getPersistenceService().getAll(Media.class);
        assertThat(mediasFromDb).hasSize(1);

        _dbTestEnvironment.commitTransaction();
    }

    @Test
    public void testCascadeDeleteMedia() {
        Album album = new Album(_dbTestEnvironment.reload(_user), "Kindergarten");
        album.addMedia(_dbTestEnvironment.reload(_testMedia));
        _albumDao.makePersistent(album);
        _dbTestEnvironment.flipTransaction();
        Album albumFromDb = _albumDao.getById(album.getId());
        assertThat(albumFromDb.getMedias()).hasSize(1);

        // delete the media
        try {
            Media media = _dbTestEnvironment.reload(_testMedia);
            _dbTestEnvironment.getPersistenceService().remove(media);
            _dbTestEnvironment.flipTransaction();
        } catch (Exception e) {
            e.printStackTrace();
            fail(e.getMessage());
        }

        albumFromDb = _albumDao.getById(album.getId());
        assertThat(albumFromDb.getMedias()).isEmpty();

        _dbTestEnvironment.commitTransaction();
    }

    @Test
    public void testLoadUser() {
        _testMedia = _dbTestEnvironment.reload(_testMedia);
        Album album = new Album(_user, "Kindergarten");
        album.addMedia(_testMedia);
        _albumDao.makePersistent(album);
        _dbTestEnvironment.flipTransaction();

        Album albumFormDb = _albumDao.getByUuid(album.getUid());

        assertThat(albumFormDb.getOwner()).isEqualTo(_user);

        _dbTestEnvironment.commitTransaction();
    }
}

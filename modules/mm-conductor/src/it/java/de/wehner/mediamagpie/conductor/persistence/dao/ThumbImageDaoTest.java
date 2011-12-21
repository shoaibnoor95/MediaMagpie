package de.wehner.mediamagpie.conductor.persistence.dao;

import static org.junit.Assert.*;

import java.io.File;
import java.util.Date;
import java.util.List;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import de.wehner.mediamagpie.common.persistence.entity.Media;
import de.wehner.mediamagpie.common.persistence.entity.ThumbImage;
import de.wehner.mediamagpie.common.test.util.TestEnvironment;
import de.wehner.mediamagpie.common.testsupport.DbTestEnvironment;
import de.wehner.mediamagpie.conductor.persistence.dao.MediaDao;
import de.wehner.mediamagpie.conductor.persistence.dao.ThumbImageDao;


public class ThumbImageDaoTest {

    private MediaDao _mediaDao;
    private ThumbImageDao _thumbImageDao;
    @Rule
    public DbTestEnvironment _dbTestEnvironment = new DbTestEnvironment();
    private TestEnvironment _testEnvironment = new TestEnvironment(getClass());
    private File _testMedia;
    private File _testThumb;

    @Before
    public void setUp() {
        _mediaDao = new MediaDao(_dbTestEnvironment.getPersistenceService());
        _thumbImageDao = new ThumbImageDao(_dbTestEnvironment.getPersistenceService());
        _testMedia = new File(_testEnvironment.getWorkingDir(), "image.png");
        _testThumb = new File(_testEnvironment.getWorkingDir(), "thumb.png");
        _dbTestEnvironment.beginTransaction();
    }

    @Test
    public void testMakePersistent() throws Exception {
        Media m1 = new Media(_dbTestEnvironment.getOrCreateTestUser(), "ralf", _testMedia.toURI(), new Date());
        ThumbImage thumbImage = new ThumbImage(m1, "label", _testThumb.getPath());
        _mediaDao.makePersistent(m1);
        _thumbImageDao.makePersistent(thumbImage);
        _dbTestEnvironment.flipTransaction();

        List<Media> all = _mediaDao.getAll();
        assertEquals(1, all.size());
        assertEquals(m1.getName(), all.get(0).getName());
        assertEquals(1, _thumbImageDao.getAll().size());
        assertEquals(thumbImage.getLabel(), _thumbImageDao.getById(thumbImage.getId()).getLabel());
        _dbTestEnvironment.commitTransaction();
    }

    @Test
    public void testMakeTransient() throws Exception {
        Media m1 = new Media(_dbTestEnvironment.getOrCreateTestUser(), "ralf", _testMedia.toURI(), new Date());
        ThumbImage thumbImage = new ThumbImage(m1, "label", _testThumb.getPath());
        _mediaDao.makePersistent(m1);
        _thumbImageDao.makePersistent(thumbImage);
        _dbTestEnvironment.flipTransaction();

        assertEquals(1, _mediaDao.getAll().size());
        assertEquals(1, _thumbImageDao.getAll().size());

        _thumbImageDao.makeTransient(_dbTestEnvironment.getPersistenceService().reload(thumbImage));
        _dbTestEnvironment.flipTransaction();

        assertEquals(1, _mediaDao.getAll().size());
        assertEquals(0, _thumbImageDao.getAll().size());
        _dbTestEnvironment.commitTransaction();
    }

}

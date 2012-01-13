package de.wehner.mediamagpie.conductor.persistence.dao;

import java.io.File;
import java.util.Date;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import de.wehner.mediamagpie.common.persistence.entity.Media;
import de.wehner.mediamagpie.common.persistence.entity.MediaTag;
import de.wehner.mediamagpie.common.testsupport.DbTestEnvironment;
import de.wehner.mediamagpie.common.testsupport.PersistenceTestUtil;
import de.wehner.mediamagpie.conductor.persistence.PersistenceService;

public class MediaTagDaoTest extends AbstractDaoTest<MediaTagDao> {

    // @BeforeClass
    // public static void setUpClass() {
    // System.setProperty("db.mode", "mysql-it");
    // }

    @Before
    public void setUp() {
        super.setUp();
        PersistenceTestUtil.deleteAll(_persistenceService);
        _persistenceService.beginTransaction();
    }

    @Override
    protected MediaTagDao createDao(PersistenceService persistenceService) {
        return new MediaTagDao(persistenceService);
    }

    @Test
    public void test() {
        Media m0 = new Media(DbTestEnvironment.getOrCreateTestUser(_persistenceService), "M0", new File("/data/picture1.jpg").toURI(), new Date());
        MediaTag tag = new MediaTag("Freizeit");
        MediaTag tag1 = new MediaTag("Brotzeit");
        MediaTag tag2 = new MediaTag("Mittagessen");
        m0.addTag(tag);
        m0.addTag(tag1);
        m0.addTag(tag2);
        MediaDao mediaDao = new MediaDao(_persistenceService);
        mediaDao.makePersistent(m0);
        _persistenceService.flipTransaction();
//
        MediaTagDao mediaTagDao = createDao();
        List<MediaTag> luceneSearchForName = mediaTagDao.luceneSearchForName("zeit");
        _persistenceService.commitTransaction();
//
//        assertThat(luceneSearchForName).hasSize(2);
//        assertThat(luceneSearchForName).contains(tag, tag1);
    }
}

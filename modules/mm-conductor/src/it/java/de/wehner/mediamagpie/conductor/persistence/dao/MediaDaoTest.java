package de.wehner.mediamagpie.conductor.persistence.dao;

import static org.junit.Assert.*;

import static org.fest.assertions.Assertions.*;

import static org.mockito.Mockito.*;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.util.Date;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.solr.common.util.DateUtil;
import org.hibernate.criterion.Order;
import org.hibernate.search.jpa.FullTextEntityManager;
import org.hibernate.search.query.dsl.QueryBuilder;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import de.wehner.mediamagpie.common.persistence.entity.ImageResizeJobExecution;
import de.wehner.mediamagpie.common.persistence.entity.LifecyleStatus;
import de.wehner.mediamagpie.common.persistence.entity.Media;
import de.wehner.mediamagpie.common.persistence.entity.MediaTag;
import de.wehner.mediamagpie.common.persistence.entity.ThumbImage;
import de.wehner.mediamagpie.common.persistence.entity.User;
import de.wehner.mediamagpie.common.test.util.TestEnvironment;
import de.wehner.mediamagpie.common.testsupport.DbTestEnvironment;
import de.wehner.mediamagpie.common.util.TimeUtil;
import de.wehner.mediamagpie.conductor.webapp.controller.commands.SearchCriteriaCommand;
import de.wehner.mediamagpie.conductor.webapp.controller.media.common.UiMediaSortOrder;

public class MediaDaoTest {

    @Rule
    public DbTestEnvironment _dbTestEnvironment = new DbTestEnvironment();
    private TestEnvironment _testEnvironment = new TestEnvironment(getClass());
    private File _testMedia;
    private File _testThumb;

    private User _user;
    private MediaDao _mediaDao;

    @Before
    public void setUp() throws IOException {
        _dbTestEnvironment.cleanDb();
        _testMedia = new File(_testEnvironment.getWorkingDir(), "image.png");
        FileUtils.writeStringToFile(_testMedia, "foo");
        _testThumb = new File(_testEnvironment.getWorkingDir(), "thumb.png");
        FileUtils.writeStringToFile(_testThumb, "foo");
        _dbTestEnvironment.beginTransaction();
        _user = _dbTestEnvironment.getOrCreateTestUser();
        _mediaDao = new MediaDao(_dbTestEnvironment.getPersistenceService());
    }

    @Test
    public void testGetAll() throws Exception {
        Media m1 = new Media(_user, "ralf", new File("/data/picture1.jpg").toURI(), new Date());

        MediaDao mediaDao = new MediaDao(_dbTestEnvironment.getPersistenceService());
        mediaDao.makePersistent(m1);
        _dbTestEnvironment.flipTransaction();

        List<Media> all = mediaDao.getAll();
        assertEquals(1, all.size());
        assertEquals(m1.getName(), all.get(0).getName());
        assertThat(m1.getOwner()).isEqualTo(_user);
        _dbTestEnvironment.commitTransaction();
    }

    @Test
    public void testGetAllByCriterias_TestDateRange() throws ParseException {
        Date t0 = TimeUtil.parseGermanDate("31.12.1999");
        Date t1 = TimeUtil.parseGermanDate("01.01.2000");
        Date t2 = TimeUtil.parseGermanDate("02.01.2000");
        Date t3 = TimeUtil.parseGermanDate("12.04.2011");
        Media m0 = new Media(_user, "M0", new File("/data/picture1.jpg").toURI(), t0);
        Media m1 = new Media(_user, "M1", new File("/data/picture21.jpg").toURI(), t1);
        Media m2 = new Media(_user, "M2", new File("/data/picture3.jpg").toURI(), t2);
        Media m3 = new Media(_user, "M3", new File("/data/picture4.jpg").toURI(), t3);
        MediaDao mediaDao = new MediaDao(_dbTestEnvironment.getPersistenceService());
        mediaDao.makePersistent(m0);
        mediaDao.makePersistent(m1);
        mediaDao.makePersistent(m2);
        mediaDao.makePersistent(m3);
        _dbTestEnvironment.flipTransaction();

        SearchCriteriaCommand criteriaCommand = mock(SearchCriteriaCommand.class);
        when(criteriaCommand.getRangeT0()).thenReturn(t1);
        when(criteriaCommand.getRangeT1()).thenReturn(t2);
        when(criteriaCommand.getSortOrder()).thenReturn(UiMediaSortOrder.DATE);
        List<Media> allByCriterias = mediaDao.getAllByCriterias(_user, 0, Integer.MAX_VALUE, true, criteriaCommand);

        assertThat(allByCriterias).containsSequence(m1, m2);
        _dbTestEnvironment.commitTransaction();
    }

    @Test
    public void testMediaTagCascadingAndDeleteOrphan() throws ParseException {
        Media m0 = new Media(_user, "M0", new File("/data/picture1.jpg").toURI(), new Date());
        Media m1 = new Media(_user, "M1", new File("/data/picture21.jpg").toURI(), new Date());
        MediaTag tag = new MediaTag("Tag0");
        MediaTag tag1 = new MediaTag("Tag1");
        MediaTag tag2 = new MediaTag("Tag2");
        m0.addTag(tag);
        m1.addTag(tag1);
        m1.addTag(tag2);
        MediaDao mediaDao = new MediaDao(_dbTestEnvironment.getPersistenceService());
        mediaDao.makePersistent(m0);
        mediaDao.makePersistent(m1);
        _dbTestEnvironment.flipTransaction();

        List<Media> allMediasFromDb = mediaDao.getAll(Order.asc("id"), 10);

        assertThat(allMediasFromDb).contains(m0, m1);
        assertThat(allMediasFromDb.get(0).getTags()).contains(tag);
        assertThat(allMediasFromDb.get(1).getTags()).contains(tag1, tag2);
        MediaTagDao mediaTagDao = new MediaTagDao(_dbTestEnvironment.getPersistenceService());
        List<MediaTag> mediaTagsFromDb = mediaTagDao.getAll();
        assertThat(mediaTagsFromDb).hasSize(3);
        _dbTestEnvironment.flipTransaction();

        mediaDao.makeTransient(_dbTestEnvironment.reload(m0));
        _dbTestEnvironment.flipTransaction();

        mediaDao.makeTransient(_dbTestEnvironment.reload(m1));
        _dbTestEnvironment.flipTransaction();

        mediaTagsFromDb = mediaTagDao.getAll();
        assertThat(mediaTagsFromDb).isEmpty();

        _dbTestEnvironment.commitTransaction();
    }

    @Test
    public void testMakeTransient_withAssignedThumbImage() throws Exception {
        Media m1 = new Media(_dbTestEnvironment.getOrCreateTestUser(), "ralf", _testMedia.toURI(), new Date());
        ThumbImage thumbImage = new ThumbImage(m1, "label", _testThumb.getPath());
        m1.getThumbImages().add(thumbImage);
        _mediaDao.makePersistent(m1);
        _dbTestEnvironment.flipTransaction();

        assertThat(_mediaDao.getAll()).hasSize(1);
        assertThat(_dbTestEnvironment.getPersistenceService().getAll(ThumbImage.class)).hasSize(1);

        _mediaDao.makeTransient(_dbTestEnvironment.reload(m1));
        _dbTestEnvironment.flipTransaction();

        assertThat(_mediaDao.getAll()).isEmpty();
        assertThat(_dbTestEnvironment.getPersistenceService().getAll(ThumbImage.class)).isEmpty();
        _dbTestEnvironment.commitTransaction();

        // verify, thumb file doesn't exists
        assertThat(_testThumb).doesNotExist();
    }

    @Test
    public void testMakeTransient_withAssignedImageResizeJobExecution() throws Exception {
        Media m1 = new Media(_dbTestEnvironment.getOrCreateTestUser(), "ralf", _testMedia.toURI(), new Date());
        ImageResizeJobExecution jobExecution = new ImageResizeJobExecution(m1, "30");
        ImageResizeJobExecutionDao imageResizeJobExecutionDao = new ImageResizeJobExecutionDao(_dbTestEnvironment.getPersistenceService());
        _mediaDao.makePersistent(m1);
        imageResizeJobExecutionDao.makePersistent(jobExecution);
        _dbTestEnvironment.flipTransaction();

        assertThat(_mediaDao.getAll()).hasSize(1);
        assertThat(_dbTestEnvironment.getPersistenceService().getAll(ImageResizeJobExecution.class)).hasSize(1);

        _mediaDao.makeTransient(_dbTestEnvironment.reload(m1));
        _dbTestEnvironment.flipTransaction();

        // verify the assigned ImageResizeJobExecution is deleted too
        assertThat(_mediaDao.getAll()).isEmpty();
        assertThat(_dbTestEnvironment.getPersistenceService().getAll(ImageResizeJobExecution.class)).isEmpty();
        _dbTestEnvironment.commitTransaction();
    }

    @Test
    public void testHibernateSearchOnMediaTags() throws ParseException {
        Media m0 = new Media(_user, "M0", new File("/data/picture1.jpg").toURI(), new Date());
        Media m1 = new Media(_user, "M1", new File("/data/picture21.jpg").toURI(), new Date());
        MediaTag tag = new MediaTag("Tag0");
        MediaTag tag1 = new MediaTag("Tag1");
        MediaTag tag2 = new MediaTag("Tag2");
        m0.addTag(tag);
        m1.addTag(tag1);
        m1.addTag(tag2);
        MediaDao mediaDao = new MediaDao(_dbTestEnvironment.getPersistenceService());
        mediaDao.makePersistent(m0);
        mediaDao.makePersistent(m1);
        _dbTestEnvironment.flipTransaction();

        FullTextEntityManager fullTextEntityManager = _dbTestEnvironment.getPersistenceService().getFullTextEntityManager();
        QueryBuilder qb = fullTextEntityManager.getSearchFactory().buildQueryBuilder().forEntity(Media.class).get();

        org.apache.lucene.search.Query query = qb.keyword().onFields("_tags._name"/* , "_tagString" */).matching(tag1.getName()).createQuery();
        // wrap Lucene query in a javax.persistence.Query
        javax.persistence.Query persistenceQuery = fullTextEntityManager.createFullTextQuery(query, Media.class);

        // execute search
        @SuppressWarnings("unchecked")
        List<Media> result = persistenceQuery.getResultList();

        assertThat(result).hasSize(1);
        assertThat(result).contains(m1);
        _dbTestEnvironment.commitTransaction();
    }

    @Test
    public void testGetAllBySearchCriterias_BasedOnJPAQuery() throws ParseException {
        Media m0 = new Media(_user, "Die Kinder beim Rotkohl essen.", new File("/data/picture1.jpg").toURI(), DateUtil.parseDate("2011-05-12"));
        Media m1 = new Media(_user, "Die Kinder beim Fussball spielen.", new File("/data/picture21.jpg").toURI(), DateUtil.parseDate("2010-05-12"));
        MediaTag tag = new MediaTag("Tag0");
        MediaTag tag1 = new MediaTag("Tag1");
        MediaTag tag2 = new MediaTag("Tag2");
        m0.addTag(tag);
        m1.addTag(tag1);
        m1.addTag(tag2);
        MediaDao mediaDao = new MediaDao(_dbTestEnvironment.getPersistenceService());
        mediaDao.makePersistent(m0);
        mediaDao.makePersistent(m1);
        _dbTestEnvironment.flipTransaction();

        SearchCriteriaCommand searchCriteria = new SearchCriteriaCommand();
        searchCriteria.setYearCriteria("2010 - 2011");
        searchCriteria.setSortOrder(UiMediaSortOrder.DATE);
        List<Media> foundMedias = mediaDao.getAllBySearchCriterias(_user, 0, Integer.MAX_VALUE, true, searchCriteria, LifecyleStatus.Living);

        assertThat(foundMedias).hasSize(2);
        assertThat(foundMedias).contains(m0, m1);
        assertThat(mediaDao.getAllBySearchCriteriasCount(_user, searchCriteria, LifecyleStatus.Living)).isEqualTo(2);
        _dbTestEnvironment.commitTransaction();
    }

    @Test
    public void testGetAllBySearchCriterias_BasedOnHibernateSearch() throws ParseException {
        Media m0 = new Media(_user, "Die Kinder beim Rotkohl essen.", new File("/data/picture1.jpg").toURI(), DateUtil.parseDate("2011-05-12"));
        Media m1 = new Media(_user, "Die Kinder beim Fussball spielen.", new File("/data/picture21.jpg").toURI(), DateUtil.parseDate("2010-05-12"));
        MediaTag tag = new MediaTag("Tag0");
        MediaTag tag1 = new MediaTag("Tag1");
        MediaTag tag2 = new MediaTag("Tag2");
        m0.addTag(tag);
        m1.addTag(tag1);
        m1.addTag(tag2);
        MediaDao mediaDao = new MediaDao(_dbTestEnvironment.getPersistenceService());
        mediaDao.makePersistent(m0);
        mediaDao.makePersistent(m1);
        _dbTestEnvironment.flipTransaction();

        SearchCriteriaCommand searchCriteria = new SearchCriteriaCommand();
        searchCriteria.setYearCriteria("2010 - 2011");
        searchCriteria.setBuzzword("Rotkohl");
        List<Media> foundMedias = mediaDao.getAllBySearchCriterias(_user, 0, Integer.MAX_VALUE, true, searchCriteria, LifecyleStatus.Living);

        assertThat(foundMedias).hasSize(1);
        assertThat(foundMedias).contains(m0);
        assertThat(mediaDao.getAllBySearchCriteriasCount(_user, searchCriteria, LifecyleStatus.Living)).isEqualTo(1);
        _dbTestEnvironment.commitTransaction();
    }
}

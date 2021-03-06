package de.wehner.mediamagpie.persistence.dao;

import static org.junit.Assert.*;

import static org.fest.assertions.Assertions.*;

import static org.mockito.Mockito.*;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.util.Arrays;
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

import de.wehner.mediamagpie.core.testsupport.TestEnvironment;
import de.wehner.mediamagpie.core.util.MinMaxValue;
import de.wehner.mediamagpie.core.util.TimeUtil;
import de.wehner.mediamagpie.persistence.dto.SearchCriteriaCommand;
import de.wehner.mediamagpie.persistence.dto.UiMediaSortOrder;
import de.wehner.mediamagpie.persistence.entity.Album;
import de.wehner.mediamagpie.persistence.entity.ConvertedVideo;
import de.wehner.mediamagpie.persistence.entity.ImageResizeJobExecution;
import de.wehner.mediamagpie.persistence.entity.LifecyleStatus;
import de.wehner.mediamagpie.persistence.entity.Media;
import de.wehner.mediamagpie.persistence.entity.MediaTag;
import de.wehner.mediamagpie.persistence.entity.ThumbImage;
import de.wehner.mediamagpie.persistence.entity.User;
import de.wehner.mediamagpie.persistence.entity.Visibility;
import de.wehner.mediamagpie.persistence.testsupport.DbTestEnvironment;

public class MediaDaoTest {

    @Rule
    public DbTestEnvironment _dbTestEnvironment = new DbTestEnvironment();
    private TestEnvironment _testEnvironment = new TestEnvironment(getClass());
    private File _testMedia;
    private File _thumbOrConvVideo;

    private User _user;
    private MediaDao _mediaDao;

    @Before
    public void setUp() throws IOException {
        _dbTestEnvironment.cleanDb();
        _testMedia = new File(_testEnvironment.getWorkingDir(), "image.png");
        FileUtils.writeStringToFile(_testMedia, "foo");
        _thumbOrConvVideo = new File(_testEnvironment.getWorkingDir(), "thumb.png");
        FileUtils.writeStringToFile(_thumbOrConvVideo, "foo");
        _dbTestEnvironment.beginTransaction();
        _user = _dbTestEnvironment.getOrCreateTestUser();
        _mediaDao = new MediaDao(_dbTestEnvironment.getPersistenceService());
    }

    @Test
    public void testGetAll() throws Exception {
        Media m1 = new Media(_user, "ralf", new File("/data/picture1.jpg").toURI(), new Date());
        _mediaDao.makePersistent(m1);
        _dbTestEnvironment.flipTransaction();

        List<Media> all = _mediaDao.getAll();
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
        _mediaDao.makePersistent(m0);
        _mediaDao.makePersistent(m1);
        _mediaDao.makePersistent(m2);
        _mediaDao.makePersistent(m3);
        _dbTestEnvironment.flipTransaction();

        SearchCriteriaCommand criteriaCommand = mock(SearchCriteriaCommand.class);
        when(criteriaCommand.getSearchBeginAsDate()).thenReturn(t1);
        when(criteriaCommand.getSearchEndAsDate()).thenReturn(t2);
        when(criteriaCommand.getSortOrder()).thenReturn(UiMediaSortOrder.DATE);
        List<Media> allByCriterias = _mediaDao.getAllByCriterias(_user, 0, Integer.MAX_VALUE, true, criteriaCommand);

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
        _mediaDao.makePersistent(m0);
        _mediaDao.makePersistent(m1);
        _dbTestEnvironment.flipTransaction();

        List<Media> allMediasFromDb = _mediaDao.getAll(Order.asc("id"), 10);

        assertThat(allMediasFromDb).contains(m0, m1);
        assertThat(allMediasFromDb.get(0).getTags()).contains(tag);
        assertThat(allMediasFromDb.get(1).getTags()).contains(tag1, tag2);
        MediaTagDao mediaTagDao = new MediaTagDao(_dbTestEnvironment.getPersistenceService());
        List<MediaTag> mediaTagsFromDb = mediaTagDao.getAll();
        assertThat(mediaTagsFromDb).hasSize(3);
        _dbTestEnvironment.flipTransaction();

        _mediaDao.makeTransient(_dbTestEnvironment.reload(m0));
        _dbTestEnvironment.flipTransaction();

        _mediaDao.makeTransient(_dbTestEnvironment.reload(m1));
        _dbTestEnvironment.flipTransaction();

        mediaTagsFromDb = mediaTagDao.getAll();
        assertThat(mediaTagsFromDb).isEmpty();

        _dbTestEnvironment.commitTransaction();
    }

    @Test
    public void testMakeTransient_withAssignedThumbImage() throws Exception {
        Media m1 = new Media(_dbTestEnvironment.getOrCreateTestUser(), "ralf", _testMedia.toURI(), new Date());
        ThumbImage thumbImage = new ThumbImage(m1, "label", _thumbOrConvVideo.getPath());
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
        assertThat(_thumbOrConvVideo).doesNotExist();
    }

    @Test
    public void testMakeTransient_withAssignedConvertedVideo() throws Exception {
        Media m1 = new Media(_dbTestEnvironment.getOrCreateTestUser(), "videoMedia", _testMedia.toURI(), new Date());
        ConvertedVideo convertedVideo = new ConvertedVideo(m1, "label", "mp4", _thumbOrConvVideo.getPath());
        m1.getConvertedVideos().add(convertedVideo);
        _mediaDao.makePersistent(m1);
        _dbTestEnvironment.flipTransaction();

        assertThat(_mediaDao.getAll()).hasSize(1);
        assertThat(_dbTestEnvironment.getPersistenceService().getAll(ConvertedVideo.class)).hasSize(1);

        _mediaDao.makeTransient(_dbTestEnvironment.reload(m1));
        _dbTestEnvironment.flipTransaction();

        assertThat(_mediaDao.getAll()).isEmpty();
        assertThat(_dbTestEnvironment.getPersistenceService().getAll(ConvertedVideo.class)).isEmpty();
        _dbTestEnvironment.commitTransaction();

        // verify, video file doesn't exists any more
        assertThat(_thumbOrConvVideo).doesNotExist();
    }

    @Test
    public void testMakeTransient_withAssignedImageResizeJobExecution() throws Exception {
        Media m1 = new Media(_dbTestEnvironment.getOrCreateTestUser(), "ralf", _testMedia.toURI(), new Date());
        ImageResizeJobExecution jobExecution = new ImageResizeJobExecution(m1, "30");
        MediaDataProcessingJobExecutionDao imageResizeJobExecutionDao = new MediaDataProcessingJobExecutionDao(_dbTestEnvironment.getPersistenceService());
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
        searchCriteria.setSliderYearValues(new MinMaxValue<Integer>(2010, 2011));
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
        searchCriteria.setSliderYearValues(new MinMaxValue<Integer>(2010, 2011));
        searchCriteria.setBuzzword("Rotkohl");
        List<Media> foundMedias = mediaDao.getAllBySearchCriterias(_user, 0, Integer.MAX_VALUE, true, searchCriteria, LifecyleStatus.Living);

        assertThat(foundMedias).hasSize(1);
        assertThat(foundMedias).contains(m0);
        assertThat(mediaDao.getAllBySearchCriteriasCount(_user, searchCriteria, LifecyleStatus.Living)).isEqualTo(1);
        _dbTestEnvironment.commitTransaction();
    }

    @Test
    public void test_getAllLastAddedPublicMedias() throws ParseException {
        MediaDao mediaDao = new MediaDao(_dbTestEnvironment.getPersistenceService());
        AlbumDao albumDao = new AlbumDao(_dbTestEnvironment.getPersistenceService());
        Album album1 = new Album(_user, "name1");
        album1.setVisibility(Visibility.PUBLIC);
        albumDao.makePersistent(album1);
        Media m0 = new Media(_user, "Die Kinder beim Rotkohl essen.", new File("/data/picture1.jpg").toURI(), DateUtil.parseDate("2011-05-12"));
        Album album2 = new Album(_user, "name2");
        album2.setVisibility(Visibility.PUBLIC);
        albumDao.makePersistent(album2);
        m0.setAlbums(Arrays.asList(album1, album2));
        album1.addMedia(m0);
        album2.addMedia(m0);
        mediaDao.makePersistent(m0);
        _dbTestEnvironment.flipTransaction();

        List<Media> lastAddedPublicMedias = mediaDao.getAllLastAddedPublicMedias(Visibility.PUBLIC, 100);

        assertThat(lastAddedPublicMedias).hasSize(1);
    }
}

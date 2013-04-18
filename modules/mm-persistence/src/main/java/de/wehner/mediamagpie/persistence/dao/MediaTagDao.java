package de.wehner.mediamagpie.persistence.dao;

import java.util.List;

import org.hibernate.search.jpa.FullTextEntityManager;
import org.hibernate.search.query.dsl.QueryBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import de.wehner.mediamagpie.persistence.entity.MediaTag;


@Repository
public class MediaTagDao extends Dao<MediaTag> {

    private static final Logger LOG = LoggerFactory.getLogger(MediaTagDao.class);

    @Autowired
    public MediaTagDao(PersistenceService persistenceService) {
        super(MediaTag.class, persistenceService);
    }

    public List<MediaTag> luceneSearchForName(String buzzword) {
        // For queries, see: http://docs.jboss.org/hibernate/stable/search/reference/en-US/html/search-query.html#search-query-lucene-api
        FullTextEntityManager fullTextEntityManager = _persistenceService.getFullTextEntityManager();
        QueryBuilder qb = fullTextEntityManager.getSearchFactory().buildQueryBuilder().forEntity(MediaTag.class).get();

        org.apache.lucene.search.Query query = qb.keyword().wildcard().onField("_name").matching("*" + buzzword + "*").createQuery();
        LOG.info("using lucene query '" + query + "'.");

        // wrap Lucene query in a javax.persistence.Query
        javax.persistence.Query persistenceQuery = fullTextEntityManager.createFullTextQuery(query, MediaTag.class);

        // execute search
        @SuppressWarnings("unchecked")
        List<MediaTag> result = persistenceQuery.getResultList();
        return result;
    }
}

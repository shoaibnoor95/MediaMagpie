package de.wehner.mediamagpie.conductor.persistence.dao;

import java.io.File;
import java.net.URI;
import java.util.List;

import javax.persistence.NoResultException;
import javax.persistence.Query;

import org.apache.commons.lang.StringUtils;
import org.hibernate.Criteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.search.jpa.FullTextEntityManager;
import org.hibernate.search.jpa.FullTextQuery;
import org.hibernate.search.query.dsl.QueryBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import de.wehner.mediamagpie.common.fslayer.IFile;
import de.wehner.mediamagpie.common.persistence.entity.LifecyleStatus;
import de.wehner.mediamagpie.common.persistence.entity.Media;
import de.wehner.mediamagpie.common.persistence.entity.User;
import de.wehner.mediamagpie.common.persistence.entity.Visibility;
import de.wehner.mediamagpie.common.util.ArrayUtil;
import de.wehner.mediamagpie.conductor.persistence.PersistenceService;
import de.wehner.mediamagpie.conductor.webapp.controller.commands.SearchCriteriaCommand;
import de.wehner.mediamagpie.conductor.webapp.controller.media.common.UiMediaSortOrder;

@Repository
public class MediaDao extends CreationDateBaseDao<Media> {

    private static final Logger LOG = LoggerFactory.getLogger(MediaDao.class);

    @Autowired
    public MediaDao(PersistenceService persistenceService) {
        super(Media.class, persistenceService);
    }

    @SuppressWarnings("unchecked")
    public boolean exists(String name) {
        Query createQuery = _persistenceService.createQuery("select t from " + _clazz.getSimpleName() + " as t where t._name = ?1");
        createQuery.setParameter(1, name);
        List<Media> result = createQuery.getResultList();
        return !result.isEmpty();
    }

    @SuppressWarnings("unchecked")
    public List<Media> getByName(String name) {
        Criteria criteria = createCriteria();
        criteria.add(Restrictions.eq("_name", name));
        return criteria.list();
    }

    public List<Media> getByName(String name, boolean hasToExist) {
        List<Media> t = null;
        try {
            t = getByName(name);
        } catch (NoResultException e) {
            if (hasToExist) {
                throw e;
            }
        }
        return t;
    }

    @SuppressWarnings("unchecked")
    public List<String> getAllNames() {
        Query query = _persistenceService.createQuery("select t._name from " + _clazz.getSimpleName() + " as t");
        return query.getResultList();
    }

    @SuppressWarnings("unchecked")
    public List<Media> getAllByPathAndUri(User owner, IFile path, List<String> uris, Integer maxResults) {
        Query query = _persistenceService.createNamedQuery("getAllInPathAndUri");
        query.setParameter("path", path.getPath());
        query.setParameter("uris", uris);
        query.setParameter("owner", owner);
        query.setFirstResult(0);
        query.setMaxResults(maxResults);
        return query.getResultList();
    }

    @SuppressWarnings("unchecked")
    public List<Media> getAllLastAddedPublicMedias(Visibility minVisibility, Integer maxResults) {
        Query query = _persistenceService.createNamedQuery("getAllLastAddedPublicMedias");
        query.setParameter("visibility", minVisibility);
        query.setMaxResults(maxResults);
        return query.getResultList();
    }

    @SuppressWarnings("unchecked")
    public List<Media> getAllByPath(User owner, IFile path, Integer maxResults) {
        Criteria criteria = createCriteria();
        criteria.add(Restrictions.eq("_path", path.getPath()));
        if (owner != null) {
            criteria.add(Restrictions.eq("_owner", owner));
        }
        return criteria.list();
    }

    public Media getByUri(User owner, URI uri) {
        Criteria criteria = createCriteria();
        criteria.add(Restrictions.eq("_uri", uri.toString()));
        if (owner != null) {
            criteria.add(Restrictions.eq("_owner", owner));
        }
        return (Media) criteria.uniqueResult();
    }

    @SuppressWarnings("unchecked")
    public List<Media> getAllByUri(User owner, URI uri) {
        Criteria criteria = createCriteria();
        criteria.add(Restrictions.eq("_uri", uri.toString()));
        if (owner != null) {
            criteria.add(Restrictions.eq("_owner", owner));
        }
        return criteria.list();
    }

    @SuppressWarnings("unchecked")
    public List<Media> getAllBySearchCriterias(User owner, int start, int max, boolean ascending, SearchCriteriaCommand searchCriteria,
            LifecyleStatus lifecycleStatus) {
        if (StringUtils.isEmpty(searchCriteria.getBuzzword())) {
            return getAllByCriterias(owner, start, max, ascending, searchCriteria, lifecycleStatus);
        }
        javax.persistence.Query persistenceQuery = createFullTextQueryBasedOnLucene(owner, searchCriteria, lifecycleStatus);
        persistenceQuery.setFirstResult(start);
        persistenceQuery.setMaxResults(max);

        // execute search
        return persistenceQuery.getResultList();
    }

    public int getAllBySearchCriteriasCount(User owner, SearchCriteriaCommand searchCriteria, LifecyleStatus lifecycleStatus) {
        if (searchCriteria == null || StringUtils.isEmpty(searchCriteria.getBuzzword())) {
            Criteria criteria = createCriteriaForSearchCommand(owner, searchCriteria, lifecycleStatus);
            criteria.setProjection(Projections.rowCount());
            return ((Long) criteria.list().get(0)).intValue();
        }
        FullTextQuery persistenceQuery = createFullTextQueryBasedOnLucene(owner, searchCriteria, lifecycleStatus);

        // execute search
        return persistenceQuery.getResultSize();
    }

    @SuppressWarnings("unchecked")
    public List<Media> getAllOrderedByCreationDate(User owner, int start, int max, UiMediaSortOrder sortOrder, boolean ascending,
            LifecyleStatus... lifecycleStatus) {
        Criteria criteria = createCriteria();
        if (owner != null) {
            criteria.add(Restrictions.eq("_owner", owner));
        }
        addSortOrderCriterias(sortOrder, ascending, criteria);
        if (!ArrayUtil.isEmpty(lifecycleStatus)) {
            criteria.add(Restrictions.in("_lifeCycleStatus", lifecycleStatus));
        }
        criteria.setFirstResult(start);
        criteria.setMaxResults(max);
        return criteria.list();
    }

    @SuppressWarnings("unchecked")
    List<Media> getAllByCriterias(User owner, int start, int max, boolean ascending, SearchCriteriaCommand searchCriteria,
            LifecyleStatus... lifecyleStatus) {
        Criteria criteria = createCriteriaForSearchCommand(owner, searchCriteria, lifecyleStatus);
        addSortOrderCriterias(searchCriteria.getSortOrder(), ascending, criteria);
        criteria.setFirstResult(start);
        criteria.setMaxResults(max);
        return criteria.list();
    }

    private void addSortOrderCriterias(UiMediaSortOrder sortOrder, boolean ascending, Criteria criteria) {
        switch (sortOrder) {
        case DATE:
            criteria.addOrder(ascending ? Order.asc("_creationDate") : Order.desc("_creationDate"));
            break;
        case ID:
            criteria.addOrder(ascending ? Order.asc("_id") : Order.desc("_id"));
            break;
        }
    }

    private Criteria createCriteriaForSearchCommand(User owner, SearchCriteriaCommand searchCriteria, LifecyleStatus... lifecyleStatus) {
        Criteria criteria = createCriteria();
        criteria.add(Restrictions.eq("_owner", owner));
        if (searchCriteria != null) {
            criteria.add(Restrictions.between("_creationDate", searchCriteria.getRangeT0(), searchCriteria.getRangeT1()));
        }
        if (!ArrayUtil.isEmpty(lifecyleStatus)) {
            criteria.add(Restrictions.in("_lifeCycleStatus", lifecyleStatus));
        }
        return criteria;
    }

    private FullTextQuery createFullTextQueryBasedOnLucene(User owner, SearchCriteriaCommand searchCriteria, LifecyleStatus lifecycleStatus) {
        FullTextEntityManager fullTextEntityManager = _persistenceService.getFullTextEntityManager();
        QueryBuilder qb = fullTextEntityManager.getSearchFactory().buildQueryBuilder().forEntity(Media.class).get();
        Long ownerId = owner.getId();
        org.apache.lucene.search.Query queryBuzzword = qb.keyword().wildcard().onField("_name").matching(searchCriteria.getBuzzword().toLowerCase())
                .createQuery();
        org.apache.lucene.search.Query queryOwner = qb.keyword().onField("_owner._id").matching(ownerId).createQuery();
        org.apache.lucene.search.Query queryDate = qb.range().onField("_creationDate").from(searchCriteria.getRangeT0()).to(searchCriteria.getRangeT1())
                .excludeLimit().createQuery();
        org.apache.lucene.search.Query queryLifecycle = qb.keyword().onField("_lifeCycleStatus").matching(lifecycleStatus).createQuery();
        org.apache.lucene.search.Query combinedLuceneQuery = qb.bool().must(queryBuzzword).must(queryOwner).must(queryDate).must(queryLifecycle)
                .createQuery();
        LOG.info("using lucene query '" + combinedLuceneQuery + "'.");

        // wrap Lucene query in a javax.persistence.Query
        FullTextQuery persistenceQuery = fullTextEntityManager.createFullTextQuery(combinedLuceneQuery, Media.class);
        return persistenceQuery;
    }
}

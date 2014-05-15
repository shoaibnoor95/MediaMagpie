package de.wehner.mediamagpie.persistence.dao;

import java.util.List;

import javax.persistence.Query;

import org.hibernate.Criteria;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import de.wehner.mediamagpie.persistence.entity.ConvertedVideo;
import de.wehner.mediamagpie.persistence.entity.Media;

@Repository
public class ConvertedVideoDao extends CreationDateBaseDao<ConvertedVideo> {

    @Autowired
    public ConvertedVideoDao(PersistenceService persistenceService) {
        super(ConvertedVideo.class, persistenceService);
    }

    private Criteria createSearchCriteria(Media media, String label, String videoFormat) {
        return createCriteria(Restrictions.eq("_media", media), Restrictions.eq("_label", label), Restrictions.eq("_videoFormat", videoFormat));
    }

    public boolean hasData(Media media, String label, String videoFormat) {
        Long result = (Long) createSearchCriteria(media, label, videoFormat).setProjection(Projections.count("id")).uniqueResult();
        return result > 0;
    }

    @SuppressWarnings("unchecked")
    public List<ConvertedVideo> getData(Media media, String label, String videoFormat, int maxResults) {
        Criteria criteria = createSearchCriteria(media, label, videoFormat);
        criteria.setMaxResults(maxResults);
        return criteria.list();
    }

    /**
     * @param mediaId
     * @param label
     * @param videoFormat
     * @return
     * @deprecated use {@linkplain #getData(Media, String, String, int)}
     */
    @SuppressWarnings("unchecked")
    public ConvertedVideo getByMediaIdAndLabel(Long mediaId, String label, String videoFormat) {
        Query query = _persistenceService.createNamedQuery("getByMediaIdAndLabelAndFormat");
        query.setParameter("mediaId", mediaId);
        query.setParameter("label", label);
        query.setParameter("videoFormat", videoFormat);
        query.setMaxResults(1);
        List<ConvertedVideo> resultList = query.getResultList();

        if (resultList.size() > 0) {
            return resultList.get(0);
        }
        return null;
    }

}

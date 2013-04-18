package de.wehner.mediamagpie.persistence.dao;

import java.util.List;

import javax.persistence.Query;

import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import de.wehner.mediamagpie.persistence.entity.Media;
import de.wehner.mediamagpie.persistence.entity.ThumbImage;


@Repository
public class ThumbImageDao extends CreationDateBaseDao<ThumbImage> {

    @Autowired
    public ThumbImageDao(PersistenceService persistenceService) {
        super(ThumbImage.class, persistenceService);
    }

    public boolean hasData(Media media, String label) {
        Long result = (Long) createCriteria(Restrictions.eq("_media", media), Restrictions.eq("_label", label)).setProjection(Projections.count("id")).uniqueResult();
        return result > 0;
    }

    @SuppressWarnings("unchecked")
    public ThumbImage getByMediaIdAndLabel(Long mediaId, String label) {
        Query query = _persistenceService.createNamedQuery("getByMediaIdAndLabel");
        query.setParameter("mediaId", mediaId);
        query.setParameter("label", label);
        query.setMaxResults(2);
        List<ThumbImage> resultList = query.getResultList();

        if (resultList.size() > 0) {
            return resultList.get(0);
        }
        return null;
    }

}

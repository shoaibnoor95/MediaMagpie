package de.wehner.mediamagpie.conductor.persistence.dao;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import de.wehner.mediamagpie.common.persistence.entity.ImageResizeJobExecution;
import de.wehner.mediamagpie.common.persistence.entity.Media;
import de.wehner.mediamagpie.common.persistence.entity.MediaDeleteJobExecution;
import de.wehner.mediamagpie.conductor.persistence.PersistenceService;

@Repository
public class MediaDeleteJobExecutionDao extends JobExecutionDao {

    @Autowired
    public MediaDeleteJobExecutionDao(PersistenceService persistenceService) {
        super(persistenceService);
    }

    @SuppressWarnings("unchecked")
    public boolean hasJob(Media media) {
        final Criteria crit = _persistenceService.createCriteria(MediaDeleteJobExecution.class);

        crit.add(Restrictions.eq("_mediaId", media.getId()));
        crit.setMaxResults(1);
        List<ImageResizeJobExecution> jobs = crit.list();
        return (jobs.size() > 0);
    }

}

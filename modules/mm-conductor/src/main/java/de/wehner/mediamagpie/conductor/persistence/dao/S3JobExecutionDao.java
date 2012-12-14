package de.wehner.mediamagpie.conductor.persistence.dao;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import de.wehner.mediamagpie.common.persistence.entity.Media;
import de.wehner.mediamagpie.common.persistence.entity.S3JobExecution;
import de.wehner.mediamagpie.conductor.persistence.PersistenceService;

@Repository
public class S3JobExecutionDao extends JobExecutionDao {

    @Autowired
    public S3JobExecutionDao(PersistenceService persistenceService) {
        super(persistenceService);
    }

    @SuppressWarnings("unchecked")
    public boolean hasJob(Media media) {
        final Criteria crit = _persistenceService.createCriteria(S3JobExecution.class);

        crit.add(Restrictions.eq("_media", media));
        crit.add(Restrictions.eq("_direction", S3JobExecution.Direction.PUT));
        crit.setMaxResults(1);
        List<S3JobExecution> jobs = crit.list();
        return (jobs.size() > 0);
    }

}

package de.wehner.mediamagpie.persistence.dao;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import de.wehner.mediamagpie.persistence.entity.CloudSyncJobExecution;
import de.wehner.mediamagpie.persistence.entity.JobStatus;
import de.wehner.mediamagpie.persistence.entity.User;

@Repository
public class CloudSyncJobExecutionDao extends JobExecutionDao {

    @Autowired
    public CloudSyncJobExecutionDao(PersistenceService persistenceService) {
        super(persistenceService);
    }

    @SuppressWarnings("unchecked")
    public CloudSyncJobExecution findS3Job(User user, JobStatus... jobStatus) {
        final Criteria crit = _persistenceService.createCriteria(CloudSyncJobExecution.class);

        crit.add(Restrictions.eq("_user", user));
        crit.add(Restrictions.eq("_cloudType", CloudSyncJobExecution.CloudType.S3));
        if (jobStatus != null && jobStatus.length > 0) {
            crit.add(Restrictions.in("_jobStatus", jobStatus));
        }
        crit.addOrder(Order.desc("_priority"));
        crit.addOrder(Order.asc("_id"));
        crit.setMaxResults(1);
        List<CloudSyncJobExecution> jobs = crit.list();
        if (jobs != null && jobs.size() > 0) {
            return jobs.get(0);
        }
        return null;
    }

}

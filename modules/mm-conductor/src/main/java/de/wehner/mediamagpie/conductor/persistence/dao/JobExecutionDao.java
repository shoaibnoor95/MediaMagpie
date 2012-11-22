package de.wehner.mediamagpie.conductor.persistence.dao;

import java.util.Date;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.CriteriaSpecification;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import de.wehner.mediamagpie.common.persistence.entity.JobExecution;
import de.wehner.mediamagpie.common.persistence.entity.JobStatus;
import de.wehner.mediamagpie.conductor.persistence.PersistenceService;


@Repository
public class JobExecutionDao extends Dao<JobExecution> {

    public enum JobOrder {
        MOST_RECENT_FIRST, OLDEST_FIRST, BY_PRIORITY_OLDEST_FIRST
    }

    protected SessionFactory _sessionFactory;

    @Autowired
    public JobExecutionDao(PersistenceService persistenceService) {
        super(JobExecution.class, persistenceService);
    }

//    @Deprecated
//    protected Criteria createMostRecentJobsCriteria(Object source, Collection<JobStatus> status) {
//        List<Criterion> criteria = new ArrayList<Criterion>();
//        if (source != null) {
//            criteria.add(Restrictions.eq("_source", source));
//        }
//        if (status != null) {
//            criteria.add(Restrictions.in("_status", status));
//        }
//        Criteria crit = createCriteria();
//        for (Criterion c : criteria) {
//            crit.add(c);
//        }
//        return crit;
//    }

//    @Deprecated
//    @SuppressWarnings("unchecked")
//    public List<JobExecution> getJobs(JobOrder order, Object source, Collection<JobStatus> status, int start, int maxNumber) {
//        Criteria criteria = createMostRecentJobsCriteria(source, status);
//
//        switch (order) {
//        case OLDEST_FIRST:
//            criteria.addOrder(Order.asc("id"));
//            break;
//        case MOST_RECENT_FIRST:
//            criteria.addOrder(Order.desc("id"));
//            break;
//        case BY_PRIORITY_OLDEST_FIRST:
//            criteria.addOrder(Order.desc("_priority"));
//            criteria.addOrder(Order.asc("id"));
//            break;
//        }
//        return criteria.setFirstResult(start).setMaxResults(maxNumber).list();
//    }

    // @SuppressWarnings("unchecked")
    // public <T extends Job<?>> List<T> getJobsForHousekeeping(Class<T> jobClass, int maxNumber, Date purgeDate, JobStatus... status) {
    // String queryString =
    // "select j from Job j where j._endTime <= :purgeDate and j._status in (:status) and j not in (select k._triggeringJob from Job k)";
    // Query query = session.createQuery(queryString);
    // query.setParameter("purgeDate", purgeDate);
    // query.setParameterList("status", status);
    // return query.setMaxResults(maxNumber).list();
    // }

    @SuppressWarnings("unchecked")
    public List<? extends JobExecution> findJobs(JobOrder order, Date searchEndTime, JobStatus... status) {
        final Criteria crit = createCriteria();

        crit.add(Restrictions.in("_status", status));
        crit.add(Restrictions.ge("_endTime", searchEndTime));

        Order ordering = ((order == JobOrder.MOST_RECENT_FIRST) ? Order.desc("id") : Order.asc("id"));
        return crit.addOrder(ordering).list();
    }

    @SuppressWarnings("unchecked")
    public List<JobExecution> getByStatus(List<JobStatus> stati, int startIndex, int numberOfJobs) {
        Criteria criteria = createCriteria();
        criteria.setResultTransformer(CriteriaSpecification.DISTINCT_ROOT_ENTITY);
        criteria.add(Restrictions.in("_jobStatus", stati));
        criteria.setFirstResult(startIndex);
        criteria.setMaxResults(numberOfJobs);
        criteria.addOrder(Order.desc("_priority"));
        criteria.addOrder(Order.asc("_id"));
        return criteria.list();
    }
}

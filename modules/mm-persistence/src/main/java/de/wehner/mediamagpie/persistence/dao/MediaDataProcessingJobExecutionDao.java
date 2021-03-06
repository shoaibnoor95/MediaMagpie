package de.wehner.mediamagpie.persistence.dao;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import de.wehner.mediamagpie.persistence.entity.ImageResizeJobExecution;
import de.wehner.mediamagpie.persistence.entity.JobExecution;
import de.wehner.mediamagpie.persistence.entity.Media;
import de.wehner.mediamagpie.persistence.entity.VideoConversionJobExecution;

@Repository
public class MediaDataProcessingJobExecutionDao extends JobExecutionDao {

    @Autowired
    public MediaDataProcessingJobExecutionDao(PersistenceService persistenceService) {
        super(persistenceService);
    }

    public boolean hasResizeJob(Media media, String label) {
        final Criteria crit = _persistenceService.createCriteria(ImageResizeJobExecution.class);

        crit.add(Restrictions.eq("_media", media));
        crit.add(Restrictions.eq("_label", label));
        crit.setMaxResults(1);
        List<?> jobs = crit.list();
        return (jobs.size() > 0);
    }

    public boolean hasVideoConversionJob(long mediaId, String destFormat, Integer widthOrHeight) {
        final Criteria crit = _persistenceService.createCriteria(VideoConversionJobExecution.class);

        crit.add(Restrictions.eq("_mediaId", mediaId));
        crit.add(Restrictions.eq("_destFormat", destFormat));
        if (widthOrHeight == null) {
            crit.add(Restrictions.isNull("_widthOrHeight"));
        } else {
            crit.add(Restrictions.eq("_widthOrHeight", widthOrHeight));
        }
        // alternative solution:
        // crit.add(Restrictions.or(Restrictions.isNull("_widthOrHeight"), Restrictions.eq("_widthOrHeight", widthOrHeight)));
        crit.setMaxResults(1);
        List<?> jobs = crit.list();
        return (jobs.size() > 0);
    }

    @SuppressWarnings("unchecked")
    public <T extends JobExecution> List<T> getJobsByMedia(Media media, int maxResult, Class<T> entityClass) {
        final Criteria crit = _persistenceService.createCriteria(entityClass);

        crit.add(Restrictions.eq("_media", media));
        crit.setMaxResults(maxResult);
        return crit.list();
    }

    @SuppressWarnings("unchecked")
    public <T extends JobExecution> List<T> getJobsByMediaId(Long mediaId, int maxResult, Class<T> entityClass) {
        final Criteria crit = _persistenceService.createCriteria(entityClass);

        crit.add(Restrictions.eq("_mediaId", mediaId));
        crit.setMaxResults(maxResult);
        return crit.list();
    }

}

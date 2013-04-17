package de.wehner.mediamagpie.persistence;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import de.wehner.mediamagpie.persistence.entity.ImageResizeJobExecution;
import de.wehner.mediamagpie.persistence.entity.Media;

@Repository
public class ImageResizeJobExecutionDao extends JobExecutionDao {

    @Autowired
    public ImageResizeJobExecutionDao(PersistenceService persistenceService) {
        super(persistenceService);
    }

    @SuppressWarnings("unchecked")
    public boolean hasResizeJob(Media media, String label) {
        final Criteria crit = _persistenceService.createCriteria(ImageResizeJobExecution.class);

        crit.add(Restrictions.eq("_media", media));
        crit.add(Restrictions.eq("_label", label));
        crit.setMaxResults(1);
        List<ImageResizeJobExecution> jobs = crit.list();
        return (jobs.size() > 0);
    }

    @SuppressWarnings("unchecked")
    public List<ImageResizeJobExecution> getJobsByMedia(Media media) {
        final Criteria crit = _persistenceService.createCriteria(ImageResizeJobExecution.class);

        crit.add(Restrictions.eq("_media", media));
        crit.setMaxResults(1);
        return crit.list();
    }

}

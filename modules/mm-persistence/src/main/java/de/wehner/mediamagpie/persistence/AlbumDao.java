package de.wehner.mediamagpie.persistence;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import de.wehner.mediamagpie.persistence.entity.Album;
import de.wehner.mediamagpie.persistence.entity.Media;
import de.wehner.mediamagpie.persistence.entity.User;
import de.wehner.mediamagpie.persistence.entity.Visibility;

@Repository
public class AlbumDao extends CreationDateBaseDao<Album> {

    private static final Logger LOG = LoggerFactory.getLogger(AlbumDao.class);

    @Autowired
    public AlbumDao(PersistenceService persistenceService) {
        super(Album.class, persistenceService);
    }

    public int getMediasCount(long id) {
        Album album = getById(id);
        return album.getMedias().size();
    }

    public List<Media> getMedias(Long id, int start, int max) {
        Album album = getById(id);
        List<Media> medias = new ArrayList<Media>();
        max = Math.min(max, album.getMedias().size());
        for (int i = start; i < max; i++) {
            if (album.getMedias().size() > i) {
                medias.add(album.getMedias().get(i));
            }
        }
        return medias;
    }

    /**
     * Provides the album by its public uuid (the url parameter)
     * 
     * @param uuid
     * @return
     */
    public Album getByUuid(String uuid) {
        Criteria criteria = createCriteria();
        criteria.add(Restrictions.eq("_uid", uuid));
        Album album = (Album) criteria.uniqueResult();
        if (album == null) {
            LOG.warn("No album found for uid '" + uuid + "'.");
        }
        return album;
    }

    @SuppressWarnings("unchecked")
    public List<Album> getByOwner(User owner) {
        Criteria criteria = createCriteria();
        criteria.add(Restrictions.eq("_owner", owner));
        return criteria.list();
    }

    public List<Media> getAllMediasByVisibility(int start, int count, Visibility... visibility) {
        // TODO Auto-generated method stub
        return null;
    }
}

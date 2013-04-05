package de.wehner.mediamagpie.common.persistence.realms;

import java.util.List;

import javax.persistence.PreRemove;

import de.wehner.mediamagpie.common.persistence.entity.Album;
import de.wehner.mediamagpie.common.persistence.entity.Media;


public class AlbumMediaRelation {

    @PreRemove
    public void removeMediaFromAlbum(Object object) {
        Media media = (Media) object;
        List<Album> albums = media.getAlbums();
        for (Album album : albums) {
            album.getMedias().remove(media);
        }
    }
}

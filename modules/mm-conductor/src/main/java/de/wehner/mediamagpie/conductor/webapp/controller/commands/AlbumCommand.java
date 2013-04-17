package de.wehner.mediamagpie.conductor.webapp.controller.commands;

import de.wehner.mediamagpie.persistence.entity.Album;

/**
 * Used to create or edit an album.
 * 
 * @author ralfwehner
 * 
 */
public class AlbumCommand extends Album {

    private Boolean _isNew;

    public AlbumCommand(boolean isNew) {
        _isNew = isNew;
    }

    public AlbumCommand() {
        this(false);
    }

    public void init(Album album) {
        setId(album.getId());
        setCreationDate(album.getCreationDate());
        setMedias(album.getMedias());
        setName(album.getName());
        setOwner(album.getOwner());
        setVisibility(album.getVisibility());
        setUid(album.getUid());
    }

    public Boolean getIsNew() {
        return _isNew;
    }

    public Boolean isNew() {
        return _isNew;
    }

    public void setNew(Boolean isNew) {
        _isNew = isNew;
    }

    public void setIsNew(Boolean isNew) {
        _isNew = isNew;
    }

    @Override
    public String toString() {
        return "id=" + getId();
        // return ReflectionToStringBuilder.toString(this, ToStringStyle.SHORT_PREFIX_STYLE);
    }

}

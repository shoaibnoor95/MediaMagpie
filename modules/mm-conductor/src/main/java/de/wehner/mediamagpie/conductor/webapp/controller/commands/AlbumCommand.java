package de.wehner.mediamagpie.conductor.webapp.controller.commands;

import ma.glasnost.orika.MapperFactory;
import de.wehner.mediamagpie.conductor.util.OrikaMapperFactoryUtil;
import de.wehner.mediamagpie.persistence.entity.Album;
import de.wehner.mediamagpie.persistence.entity.Visibility;

/**
 * Used to create or edit an album.
 * 
 * @author ralfwehner
 * 
 */
public class AlbumCommand extends Album {

    private Boolean _isNew;

    private static final OrikaMapperFactoryUtil orikaMapperFactoryUtil = new OrikaMapperFactoryUtil();

    public static AlbumCommand createCommand(Album album) {
        MapperFactory mapperFactory = orikaMapperFactoryUtil.getOrikaMapperFactory();
        AlbumCommand command = mapperFactory.getMapperFacade().map(album, AlbumCommand.class);
        command._isNew = false;
        return command;
    }

    public AlbumCommand() {
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

    public String getOverviewUrl() {
        return getBaseUrl() + "/view";
    }

    public String getBaseUrl() {
        if (getVisibility() == Visibility.PUBLIC) {
            return String.format("public/album/%s", getUid());
        } else {
            return String.format("private/album/%s", getUid());
        }
    }

    @Override
    public String toString() {
        return "id=" + getId();
        // return ReflectionToStringBuilder.toString(this, ToStringStyle.SHORT_PREFIX_STYLE);
    }

}

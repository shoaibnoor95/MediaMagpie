package de.wehner.mediamagpie.conductor.webapp.controller.commands;


/**
 * Used in media search view currently to select or switch between albums.
 * @author ralfwehner
 *
 */
public class AlbumSelectionCommand {

    private Long _albumId;

    public Long getAlbumId() {
        return _albumId;
    }

    public void setAlbumId(Long albumId) {
        _albumId = albumId;
    }
}

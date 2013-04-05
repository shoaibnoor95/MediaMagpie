package de.wehner.mediamagpie.common.persistence.entity;

/**
 * As status which is currently used for Medias. The normal status of a media is 'Living', when the user puts it into trash can it will be
 * moved to 'MovedToTransCan'. When the trash can is purged, the status will be moved to 'MarkedForErasue' until the media is deleted from
 * file system and database.
 * 
 * @author ralfwehner
 * 
 */
public enum LifecyleStatus {
    Living, MovedToTrashCan, MarkedForErasure
}

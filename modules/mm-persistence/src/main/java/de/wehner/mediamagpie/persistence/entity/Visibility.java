package de.wehner.mediamagpie.persistence.entity;

public enum Visibility {

    /**
     * everyone can see this album
     */
    PUBLIC,
    /**
     * Only logged in users can see this album
     */
    USERS,
    /**
     * Only the user (owner) itself can see this album
     */
    OWNER

}

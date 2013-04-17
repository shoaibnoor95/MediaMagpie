package de.wehner.mediamagpie.persistence.entity.properties;

import org.springframework.validation.Errors;

public interface FilesystemConfiguration {

    /**
     * Creates all necessary directories on the filesystem.
     */
    void prepareDirectories(Errors e);

}

package de.wehner.mediamagpie.api.mediaExport;

import de.wehner.mediamagpie.api.MediaExport;
import de.wehner.mediamagpie.api.MediaExportRepository;

/**
 * A factory class that provides a {@linkplain MediaExportRepository} which is able to export {@linkplain MediaExport} entities to a S3 or
 * file syste etc.
 * 
 * @author ralfwehner
 * TODO rwe: does we need this?
 */
public interface MediaExportRepositoryFactory {

    public MediaExportRepository getMediaRepository();
}

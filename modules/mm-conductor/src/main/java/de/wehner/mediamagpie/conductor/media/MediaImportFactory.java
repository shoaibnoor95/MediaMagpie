package de.wehner.mediamagpie.conductor.media;

import java.io.File;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.wehner.mediamagpie.api.MediaExport;
import de.wehner.mediamagpie.conductor.webapp.services.UploadService;
import de.wehner.mediamagpie.core.util.Pair;
import de.wehner.mediamagpie.persistence.dao.TransactionHandler;
import de.wehner.mediamagpie.persistence.entity.Media;
import de.wehner.mediamagpie.persistence.entity.User;
import de.wehner.mediamagpie.persistence.service.ConfigurationProvider;

public class MediaImportFactory {

    private static final Logger LOG = LoggerFactory.getLogger(MediaImportFactory.class);

    private final UploadService _uploadService;
    private final User _user;
    private final ConfigurationProvider _configurationProvider;
    private final TransactionHandler _transactionHandler;

    public MediaImportFactory(UploadService uploadService, User user, ConfigurationProvider configurationProvider, TransactionHandler transactionHandler) {
        super();
        _uploadService = uploadService;
        _user = user;
        _configurationProvider = configurationProvider;
        _transactionHandler = transactionHandler;
    }

    public Media create(MediaExport mediaExport) {
        Pair<String, File> uploadFileInfo = _uploadService.createUniqueUserStoreFile(_user, mediaExport.getOriginalFileName());

        LOG.info("Try dump upload stream '" + uploadFileInfo.getFirst() + "' into file '" + uploadFileInfo.getSecond().getPath() + "'");
        Media newMedia = _uploadService.handleUploadStream(_user, uploadFileInfo.getSecond(), mediaExport.getInputStream());
// TODO rwe: persist media here...
        
        // add metainformation from mediaExport to new media
        // TODO rwe...

        // create job executions for image resizing
        _uploadService.createJobsForAllThumbImages(newMedia, _configurationProvider);

        return newMedia;
    }
}

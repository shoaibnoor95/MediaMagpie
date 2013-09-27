package de.wehner.mediamagpie.conductor.media;

import java.io.File;
import java.util.Date;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.wehner.mediamagpie.api.MediaExport;
import de.wehner.mediamagpie.conductor.webapp.services.UploadService;
import de.wehner.mediamagpie.core.util.Pair;
import de.wehner.mediamagpie.core.util.TimeUtil;
import de.wehner.mediamagpie.persistence.dao.MediaDao;
import de.wehner.mediamagpie.persistence.dao.MediaTagDao;
import de.wehner.mediamagpie.persistence.entity.Media;
import de.wehner.mediamagpie.persistence.entity.MediaTag;
import de.wehner.mediamagpie.persistence.entity.User;
import de.wehner.mediamagpie.persistence.service.ConfigurationProvider;
import de.wehner.mediamagpie.persistence.util.TimeProvider;

public class MediaImportFactory {

    private static final Logger LOG = LoggerFactory.getLogger(MediaImportFactory.class);

    private final UploadService _uploadService;
    private final User _user;
    private final ConfigurationProvider _configurationProvider;
    private final MediaDao _mediaDao;
    private final MediaTagDao _mediaTagDao;
    private final TimeProvider _timeProvider;

    public MediaImportFactory(UploadService uploadService, User user, ConfigurationProvider configurationProvider, MediaDao mediaDao,
            MediaTagDao mediaTagDao, TimeProvider timeProvider) {
        super();
        _uploadService = uploadService;
        _user = user;
        _configurationProvider = configurationProvider;
        _mediaDao = mediaDao;
        _mediaTagDao = mediaTagDao;
        _timeProvider = timeProvider;
    }

    /**
     * Creates a new Media and stores the binary media content into the local file system.
     * <p>
     * <b>Beware: </b>The methods needs to be called within an open transaction.
     * </p>
     * 
     * @param mediaExport
     * @return
     */
    public Media create(MediaExport mediaExport) {
        String newMediaFileName = mediaExport.getOriginalFileName();
        if (StringUtils.isEmpty(newMediaFileName)) {
            LOG.warn("Can not find original file name from mediaExport with hash vaule '{}'. Create new file name.", mediaExport.getHashValue());
            newMediaFileName = TimeUtil.buildFileNameWithTimeStamp(new File("importedMedia_id" + mediaExport.getMediaId() + "_ts.bin"),
                    new Date(_timeProvider.getTime()), null).getName();
        }
        Pair<String, File> uploadFileInfo = _uploadService.createUniqueUserStoreFile(_user, newMediaFileName);

        LOG.info("Try download stream '" + uploadFileInfo.getFirst() + "' into file '" + uploadFileInfo.getSecond().getPath() + "' for media id: "
                + mediaExport.getMediaId() + ", hash: " + mediaExport.getHashValue());
        Media newMedia = _uploadService.saveInputStreamToFileSystemAndCreateMedia(_user, uploadFileInfo.getSecond(), mediaExport.getInputStream());

        // check hash value of new created Media against the incomming MediaExport
        if (!newMedia.getHashValue().equals(mediaExport.getHashValue())) {
            LOG.warn(String.format("Hashvalue of new created media is not equal to incomming one (%s/%s). MediaExport.name=%s, local file=%s",
                    newMedia.getHashValue(), mediaExport.getHashValue(), (mediaExport.getName() + ""), newMedia.getFileFromUri().getPath()));
        }
        // add meta informations from mediaExport to new media entity
        if (mediaExport.getCreationDate() != null) {
            newMedia.setCreationDate(mediaExport.getCreationDate());
        }
        newMedia.setDescription(mediaExport.getDescription());
        newMedia.setName(mediaExport.getName());
        if (mediaExport.getTags() != null && mediaExport.getTags().size() > 0) {
            // test if tag already exists
            for (String tagName : mediaExport.getTags()) {
                MediaTag mediaTag = _mediaTagDao.getByName(tagName);
                if (mediaTag == null) {
                    mediaTag = new MediaTag(tagName);
                }
                newMedia.addTag(mediaTag);
            }
        }

        // create job executions for image resizing
        _mediaDao.makePersistent(newMedia);
        _uploadService.createJobsForAllThumbImages(newMedia, _configurationProvider);

        return newMedia;
    }
}

package de.wehner.mediamagpie.conductor.webapp.services;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.concurrent.Callable;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import de.wehner.mediamagpie.common.fslayer.IFSLayer;
import de.wehner.mediamagpie.common.fslayer.IFile;
import de.wehner.mediamagpie.common.persistence.entity.Media;
import de.wehner.mediamagpie.common.persistence.entity.Priority;
import de.wehner.mediamagpie.common.persistence.entity.ThumbImage;
import de.wehner.mediamagpie.common.persistence.entity.User;
import de.wehner.mediamagpie.common.persistence.entity.properties.MainConfiguration;
import de.wehner.mediamagpie.common.util.FileSystemUtil;
import de.wehner.mediamagpie.common.util.Pair;
import de.wehner.mediamagpie.conductor.persistence.PersistenceService;
import de.wehner.mediamagpie.conductor.persistence.dao.ConfigurationDao;
import de.wehner.mediamagpie.conductor.persistence.dao.MediaDao;
import de.wehner.mediamagpie.conductor.persistence.dao.ThumbImageDao;
import de.wehner.mediamagpie.conductor.webapp.util.TimeoutExecutor;

@Service
public class UploadService {

    public static final String UPLOAD_PREVIEW_THUMB_LABEL = "50";

    private static final Logger LOG = LoggerFactory.getLogger(UploadService.class);

    private final ConfigurationDao _configurationDao;
    private final MediaDao _mediaDao;
    private final ImageService _imageService;
    private final PersistenceService _persistenceService;
    private final ThumbImageDao _thumbImageDao;
    private final IFSLayer _fsLayer;

    @Autowired
    public UploadService(ConfigurationDao configurationDao, MediaDao mediaDao, ImageService imageService, PersistenceService persistenceService,
            ThumbImageDao thumbImageDao, IFSLayer fsLayer) {
        super();
        _configurationDao = configurationDao;
        _mediaDao = mediaDao;
        _imageService = imageService;
        _persistenceService = persistenceService;
        _thumbImageDao = thumbImageDao;
        _fsLayer = fsLayer;
    }

    /**
     * Provides a pair with the original file name based on the upload file and the final destination file name were the binary data will be
     * dumped into. The method creates all required directories to create a new file provided by its return value.
     * 
     * @param currentUser
     * @param originalFilename
     * @return The original file name and the complete file name used to store the medias data in fs.
     */
    public synchronized Pair<String, IFile> createUniqueUserStoreFile(User currentUser, String originalFilename) {
        File testRelFileName = buildRelativeMediaFileName(currentUser, originalFilename);
        String baseUploadPath = _configurationDao.getConfiguration(MainConfiguration.class).getBaseUploadPath();
        IFile tempFile = _fsLayer.createFile(baseUploadPath, testRelFileName.getPath());
        try {
            if (!tempFile.getParentFile().exists()) {
                // create the user's upload directory in case that the user hasn't uploaded a media yet
                _fsLayer.forceMkdir(tempFile.getParentFile());
            }
            if (tempFile.exists()) {
                // file already exists. Maybe it is loaded again.
                LOG.debug("Upload file '" + tempFile.getPath() + "' already exists.");
                // build new unique temp file name
                tempFile = FileSystemUtil.getNextUniqueFilename(_fsLayer, tempFile);
                LOG.debug("Generate the new file '" + tempFile.getPath() + "'.");
            }
            tempFile.createNewFile();
        } catch (IOException e) {
            LOG.warn("Can not write upload file to disk.", e);
        }
        return new Pair<String, IFile>(originalFilename, tempFile);
    }

    private File buildRelativeMediaFileName(User currentUser, String originalFilename) {
        File testRelFileName = new File(String.format("user_%06d", currentUser.getId()), originalFilename);
        return testRelFileName;
    }

    /**
     * Currently the media provided in <code>inputStream</code> is simply stored within the user's baseUploadPath.<br/>
     * 
     * @param currentUser
     * @param mediaFile
     *            The final and unique file name were the input bytes will be written into.
     * @param inputStream
     * @param uniqueCounter
     * @return
     */
    public String handleUploadStream(final User currentUser, IFile mediaFile, InputStream inputStream, int uniqueCounter) {
        if (mediaFile.exists()) {
            // we expect an empty existing file to write into
            if (mediaFile.length() > 0) {
                LOG.error("Internal error. The file were input stream should be written into isn't empty.");
            }
            mediaFile.delete();
        }

        // create a file for the new media
        OutputStream outputStream = null;
        try {
            outputStream = mediaFile.getOutputStream();
            IOUtils.copy(inputStream, outputStream);
        } catch (IOException e) {
            LOG.warn("Can not write upload file to disk.", e);
        } finally {
            IOUtils.closeQuietly(outputStream);
        }

        // create a Media entity, add to db and create a ThumbImage as well
        Media newMedia;
        try {
            newMedia = MediaSyncService.createMediaFromMediaFile(currentUser, mediaFile.toURI());
        } catch (FileNotFoundException e) {
            throw new RuntimeException("Can not create new Media entity.", e);
        }
        _mediaDao.makePersistent(newMedia);

        _imageService.addImageResizeJobExecutionIfNecessary(UPLOAD_PREVIEW_THUMB_LABEL, _mediaDao.getById(newMedia.getId()), Priority.HIGH);
        _persistenceService.flipTransaction();

        // wait a little time until the thumb is ready
        TimeoutExecutor timeoutExecutor = new TimeoutExecutor(2000, 250);
        final Long newMediaId = newMedia.getId();
        timeoutExecutor.checkUntilConditionIsTrue(new Callable<Boolean>() {

            @Override
            public Boolean call() throws Exception {
                ThumbImage thumbImage = _thumbImageDao.getByMediaIdAndLabel(newMediaId, UPLOAD_PREVIEW_THUMB_LABEL);
                return (thumbImage != null);
            }
        });
        return _imageService.createLink(newMedia, UPLOAD_PREVIEW_THUMB_LABEL, Priority.NORMAL);
    }

    public void deleteFile(User user, String mediaFileName) {
        String baseUploadPath = _configurationDao.getConfiguration(MainConfiguration.class).getBaseUploadPath();
        File relativeFileName = buildRelativeMediaFileName(user, mediaFileName);
        final IFile mediaFile = _fsLayer.createFile(baseUploadPath, relativeFileName.getPath());
        Media media = _mediaDao.getByUri(user, mediaFile.toURI());
        if (media != null) {
            _imageService.deleteMediaCompletely(media);
        }
    }

}

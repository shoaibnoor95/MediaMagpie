package de.wehner.mediamagpie.conductor.webapp.services;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.concurrent.Callable;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import de.wehner.mediamagpie.core.util.FileSystemUtil;
import de.wehner.mediamagpie.core.util.Pair;
import de.wehner.mediamagpie.core.util.StringUtil;
import de.wehner.mediamagpie.core.util.TimeoutExecutor;
import de.wehner.mediamagpie.persistence.dao.MediaDao;
import de.wehner.mediamagpie.persistence.dao.PersistenceService;
import de.wehner.mediamagpie.persistence.dao.ThumbImageDao;
import de.wehner.mediamagpie.persistence.entity.Media;
import de.wehner.mediamagpie.persistence.entity.Priority;
import de.wehner.mediamagpie.persistence.entity.ThumbImage;
import de.wehner.mediamagpie.persistence.entity.User;
import de.wehner.mediamagpie.persistence.service.ConfigurationProvider;

@Service
public class UploadService {

    public static final String UPLOAD_PREVIEW_THUMB_LABEL = "50";

    private static final Logger LOG = LoggerFactory.getLogger(UploadService.class);

    private final ConfigurationProvider _configurationProvider;
    private final MediaDao _mediaDao;
    private final ImageService _imageService;
    private final PersistenceService _persistenceService;
    private final ThumbImageDao _thumbImageDao;

    @Autowired
    public UploadService(ConfigurationProvider configurationProvider, MediaDao mediaDao, ImageService imageService, PersistenceService persistenceService,
            ThumbImageDao thumbImageDao) {
        super();
        _configurationProvider = configurationProvider;
        _mediaDao = mediaDao;
        _imageService = imageService;
        _persistenceService = persistenceService;
        _thumbImageDao = thumbImageDao;
    }

    /**
     * Provides a pair with the original file name based on the upload file and the final destination file name were the binary data will be
     * dumped into. The method creates all required directories to create a new file provided by its return value.
     * 
     * @param currentUser
     * @param originalFilename
     * @return The original file name and the complete file name used to store the medias data in fs.
     */
    public synchronized Pair<String, File> createUniqueUserStoreFile(User currentUser, String originalFilename) {
        File testRelFileName = buildRelativeMediaFileName(currentUser, originalFilename);
        String baseUploadPath = _configurationProvider.getMainConfiguration().getBaseUploadPath();
        File tempFile = new File(baseUploadPath, testRelFileName.getPath());
        try {
            if (!tempFile.getParentFile().exists()) {
                // create the user's upload directory in case that the user hasn't uploaded a media yet
                File parentTempFile = tempFile.getParentFile();
                FileUtils.forceMkdir(parentTempFile);
            }
            if (tempFile.exists()) {
                // file already exists. Maybe it is loaded again.
                LOG.debug("Upload file '" + tempFile.getPath() + "' already exists.");
                // build new unique temp file name
                tempFile = FileSystemUtil.getNextUniqueFilename(tempFile);
                LOG.debug("Generate the new file '" + tempFile.getPath() + "'.");
            }
            tempFile.createNewFile();
        } catch (IOException e) {
            LOG.warn("Can not write upload file to disk.", e);
        }
        return new Pair<String, File>(originalFilename, tempFile);
    }

    /**
     * Provides the path name of a media below the 'useruploads'-path. E.g. <code>user_000001/IMG_1150.jpg</code>.
     * 
     * @param currentUser
     * @param originalFilename
     * @return
     */
    private File buildRelativeMediaFileName(User currentUser, String originalFilename) {
        File testRelFileName = new File(String.format("user_%06d", currentUser.getId()), originalFilename);
        return testRelFileName;
    }

    /**
     * The <code>inputStream</code> will be written to file system using the user's baseUploadPath and given mediaFile name.
     * 
     * @param currentUser
     * @param mediaFile
     *            The final and unique file name were the input bytes will be written into.
     * @param inputStream
     * @return A new created and persisted <code>Media</code> object which refers to the stored file.
     */
    public Media saveInputStreamToFileSystemAndCreateMedia(final User currentUser, File mediaFile, InputStream inputStream) {
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
            outputStream = new FileOutputStream(mediaFile);
            int bytesCopied = IOUtils.copy(inputStream, outputStream);
            LOG.trace("copied " + bytesCopied + " bytes (" + StringUtil.formatBytesToHumanReadableRepresentation(bytesCopied) + ") to output stream.");
        } catch (IOException e) {
            LOG.warn("Can not write upload file to disk.", e);
        } finally {
            IOUtils.closeQuietly(outputStream);
            IOUtils.closeQuietly(inputStream);
        }

        // create a Media entity, with hash value and camera meta data
        Media newMedia;
        try {
            newMedia = MediaSyncService.createMediaFromMediaFile(currentUser, mediaFile.toURI());
        } catch (IOException e) {
            throw new RuntimeException("Can not create new Media entity.", e);
        }

        return newMedia;
    }

    public void deleteFile(User user, String mediaFileName) {
        String baseUploadPath = _configurationProvider.getMainConfiguration().getBaseUploadPath();
        File relativeFileName = buildRelativeMediaFileName(user, mediaFileName);
        final File mediaFile = new File(baseUploadPath, relativeFileName.getPath());
        Media media = _mediaDao.getByUri(user, mediaFile.toURI());
        if (media != null) {
            _imageService.deleteMediaCompletely(media);
        }
    }

    public String createThumbImage(Media media, final String label, Priority priority, int waitUntilFinished) {
        final Long mediaId = media.getId();
        _imageService.addImageResizeJobExecutionIfNecessary(label, _mediaDao.getById(mediaId), priority);
        _persistenceService.flipTransaction();

        // if wanted, wait until thumb image is resized
        if (waitUntilFinished > 0) {
            TimeoutExecutor timeoutExecutor = new TimeoutExecutor(waitUntilFinished, 250);
            timeoutExecutor.checkUntilConditionIsTrue(new Callable<Boolean>() {

                @Override
                public Boolean call() throws Exception {
                    ThumbImage thumbImage = _thumbImageDao.getByMediaIdAndLabel(mediaId, label);
                    return (thumbImage != null);
                }
            });
        }
        return _imageService.createLink(media, UPLOAD_PREVIEW_THUMB_LABEL, Priority.NORMAL);
    }

    /**
     * Be aware: This method does call a flipTransaction() internally and detaches entities.
     * 
     * @param newMedia
     * @param configurationProvider
     */
    public void createJobsForAllThumbImages(Media newMedia, ConfigurationProvider configurationProvider) {

        List<Integer> allThumbSizes = configurationProvider.createConfigurationFacade(newMedia.getOwner()).getAllThumbSizes();
        for (Integer thumbSize : allThumbSizes) {
            createThumbImage(newMedia, "" + thumbSize, Priority.LOW, 0);
        }
    }

}

package de.wehner.mediamagpie.conductor.webapp.services;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLConnection;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.dom4j.Element;
import org.hibernate.criterion.Order;
import org.mortbay.log.Log;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.drew.imaging.jpeg.JpegMetadataReader;
import com.drew.imaging.jpeg.JpegProcessingException;
import com.drew.metadata.Directory;
import com.drew.metadata.Metadata;
import com.drew.metadata.MetadataException;
import com.drew.metadata.Tag;
import com.drew.metadata.exif.ExifDirectory;

import de.wehner.mediamagpie.common.core.util.DigestUtil;
import de.wehner.mediamagpie.common.persistence.entity.LifecyleStatus;
import de.wehner.mediamagpie.common.persistence.entity.Media;
import de.wehner.mediamagpie.common.persistence.entity.Orientation;
import de.wehner.mediamagpie.common.persistence.entity.User;
import de.wehner.mediamagpie.common.persistence.entity.properties.UserConfiguration;
import de.wehner.mediamagpie.common.util.ExceptionUtil;
import de.wehner.mediamagpie.conductor.fslayer.IFSLayer;
import de.wehner.mediamagpie.conductor.fslayer.IFile;
import de.wehner.mediamagpie.conductor.job.SingleThreadedController;
import de.wehner.mediamagpie.conductor.persistence.TransactionHandler;
import de.wehner.mediamagpie.conductor.persistence.dao.MediaDao;
import de.wehner.mediamagpie.conductor.persistence.dao.UserConfigurationDao;
import de.wehner.mediamagpie.conductor.persistence.dao.UserDao;

@Service
public class MediaSyncService extends SingleThreadedController {

    private static final Logger LOG = LoggerFactory.getLogger(MediaSyncService.class);

    private static final Set<String> _validMediaExtensions = new HashSet<String>(Arrays.asList(".jpg", ".png"));
    public static final int MAX_DATAS_PER_REQUEST = 20;

    private final TransactionHandler _transactionHandler;
    private final UserConfigurationDao _configurationDao;
    private final MediaDao _mediaDao;
    private final UserDao _userDao;
    private final IFSLayer _fsLayer;
    private final Map<IFile, CountDownLatch> _processingPathes = new ConcurrentHashMap<IFile, CountDownLatch>();

    @Autowired
    public MediaSyncService(TransactionHandler transactionHandler, UserDao userDao, MediaDao mediaDao, UserConfigurationDao userConfigurationDao,
            IFSLayer fsLayer) {
        super(TimeUnit.MINUTES, 5);
        _userDao = userDao;
        _mediaDao = mediaDao;
        _transactionHandler = transactionHandler;
        _configurationDao = userConfigurationDao;
        _fsLayer = fsLayer;
    }

    @Override
    public boolean execute() {

        List<User> allUsers = _transactionHandler.executeInTransaction(new Callable<List<User>>() {

            @Override
            public List<User> call() throws Exception {
                return _userDao.getAll(Order.asc("_id"), Integer.MAX_VALUE);
            }
        });

        boolean someOneWasBusy = false;
        for (final User user : allUsers) {
            UserConfiguration userConfiguration = _transactionHandler.executeInTransaction(new Callable<UserConfiguration>() {

                @Override
                public UserConfiguration call() throws Exception {
                    return _configurationDao.getConfiguration(user, UserConfiguration.class);
                }
            });
            if (syncMediaPahtes(user, userConfiguration.getRootMediaPathes())) {
                someOneWasBusy = true;
            }
        }

        return someOneWasBusy;
    }

    public boolean syncMediaPahtes(final User user, String... mediaPathes) {
        if (mediaPathes == null) {
            return false;
        }
        boolean someOneWasBusy = false;
        for (String mediaPath : mediaPathes) {
            IFile syncDir = _fsLayer.createDir(mediaPath);

            // wait if another process is scanning same directory and lock when ready to sync
            CountDownLatch syncEndSignal = new CountDownLatch(1);
            synchronized (_processingPathes) {
                CountDownLatch countDownLatch = _processingPathes.get(syncDir);
                if (countDownLatch != null) {
                    try {
                        countDownLatch.await();
                    } catch (InterruptedException e) {
                    }
                }
                _processingPathes.put(syncDir, syncEndSignal);
            }

            try {
                LOG.info("start sync user's '" + user.getName() + "' directory '" + mediaPath + "'.");
                int filesSynchronized = syncDir(user, syncDir);
                boolean wasBusy = filesSynchronized > 0;

                if (!wasBusy) {
                    wasBusy = purgeObsoleteMedias(syncDir);
                }
                if (!someOneWasBusy) {
                    someOneWasBusy = wasBusy;
                }
            } finally {
                // remove syncEndSignal from map and trigger signal for waiting threads to start scanning on same directory as well
                _processingPathes.remove(syncDir);
                syncEndSignal.countDown();
            }
        }
        return someOneWasBusy;
    }

    private synchronized boolean purgeObsoleteMedias(IFile file) {
        Integer purgedMediasCount = _transactionHandler.executeInTransaction(new Callable<Integer>() {

            @Override
            public Integer call() throws Exception {
                List<Media> allMedias = _mediaDao.getAll();
                List<Media> mediasToPurge = new ArrayList<Media>();
                for (Media media : allMedias) {
                    String uriAsString = media.getUri();
                    URI uri = new URI(uriAsString);
                    File fileToTest = new File(uri);
                    Log.debug("test existence of file '" + fileToTest.getAbsolutePath() + "'.");
                    if (!fileToTest.exists() && (media.getLifeCycleStatus() != LifecyleStatus.MarkedForErasure)) {
                        mediasToPurge.add(media);
                    }
                    if (mediasToPurge.size() == 100) {
                        break;
                    }
                }
                for (Media media : mediasToPurge) {
                    LOG.info("Delete obsolete media with uri '" + media.getUri() + "' from db.");
                    _mediaDao.makeTransient(media);
                }
                return mediasToPurge.size();
            }
        });
        return (purgedMediasCount > 0);
    }

    private int syncDir(User user, IFile dirToSync) {
        if (!dirToSync.exists()) {
            LOG.warn("Directory '" + dirToSync.getPath() + "' does not exist. Does anybody has deleted this directory during scanning phase?");
            return 0;
        }
        IFile[] listFiles = dirToSync.listFiles();
        int sumFilesSynchronized = 0;
        List<IFile> mediaFiles = new ArrayList<IFile>();
        for (IFile file : listFiles) {
            if (file.isDirectory()) {
                sumFilesSynchronized += syncDir(user, file);
            } else if (isMedia(file.getName().toLowerCase())) {
                mediaFiles.add(file);
            }
        }

        sumFilesSynchronized += syncFiles(user, mediaFiles);
        return sumFilesSynchronized;
    }

    private boolean isMedia(String name) {
        for (String validExtension : _validMediaExtensions) {
            if (name.endsWith(validExtension)) {
                return true;
            }
        }
        return false;
    }

    private synchronized int syncFiles(User user, List<IFile> filesOnFs) {
        if (filesOnFs.size() == 0) {
            return 0;
        }
        IFile path = filesOnFs.get(0).getParentFile();

        int addedOrRemovedMediaCount = removeObsoleteMediasFromDb(user, path, filesOnFs);

        addedOrRemovedMediaCount += addNewMediasToDb(user, path, files2URIList(filesOnFs));

        return addedOrRemovedMediaCount;
    }

    private int removeObsoleteMediasFromDb(final User owner, final IFile path, final List<IFile> filesInFs) {
        return _transactionHandler.executeInTransaction(new Callable<Integer>() {

            @Override
            public Integer call() throws Exception {
                // find out which Medias of specified user and path are known in DB
                List<Media> knownFilesInDb = _mediaDao.getAllByPath(owner, path, Integer.MAX_VALUE);
                final Set<URI> filesNotInFs = new HashSet<URI>(mediaToUris(knownFilesInDb));
                // substract files from FS from files from DB (-> we get files which have to be removed from db)
                for (IFile fileInFs : filesInFs) {
                    if (filesNotInFs.contains(fileInFs.toURI())) {
                        filesNotInFs.remove(fileInFs.toURI());
                    }
                }
                // for each remaining URI, remove corresponding Media from DB
                int removedMediasCount = 0;
                for (URI uri : filesNotInFs) {
                    Media mediaToRemove = _mediaDao.getByUri(owner, uri);
                    LOG.info("Remove media '" + mediaToRemove.getId() + "' with uri '" + uri + "' from db.");
                    _mediaDao.makeTransient(mediaToRemove);
                    removedMediasCount++;
                }
                return removedMediasCount;
            }
        });
    }

    private int addNewMediasToDb(final User user, final IFile path, final List<URI> filesInFs) {

        return _transactionHandler.executeInTransaction(new Callable<Integer>() {

            @Override
            public Integer call() throws Exception {
                Set<URI> filesNotInDb = new HashSet<URI>(filesInFs);
                List<Media> allInPathAndUri = _mediaDao.getAllByPathAndUri(user, path, uRIs2StringList(filesInFs), Integer.MAX_VALUE);
                for (Media media : allInPathAndUri) {
                    URI uriMediaInDb;
                    try {
                        uriMediaInDb = new URI(media.getUri());
                        if (filesNotInDb.contains(uriMediaInDb)) {
                            filesNotInDb.remove(uriMediaInDb);
                        }
                    } catch (URISyntaxException e) {
                        ExceptionUtil.convertToRuntimeException(e);
                    }
                }
                int filesAddedToDb = 0;
                for (URI fileNotInDb : filesNotInDb) {
                    Media newMedia = createMediaFromMediaFile(user, fileNotInDb);
                    _mediaDao.makePersistent(newMedia);
                    LOG.info("Add new scanned media with uri '" + newMedia.getUri() + "' into db.");
                    filesAddedToDb++;
                }
                return filesAddedToDb;
            }
        });
    }

    public static Media createMediaFromMediaFile(final User user, URI mediaFileUri) throws FileNotFoundException {
        Metadata metadataFromMedia = getMetadataFromMedia(mediaFileUri);
        Date creationDate = resolveCreationDateOfMedia(metadataFromMedia, mediaFileUri);
        // TODO rwe: add metadata to media, plugin-stuff?
        org.dom4j.Element elementMetadata = convertMetadataToElement(metadataFromMedia);

        Orientation orientation = resolveOrientation(metadataFromMedia, mediaFileUri);
        Media newMedia = new Media(user, null, mediaFileUri, creationDate);
        newMedia.setOrientation(orientation);
        InputStream is = null;
        try {
            is = new FileInputStream(new File(mediaFileUri));
            newMedia.setHashValue(DigestUtil.computeSha1AsHexString(is));
        } finally {
            IOUtils.closeQuietly(is);
        }
        return newMedia;
    }

    private static Element convertMetadataToElement(Metadata metadataFromMedia) {
        // TODO rwe: plugin-in stuff, implement when its time to implement...
        return null;
    }

    @SuppressWarnings("rawtypes")
    static Date resolveCreationDateOfMedia(Metadata metadata, URI mediaUri) {
        if (metadata != null) {
            // iterate through metadata directories
            Iterator directories = metadata.getDirectoryIterator();
            while (directories.hasNext()) {
                Directory directory = (Directory) directories.next();
                // iterate through tags and print to System.out
                Iterator tags = directory.getTagIterator();
                while (tags.hasNext()) {
                    Tag tag = (Tag) tags.next(); // use Tag.toString()
                    if (tag.getTagName().equals("Date/Time Original")) {
                        LOG.debug("In Media '" + metadata + "' found date information within picture's meta data:" + tag);
                        // try to parse
                        DateFormat df = new SimpleDateFormat("yyyy:MM:dd HH:mm:ss");
                        String description = null;
                        try {
                            description = tag.getDescription();
                        } catch (MetadataException e) {
                            LOG.warn("can not get tag description.", e);
                        }
                        if (!StringUtils.isEmpty(description)) {
                            try {
                                return df.parse(description);
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
            }
        }
        if (mediaUri.getScheme().equals("file") && new File(mediaUri.getPath()).exists()) {
            return new Date(new File(mediaUri.getPath()).lastModified());
        }
        return new Date();
    }

    /**
     * This method uses the <code>JpegMetadataReader</code> to read meta informations from only JPEG files.<br/>
     * TODO rwe: plugin-stuff? Better use a separate class like 'MetadataextractorUtil' to get meta informations from media files which is
     * more flexible and can provide meta informations from videos as well.
     * 
     * @param mediaUri
     * @return
     */
    static Metadata getMetadataFromMedia(URI mediaUri) {
        String scheme = mediaUri.getScheme();
        if (scheme.equals("file") && new File(mediaUri.getPath()).exists()) {
            File mediaFile = new File(mediaUri.getPath());
            final Set<String> PARSABLE_FILE_EXTENSIONS = new HashSet<String>(Arrays.asList("jpg"));
            if (!PARSABLE_FILE_EXTENSIONS.contains(FilenameUtils.getExtension(mediaFile.getName()).toLowerCase())) {
                // Can not determine file type by file extension, so try to analyze its content
                FileInputStream fileInputStream = null;
                try {
                    fileInputStream = new FileInputStream(mediaFile);
                    String mimeType = URLConnection.guessContentTypeFromStream(new BufferedInputStream(fileInputStream));
                    LOG.info("Found mime type '" + mimeType + "' for file with name '" + mediaUri + "'.");
                    if (!"image/jpeg".equals(mimeType)) {
                        return null;
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    IOUtils.closeQuietly(fileInputStream);
                }
            }
            try {
                return JpegMetadataReader.readMetadata(mediaFile);

            } catch (JpegProcessingException e) {
                LOG.warn("Can not read metadata from media file '" + mediaFile.getPath() + "'.", e);
            }
        }
        return null;
    }

    static List<URI> files2URIList(List<IFile> inList) {
        List<URI> outList = new ArrayList<URI>(inList.size());
        for (IFile input : inList) {
            outList.add(input.toURI());
        }
        return outList;
    }

    static List<String> uRIs2StringList(List<URI> inList) {
        List<String> outList = new ArrayList<String>(inList.size());
        for (URI input : inList) {
            outList.add(input.toString());
        }
        return outList;
    }

    private Collection<URI> mediaToUris(List<Media> inList) {
        List<URI> outList = new ArrayList<URI>(inList.size());
        for (Media in : inList) {
            try {
                outList.add(new URI(in.getUri()));
            } catch (URISyntaxException e) {
                throw ExceptionUtil.convertToRuntimeException(e);
            }
        }
        return outList;
    }

    public static Orientation resolveOrientation(Metadata metadataFromMedia, URI sourceUri) {
        if (metadataFromMedia != null) {
            Directory directory = metadataFromMedia.getDirectory(ExifDirectory.class);
            try {
                String description = directory.getDescription(ExifDirectory.TAG_ORIENTATION);
                if (description != null) {
                    String cameraOrientation = description.toLowerCase();
                    // first, i will just handle the 'normal' expected orientations of a picture
                    if (cameraOrientation.startsWith("top, left side")) {
                        return Orientation.TOP_LEFT_SIDE;
                    } else if (cameraOrientation.startsWith("right side, top")) {
                        return Orientation.RIGHT_SIDE_TOP;
                    } else if (cameraOrientation.startsWith("left side, bottom")) {
                        return Orientation.LEFT_SIDE_BOTTOM;
                    }
                }
            } catch (MetadataException e) {
                LOG.warn("Unexpected error while examine the orientation of picture '" + sourceUri + "'.", e);
            }
        }
        return Orientation.UNKNOWN;
    }
}

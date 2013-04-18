package de.wehner.mediamagpie.conductor.webapp.services;

import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.hibernate.criterion.Order;
import org.mortbay.log.Log;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import de.wehner.mediamagpie.conductor.media.PhotoMetadataExtractor;
import de.wehner.mediamagpie.conductor.metadata.CameraMetaData;
import de.wehner.mediamagpie.core.concurrent.SingleThreadedController;
import de.wehner.mediamagpie.core.util.ExceptionUtil;
import de.wehner.mediamagpie.persistence.dao.MediaDao;
import de.wehner.mediamagpie.persistence.dao.TransactionHandler;
import de.wehner.mediamagpie.persistence.dao.UserConfigurationDao;
import de.wehner.mediamagpie.persistence.dao.UserDao;
import de.wehner.mediamagpie.persistence.entity.LifecyleStatus;
import de.wehner.mediamagpie.persistence.entity.Media;
import de.wehner.mediamagpie.persistence.entity.Orientation;
import de.wehner.mediamagpie.persistence.entity.User;
import de.wehner.mediamagpie.persistence.entity.properties.UserConfiguration;

@Service
public class MediaSyncService extends SingleThreadedController {

    private static final Logger LOG = LoggerFactory.getLogger(MediaSyncService.class);

    private static final Set<String> _validMediaExtensions = new HashSet<String>(Arrays.asList(".jpg", ".png"));
    public static final int MAX_DATAS_PER_REQUEST = 20;

    private final TransactionHandler _transactionHandler;
    private final UserConfigurationDao _configurationDao;
    private final MediaDao _mediaDao;
    private final UserDao _userDao;
    private final Map<File, CountDownLatch> _processingPathes = new ConcurrentHashMap<File, CountDownLatch>();
    private static ObjectMapper _mapper = new ObjectMapper();

    @Autowired
    public MediaSyncService(TransactionHandler transactionHandler, UserDao userDao, MediaDao mediaDao, UserConfigurationDao userConfigurationDao) {
        super(TimeUnit.MINUTES, 5);
        _userDao = userDao;
        _mediaDao = mediaDao;
        _transactionHandler = transactionHandler;
        _configurationDao = userConfigurationDao;
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
            try {
                if (syncMediaPathes(user, userConfiguration.getRootMediaPathes())) {
                    someOneWasBusy = true;
                }
            } catch (IOException e) {
                LOG.warn("Can not sync / create media path.", e);
            }
        }

        return someOneWasBusy;
    }

    public boolean syncMediaPathes(final User user, String... mediaPathes) throws IOException {
        if (mediaPathes == null) {
            return false;
        }
        boolean someOneWasBusy = false;
        for (String mediaPath : mediaPathes) {
            File syncDirPath = new File(mediaPath);
            if (!syncDirPath.exists()) {
                syncDirPath.mkdir();
            }
            // wait if another process is scanning same directory and lock when ready to sync
            CountDownLatch syncEndSignal = new CountDownLatch(1);
            synchronized (_processingPathes) {
                CountDownLatch countDownLatch = _processingPathes.get(syncDirPath);
                if (countDownLatch != null) {
                    try {
                        countDownLatch.await();
                    } catch (InterruptedException e) {
                    }
                }
                _processingPathes.put(syncDirPath, syncEndSignal);
            }

            try {
                LOG.info("start sync user's '" + user.getName() + "' directory '" + mediaPath + "'.");
                int filesSynchronized = syncDir(user, syncDirPath);
                boolean wasBusy = filesSynchronized > 0;

                if (!wasBusy) {
                    wasBusy = purgeObsoleteMedias();
                }
                if (!someOneWasBusy) {
                    someOneWasBusy = wasBusy;
                }
            } finally {
                // remove syncEndSignal from map and trigger signal for waiting threads to start scanning on same directory as well
                _processingPathes.remove(syncDirPath);
                syncEndSignal.countDown();
            }
        }
        return someOneWasBusy;
    }

    private synchronized boolean purgeObsoleteMedias() {
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

    private int syncDir(User user, File dirToSync) throws IOException {
        if (!dirToSync.exists()) {
            LOG.warn("Directory '" + dirToSync + "' does not exist. Does anybody has deleted this directory during scanning phase?");
            return 0;
        }
        File[] listFiles = dirToSync.listFiles();
        int sumFilesSynchronized = 0;
        List<File> mediaFiles = new ArrayList<File>();
        for (File file : listFiles) {
            if (file.isDirectory()) {
                sumFilesSynchronized += syncDir(user, file);
            } else if (isMedia(file)) {
                mediaFiles.add(file);
            }
        }

        sumFilesSynchronized += syncFiles(user, mediaFiles);
        return sumFilesSynchronized;
    }

    private boolean isMedia(File file) {
        String name = file.getPath().toLowerCase();
        for (String validExtension : _validMediaExtensions) {
            if (name.endsWith(validExtension)) {
                return true;
            }
        }
        return false;
    }

    private synchronized int syncFiles(User user, List<File> filesOnFs) {
        if (filesOnFs.size() == 0) {
            return 0;
        }
        File path = filesOnFs.get(0).getParentFile();

        int addedOrRemovedMediaCount = removeObsoleteMediasFromDb(user, path, filesOnFs);

        addedOrRemovedMediaCount += addNewMediasToDb(user, path, files2URIList(filesOnFs));

        return addedOrRemovedMediaCount;
    }

    private int removeObsoleteMediasFromDb(final User owner, final File path, final List<File> filesInFs) {
        return _transactionHandler.executeInTransaction(new Callable<Integer>() {

            @Override
            public Integer call() throws Exception {
                // find out which Medias of specified user and path are known in DB
                List<Media> knownFilesInDb = _mediaDao.getAllByPath(owner, path.getPath(), Integer.MAX_VALUE);
                final Set<URI> filesNotInFs = new HashSet<URI>(mediaToUris(knownFilesInDb));
                // substract files from FS from files from DB (-> we get files which have to be removed from db)
                for (File fileInFs : filesInFs) {
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

    private int addNewMediasToDb(final User user, final File path, final List<URI> filesInFs) {

        return _transactionHandler.executeInTransaction(new Callable<Integer>() {

            @Override
            public Integer call() throws Exception {
                Set<URI> filesNotInDb = new HashSet<URI>(filesInFs);
                List<Media> allInPathAndUri = _mediaDao.getAllByPathAndUri(user, path.getPath(), uRIs2StringList(filesInFs), Integer.MAX_VALUE);
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

    public static Media createMediaFromMediaFile(final User user, URI mediaFileUri) throws IOException {
        PhotoMetadataExtractor metadataExtractor = new PhotoMetadataExtractor(mediaFileUri);
        Date creationDate = resolveCreationDateOfMedia(metadataExtractor, mediaFileUri);
        Orientation orientation = metadataExtractor.resolveOrientation();
        Media newMedia = Media.createWithHashValue(user, null, mediaFileUri, creationDate);
        // Media newMedia = new Media(user, null, mediaFileUri, creationDate);
        newMedia.setOrientation(orientation);
        addCameraMetaDataToMedia(metadataExtractor, newMedia);
        // InputStream is = null;
        // try {
        // is = new FileInputStream(new File(mediaFileUri));
        // newMedia.setHashValue(DigestUtil.computeSha1AsHexString(is));
        // } finally {
        // IOUtils.closeQuietly(is);
        // }
        return newMedia;
    }

    private static void addCameraMetaDataToMedia(PhotoMetadataExtractor metadataExtractor, Media newMedia) throws IOException, JsonGenerationException,
            JsonMappingException {
        CameraMetaData cameraMetaData = metadataExtractor.createCameraMetaData();
        if (cameraMetaData == null) {
            return;
        }

        Writer stringWriter = new StringWriter();
        _mapper.writeValue(stringWriter, cameraMetaData);
        newMedia.setCameraMetaData(stringWriter.toString());
    }

    private static Date resolveCreationDateOfMedia(PhotoMetadataExtractor metadataExtractor, URI mediaUri) {
        Date date = metadataExtractor.resolveDateTimeOriginal();
        if (date != null) {
            return date;
        }
        // use the file's time stamp instead
        if (mediaUri.getScheme().equals("file") && new File(mediaUri.getPath()).exists()) {
            return new Date(new File(mediaUri.getPath()).lastModified());
        }
        return new Date();
    }

    static List<URI> files2URIList(List<File> inList) {
        List<URI> outList = new ArrayList<URI>(inList.size());
        for (File input : inList) {
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

}

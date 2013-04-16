package de.wehner.mediamagpie.conductor.performingjob;

import java.io.File;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.wehner.mediamagpie.api.MediaExport;
import de.wehner.mediamagpie.api.MediaExportRepository;
import de.wehner.mediamagpie.aws.s3.S3MediaExportRepository;
import de.wehner.mediamagpie.common.persistence.MediaExportFactory;
import de.wehner.mediamagpie.common.persistence.dao.MediaDao;
import de.wehner.mediamagpie.common.persistence.entity.LifecyleStatus;
import de.wehner.mediamagpie.common.persistence.entity.Media;
import de.wehner.mediamagpie.common.persistence.entity.User;
import de.wehner.mediamagpie.conductor.configuration.ConfigurationProvider;
import de.wehner.mediamagpie.conductor.webapp.services.UploadService;
import de.wehner.mediamagpie.core.util.Pair;
import de.wehner.mediamagpie.persistence.TransactionHandler;

public class S3SyncJob extends AbstractJob {

    private static final Logger LOG = LoggerFactory.getLogger(S3SyncJob.class);

    private final User _user;
    private final ConfigurationProvider _configurationProvider;
    protected final MediaExportRepository _s3MediaRepositiory;
    private final MediaDao _mediaDao;
    private final TransactionHandler _transactionHandler;
    private final MediaExportFactory _mediaExportFactory = new MediaExportFactory();
    private final UploadService _uploadService;

    public S3SyncJob(S3MediaExportRepository s3MediaExportRepository, UploadService uploadService, User user, ConfigurationProvider configurationProvider,
            TransactionHandler transactionHandler, MediaDao mediaDao) {
        super();
        _s3MediaRepositiory = s3MediaExportRepository;
        _uploadService = uploadService;
        _user = user;
        _configurationProvider = configurationProvider;
        _transactionHandler = transactionHandler;
        _mediaDao = mediaDao;
    }

    @Override
    public JobCallable prepare() throws Exception {
        return new JobCallable() {

            @Override
            public URI call() throws Exception {
                LOG.debug("sync medias from S3 bucket ...");
                List<Media> medias = _transactionHandler.executeInTransaction(new Callable<List<Media>>() {

                    @Override
                    public List<Media> call() throws Exception {
                        return _mediaDao.getAllOfUser(_user, LifecyleStatus.Living);
                    }
                });
                // build map for medias that are local but not processed with matching medias in S3
                Map<String, Media> unmatchedMedias = new HashMap<String, Media>();
                for (Media media : medias) {
                    unmatchedMedias.put(media.getHashValue(), media);
                }

                Iterator<MediaExport> iteratorPhotos = _s3MediaRepositiory.iteratorPhotos(_user.getName());
                List<MediaExport> unkonwMediaOnS3 = new ArrayList<MediaExport>();
                while (iteratorPhotos.hasNext()) {
                    MediaExport mediaExport = iteratorPhotos.next();
                    if (unmatchedMedias.containsKey(mediaExport.getHashValue())) {
                        // we have found an Media on S3 which matches to a local one
                        LOG.trace("Found media '" + mediaExport.getName() + "' on S3 with same hash value than a local media has.");
                        unmatchedMedias.remove(mediaExport.getHashValue());
                    } else {
                        LOG.trace("Found media '" + mediaExport.getName() + "' on S3 with a hash value that is unknown local.");
                        unkonwMediaOnS3.add(mediaExport);
                    }
                }

                // add all unmatched medias which have to be pushed to S3
                for (Media mediaToExport : unmatchedMedias.values()) {
                    LOG.debug(String.format("try to push media '%s' to S3.", mediaToExport.getName()));
                    MediaExport mediaExport = _mediaExportFactory.create(mediaToExport);
                    _s3MediaRepositiory.addMedia(_user.getName(), mediaExport);
                }

                // retrieve all unknown Medias from S3 to local DB and file system
                for (MediaExport mediaExport : unkonwMediaOnS3) {
                    LOG.debug(String.format("try to import media '%s' from S3.", mediaExport.getName()));
                    Pair<String, File> uploadFileInfo = _uploadService.createUniqueUserStoreFile(_user, mediaExport.getOriginalFileName());
                    LOG.info("Try dump upload stream '" + uploadFileInfo.getFirst() + "' into file '" + uploadFileInfo.getSecond().getPath() + "'");
                    Media newMedia = _uploadService.handleUploadStream(_user, uploadFileInfo.getSecond(), mediaExport.getInputStream());

                    // create job executions for a) image resizing and S3 Upload
                    _uploadService.createJobsForFreshUploadedMedias(newMedia, _configurationProvider);
                    _mediaDao.makePersistent(newMedia);
                }
                return null;
            }

            @Override
            public int getProgress() {
                return 0;
            }

            @Override
            public void cancel() throws Exception {
                LOG.info("cancel called....");
            }

            @Override
            public void handleResult(URI result) {
                // // mark media to be sucessfully exported to S3
                // Media media = _mediaDao.getById(Long.parseLong(_mediaExport.getMediaId()));
                // media.setExportedToS3(true);
                // LOG.info("Mark Media entity with URI '" + media.getUri() + "' to be sucessfully exported to S3.");
                // _mediaDao.makePersistent(media);
            }
        };
    }
}

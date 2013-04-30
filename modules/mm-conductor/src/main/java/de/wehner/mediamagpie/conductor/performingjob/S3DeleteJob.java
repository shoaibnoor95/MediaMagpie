package de.wehner.mediamagpie.conductor.performingjob;

import java.io.FileNotFoundException;
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
import de.wehner.mediamagpie.api.MediaExportResults;
import de.wehner.mediamagpie.aws.s3.S3MediaExportRepository;
import de.wehner.mediamagpie.conductor.media.MediaImportFactory;
import de.wehner.mediamagpie.conductor.webapp.services.UploadService;
import de.wehner.mediamagpie.core.util.ExceptionUtil;
import de.wehner.mediamagpie.persistence.MediaExportFactory;
import de.wehner.mediamagpie.persistence.dao.MediaDao;
import de.wehner.mediamagpie.persistence.dao.TransactionHandler;
import de.wehner.mediamagpie.persistence.entity.LifecyleStatus;
import de.wehner.mediamagpie.persistence.entity.Media;
import de.wehner.mediamagpie.persistence.entity.User;
import de.wehner.mediamagpie.persistence.service.ConfigurationProvider;

public class S3DeleteJob extends AbstractJob {

    private static final Logger LOG = LoggerFactory.getLogger(S3DeleteJob.class);

    private final User _user;
    private final ConfigurationProvider _configurationProvider;
    protected final MediaExportRepository _s3MediaRepositiory;
    private final MediaDao _mediaDao;
    private final TransactionHandler _transactionHandler;
    private final MediaExportFactory _mediaExportFactory = new MediaExportFactory();
    private final UploadService _uploadService;

    public S3DeleteJob(S3MediaExportRepository s3MediaExportRepository, UploadService uploadService, User user, ConfigurationProvider configurationProvider,
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
                        LOG.trace("Media '" + mediaExport.getName() + "' on S3 has same hash value than a local one.");
                        unmatchedMedias.remove(mediaExport.getHashValue());
                    } else {
                        LOG.trace("Media '" + mediaExport.getName() + "' on S3 is unknown on local side.");
                        unkonwMediaOnS3.add(mediaExport);
                    }
                }

                // --> Push all unmatched medias to S3
                for (Media mediaToExport : unmatchedMedias.values()) {
                    final Media media = mediaToExport;
                    _transactionHandler.executeInTransaction(new Runnable() {

                        @Override
                        public void run() {
                            LOG.debug("Try to push media with id " + media.getId() + " to S3.");
                            try {
                                Media mediaInSession = _transactionHandler.reload(media);
                                MediaExport mediaExport = _mediaExportFactory.create(mediaInSession);
                                MediaExportResults mediaExportResults = _s3MediaRepositiory.addMedia(_user.getName(), mediaExport);
                                // TODO : implement an error-handling when one or more objects can not be uploaded sucessfully
                            } catch (FileNotFoundException e) {
                                ExceptionUtil.convertToRuntimeException(e);
                            }
                        }
                    });
                }

                // <-- Pull all unknown Medias from S3 and store in local DB and file system
                final MediaImportFactory mediaImportFactory = new MediaImportFactory(_uploadService, _user, _configurationProvider, _transactionHandler,
                        _mediaDao);
                for (final MediaExport mediaExport : unkonwMediaOnS3) {
                    LOG.debug(String.format("try to import object '%s' from S3.", mediaExport.getName()));
                    _transactionHandler.executeInTransaction(new Callable<Boolean>() {

                        @Override
                        public Boolean call() throws Exception {
                            Media newMedia = mediaImportFactory.create(mediaExport);
                            newMedia = _mediaDao.getPersistenceService().reload(newMedia);
                            newMedia.setExportedToS3(true);
                            _mediaDao.makePersistent(newMedia);
                            return null;
                        }
                    });
                }
                LOG.info("finised " + getClass().getSimpleName());
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

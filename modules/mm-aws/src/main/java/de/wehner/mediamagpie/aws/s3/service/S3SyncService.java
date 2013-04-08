package de.wehner.mediamagpie.aws.s3.service;

import java.io.File;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import de.wehner.mediamagpie.common.persistence.MediaExportFactory;
import de.wehner.mediamagpie.common.persistence.dao.MediaDao;
import de.wehner.mediamagpie.common.persistence.dao.S3JobExecutionDao;
import de.wehner.mediamagpie.common.persistence.dao.UserConfigurationDao;
import de.wehner.mediamagpie.common.persistence.dao.UserDao;
import de.wehner.mediamagpie.common.persistence.entity.Media;
import de.wehner.mediamagpie.common.persistence.entity.S3JobExecution;
import de.wehner.mediamagpie.core.concurrent.SingleThreadedController;
import de.wehner.mediamagpie.persistence.TransactionHandler;

@Service
public class S3SyncService extends SingleThreadedController {

    private static final Logger LOG = LoggerFactory.getLogger(S3SyncService.class);

    private final MediaExportFactory _mediaExportFactory = new MediaExportFactory();

    private final S3JobExecutionDao _s3JobExecutionDao;

    private static final Set<String> _validMediaExtensions = new HashSet<String>(Arrays.asList(".jpg", ".png"));
    public static final int MAX_DATAS_PER_REQUEST = 20;

    private final TransactionHandler _transactionHandler;
    private final UserConfigurationDao _configurationDao;
    private final MediaDao _mediaDao;
    private final UserDao _userDao;
    private final Map<File, CountDownLatch> _processingPathes = new ConcurrentHashMap<File, CountDownLatch>();
    private static ObjectMapper _mapper = new ObjectMapper();

    @Autowired
    public S3SyncService(TransactionHandler transactionHandler, UserDao userDao, MediaDao mediaDao, UserConfigurationDao userConfigurationDao,
            S3JobExecutionDao s3JobExecutionDao) {
        super(TimeUnit.MINUTES, 5);
        _userDao = userDao;
        _mediaDao = mediaDao;
        _transactionHandler = transactionHandler;
        _configurationDao = userConfigurationDao;
        _s3JobExecutionDao = s3JobExecutionDao;
    }

    @Override
    protected boolean execute() {
        // TODO Auto-generated method stub
        return false;
    }

    public void pushToS3(Media media) {
        S3JobExecution s3JobExecution = new S3JobExecution(media, S3JobExecution.Direction.PUT);
        _s3JobExecutionDao.makePersistent(s3JobExecution);
        LOG.info("Upload to S3 job for media '" + media.getId() + "' added with priority '" + s3JobExecution.getPriority() + "'.");
    }

}

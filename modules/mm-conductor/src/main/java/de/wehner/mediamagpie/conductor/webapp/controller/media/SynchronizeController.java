package de.wehner.mediamagpie.conductor.webapp.controller.media;

import java.io.FileNotFoundException;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.BasicAWSCredentials;

import de.wehner.mediamagpie.api.MediaExport;
import de.wehner.mediamagpie.api.MediaExportResult;
import de.wehner.mediamagpie.api.MediaExportResults;
import de.wehner.mediamagpie.aws.s3.S3MediaExportRepository;
import de.wehner.mediamagpie.conductor.webapp.controller.AbstractConfigurationSupportController;
import de.wehner.mediamagpie.conductor.webapp.util.security.SecurityUtil;
import de.wehner.mediamagpie.persistence.MediaDao;
import de.wehner.mediamagpie.persistence.MediaExportFactory;
import de.wehner.mediamagpie.persistence.dto.SearchCriteriaCommand;
import de.wehner.mediamagpie.persistence.entity.LifecyleStatus;
import de.wehner.mediamagpie.persistence.entity.Media;
import de.wehner.mediamagpie.persistence.entity.User;
import de.wehner.mediamagpie.persistence.entity.properties.S3Configuration;
import de.wehner.mediamagpie.persistence.service.ConfigurationProvider;

@Controller
@RequestMapping("/media/synchronize")
public class SynchronizeController extends AbstractConfigurationSupportController {

    private static final Logger LOG = LoggerFactory.getLogger(SynchronizeController.class);

    public static final String URL_INDEX = "/";
    public static final String VIEW_INDEX = "media/synchronize";

    public static final String URL_SYNC_S3 = "synchronize_s3";

    private final MediaDao _mediaDao;

    @Autowired
    public SynchronizeController(MediaDao mediaDao, ConfigurationProvider configurationProvider) {
        super(configurationProvider, null);
        _mediaDao = mediaDao;
    }

    @RequestMapping(method = RequestMethod.GET, value = URL_INDEX)
    public String index(Model model) {
        model.addAttribute("hasS3Config", getCurrentUsersS3Configuration(SecurityUtil.getCurrentUser()) != null);
        return VIEW_INDEX;
    }

    @RequestMapping(method = RequestMethod.POST, value = URL_SYNC_S3, params = { "submitSelect=pull" })
    public String pullFromS3() {
        LOG.info("Start pulling from s3...");
        return VIEW_INDEX;
    }

    @RequestMapping(method = RequestMethod.POST, value = URL_SYNC_S3, params = { "submitSelect=push" })
    public String pushToS3() {
        LOG.info("Start pushing to s3... ");
        User user = SecurityUtil.getCurrentUser();
        S3Configuration userS3Configuration = getCurrentUsersS3Configuration(user);
        if (userS3Configuration == null) {
            throw new RuntimeException("The AWS access key or secret key is empty. Can not export to S3.");
        }

        // create MediaExportRepository layer
        AWSCredentials credentials = new BasicAWSCredentials(userS3Configuration.getAccessKey(), userS3Configuration.getSecretKey());
        S3MediaExportRepository s3MediaExportRepository = new S3MediaExportRepository(credentials);

        // create MediaExportFactory
        MediaExportFactory mediaExportFactory = new MediaExportFactory();

        // get all pictures of user
        // TODO rwe: add some filter to reduce the amount of medias for transfer
        List<Media> allPictures = _mediaDao.getAllBySearchCriterias(user, 0, Integer.MAX_VALUE, true, SearchCriteriaCommand.createInstance(),
                LifecyleStatus.Living);

        for (Media media : allPictures) {
            try {
                MediaExport mediaExport = mediaExportFactory.create(media);
                MediaExportResults exportResults = s3MediaExportRepository.addMedia(user.getName(), mediaExport);
                LOG.info("MediaExport result for media [id=" + media.getId() + "] was: " + exportResults.getExportStatus());
                if (exportResults.detectStatus(MediaExportResult.ExportStatus.ALREADY_EXPORTED)) {
                    break;
                }
            } catch (FileNotFoundException e) {
                LOG.warn("Can not create MediaExport object from Media object.", e);
            }
        }
        return VIEW_INDEX;
    }

    private static String getBaseRequestMappingUrl() {
        return SynchronizeController.class.getAnnotation(RequestMapping.class).value()[0];
    }

    public static String getIndexUrl() {
        return getBaseRequestMappingUrl() + URL_INDEX;
    }

    private S3Configuration getCurrentUsersS3Configuration(User user) {
        if (user != null) {
            S3Configuration userS3Configuration = _configurationProvider.getS3Configuration(user);
            if (userS3Configuration != null && !StringUtils.isEmpty(userS3Configuration.getAccessKey())
                    && !StringUtils.isEmpty(userS3Configuration.getSecretKey())) {
                return userS3Configuration;
            }
        }
        return null;
    }
}

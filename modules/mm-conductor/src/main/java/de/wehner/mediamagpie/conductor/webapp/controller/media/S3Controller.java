package de.wehner.mediamagpie.conductor.webapp.controller.media;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import de.wehner.mediamagpie.aws.s3.service.S3SyncService;
import de.wehner.mediamagpie.common.persistence.dao.MediaDao;
import de.wehner.mediamagpie.common.persistence.entity.Media;
import de.wehner.mediamagpie.conductor.configuration.ConfigurationProvider;
import de.wehner.mediamagpie.conductor.webapp.controller.AbstractConfigurationSupportController;

@Controller
@RequestMapping("/s3")
public class S3Controller extends AbstractConfigurationSupportController {

    private static final Logger LOG = LoggerFactory.getLogger(S3Controller.class);

    public static final String AJAX_PUTTOS3 = "/ajaxPutToS3";

    private final MediaDao _mediaDao;
    private final S3SyncService _s3SyncService;

    @Autowired
    public S3Controller(MediaDao mediaDao, ConfigurationProvider configurationProvider, S3SyncService s3SyncService) {
        super(configurationProvider, null);
        _mediaDao = mediaDao;
        _s3SyncService = s3SyncService;
    }

    @RequestMapping(method = RequestMethod.POST, value = AJAX_PUTTOS3, params = {})
    public void putMediaToS3(Model model, @RequestParam("id") Long mediaId, HttpServletRequest request) {
        LOG.info("Try to move media " + mediaId + " to s3");
        Media media = _mediaDao.getById(mediaId);
        // add new upload job to S3
        _s3SyncService.pushToS3(media);
    }

    public static String getBaseRequestMappingUrl() {
        return S3Controller.class.getAnnotation(RequestMapping.class).value()[0];
    }

    public static String getAjaxUrlAddMediaToS3() {
        return getBaseRequestMappingUrl() + AJAX_PUTTOS3;
    }

}

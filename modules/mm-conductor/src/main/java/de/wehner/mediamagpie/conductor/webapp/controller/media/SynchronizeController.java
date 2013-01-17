package de.wehner.mediamagpie.conductor.webapp.controller.media;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
@RequestMapping("/media/synchronize")
public class SynchronizeController {

    private static final Logger LOG = LoggerFactory.getLogger(SynchronizeController.class);

    public static final String URL_INDEX = "/";
    public static final String VIEW_INDEX = "media/synchronize";

    public static final String URL_SYNC_S3 = "synchronize_s3";

    @RequestMapping(method = RequestMethod.GET, value = URL_INDEX)
    public String index(Model model) {
        return VIEW_INDEX;
    }

    @RequestMapping(method = RequestMethod.POST, value = URL_SYNC_S3, params = { "submitSelect=pull" })
    public String pullFromS3() {
        LOG.info("Start pulling from s3...");
        return VIEW_INDEX;
    }

    @RequestMapping(method = RequestMethod.POST, value = URL_SYNC_S3, params = { "submitSelect=push" })
    public String pushToS3() {
        LOG.info("Start pushing to s3...");
        return VIEW_INDEX;
    }

    private static String getBaseRequestMappingUrl() {
        return SynchronizeController.class.getAnnotation(RequestMapping.class).value()[0];
    }

    public static String getIndexUrl() {
        return getBaseRequestMappingUrl() + URL_INDEX;
    }
}

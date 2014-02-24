package de.wehner.mediamagpie.conductor.webapp.controller.media;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import de.wehner.mediamagpie.conductor.webapp.controller.AbstractConfigurationSupportController;
import de.wehner.mediamagpie.conductor.webapp.services.ImageService;
import de.wehner.mediamagpie.persistence.dao.AlbumDao;
import de.wehner.mediamagpie.persistence.service.ConfigurationProvider;

@Controller
@RequestMapping("/public/download")
public class DownloadController extends AbstractConfigurationSupportController {

    @SuppressWarnings("unused")
    private static final Logger LOG = LoggerFactory.getLogger(DownloadController.class);

    public static final String URL_VIEW = "/album/{albumId}/{mediaId}";
    public static final String VIEW_VIEW = "public/download";

    private final AlbumDao _albumDao;
    private final ImageService _imageSerivce;

    @Autowired
    public DownloadController(ConfigurationProvider configurationProvider, AlbumDao albumDao, ImageService imageService) {
        super(configurationProvider, null);
        _albumDao = albumDao;
        _imageSerivce = imageService;
    }

    @RequestMapping(method = RequestMethod.GET, value = URL_VIEW)
    public String downloadAlbum(Model model, @PathVariable String albumId, @PathVariable String mediaId, HttpServletRequest request) {
        return null;
    }

    public static String getBaseRequestMappingUrl() {
        return DownloadController.class.getAnnotation(RequestMapping.class).value()[0];
    }

}

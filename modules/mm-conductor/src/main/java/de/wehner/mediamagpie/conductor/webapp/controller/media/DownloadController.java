package de.wehner.mediamagpie.conductor.webapp.controller.media;

import java.io.IOException;
import java.net.URLEncoder;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.sun.xml.internal.messaging.saaj.packaging.mime.internet.MimeUtility;

import de.wehner.mediamagpie.conductor.webapp.controller.AbstractConfigurationSupportController;
import de.wehner.mediamagpie.conductor.webapp.controller.ImageController;
import de.wehner.mediamagpie.persistence.dao.AlbumDao;
import de.wehner.mediamagpie.persistence.dao.MediaDao;
import de.wehner.mediamagpie.persistence.entity.Album;
import de.wehner.mediamagpie.persistence.entity.Media;
import de.wehner.mediamagpie.persistence.service.ConfigurationProvider;

@Controller
@RequestMapping("/public/download")
public class DownloadController extends AbstractConfigurationSupportController {

    @SuppressWarnings("unused")
    private static final Logger LOG = LoggerFactory.getLogger(DownloadController.class);

    public static final String URL_VIEW = "/album/{albumId}/{mediaId}";
    public static final String VIEW_VIEW = "public/download";

    private final AlbumDao _albumDao;
    private final MediaDao _mediaDao;

    @Autowired
    public DownloadController(ConfigurationProvider configurationProvider, AlbumDao albumDao, MediaDao mediaDao) {
        super(configurationProvider, null);
        _albumDao = albumDao;
        _mediaDao = mediaDao;
    }

    @RequestMapping(method = RequestMethod.GET, value = URL_VIEW)
    public void downloadAlbum(Model model, @PathVariable String albumId, @PathVariable Long mediaId, HttpServletRequest request,
            HttpServletResponse response) throws IOException {

        Album album = _albumDao.getByUuid(albumId);

        if (album == null) {
            // skip download
            return;
        }
        // TODO rwe: verify user has enough permission to read this album

        Media media = _mediaDao.getById(mediaId);

        String filename = !StringUtils.isEmpty(media.getOriginalFileName()) ? media.getOriginalFileName() : media.getFileFromUri().getName();
        String agent = request.getHeader("USER-AGENT");

        if (agent != null && agent.indexOf("MSIE") != -1) {
            filename = URLEncoder.encode(filename, "UTF8");
            response.setContentType("application/x-download");
            response.setHeader("Content-Disposition", "attachment;filename=" + filename);
        } else if (agent != null && agent.indexOf("Mozilla") != -1) {
            response.setCharacterEncoding("UTF-8");
            filename = MimeUtility.encodeText(filename, "UTF8", "B");
            response.setContentType("application/force-download");
            response.addHeader("Content-Disposition", "attachment; filename=\"" + filename + "\"");
        }

        ImageController.readImageIntoOutputStream(media.getFileFromUri().getPath(), response.getOutputStream());
    }

    public static String getBaseRequestMappingUrl() {
        return DownloadController.class.getAnnotation(RequestMapping.class).value()[0];
    }

}

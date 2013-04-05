package de.wehner.mediamagpie.conductor.webapp.controller;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import de.wehner.mediamagpie.common.persistence.dao.MediaDao;
import de.wehner.mediamagpie.common.persistence.entity.Media;
import de.wehner.mediamagpie.common.persistence.entity.properties.MainConfiguration;
import de.wehner.mediamagpie.conductor.configuration.ConfigurationProvider;
import de.wehner.mediamagpie.conductor.webapp.controller.commands.MediaCommand;
import de.wehner.mediamagpie.conductor.webapp.controller.commands.MediaFeedCommand;
import de.wehner.mediamagpie.conductor.webapp.controller.commands.MediaFeedCommand.Item;
import de.wehner.mediamagpie.conductor.webapp.services.ImageService;
import de.wehner.mediamagpie.conductor.webapp.util.WebAppUtils;
import de.wehner.mediamagpie.persistence.dto.UiMediaSortOrder;

/**
 * @author ralfwehner
 * TODO rwe: The dashboard is currently removed from main menu. Maybe, we will find some good ideas for a new content.
 */
@Controller
public class DashboardController {

    @SuppressWarnings("unused")
    private static final Logger LOG = LoggerFactory.getLogger(DashboardController.class);

    public static final String URL_DASHBORAD = "/dashboard";
    public static final String VIEW_DASHBORAD = "public/dashboard";
    public static final String URL_DASHBORAD2 = "/dashboard_cooliris";
    public static final String VIEW_DASHBORAD2 = "public/dashboard_cooliris";
    public static final String URL_RSS = "/dashboard/rss";
    public static final String VIEW_RSS = "rss/cooliris_rss";

    private final MediaDao _mediaDao;
    private final ImageService _imageSerivce;
    private final ConfigurationProvider _configurationProvider;

    @Autowired
    public DashboardController(MediaDao mediaDao, ImageService imageService, ConfigurationProvider configurationProvider) {
        super();
        _mediaDao = mediaDao;
        _imageSerivce = imageService;
        _configurationProvider = configurationProvider;
    }

    @RequestMapping(method = RequestMethod.GET, value = URL_DASHBORAD)
    public String setupDashboard(Model model, @RequestParam(value = "start", required = false) Integer start) {
        List<MediaCommand> pictures = new ArrayList<MediaCommand>();
        int startIndex = (start != null) ? start : 0;
        MainConfiguration mainConfiguration = _configurationProvider.getMainConfiguration();
        List<Media> allPictures = _mediaDao
                .getAllOrderedByCreationDate(null, startIndex, mainConfiguration.getHitsPerPage(), UiMediaSortOrder.DATE, true);
        for (Media media : allPictures) {
            MediaCommand pictureCommand = new MediaCommand(media, _imageSerivce, mainConfiguration.getDefaultThumbSize());
            pictureCommand.setDetailImageUrl(_imageSerivce.getOrCreateImageUrl(media, null));
            pictures.add(pictureCommand);
        }

        model.addAttribute("pictures", pictures);
        model.addAttribute("start", start);
        model.addAttribute("pageSize", mainConfiguration.getHitsPerPage());
        model.addAttribute("totalHits", _mediaDao.countAll());
        return VIEW_DASHBORAD;
    }

    @RequestMapping(method = RequestMethod.GET, value = URL_DASHBORAD2)
    public String setupDashbaordCooliris(ModelMap model) {
        return VIEW_DASHBORAD2;
    }

    @RequestMapping(method = RequestMethod.GET, value = URL_RSS)
    public String getDashboardRss(ModelMap model, HttpServletRequest request) {
        MediaFeedCommand command = new MediaFeedCommand();
        List<Media> allPictures = _mediaDao.getAllOrderedByCreationDate(null, 0, 200, UiMediaSortOrder.DATE, true);
        int counter = 0;
        MainConfiguration mainConfiguration = _configurationProvider.getMainConfiguration();
        for (Media media : allPictures) {
            String thumbUrl = _imageSerivce.getOrCreateImageUrl(media, mainConfiguration.getDefaultThumbSize());
            String imageUrl = _imageSerivce.getOrCreateImageUrl(media, mainConfiguration.getDefaultDetailThumbSize());
            String title = (media.getName() != null) ? media.getName() : "";
            Item item;
            try {
                // item = new MediaFeedCommand.Item(title, FilenameUtils.getName(new URI(media.getUri()).toURL().getFile()), "link",
                // IConstantsWorkaround.HTTP_LOCALHOST_8088 + thumbUrl, IConstantsWorkaround.HTTP_LOCALHOST_8088 + imageUrl);
                item = new MediaFeedCommand.Item();
                item.setTitle(title);
                item.setDescription(FilenameUtils.getName(new URI(media.getUri()).toURL().getFile()));
                item.setLink("link");
                item.setUrlThumbnail(WebAppUtils.getRequestUrlUpToContextPath(request) + thumbUrl);
                item.setUrlContent(WebAppUtils.getRequestUrlUpToContextPath(request) + imageUrl);
                command.addItem(item);
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (URISyntaxException e) {
                e.printStackTrace();
            }
            if (++counter >= 200) {
                break;
            }
        }
        model.addAttribute("command", command);
        return VIEW_RSS;
    }

    // public static String getBaseRequestMappingUrl() {
    // return DashboardController.class.getAnnotation(RequestMapping.class).value()[0];
    // }
}

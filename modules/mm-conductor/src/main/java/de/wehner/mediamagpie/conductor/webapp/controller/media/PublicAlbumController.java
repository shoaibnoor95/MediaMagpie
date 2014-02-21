package de.wehner.mediamagpie.conductor.webapp.controller.media;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import de.wehner.mediamagpie.conductor.webapp.controller.AbstractConfigurationSupportController;
import de.wehner.mediamagpie.conductor.webapp.controller.commands.MediaDetailCommand;
import de.wehner.mediamagpie.conductor.webapp.controller.commands.MediaFeedCommand;
import de.wehner.mediamagpie.conductor.webapp.controller.commands.MediaFeedCommand.Item;
import de.wehner.mediamagpie.conductor.webapp.controller.commands.MediaThumbCommand;
import de.wehner.mediamagpie.conductor.webapp.services.ImageService;
import de.wehner.mediamagpie.conductor.webapp.util.WebAppUtils;
import de.wehner.mediamagpie.conductor.webapp.util.security.SecurityUtil;
import de.wehner.mediamagpie.persistence.dao.AlbumDao;
import de.wehner.mediamagpie.persistence.entity.Album;
import de.wehner.mediamagpie.persistence.entity.Media;
import de.wehner.mediamagpie.persistence.entity.Priority;
import de.wehner.mediamagpie.persistence.entity.User;
import de.wehner.mediamagpie.persistence.entity.Visibility;
import de.wehner.mediamagpie.persistence.entity.properties.MainConfiguration;
import de.wehner.mediamagpie.persistence.entity.properties.UserConfiguration;
import de.wehner.mediamagpie.persistence.service.ConfigurationProvider;

@Controller
@RequestMapping("/public/album")
public class PublicAlbumController extends AbstractConfigurationSupportController {

    private static final String SORRY_YOU_ARE_NOT_AUTHORIZED_TO_VIEW_THIS_ALBUM = "Sorry, you are not authorized to view this album.";

    @SuppressWarnings("unused")
    private static final Logger LOG = LoggerFactory.getLogger(PublicAlbumController.class);

    public static final String URL_VIEW = "/{uuid}/view";
    public static final String VIEW_VIEW = "public/album/viewAlbum";

    public static final String URL_DETAIL_PICTURE = "/{uuid}/{pos}";
    public static final String VIEW_DETAIL_PICTURE = "public/album/detail_picture";
    public static final String VIEW_VIEW_COOLIRIS = "public/album/coolirisViewAlbum";

    public static final String URL_RSS = "/{uuid}/rss";
    public static final String VIEW_RSS = "rss/cooliris_rss";

    private final AlbumDao _albumDao;
    private final ImageService _imageSerivce;

    @Autowired
    public PublicAlbumController(ConfigurationProvider configurationProvider, AlbumDao albumDao, ImageService imageService) {
        super(configurationProvider, null);
        _albumDao = albumDao;
        _imageSerivce = imageService;
    }

    @RequestMapping(method = RequestMethod.GET, value = URL_VIEW)
    public String view(Model model, @PathVariable String uuid, @RequestParam(value = "start", required = false) Integer start, HttpServletRequest request) {
        start = (start != null) ? start : 0;
        Album album = _albumDao.getByUuid(uuid);
        MainConfiguration mainConfiguration = getMainConfiguration();
        model.addAttribute("start", start);
        model.addAttribute("pageSize", mainConfiguration.getHitsPerPage());
        if (album != null) {
            String errorMsg = doVisibilityValidation(album);
            if (errorMsg == null) {
                // AlbumCommand albumCommand = new AlbumCommand();
                List<MediaThumbCommand> mediaThumbCommands = new ArrayList<MediaThumbCommand>();
                User owner = album.getOwner();
                UserConfiguration userConfiguration = _configurationProvider.getUserConfiguration(owner);
                final List<Media> mediasInAlbum = album.getMedias();
                int end = Math.min((start + mainConfiguration.getHitsPerPage()), mediasInAlbum.size());
                for (int i = start; i < end; i++) {
                    Media media = mediasInAlbum.get(i);
                    mediaThumbCommands.add(_imageSerivce.createMediaThumbCommand(media, mainConfiguration, userConfiguration, request));
                }
                // for (Media media : mediasInAlbum) {
                // mediaThumbCommands.add(_imageSerivce.createMediaThumbCommand(media, mainConfiguration, userConfiguration, request));
                // }
                // albumCommand.init(album, _imageSerivce, userConfiguration.getThumbImageSize());
                model.addAttribute("albumCommand", album);
                model.addAttribute(mediaThumbCommands);
                model.addAttribute("totalHits", mediasInAlbum.size());
            } else {
                model.addAttribute("error", errorMsg);
            }
        } else {
            model.addAttribute("error", "Sorry, your requested album does not exists.");
        }
        return VIEW_VIEW;
    }

    @RequestMapping(method = RequestMethod.GET, value = URL_RSS)
    public String viewCooliris(ModelMap model, @PathVariable String uuid, @RequestParam(value = "start", required = false) Integer start,
            HttpServletRequest request) {
        Album album = _albumDao.getByUuid(uuid);
        if (album != null) {
            String errorMsg = doVisibilityValidation(album);
            if (StringUtils.isEmpty(errorMsg)) {
                MediaFeedCommand command = new MediaFeedCommand();
                List<Media> allPictures = album.getMedias();
                MainConfiguration mainConfiguration = getMainConfiguration();
                for (Media media : allPictures) {
                    String thumbUrl = _imageSerivce.getOrCreateImageUrl(media, mainConfiguration.getDefaultThumbSize());
                    String imageUrl = _imageSerivce.getOrCreateImageUrl(media, mainConfiguration.getDefaultDetailThumbSize());
                    String urlToOriginal = imageUrl;
                    String title = (media.getName() != null) ? media.getName() : "";
                    Item item;
                    try {
                        String description = media.getDescription();
                        if (StringUtils.isEmpty(description)) {
                            description = FilenameUtils.getName(new URI(media.getUri()).toURL().getFile());
                        }
                        // item = new MediaFeedCommand.Item(title, description, urlToOriginal,
                        // WebAppUtils.getRequestUrlUpToContextPath(request) + thumbUrl,
                        // WebAppUtils.getRequestUrlUpToContextPath(request) + imageUrl);
                        item = new MediaFeedCommand.Item();
                        item.setTitle(title);
                        item.setDescription(description);
                        item.setLink(urlToOriginal);
                        item.setUrlThumbnail(WebAppUtils.getRequestUrlUpToContextPath(request) + thumbUrl);
                        item.setUrlContent(WebAppUtils.getRequestUrlUpToContextPath(request) + imageUrl);
                        command.addItem(item);
                    } catch (MalformedURLException e) {
                        e.printStackTrace();
                    } catch (URISyntaxException e) {
                        e.printStackTrace();
                    }
                }
                model.addAttribute("command", command);
                return VIEW_RSS;
            }
            // } else {
            // model.addAttribute("error", "Sorry, your requested album does not exists.");
        }
        return null;
    }

    @RequestMapping(method = RequestMethod.GET, value = URL_DETAIL_PICTURE)
    public String showDetailPicture(Model model, @PathVariable String uuid, @PathVariable Integer pos,
            @RequestParam(value = "renderer", required = false) String renderer, HttpServletRequest servletRequest) {
        MainConfiguration mainConfiguration = getMainConfiguration();
        Album album = _albumDao.getByUuid(uuid);
        Media media = null;
        MediaDetailCommand mediaDetailCommand;
        if (pos < 0) {
            mediaDetailCommand = new MediaDetailCommand(album);
        } else {
            media = album.getMedias().get(pos);
            mediaDetailCommand = new MediaDetailCommand(album, media);
        }
        if ("cooliris".equals(renderer)) {
            model.addAttribute("mediaDetailCommand", mediaDetailCommand);
            model.addAttribute("pos", pos);
            return VIEW_VIEW_COOLIRIS;
        }

        String imageUrl = _imageSerivce.getOrCreateImageUrl(media, mainConfiguration.getDefaultDetailThumbSize(), false, Priority.HIGH);
        mediaDetailCommand.setImageLink(servletRequest.getContextPath() + imageUrl);
        // mediaDetailCommand.setOverviewUrl(servletRequest.getHeader("Referer"));
        mediaDetailCommand.setUrlPrev(buildPrevNextUrl(servletRequest, album, pos - 1));
        mediaDetailCommand.setUrlNext(buildPrevNextUrl(servletRequest, album, pos + 1));
        model.addAttribute("mediaDetailCommand", mediaDetailCommand);
        model.addAttribute("pos", pos);
        return VIEW_DETAIL_PICTURE;
    }

    public static String getBaseRequestMappingUrl() {
        return PublicAlbumController.class.getAnnotation(RequestMapping.class).value()[0];
    }

    public static String getPublicViewUrl(String uuid) {
        return String.format("%s/%s/view", getBaseRequestMappingUrl(), uuid);
    }

    private String buildPrevNextUrl(HttpServletRequest servletRequest, Album album, int pos) {
        if (pos < 0 || pos >= album.getMedias().size()) {
            return null;
        }
        return String.format("%s%s/%s/%s", servletRequest.getContextPath(), getBaseRequestMappingUrl(), album.getUid(), pos);
    }

    private String doVisibilityValidation(Album album) {
        Visibility visibility = album.getVisibility();
        if (visibility != Visibility.OWNER) {
            // When the album is not marked as USER, all visitors that got the link invitation have the right to see this album.
            return null;
        }

        User currentUser = SecurityUtil.getCurrentUser();
        if (currentUser != null && currentUser.getId() == album.getOwner().getId()) {
            return null;
        } else {
            return SORRY_YOU_ARE_NOT_AUTHORIZED_TO_VIEW_THIS_ALBUM;
        }
    }
}

package de.wehner.mediamagpie.conductor.webapp.controller.media;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import de.wehner.mediamagpie.common.persistence.dao.MediaDao;
import de.wehner.mediamagpie.common.persistence.dao.UserConfigurationDao;
import de.wehner.mediamagpie.common.persistence.entity.Media;
import de.wehner.mediamagpie.common.persistence.entity.MediaTag;
import de.wehner.mediamagpie.common.persistence.entity.Priority;
import de.wehner.mediamagpie.conductor.configuration.ConfigurationProvider;
import de.wehner.mediamagpie.conductor.webapp.controller.AbstractConfigurationSupportController;
import de.wehner.mediamagpie.conductor.webapp.controller.commands.MediaDetailCommand;
import de.wehner.mediamagpie.conductor.webapp.services.ImageService;

@Controller
public class MediaDetailController extends AbstractConfigurationSupportController {

    @SuppressWarnings("unused")
    private static final Logger LOG = LoggerFactory.getLogger(MediaDetailController.class);

    public static final String URL_BASE_DETAIL_PICTURE_EDIT = "/detail_picture_edit/";
    public static final String URL_DETAIL_PICTURE_EDIT = URL_BASE_DETAIL_PICTURE_EDIT + "{mediaId}";
    public static final String VIEW_DETAIL_PICTURE_EDIT = "media/detail_picture_edit";

    private final MediaDao _mediaDao;
    private final ImageService _imageSerivce;

    @Autowired
    public MediaDetailController(MediaDao mediaDao, ImageService imageService, ConfigurationProvider configurationProvider,
            UserConfigurationDao userConfigurationDao) {
        super(configurationProvider, null);
        _mediaDao = mediaDao;
        _imageSerivce = imageService;
    }

    @RequestMapping(method = RequestMethod.POST, value = { URL_DETAIL_PICTURE_EDIT }, params = "submitSelect=goBack")
    public String submitDetailPictureAndGoToOverview(Model model, @PathVariable Long mediaId, MediaDetailCommand mediaDetailCommand) {
        Media media = _mediaDao.getById(mediaId);
        media.setName(mediaDetailCommand.getName());
        media.setDescription(mediaDetailCommand.getDescription());
        mergeTags(media, mediaDetailCommand.getTagsAsString());
        _mediaDao.makePersistent(media);
        if (!StringUtils.isEmpty(mediaDetailCommand.getOverviewUrl())) {
            return "redirect:" + mediaDetailCommand.getOverviewUrl();
        }
        return VIEW_DETAIL_PICTURE_EDIT;
    }

    @RequestMapping(method = RequestMethod.GET, value = URL_DETAIL_PICTURE_EDIT)
    public String showDetailPictureEdit(Model model, @PathVariable Long mediaId, HttpServletRequest servletRequest) {
        Media media = _mediaDao.getById(mediaId);
        String imageUrl = _imageSerivce.getOrCreateImageUrl(media, getCurrentUserConfiguration().getDetailImageSize(), false, Priority.HIGH);
        MediaDetailCommand mediaDetailCommand = new MediaDetailCommand(null, media);
        mediaDetailCommand.setImageLink(imageUrl);
        mediaDetailCommand.setOverviewUrl(servletRequest.getHeader("Referer"));
        model.addAttribute(mediaDetailCommand);
        return VIEW_DETAIL_PICTURE_EDIT;
    }

    private void mergeTags(Media media, String tagsAsString) {
        Map<String, MediaTag> existingTagsInMediaMap = new HashMap<String, MediaTag>();
        for (MediaTag existingTag : media.getTags()) {
            existingTagsInMediaMap.put(existingTag.getName(), existingTag);
        }
        List<MediaTag> tagsToRemove = new ArrayList<MediaTag>(media.getTags());
        String[] newTags = StringUtils.split(tagsAsString, ',');
        for (String newTag : newTags) {
            newTag = newTag.trim();
            if (newTag.length() > 0) {
                MediaTag existingTag = existingTagsInMediaMap.get(newTag);
                if (existingTag != null) {
                    // newTag is already in media
                    tagsToRemove.remove(existingTag);
                } else {
                    // media doesn't contains newTag
                    media.addTag(new MediaTag(newTag));
                }
            }
        }
        for (MediaTag mediaTag : tagsToRemove) {
            media.getTags().remove(mediaTag);
        }
    }
}

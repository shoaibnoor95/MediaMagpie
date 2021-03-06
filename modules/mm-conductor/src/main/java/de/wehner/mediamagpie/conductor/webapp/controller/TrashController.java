package de.wehner.mediamagpie.conductor.webapp.controller;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import de.wehner.mediamagpie.conductor.webapp.controller.commands.EditCommand;
import de.wehner.mediamagpie.conductor.webapp.controller.commands.EditCommand.Action;
import de.wehner.mediamagpie.conductor.webapp.controller.commands.MediaThumbCommand;
import de.wehner.mediamagpie.conductor.webapp.services.ImageService;
import de.wehner.mediamagpie.conductor.webapp.util.security.SecurityUtil;
import de.wehner.mediamagpie.persistence.dao.MediaDao;
import de.wehner.mediamagpie.persistence.dto.UiMediaSortOrder;
import de.wehner.mediamagpie.persistence.entity.LifecyleStatus;
import de.wehner.mediamagpie.persistence.entity.Media;
import de.wehner.mediamagpie.persistence.service.ConfigurationProvider;

@Controller
@RequestMapping("/media/trash")
public class TrashController extends AbstractConfigurationSupportController {

    @SuppressWarnings("unused")
    private static final Logger LOG = LoggerFactory.getLogger(TrashController.class);

    public static final String URL_TRASH = "/content";
    public static final String VIEW_TRASH = "media/trash";

    private final MediaDao _mediaDao;
    private final ImageService _imageSerivce;

    @Autowired
    public TrashController(MediaDao mediaDao, ImageService imageService, ConfigurationProvider configurationProvider) {
        super(configurationProvider, null);
        _mediaDao = mediaDao;
        _imageSerivce = imageService;
    }

    @RequestMapping(method = RequestMethod.GET, value = URL_TRASH)
    public String setupView(Model model, @RequestParam(value = "start", required = false) Integer start) {
        model.addAttribute(new EditCommand());
        return searchMediasAndPutIntoModel(model, start);
    }

    @RequestMapping(method = RequestMethod.POST, value = URL_TRASH)
    public String submit(Model model, EditCommand searchCriteria, @RequestParam(value = "start", required = false) Integer start) {
        if (searchCriteria.getAction() == Action.DELETE) {
            _imageSerivce.deleteMediaCompletely(_mediaDao.getById(searchCriteria.getId()));
        } else if (searchCriteria.getAction() == Action.UNDO) {
            Media media = _mediaDao.getById(searchCriteria.getId());
            media.setLifeCycleStatus(LifecyleStatus.Living);
            _mediaDao.makePersistent(media);
        }
        return searchMediasAndPutIntoModel(model, start);
    }

    private String searchMediasAndPutIntoModel(Model model, Integer start) {
        int startIndex = (start != null) ? start : 0;
        List<MediaThumbCommand> pictures = new ArrayList<MediaThumbCommand>();
        final int hitsPerPage = getMainConfiguration().getHitsPerPage();
        List<Media> allPictures = _mediaDao.getAllOrderedByCreationDate(SecurityUtil.getCurrentUser(), startIndex, hitsPerPage, UiMediaSortOrder.DATE,
                true, LifecyleStatus.MovedToTrashCan);
        int hits = _mediaDao.getAllBySearchCriteriasCount(SecurityUtil.getCurrentUser(), null, LifecyleStatus.MovedToTrashCan);
        for (Media media : allPictures) {
            MediaThumbCommand pictureCommand = new MediaThumbCommand(media);
            String imageUrl = _imageSerivce.getOrCreateImageUrl(media, getCurrentUserConfiguration().getThumbImageSizeTable());
            pictureCommand.setUrlThumbImage(imageUrl);
            pictures.add(pictureCommand);
        }

        model.addAttribute("pictures", pictures);
        model.addAttribute("start", startIndex);
        model.addAttribute("pageSize", getMainConfiguration().getHitsPerPage());
        model.addAttribute("totalHits", hits);
        return VIEW_TRASH;
    }

    public static String getBaseRequestMappingUrl() {
        return TrashController.class.getAnnotation(RequestMapping.class).value()[0];
    }

    public static String getTrashUrl() {
        return getBaseRequestMappingUrl() + URL_TRASH;
    }

}

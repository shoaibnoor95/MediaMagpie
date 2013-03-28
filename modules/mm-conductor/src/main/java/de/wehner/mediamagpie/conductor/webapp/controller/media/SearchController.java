package de.wehner.mediamagpie.conductor.webapp.controller.media;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttributes;

import de.wehner.mediamagpie.common.persistence.entity.Album;
import de.wehner.mediamagpie.common.persistence.entity.LifecyleStatus;
import de.wehner.mediamagpie.common.persistence.entity.Media;
import de.wehner.mediamagpie.common.persistence.entity.User;
import de.wehner.mediamagpie.common.util.MinMaxValue;
import de.wehner.mediamagpie.common.util.TimeUtil;
import de.wehner.mediamagpie.conductor.configuration.ConfigurationProvider;
import de.wehner.mediamagpie.conductor.persistence.dao.AlbumDao;
import de.wehner.mediamagpie.conductor.persistence.dao.MediaDao;
import de.wehner.mediamagpie.conductor.persistence.dao.UserConfigurationDao;
import de.wehner.mediamagpie.conductor.webapp.commands.binder.EnumBinder;
import de.wehner.mediamagpie.conductor.webapp.commands.binder.MinMaxIntegerBinder;
import de.wehner.mediamagpie.conductor.webapp.controller.AbstractConfigurationSupportController;
import de.wehner.mediamagpie.conductor.webapp.controller.commands.AlbumCommand;
import de.wehner.mediamagpie.conductor.webapp.controller.commands.AlbumSelectionCommand;
import de.wehner.mediamagpie.conductor.webapp.controller.commands.MediaThumbCommand;
import de.wehner.mediamagpie.conductor.webapp.controller.commands.SearchCriteriaCommand;
import de.wehner.mediamagpie.conductor.webapp.controller.commands.SearchCriteriaCommand.Action;
import de.wehner.mediamagpie.conductor.webapp.controller.media.common.UiMediaSortOrder;
import de.wehner.mediamagpie.conductor.webapp.services.ImageService;
import de.wehner.mediamagpie.conductor.webapp.util.WebAppUtils;
import de.wehner.mediamagpie.conductor.webapp.util.security.SecurityUtil;
import de.wehner.mediamagpie.conductor.webapp.validator.SearchCriteriaCommandValidator;

@Controller
@RequestMapping("/media")
@SessionAttributes({ "searchCriteria", "albumSelectionCommand", "albumCommand" })
public class SearchController extends AbstractConfigurationSupportController {

    private static final Logger LOG = LoggerFactory.getLogger(SearchController.class);

    public static final String URL_MEDIA_SEARCH = "/search_pictures";
    public static final String VIEW_MEDIA_SEARCH = "media/searchPictures";

    public static final String URL_SELECT_ALBUM = "/select_album";
    public static final String URL_ADD_MEDIA_TO_ALBUM = "/searchPictures/ajaxAddMedia";
    public static final String URL_REMOVE_MEDIA_TO_ALBUM = "/searchPictures/ajaxRemoveMedia";
    public static final String URL_UPDATE_SORT_ORDER_ALBUM = "/searchPictures/ajaxUpdateSortOrderOfAlbum";

    public static final String AJAX_CURRENT_ALBUM = "/ajaxCurrentAlbumTemplate";
    public static final String VIEW_CURRENT_ALBUM = "media/album-media-template";

    final int hitsPerPageAlbum = 5;

    private final MediaDao _mediaDao;
    private final AlbumDao _albumDao;
    private final ImageService _imageSerivce;

    @Autowired
    public SearchController(MediaDao mediaDao, AlbumDao albumDao, ImageService imageService, ConfigurationProvider configurationProvider,
            UserConfigurationDao userConfigurationDao) {
        super(configurationProvider, userConfigurationDao, null);
        _mediaDao = mediaDao;
        _albumDao = albumDao;
        _imageSerivce = imageService;
    }

    @ModelAttribute("availableAlbums")
    public List<Album> getAllAlbumms() {
        User currentUser = SecurityUtil.getCurrentUser();
        if (currentUser == null) {
            return java.util.Collections.emptyList();
        }
        return _albumDao.getByOwner(currentUser);
    }

    @ModelAttribute("mediaSortOrders")
    public List<UiMediaSortOrder> getAllMediaSortOrders() {
        return Arrays.asList(UiMediaSortOrder.DATE, UiMediaSortOrder.ID);
    }

    @SuppressWarnings("rawtypes")
    @InitBinder
    public void initBinder(WebDataBinder binder) {
        binder.registerCustomEditor(MinMaxValue.class, new MinMaxIntegerBinder());
        binder.registerCustomEditor(UiMediaSortOrder.class, new EnumBinder(UiMediaSortOrder.class));
    }

    @RequestMapping(method = RequestMethod.GET, value = URL_MEDIA_SEARCH)
    public String setupSearch(Model model, @RequestParam(value = "start", required = false) Integer start, HttpServletRequest request) {
        SearchCriteriaCommand searchCriteria = (SearchCriteriaCommand) model.asMap().get("searchCriteria");
        if (searchCriteria == null) {
            searchCriteria = createDefaultSearchCriteriaCommand();
        }
        AlbumSelectionCommand albumSelectionCommand = (AlbumSelectionCommand) model.asMap().get("albumSelectionCommand");
        if (albumSelectionCommand == null) {
            albumSelectionCommand = new AlbumSelectionCommand();
            model.addAttribute(albumSelectionCommand);
        }

        setCurrentAlbumIntoModel(model, 0, request);
        return searchMediaAndPutIntoModel(model, start, searchCriteria, request);
    }

    @RequestMapping(method = RequestMethod.POST, value = URL_MEDIA_SEARCH, params = {})
    public String submitMediaSearch(@ModelAttribute("searchCriteria") SearchCriteriaCommand searchCriteria, BindingResult result, Model model,
            @RequestParam(value = "start", required = false) Integer start, HttpServletRequest request) {
        new SearchCriteriaCommandValidator().validate(searchCriteria, result);
        if (result.hasErrors()) {
            LOG.info(result.toString());
            return VIEW_MEDIA_SEARCH;
        }
        if (searchCriteria.getAction() == Action.DELETE) {
            Media media = _mediaDao.getById(searchCriteria.getId());
            media.setLifeCycleStatus(LifecyleStatus.MovedToTrashCan);
            _mediaDao.makePersistent(media);
        }
        return searchMediaAndPutIntoModel(model, start, searchCriteria, request);
    }

    @RequestMapping(method = RequestMethod.POST, value = URL_SELECT_ALBUM)
    public String submitAlbumSelection(Model model, AlbumSelectionCommand albumSelectionCommand,
            @RequestParam(value = "start", required = false) Integer startMedia, @RequestParam(value = "startAlbum", required = false) Integer start) {
        if (albumSelectionCommand.getAlbumId() != null) {
            Album album = _albumDao.getById(albumSelectionCommand.getAlbumId());
            AlbumCommand albumCommand = new AlbumCommand();
            albumCommand.init(album);
            model.addAttribute(albumCommand);
            model.addAttribute("start", startMedia);
        }
        return WebAppUtils.redirect(this, URL_MEDIA_SEARCH);
    }

    @RequestMapping(method = RequestMethod.POST, value = URL_ADD_MEDIA_TO_ALBUM)
    public String submitAddMediaToCurrentAlbum(Model model, @RequestParam("id") Long id,
            @RequestParam(value = "startAlbum", required = false) Integer start, HttpServletRequest request) {
        AlbumCommand albumCommand = (AlbumCommand) model.asMap().get("albumCommand");
        Album album = _albumDao.getById(albumCommand.getId());
        Media media = _mediaDao.getById(id);
        album.addMedia(media);
        _albumDao.makePersistent(album);
        setCurrentAlbumIntoModel(model, start, request);
        return VIEW_CURRENT_ALBUM;
    }

    @RequestMapping(method = RequestMethod.POST, value = URL_REMOVE_MEDIA_TO_ALBUM)
    public String submitRemoveMediaToCurrentAlbum(Model model, @RequestParam("id") Long id, HttpServletRequest request) {
        AlbumCommand albumCommand = (AlbumCommand) model.asMap().get("albumCommand");
        Album album = _albumDao.getById(albumCommand.getId());
        Media media = _mediaDao.getById(id);
        album.getMedias().remove(media);
        _albumDao.makePersistent(album);
        setCurrentAlbumIntoModel(model, Integer.MAX_VALUE, request);
        return VIEW_CURRENT_ALBUM;
    }

    @RequestMapping(method = RequestMethod.GET, value = URL_UPDATE_SORT_ORDER_ALBUM)
    public void updateSortOrderOfAlbum(Model model, @RequestParam("mediaIds") List<Long> mediaIds) {
        LOG.info("Reorder album with ids '" + mediaIds + "'.");
        AlbumCommand albumCommand = (AlbumCommand) model.asMap().get("albumCommand");
        Album album = _albumDao.getById(albumCommand.getId());
        if (album != null) {
            // iterate through medias and replace all improper medias
            for (int i = 0; i < album.getMedias().size(); i++) {
                Media mediaInAlbum = album.getMedias().get(i);
                Long targetMediaId = mediaIds.get(i);
                if (!mediaInAlbum.getId().equals(targetMediaId)) {
                    Media newMedia = _mediaDao.getById(targetMediaId);
                    album.getMedias().set(i, newMedia);
                }
            }
        }
    }

    private String searchMediaAndPutIntoModel(Model model, Integer start, SearchCriteriaCommand searchCriteria, HttpServletRequest request) {
        int startIndex = (start != null) ? start : 0;
        List<MediaThumbCommand> mediaThumbCommands = new ArrayList<MediaThumbCommand>();
        final int hitsPerPage = getMainConfiguration().getHitsPerPage();
        List<Media> allPictures = _mediaDao.getAllBySearchCriterias(SecurityUtil.getCurrentUser(), startIndex, hitsPerPage, true, searchCriteria,
                LifecyleStatus.Living);
        int hits = _mediaDao.getAllBySearchCriteriasCount(SecurityUtil.getCurrentUser(), searchCriteria, LifecyleStatus.Living);
        for (Media media : allPictures) {
            MediaThumbCommand mediaThumbCommand = _imageSerivce.createMediaThumbCommand(media, getMainConfiguration(), getCurrentUserConfiguration(),
                    request);
            mediaThumbCommands.add(mediaThumbCommand);
        }

        model.addAttribute("pictures", mediaThumbCommands);
        model.addAttribute("start", start);
        model.addAttribute("pageSize", hitsPerPage);
        model.addAttribute("totalHits", hits);
        if (searchCriteria.getYearStartFromInputField() != null) {
            searchCriteria.getSliderYearValues().setMin(searchCriteria.getYearStartFromInputField());
        } else {
            if (allPictures.size() > 0) {
                Media oldestMedia = allPictures.get(0);
                if (oldestMedia.getCreationDate() != null) {
                    searchCriteria.getSliderYearValues().setMin(TimeUtil.getYearFromDate(oldestMedia.getCreationDate()));
                }
            }
        }
        if (searchCriteria.getYearEndFromInputField() != null) {
            searchCriteria.getSliderYearValues().setMax(searchCriteria.getYearEndFromInputField());
        }
        // extend the range of slide if necessary
        if (searchCriteria.getSliderYearValues().getMin() < searchCriteria.getSliderYearMinMax().getMin()) {
            searchCriteria.getSliderYearMinMax().setMin(searchCriteria.getSliderYearValues().getMin());
        }
        if (searchCriteria.getSliderYearValues().getMax() > searchCriteria.getSliderYearMinMax().getMax()) {
            searchCriteria.getSliderYearMinMax().setMax(searchCriteria.getSliderYearValues().getMax());
        }
        model.addAttribute("searchCriteria", searchCriteria);
        return VIEW_MEDIA_SEARCH;
    }

    private SearchCriteriaCommand createDefaultSearchCriteriaCommand() {
        SearchCriteriaCommand searchCriteria = new SearchCriteriaCommand();
        int year = TimeUtil.getYearFromDate(new Date());
        searchCriteria.setSliderYearMinMax(new MinMaxValue<Integer>(1999, year));
        searchCriteria.setSliderYearValues(new MinMaxValue<Integer>(year - 5, year));
        searchCriteria.setSortOrder(UiMediaSortOrder.DATE);
        return searchCriteria;
    }

    public static String getBaseRequestMappingUrl() {
        return SearchController.class.getAnnotation(RequestMapping.class).value()[0];
    }

    public static String getAjaxUrlCurrentAlbum() {
        return getBaseRequestMappingUrl() + AJAX_CURRENT_ALBUM;
    }

    public static String getAjaxUrlAddMediaToCurrentAlbum() {
        return getBaseRequestMappingUrl() + URL_ADD_MEDIA_TO_ALBUM;
    }

    public static String getAjaxUrlRemoveMediaToCurrentAlbum() {
        return getBaseRequestMappingUrl() + URL_REMOVE_MEDIA_TO_ALBUM;
    }

    public static String getAjaxUrlUpdateSortOrderOfAlbum() {
        return getBaseRequestMappingUrl() + URL_UPDATE_SORT_ORDER_ALBUM;
    }

    @RequestMapping(method = RequestMethod.GET, value = AJAX_CURRENT_ALBUM)
    public String getCurrentAlbumTemplate(Model model, @RequestParam(value = "startAlbum", required = false) Integer start, HttpServletRequest request) {
        setCurrentAlbumIntoModel(model, start, request);
        return VIEW_CURRENT_ALBUM;
    }

    private void setCurrentAlbumIntoModel(Model model, Integer start, HttpServletRequest request) {
        AlbumCommand albumCommand = (AlbumCommand) model.asMap().get("albumCommand");
        // int startIndex = (start != null) ? start : 0;
        // model.addAttribute("startAlbum", start);
        if (albumCommand != null) {
            List<MediaThumbCommand> mediasThumbCommands = new ArrayList<MediaThumbCommand>();

            List<Media> allPictures = _albumDao.getMedias(albumCommand.getId(), 0, Integer.MAX_VALUE/* startIndex, hitsPerPageAlbum */);
            /* int hits = */_albumDao.getMediasCount(albumCommand.getId());
            for (Media media : allPictures) {
                MediaThumbCommand mediaThumbCommand = _imageSerivce.createMediaThumbCommand(media, getMainConfiguration(), getCurrentUserConfiguration(),
                        request);
                mediasThumbCommands.add(mediaThumbCommand);
            }

            model.addAttribute("mediasInAlbum", mediasThumbCommands);
            // model.addAttribute("pageSizeAlbum", hitsPerPageAlbum);
            // model.addAttribute("totalHitsAlbum", hits);
        }
    }
}

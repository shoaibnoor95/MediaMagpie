package de.wehner.mediamagpie.conductor.webapp.controller.media;

import java.util.List;
import java.util.UUID;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import de.wehner.mediamagpie.conductor.webapp.controller.commands.AlbumCommand;
import de.wehner.mediamagpie.conductor.webapp.util.security.SecurityUtil;
import de.wehner.mediamagpie.persistence.AlbumDao;
import de.wehner.mediamagpie.persistence.dto.CrudOperation;
import de.wehner.mediamagpie.persistence.entity.Album;
import de.wehner.mediamagpie.persistence.entity.User;
import de.wehner.mediamagpie.persistence.entity.Visibility;


@Controller
@RequestMapping("/media/album")
public class AlbumController {

    private static final Logger LOG = LoggerFactory.getLogger(AlbumController.class);

    public static final String URL_LIST = "/list";
    public static final String VIEW_LIST = "media/album/listAlbums";
    public static final String URL_CREATE = "/create";
    public static final String URL_VIEW = "/{id}/view";
    public static final String VIEW_VIEW = "media/album/viewAlbum";
    public static final String URL_EDIT = "/{id}/edit";
    public static final String VIEW_EDIT = "media/album/editAlbum";
    public static final String URL_DELETE = "/delete";

    private final AlbumDao _albumDao;

    @Autowired
    public AlbumController(AlbumDao albumDao) {
        _albumDao = albumDao;
    }

    @RequestMapping(method = RequestMethod.GET, value = URL_LIST)
    public String listAllAlbums(Model model) {
        return prepareView(CrudOperation.LIST, model, null, null);
    }

    @RequestMapping(method = RequestMethod.GET, value = URL_CREATE)
    public String createNew(Model model) {
        Album album = new Album(SecurityUtil.getCurrentUser(), "new Album");
        album.setVisibility(Visibility.PUBLIC);
        album.setUid(UUID.randomUUID().toString());
        return prepareView(CrudOperation.CREATE, model, album, null);
    }

    @RequestMapping(method = RequestMethod.POST, value = URL_DELETE)
    public final String submitDelete(Model model, @RequestParam("id") Long id, @RequestParam(value = "page", required = false) Integer page, HttpSession session)
            throws Exception {
        Album album = _albumDao.getById(id);
        if (album == null) {
            return redirectToListUrl(page);
        }
        LOG.info("delete album with id [" + id + "]");
        Album currentAlbum = (Album) session.getAttribute("albumCommand");
        if (currentAlbum != null && currentAlbum.getId() == id) {
            session.removeAttribute("albumCommand");
        }
        return delete(model, album, page);
    }

    @RequestMapping(method = RequestMethod.GET, value = URL_VIEW)
    public String view(Model model, @PathVariable Long id) {
        Album album = _albumDao.getById(id);
        return prepareView(CrudOperation.VIEW, model, album, null);
    }

    @RequestMapping(method = RequestMethod.GET, value = URL_EDIT)
    public String edit(Model model, @PathVariable Long id) {
        Album album = _albumDao.getById(id);
        return prepareView(CrudOperation.EDIT, model, album, null);
    }

    @RequestMapping(method = RequestMethod.POST, value = { URL_CREATE, URL_EDIT })
    public String submitSave(Model model, @Valid AlbumCommand command, BindingResult errors) {
        if (errors.hasErrors()) {
            LOG.info("Got validation errors: " + errors.toString());
            return prepareView(CrudOperation.EDIT, model, null, command);
        }
        if (command.getIsNew()) {
            Album album = new Album(command);
            album.setOwner(SecurityUtil.getCurrentUser());
            _albumDao.makePersistent(album);
        } else {
            Album album = _albumDao.getById(command.getId());
            album.setName(command.getName());
            album.setVisibility(command.getVisibility());
        }
        return redirectToListUrl(null);
    }

    protected String delete(Model model, Album album, Integer page) throws Exception {
        _albumDao.makeTransient(album);
        return redirectToListUrl(page);
    }

    protected String prepareView(CrudOperation crudOperation, Model model, Album album, AlbumCommand commandObject) {
        switch (crudOperation) {
        case LIST:
            User currentUser = SecurityUtil.getCurrentUser();
            List<Album> allAlbums = java.util.Collections.emptyList();
            if(currentUser != null){
                allAlbums = _albumDao.getByOwner(currentUser);
            }
            model.addAttribute("albums", allAlbums);
            return VIEW_LIST;
        case VIEW:
            AlbumCommand albumCommand = new AlbumCommand();
            albumCommand.init(album);
            model.addAttribute("albumCommand", albumCommand);
            return VIEW_VIEW;
        case CREATE:
        case EDIT:
            if (commandObject == null) {
                commandObject = new AlbumCommand(crudOperation == CrudOperation.CREATE);
            }

            if (album != null) {
                commandObject.init(album);
            }
            model.addAttribute("albumCommand", commandObject);
            return VIEW_EDIT;
        default:
            throw new UnsupportedOperationException(crudOperation.toString());
        }
    }

    private String redirectToListUrl(Integer page) {
        String url = "redirect:" + getBaseRequestMappingUrl() + URL_LIST;
        if (page != null) {
            url += "?start=" + page;
        }
        return url;
    }

    public static String getBaseRequestMappingUrl() {
        return AlbumController.class.getAnnotation(RequestMapping.class).value()[0];
    }
}

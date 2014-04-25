package de.wehner.mediamagpie.conductor.webapp.controller;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import de.wehner.mediamagpie.conductor.webapp.controller.json.TagAutocompleteCommand;
import de.wehner.mediamagpie.persistence.dao.MediaTagDao;
import de.wehner.mediamagpie.persistence.entity.MediaTag;

@Controller
@RequestMapping("/ajax")
public class AjaxController {

    private static final Logger LOG = LoggerFactory.getLogger(AjaxController.class);

    public static final String AJAX_SEARCH_TAGS = "/tagsOfUser";
    private final MediaTagDao _mediaTagDao;

    @Autowired
    public AjaxController(MediaTagDao mediaTagDao) {
        super();
        _mediaTagDao = mediaTagDao;
    }

    @RequestMapping(method = RequestMethod.GET, value = AJAX_SEARCH_TAGS)
    public @ResponseBody
    List<TagAutocompleteCommand> getAutocompleteTags(HttpServletResponse response, @RequestParam(value = "term") String term) {
        List<TagAutocompleteCommand> tagCommands = new ArrayList<TagAutocompleteCommand>();

        LOG.debug("searching tags for '{}'", term);
        // TODO rwe: Only suggest tags that belongs to the user!
        // User currentUser = SecurityUtil.getCurrentUser();

        List<MediaTag> hits = _mediaTagDao.luceneSearchForName(term);
        for (MediaTag mediaTag : hits) {
            tagCommands.add(new TagAutocompleteCommand(mediaTag.getName()));
        }
        LOG.debug("found result '{}'.", tagCommands);
        return tagCommands;
    }

}

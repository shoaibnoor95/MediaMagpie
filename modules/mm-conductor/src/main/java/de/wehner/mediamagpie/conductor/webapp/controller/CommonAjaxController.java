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
import de.wehner.mediamagpie.conductor.webapp.util.security.SecurityUtil;
import de.wehner.mediamagpie.persistence.MediaTagDao;
import de.wehner.mediamagpie.persistence.entity.MediaTag;
import de.wehner.mediamagpie.persistence.entity.User;


@Controller
@RequestMapping("/ajax")
public class CommonAjaxController {

    private static final Logger LOG = LoggerFactory.getLogger(CommonAjaxController.class);

    public static final String AJAX_SEARCH_TAGS = "/tagsOfUser";
    private final MediaTagDao _mediaTagDao;

    @Autowired
    public CommonAjaxController(MediaTagDao mediaTagDao) {
        super();
        _mediaTagDao = mediaTagDao;
    }

    @RequestMapping(method = RequestMethod.GET, value = AJAX_SEARCH_TAGS)
    public @ResponseBody
    List<TagAutocompleteCommand> getAutocompleteTags(HttpServletResponse response, @RequestParam(value = "term") String term) {
        List<TagAutocompleteCommand> tagCommands = new ArrayList<TagAutocompleteCommand>();

        LOG.info("searching tags for '" + term + "'");
        // TODO rwe: use this for ajax requsts... (MB-44)
        User currentUser = SecurityUtil.getCurrentUser();

        List<MediaTag> hits = _mediaTagDao.luceneSearchForName(term);
        for (MediaTag mediaTag : hits) {
            tagCommands.add(new TagAutocompleteCommand(mediaTag.getName()));
        }
        return tagCommands;
    }

    // @SuppressWarnings("unchecked")
    // @RequestMapping(value = AJAX_FINDPHARMACIES, method = RequestMethod.GET)
    // public @ResponseBody
    // List<PharmacyAutocompleteCommand> getJsonDataForBarChart(HttpServletResponse response, @RequestParam(value = "term") String term) {
    // String postalCode = StringUtils.left(term, 5);
    // response.setStatus(HttpServletResponse.SC_OK);
    // List<PharmacyAutocompleteCommand> advices = new ArrayList<PharmacyAutocompleteCommand>();
    // for (Pharmacy pharmacy : (List<Pharmacy>) _prescriptionManagementGL.searchPharmaciesByPostalcode(Integer.parseInt(postalCode), 50)) {
    // advices.add(new PharmacyAutocompleteCommand(pharmacy));
    // }
    // return advices;
    // }
    //
    // @SuppressWarnings("unchecked")
    // @RequestMapping(value = AJAX_SEARCHPHARMACIES, method = RequestMethod.GET)
    // public @ResponseBody
    // List<Pharmacy> searchPharmacies(HttpServletRequest request, HttpServletResponse response) {
    // List<Pharmacy> pharmacies = _prescriptionManagementGL.searchPharmacies(request.getParameter("ik"), request.getParameter("name"),
    // request.getParameter("postalCode"),
    // request.getParameter("city"), request.getParameter("street"), 50);
    // return pharmacies;
    // }
}

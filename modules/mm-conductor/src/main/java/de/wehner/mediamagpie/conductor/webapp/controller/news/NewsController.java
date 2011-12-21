package de.wehner.mediamagpie.conductor.webapp.controller.news;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * @author ralfwehner
 * @deprecated
 */
@Controller
public class NewsController {

    public static final String LIST_DATSOURCES_URL = "/news/{newsId}";

    private static final String NEWS_TEMPLATE_VIEW = "/public/news_%s";

    public NewsController() {
    }

    // @ModelAttribute("dataSources")
    // public List<DataSourceConfiguration> injectAvailableDataSource() {
    // return _dataSourceDao.getAll();
    // }

    @RequestMapping(method = RequestMethod.GET, value = LIST_DATSOURCES_URL)
    public String getView(@PathVariable String newsId, Model model) {
        return String.format(NEWS_TEMPLATE_VIEW, newsId);
    }
}

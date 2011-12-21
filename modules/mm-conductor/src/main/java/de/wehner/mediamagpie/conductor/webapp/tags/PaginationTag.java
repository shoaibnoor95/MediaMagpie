package de.wehner.mediamagpie.conductor.webapp.tags;

import java.io.IOException;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.SimpleTagSupport;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PaginationTag extends SimpleTagSupport {

    private static final long serialVersionUID = 2541733921057845210L;

    private static final Logger LOG = LoggerFactory.getLogger(PaginationTag.class);

    private int _current;
    private int _total;
    private int _pageSize;
    private String _query;

    @Override
    public void doTag() throws JspException {
        final int DISTANCE_FROM_ACTUAL_PAGE = 10;
        final int EDGE_PAGE_LINKS = 3;
        PageContext pageContext = (PageContext) getJspContext();
        JspWriter writer = pageContext.getOut();
        try {
            writer.write("<div class=\"pagination\">");
            writeLink(writer, "link", 0, "&lt;&lt;first");

            int previousPage = _current - _pageSize;
            if (previousPage < 0) {
                previousPage = 0;
            }
            writeLink(writer, "link", previousPage, "&lt;previous");
            int nextPage = _current + _pageSize;
            if (nextPage >= _total) {
                nextPage = _current;
            }
            int numberOfPages = ((_total - 1) / _pageSize) + 1;
            int lastPage = (numberOfPages - 1) * _pageSize;

            int actualPage = ((_current + _pageSize) / _pageSize) - 1;
            int leftMiddlePageStart = Math.max(0, actualPage - DISTANCE_FROM_ACTUAL_PAGE);
            int rightMiddlePageStart = Math.min(numberOfPages, actualPage + DISTANCE_FROM_ACTUAL_PAGE);

            for (int page = 0; page < numberOfPages; page++) {
                boolean drawLink = ((page >= leftMiddlePageStart) && (page <= rightMiddlePageStart));
                if (!drawLink) {
                    // check edge case
                    drawLink = ((page < EDGE_PAGE_LINKS) || (page > (numberOfPages - 1 - EDGE_PAGE_LINKS)));
                }
                if (drawLink) {
                    writeLink(writer, "page", page * _pageSize, "" + (page + 1));
                } else {
                    if (page == EDGE_PAGE_LINKS || page == (numberOfPages - 1 - EDGE_PAGE_LINKS)) {
                        writer.write("&nbsp;...&nbsp;");
                    }
                }
            }

            writeLink(writer, "link", nextPage, "next&gt;");
            writeLink(writer, "link", lastPage, "last&gt;&gt;");
            writer.write("</div>");
        } catch (IOException e) {
            throw new JspException(e);
        }
    }

    private void writeLink(JspWriter writer, String clazz, int index, String name) throws IOException {
        if (index == _current) {
            writer.write("<span class=\"pagination-" + clazz + "-current" + "\">" + name + "</span>&nbsp;");
        } else {
            writer.write("<a class=\"pagination-" + clazz + "\" href=\"" + _query + "?start=" + index);
            writer.write("&amp;hitsPerPage=" + _pageSize);
            writer.write("\">" + name + "</a>&nbsp;");
        }
    }

    public void setCurrent(int current) {
        _current = current;
    }

    public void setTotal(int total) {
        _total = total;
    }

    public void setPageSize(int pageSize) {
        if (pageSize <= 0) {
            LOG.warn("invalid pageSize value '" + pageSize + "' - using 1 instead");
        }
        _pageSize = Math.max(pageSize, 1);
    }

    public void setQuery(String query) {
        _query = query;
    }
}

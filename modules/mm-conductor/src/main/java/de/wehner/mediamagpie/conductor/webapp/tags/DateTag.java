package de.wehner.mediamagpie.conductor.webapp.tags;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.TagSupport;

import org.apache.commons.lang.StringEscapeUtils;

public class DateTag extends TagSupport {

    private static final long serialVersionUID = 1L;

    private Date _date;

    @Override
    public int doStartTag() throws JspException {
        JspWriter writer = pageContext.getOut();

        try {
            writer.write(StringEscapeUtils.escapeHtml(getText()));
        } catch (IOException e) {
            throw new JspException(e);
        }
        return EVAL_BODY_INCLUDE;
    }

    String getText() {
        if (_date == null) {
            return "n.a.";
        }
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(_date);
    }

    public void setDate(Date date) {
        _date = date;
    }
}

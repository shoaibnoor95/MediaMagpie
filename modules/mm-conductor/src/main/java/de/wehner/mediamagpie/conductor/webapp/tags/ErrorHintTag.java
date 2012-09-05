package de.wehner.mediamagpie.conductor.webapp.tags;

import java.io.IOException;

import javax.servlet.jsp.JspContext;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.SimpleTagSupport;

import de.wehner.mediamagpie.common.persistence.entity.properties.RequiredSetupTasks;
import de.wehner.mediamagpie.common.persistence.entity.properties.SetupTask;

public class ErrorHintTag extends SimpleTagSupport {

    private RequiredSetupTasks _requiredSetupTasks;
    private String _mainItemName;
    private String _subItemName;

    @Override
    public void doTag() throws JspException, IOException {
        if (_requiredSetupTasks == null) {
            return;
        }

        /**
         * <img class="error_hint" src="<%=request.getContextPath()%>/static/images/famfamfam_silk/bullet_error.png"/>
         */
        PageContext pageContext = (PageContext) getJspContext();
        JspWriter writer = pageContext.getOut();

        boolean matches = false;

        if (!matches && isMainConfigurationNecessary()) {
            matches = true;
        }
        if (matches) {
            try {
                StringBuilder builder = new StringBuilder("<img class=\"error_hint\" src=\"");
                JspContext jspContext2 = this.getJspContext();
                String contextPath = ((PageContext) jspContext2).getServletContext().getContextPath();
                builder.append(contextPath);
                builder.append("/static/images/famfamfam_silk/bullet_error.png\"/>");
                writer.write(builder.toString());
            } catch (IOException e) {
                throw new JspException(e);
            }
        }
    }

    private boolean isMainConfigurationNecessary() {
        if (_requiredSetupTasks.getSetupTasks().contains(SetupTask.CONFIGURE_SYSTEM_DIRS)) {
            // we need to configure the system directories on main configuration page
            return ("config".equals(_mainItemName) || "config".equals(_subItemName));
        }
        return false;
    }

    public void setMainItemName(String mainItemName) {
        _mainItemName = mainItemName;
    }

    public void setSubItemName(String subItemName) {
        _subItemName = subItemName;
    }

    public void setRequiredSetupTasks(RequiredSetupTasks requiredSetupTasks) {
        _requiredSetupTasks = requiredSetupTasks;
    }

}

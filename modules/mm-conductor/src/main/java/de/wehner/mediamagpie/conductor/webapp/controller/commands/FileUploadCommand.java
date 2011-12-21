package de.wehner.mediamagpie.conductor.webapp.controller.commands;

import org.apache.commons.lang.builder.ReflectionToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
import org.springframework.web.multipart.MultipartFile;

public class FileUploadCommand {

    boolean _showOverwrite;

    boolean _overwrite;

    MultipartFile _file;

    public boolean isShowOverwrite() {
        return _showOverwrite;
    }

    public void setShowOverwrite(boolean showOverwrite) {
        _showOverwrite = showOverwrite;
    }

    public boolean isOverwrite() {
        return _overwrite;
    }

    public void setOverwrite(boolean overwrite) {
        _overwrite = overwrite;
    }

    public MultipartFile getFile() {
        return _file;
    }

    public void setFile(MultipartFile file) {
        _file = file;
    }

    @Override
    public String toString() {
        return ReflectionToStringBuilder.toString(this, ToStringStyle.SIMPLE_STYLE, true, true);
    }

}

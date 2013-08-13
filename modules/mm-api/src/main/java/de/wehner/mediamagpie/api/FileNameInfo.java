package de.wehner.mediamagpie.api;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * encapsulates the file name of media file and media's metadata file.
 */
public class FileNameInfo {

    private final String _nameObject;

    private String _nameMetadata;

    public FileNameInfo(String nameObject, String nameMetadata) {
        super();
        _nameObject = nameObject;
        _nameMetadata = nameMetadata;
    }

    public String getNameObject() {
        return _nameObject;
    }

    public String getNameMetadata() {
        return _nameMetadata;
    }

    public void setNameMetadata(String nameMetadata) {
        _nameMetadata = nameMetadata;
    }

    @Override
    public String toString() {
        return ReflectionToStringBuilder.toString(this, ToStringStyle.SHORT_PREFIX_STYLE);
    }

}

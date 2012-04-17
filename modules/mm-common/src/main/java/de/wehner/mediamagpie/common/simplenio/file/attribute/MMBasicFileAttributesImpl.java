package de.wehner.mediamagpie.common.simplenio.file.attribute;

import org.apache.commons.lang.builder.ReflectionToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

public class MMBasicFileAttributesImpl implements MMBasicFileAttributes {

    private final long _lastModifiedTime;
    private final boolean _isDirectory;
    private final long _size;

    public MMBasicFileAttributesImpl(long lastModifiedTime, boolean isDirectory, long size) {
        super();
        _lastModifiedTime = lastModifiedTime;
        _isDirectory = isDirectory;
        _size = size;
    }

    @Override
    public long lastModifiedTime() {
        return _lastModifiedTime;
    }

    @Override
    public boolean isDirectory() {
        return _isDirectory;
    }

    @Override
    public long size() {
        return _size;
    }

    @Override
    public String toString() {
        return ReflectionToStringBuilder.toString(this, ToStringStyle.SHORT_PREFIX_STYLE);
    }
}

package de.wehner.mediamagpie.common.simplenio.file.attribute;

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

}

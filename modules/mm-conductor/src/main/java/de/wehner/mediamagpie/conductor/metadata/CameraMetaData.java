package de.wehner.mediamagpie.conductor.metadata;

import java.util.Map;
import java.util.TreeMap;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

public class CameraMetaData {

    private Map<String, String> _exifData = new TreeMap<String, String>();

    private Map<String, String> _metaData = new TreeMap<String, String>();

    public Map<String, String> getExifData() {
        return _exifData;
    }

    public void setExifData(Map<String, String> exifData) {
        _exifData = exifData;
    }

    public Map<String, String> getMetaData() {
        return _metaData;
    }

    public void setMetaData(Map<String, String> metaData) {
        _metaData = metaData;
    }

    @Override
    public String toString() {
        return ReflectionToStringBuilder.toString(this, ToStringStyle.SHORT_PREFIX_STYLE);
    }
}

package de.wehner.mediamagpie.conductor.metadata;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

public class VideoMetaData {

    private List<String> _ffmpegLines = new ArrayList<String>();

    /**
     * The duration of this video in milliseconds
     */
    private long _duration;

    public List<String> getFfmpegLines() {
        return _ffmpegLines;
    }

    public void setFfmpegLines(List<String> ffmpegLines) {
        _ffmpegLines = ffmpegLines;
    }

    public long getDuration() {
        return _duration;
    }

    public void setDuration(long duration) {
        _duration = duration;
    }

    @Override
    public String toString() {
        return ReflectionToStringBuilder.toString(this, ToStringStyle.SHORT_PREFIX_STYLE);
    }
}

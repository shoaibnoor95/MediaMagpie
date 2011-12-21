package de.wehner.mediamagpie.conductor.webapp.controller.media.common;

import org.apache.commons.lang.builder.ReflectionToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

public enum UiMediaSortOrder {

    DATE("Date"), ID("Upload order");

    private final String _displayName;

    private UiMediaSortOrder(String displayName) {
        _displayName = displayName;
    }

    public String getDisplayName() {
        return _displayName;
    }

    @Override
    public String toString() {
        return ReflectionToStringBuilder.toString(this, ToStringStyle.SHORT_PREFIX_STYLE);
    }
}

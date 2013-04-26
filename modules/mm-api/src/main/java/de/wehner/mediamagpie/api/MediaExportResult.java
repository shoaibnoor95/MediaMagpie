package de.wehner.mediamagpie.api;

import java.net.URI;

import org.apache.commons.lang3.builder.ToStringBuilder;

public class MediaExportResult {

    public static enum ExportStatus {
        UNDEFINED, SUCCESS, ALREADY_EXPORTED, FAILURE
    };

    private final ExportStatus _exportStatus;

    private final URI _url;

    public MediaExportResult(ExportStatus exportStatus, URI url) {
        super();
        _exportStatus = exportStatus;
        _url = url;
    }

    public ExportStatus getExportStatus() {
        return _exportStatus;
    }

    public URI getUri() {
        return _url;
    }
    
    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }
}

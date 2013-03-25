package de.wehner.mediamagpie.api;

import java.net.URI;
import java.util.List;

import org.apache.commons.lang3.builder.ToStringBuilder;

import de.wehner.mediamagpie.api.util.StringJoiner;

public class MediaExportResults {

    private final List<MediaExportResult> _results;

    public MediaExportResults(List<MediaExportResult> results) {
        super();
        _results = results;
    }

    public List<MediaExportResult> getResults() {
        return _results;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }

    public URI getUri() {
        if (_results.size() > 0) {
            return _results.get(0).getUri();
        }
        return null;
    }

    public String getExportStatus() {
        StringJoiner stringJoiner = new StringJoiner();
        for (MediaExportResult exportResult : _results) {
            stringJoiner.add(exportResult.getUri() + ":" + exportResult.getExportStatus());
        }
        return stringJoiner.toString();
    }

    public boolean detectStatus(MediaExportResult.ExportStatus status) {
        for (MediaExportResult result : _results) {
            if (result.getExportStatus() == status) {
                return true;
            }
        }
        return false;
    }
}

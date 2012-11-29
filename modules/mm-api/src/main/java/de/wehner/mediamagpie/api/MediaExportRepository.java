package de.wehner.mediamagpie.api;

import java.util.Iterator;

public interface MediaExportRepository {

    /** export functionality */

    public abstract void addMedia(String user, MediaExport mediaExport);

    /** import functionality */

    public abstract Iterator<MediaExport> iteratorPhotos(String user);

    public abstract Iterator<MediaExport> iteratorVideos();

}
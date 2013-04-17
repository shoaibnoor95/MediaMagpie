package de.wehner.mediamagpie.api;

import java.util.Iterator;

public interface MediaExportRepository {

    /** export functionality */

    /**
     * Exports a <code>MediaExport</code> object
     * 
     * @param userName
     *            The user's name used for login (equivalent to <code>User.getName()</code>)
     * @param mediaExport
     *            The media to export
     * @return The result of media export
     */
    public abstract MediaExportResults addMedia(String userName, MediaExport mediaExport);

    /** import functionality */

    public abstract Iterator<MediaExport> iteratorPhotos(String user);

    public abstract Iterator<MediaExport> iteratorVideos();

}
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

    /**
     * Builds the storage path for a file. This path is relevant, if you try to delete a media from S3 bucket.
     * 
     * @param userLoginId
     * @param mediaType
     * @param hashValue
     * @param originalFileName
     * @return
     */
    public abstract MediaStorageInfo getMediaStorageInfo(String userLoginId, MediaType mediaType, String hashValue, String originalFileName);

    /**
     * @param bucketName
     *            This is a kind of prefix of path and currently build based on the MediaType
     * @param mediaStoragePath
     *            The path which should be deleted on the cloud
     */
    public abstract void deleteMediaStoragePath(String bucketName, MediaStorageInfo mediaStorageInfo);

    /** import functionality */

    public abstract Iterator<MediaExport> iteratorPhotos(String user);

    public abstract Iterator<MediaExport> iteratorVideos();

}
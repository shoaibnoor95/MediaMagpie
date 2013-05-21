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
     * @param userLoginId
     *            The user's name used for login (equivalent to <code>User.getName()</code>)
     * @param mediaType
     *            The media type (photo or video)
     * @param sha1Hash
     *            The media's hash value
     * @return The path used to store the media and its metadata file on external systems. (EG:
     *         <code>test-user/PHOTO/SHA1-14eed328269944441c66fa362eb461516e203172/</code>)
     */
    public abstract String buildMediaStoragePath(String userLoginId, MediaType mediaType, String sha1Hash);

    /**
     * @param bucketName
     *            This is a kind of prefix of path and currently build based on the MediaType
     * @param mediaStoragePath
     *            The path which should be deleted on the cloud
     */
    public abstract void deleteMediaStoragePath(String bucketName, String mediaStoragePath);

    /** import functionality */

    public abstract Iterator<MediaExport> iteratorPhotos(String user);

    public abstract Iterator<MediaExport> iteratorVideos();

}
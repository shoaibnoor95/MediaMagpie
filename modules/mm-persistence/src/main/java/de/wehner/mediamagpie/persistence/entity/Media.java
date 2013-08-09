package de.wehner.mediamagpie.persistence.entity;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.net.URI;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.builder.ReflectionToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
import org.hibernate.search.annotations.Analyze;
import org.hibernate.search.annotations.Field;
import org.hibernate.search.annotations.Index;
import org.hibernate.search.annotations.Indexed;
import org.hibernate.search.annotations.IndexedEmbedded;
import org.hibernate.search.annotations.Store;

import de.wehner.mediamagpie.api.util.DigestUtil;
import de.wehner.mediamagpie.persistence.realms.AlbumMediaRelation;

// select * from (select uri, count(*) as N from media group by uri order by N desc) as uris where N > 1
@Entity
@Indexed
@EntityListeners({ AlbumMediaRelation.class })
@NamedQueries({
        @NamedQuery(name = "getAllInPathAndUri", query = "select m from Media as m where m._path = :path and m._owner = :owner and m._uri in (:uris)"),
        @NamedQuery(name = "getAllLastAddedPublicMedias", query = "select distinct m from Media as m inner join m._albums as a where (a._visibility <= :visibility) order by m._id desc") })
public class Media extends CreationDateBase {

    /**
     * The name of the media. This field is optional and will be initialized with the file name as default.
     */
    @Field(index = Index.YES, analyze = Analyze.YES, store = Store.NO)
    private String _name;

    @Field(index = Index.YES, analyze = Analyze.YES, store = Store.NO)
    private String _description;

    /** The path in fs were the picture is located. This is only of interest for the path sync jobs! */
    @Column(nullable = false)
    private String _path;

    /**
     * This is the original file name of the media which will be defined by the generating device (eg. your camera)
     */
    private String _originalFileName;

    /** URI of the data location in the file system. */
    @Column(nullable = false, unique = true)
    private String _uri;

    /**
     * The sha1 hash value of media encoded in base 64. (This string will be created with
     * {@linkplain DigestUtil#computeSha1AsHexString(java.io.InputStream)};)
     */
    private String _hashValue;

    @OneToMany(mappedBy = "_media", fetch = FetchType.LAZY, cascade = { CascadeType.REMOVE, CascadeType.PERSIST }, orphanRemoval = true)
    private List<ThumbImage> _thumbImages = new ArrayList<ThumbImage>();

    /**
     * This attribute is only present for automatic removal of assigned ImageResizeJobExecutions when a media is deleted from db.
     */
    @OneToMany(mappedBy = "_media", fetch = FetchType.LAZY, cascade = { CascadeType.REMOVE, CascadeType.PERSIST }, orphanRemoval = true)
    private List<ImageResizeJobExecution> _imageResizeJobExecutions = new ArrayList<ImageResizeJobExecution>();

    @OneToMany(mappedBy = "_media", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @IndexedEmbedded
    private List<MediaTag> _tags = new ArrayList<MediaTag>();

    @ManyToOne(optional = false)
    @JoinColumn(name = "user_fk")
    @IndexedEmbedded
    private User _owner;

    @Field(index = Index.YES, analyze = Analyze.NO, store = Store.NO)
    @Column(nullable = false)
    private LifecyleStatus _lifeCycleStatus = LifecyleStatus.Living;

    @ManyToMany(fetch = FetchType.LAZY, mappedBy = "_medias")
    private List<Album> _albums = new ArrayList<Album>();

    private Orientation _orientation;

    /** an object of <code>CameraMetaData</code> encoded as JSON string */
    @Lob
    @Column(nullable = true, length = 65535)
    private String _cameraMetaData;

    private boolean _exportedToS3 = false;

    public Media() {
        // needed for PersistenceUtil.deleteAll()
    }

    public Media(Media media) {
        super(media.getCreationDate());
        _owner = media._owner;
        _name = media._name;
        _originalFileName = media._originalFileName;
        _description = media._description;
        _path = media._path;
        _uri = media._uri;
        _hashValue = media._hashValue;
        _id = media._id;
        _tags = media._tags;
    }

    public Media(User owner, String name, URI uri, Date creationDate) {
        super(creationDate);
        _owner = owner;
        _name = name;
        if (uri != null) {
            _uri = uri.toString();
            if ("file".equals(uri.getScheme())) {
                File file = new File(uri);
                _originalFileName = file.getName();
                _path = file.getParent();
            }
        }
    }

    /**
     * Creates a new Media and reads the media's binary content to compute the SHA-1 hash value.
     * 
     * @param owner
     * @param name
     *            Can be <code>null</code>
     * @param mediaFileUri
     * @param creationDate
     * @return The new Media instance
     * @throws FileNotFoundException
     */
    public static Media createWithHashValue(User owner, String name, URI mediaFileUri, Date creationDate) throws FileNotFoundException {
        Media newMedia = new Media(owner, name, mediaFileUri, creationDate);
        InputStream is = null;
        try {
            is = new FileInputStream(new File(mediaFileUri));
            newMedia.setHashValue(DigestUtil.computeSha1AsHexString(is));
        } finally {
            IOUtils.closeQuietly(is);
        }
        return newMedia;
    }

    public String getName() {
        return _name;
    }

    public void setName(String name) {
        _name = name;
    }

    public String getDescription() {
        return _description;
    }

    public void setDescription(String description) {
        _description = description;
    }

    public String getPath() {
        return _path;
    }

    public void setPath(String path) {
        _path = path;
    }

    public String getOriginalFileName() {
        return _originalFileName;
    }

    public void setOriginalFileName(String originalFileName) {
        this._originalFileName = originalFileName;
    }

    public String getUri() {
        return _uri;
    }

    public void setUri(String uri) {
        _uri = uri;
    }

    public String getHashValue() {
        return _hashValue;
    }

    public void setHashValue(String hashValue) {
        _hashValue = hashValue;
    }

    public List<ThumbImage> getThumbImages() {
        return _thumbImages;
    }

    public void setThumbImages(List<ThumbImage> thumbImages) {
        _thumbImages = thumbImages;
    }

    public String toString() {
        ReflectionToStringBuilder builder = new ReflectionToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE);
        return builder.toString();
    }

    public String getImageType() {
        String path = URI.create(_uri).getPath();

        return FilenameUtils.getExtension(path);
    }

    /**
     * Provides the media data as <code>File</code>.
     * 
     * @return The media data as file.
     */
    public File getFileFromUri() {
        return new File(URI.create(_uri));
    }

    public List<MediaTag> getTags() {
        return _tags;
    }

    public void setTags(List<MediaTag> tags) {
        for (MediaTag mediaTag : tags) {
            mediaTag.setMedia(this);
        }
        _tags = tags;
    }

    public void addTag(MediaTag tag) {
        tag.setMedia(this);
        _tags.add(tag);
    }

    public void setOwner(User owner) {
        _owner = owner;
    }

    public User getOwner() {
        return _owner;
    }

    public void setLifeCycleStatus(LifecyleStatus lifeCycleStatus) {
        _lifeCycleStatus = lifeCycleStatus;
    }

    public LifecyleStatus getLifeCycleStatus() {
        return _lifeCycleStatus;
    }

    public List<Album> getAlbums() {
        return _albums;
    }

    public void setAlbums(List<Album> albums) {
        _albums = albums;
    }

    public Orientation getOrientation() {
        return _orientation;
    }

    public void setOrientation(Orientation orientation) {
        _orientation = orientation;
    }

    public String getCameraMetaData() {
        return _cameraMetaData;
    }

    public void setCameraMetaData(String cameraMetaData) {
        _cameraMetaData = cameraMetaData;
    }

    public boolean isExportedToS3() {
        return _exportedToS3;
    }

    public void setExportedToS3(boolean exportedToS3) {
        _exportedToS3 = exportedToS3;
    }
}

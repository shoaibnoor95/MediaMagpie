package de.wehner.mediamagpie.common.persistence.entity;

import java.io.File;
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
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang.builder.ReflectionToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
import org.hibernate.search.annotations.Field;
import org.hibernate.search.annotations.Index;
import org.hibernate.search.annotations.Indexed;
import org.hibernate.search.annotations.IndexedEmbedded;
import org.hibernate.search.annotations.Store;

import de.wehner.mediamagpie.common.persistence.realms.AlbumMediaRelation;

// select * from (select uri, count(*) as N from media group by uri order by N desc) as uris where N > 1
@Entity
@Indexed
@EntityListeners({ AlbumMediaRelation.class })
@NamedQueries({
        @NamedQuery(name = "getAllInPathAndUri", query = "select m from Media as m where m._path = :path and m._owner = :owner and m._uri in (:uris)"),
        @NamedQuery(name = "getAllLastAddedPublicMedias", query = "select distinct m from Media as m inner join m._albums as a where (a._visibility <= :visibility) order by m._id desc") })
public class Media extends CreationDateBase {

    @Field(index = Index.TOKENIZED, store = Store.NO)
    // @Field(index = Index.YES, analyze = Analyze.YES, store = Store.NO)
    private String _name;

    @Field(index = Index.TOKENIZED, store = Store.NO)
    // @Field(index = Index.YES, analyze = Analyze.YES, store = Store.NO)
    private String _description;

    /** the path in fs were the picture is located */
    @Column(nullable = false)
    private String _path;

    /** URI of the data location in the file system. */
    @Column(nullable = false, unique = true)
    private String _uri;

    /**
     * The sha1 hash value of media endoced into base 64
     */
    private String _hashValue;

    @OneToMany(mappedBy = "_media", fetch = FetchType.LAZY, cascade = { CascadeType.REMOVE, CascadeType.PERSIST }, orphanRemoval = true)
    private List<ThumbImage> _thumbImages = new ArrayList<ThumbImage>();

    /**
     * This attribute is only present for automatic removal of assigned ImageResizeJobExecutions when a media is deleted from db.
     */
    @SuppressWarnings("unused")
    @OneToMany(mappedBy = "_media", fetch = FetchType.LAZY, cascade = { CascadeType.REMOVE, CascadeType.PERSIST }, orphanRemoval = true)
    private List<ImageResizeJobExecution> _imageResizeJobExecutions = new ArrayList<ImageResizeJobExecution>();

    @OneToMany(mappedBy = "_media", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @IndexedEmbedded
    private List<MediaTag> _tags = new ArrayList<MediaTag>();

    @ManyToOne(optional = false)
    @JoinColumn(name = "user_fk")
    @IndexedEmbedded
    private User _owner;

    @Field(index = Index.UN_TOKENIZED, store = Store.NO)
    // @Field(index = Index.YES, analyze = Analyze.NO, store = Store.NO)
    @Column(nullable = false)
    private LifecyleStatus _lifeCycleStatus = LifecyleStatus.Living;

    @ManyToMany(fetch = FetchType.LAZY, mappedBy = "_medias")
    private List<Album> _albums = new ArrayList<Album>();

    private Orientation _orientation;

    public Media() {
        // needed for PersistenceUtil.deleteAll()
    }

    public Media(Media media) {
        super(media.getCreationDate());
        _owner = media._owner;
        _name = media._name;
        _description = media._description;
        _path = media._path;
        _uri = media._uri;
        _id = media._id;
        _tags = media._tags;
    }

    @Deprecated
    public Media(User owner, String name, String path, String uri, Date creationDate) {
        super(creationDate);
        _owner = owner;
        _name = name;
        _path = path;
        _uri = uri;
    }

    public Media(User owner, String name, URI uri, Date creationDate) {
        super(creationDate);
        _owner = owner;
        _name = name;
        if (uri != null) {
            File fileMedia = new File(uri);
            _path = fileMedia.getParent();
            _uri = uri.toString();
        }
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
}

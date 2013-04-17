package de.wehner.mediamagpie.persistence.entity;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.NotEmpty;

@Entity
public class Album extends CreationDateBase {

    private String _name;

    @ManyToOne(optional = false)
    @JoinColumn(name = "user_fk")
    private User _owner;

    // rwe: don't use the CascadeType.REMOVE!
    @ManyToMany(fetch = FetchType.LAZY, cascade = { CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REFRESH })
    private List<Media> _medias = new ArrayList<Media>();

    private Visibility _visibility = Visibility.PUBLIC;

    @Column(length = 64)
    private String _uid;

    public Album(User owner, String name) {
        super();
        _name = name;
        _owner = owner;
        _uid = UUID.randomUUID().toString();
    }

    public Album() {
    }

    public Album(Album other) {
        _name = other._name;
        _owner = other._owner;
        _medias = other._medias;
        _visibility = other._visibility;
        _uid = other._uid;
    }

    @NotEmpty
    public String getName() {
        return _name;
    }

    public void setName(String name) {
        _name = name;
    }

    public User getOwner() {
        return _owner;
    }

    public void setOwner(User owner) {
        _owner = owner;
    }

    public List<Media> getMedias() {
        return _medias;
    }

    public void addMedia(Media media) {
        _medias.add(media);
    }

    public void setMedias(List<Media> medias) {
        _medias = medias;
    }

    @NotNull
    public Visibility getVisibility() {
        return _visibility;
    }

    public void setVisibility(Visibility visibility) {
        _visibility = visibility;
    }

    public String getUid() {
        return _uid;
    }

    public void setUid(String uid) {
        _uid = uid;
    }
}

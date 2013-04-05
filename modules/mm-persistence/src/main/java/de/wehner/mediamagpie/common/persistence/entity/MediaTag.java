package de.wehner.mediamagpie.common.persistence.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import org.hibernate.search.annotations.Analyze;
import org.hibernate.search.annotations.ContainedIn;
import org.hibernate.search.annotations.Field;
import org.hibernate.search.annotations.Index;
import org.hibernate.search.annotations.Indexed;
import org.hibernate.search.annotations.Store;

@Entity
@Indexed
// @Table(uniqueConstraints = { @UniqueConstraint(columnNames = { "name", "media_fk" }) })
public class MediaTag extends Base {

    @Field(index = Index.YES, analyze = Analyze.NO, store = Store.YES)
    @Column(name = "name", nullable = false)
    private String _name;

    @ContainedIn
    @ManyToOne(optional = false)
    @JoinColumn(name = "media_fk")
    private Media _media;

    public MediaTag() {
    }

    public MediaTag(String name) {
        _name = name;
    }

    public String getName() {
        return _name;
    }

    public void setName(String name) {
        _name = name;
    }

    public Media getMedia() {
        return _media;
    }

    public void setMedia(Media media) {
        _media = media;
    }

}

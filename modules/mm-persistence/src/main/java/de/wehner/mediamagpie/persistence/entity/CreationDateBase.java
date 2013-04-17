package de.wehner.mediamagpie.persistence.entity;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.search.annotations.DateBridge;
import org.hibernate.search.annotations.Field;
import org.hibernate.search.annotations.Resolution;

@MappedSuperclass
public class CreationDateBase extends Base {

    @Field
    @DateBridge(resolution = Resolution.DAY)
    @Temporal(TemporalType.TIMESTAMP)
    @Column(nullable = false)
    private Date _creationDate;

    public CreationDateBase() {
        super();
        _creationDate = new Date();
    }

    public CreationDateBase(Date creationDate) {
        super();
        if (creationDate != null) {
            _creationDate = creationDate;
        } else {
            _creationDate = new Date();
        }
    }

    public Date getCreationDate() {
        return _creationDate;
    }

    public void setCreationDate(Date creationDate) {
        _creationDate = creationDate;
    }
}

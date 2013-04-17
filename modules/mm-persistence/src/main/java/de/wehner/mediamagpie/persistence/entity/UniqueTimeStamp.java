package de.wehner.mediamagpie.persistence.entity;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

@Deprecated
@MappedSuperclass
public class UniqueTimeStamp extends UniqueBaseName {

    @Temporal(TemporalType.TIMESTAMP)
    @Column(nullable = false)
    private Date _creationDate;

    public UniqueTimeStamp() {
        this(null);
    }

    public UniqueTimeStamp(String name) {
        super(name);
        _creationDate = new Date();
    }

    public Date getCreationDate() {
        return _creationDate;
    }

}

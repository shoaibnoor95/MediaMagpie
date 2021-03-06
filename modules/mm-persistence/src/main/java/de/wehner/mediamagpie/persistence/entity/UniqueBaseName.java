package de.wehner.mediamagpie.persistence.entity;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;

@MappedSuperclass
public class UniqueBaseName extends Base {

    @Column(unique = true, nullable = false)
    protected String _name;

    public UniqueBaseName() {
    }

    public UniqueBaseName(String name) {
        _name = name;
    }

    public String getName() {
        return _name;
    }

    public void setName(String name) {
        _name = name;
    }

}

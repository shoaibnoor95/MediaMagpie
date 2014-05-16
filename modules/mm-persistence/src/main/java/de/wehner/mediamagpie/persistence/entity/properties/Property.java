package de.wehner.mediamagpie.persistence.entity.properties;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import de.wehner.mediamagpie.persistence.entity.Base;


@Entity
// rwe: Unfortunately this constrain doesn't work if we use non-user Properties which uses <null> as user_fk.
@Table(uniqueConstraints = { @UniqueConstraint(columnNames = { "NAME", "USER_FK" }) })
public class Property extends Base {

    @Column(nullable = false, name = "name")
    private String _name;

    @Column(nullable = false)
    private String _value;

    public Property() {
        // for persistence
    }

    public Property(String name, String value) {
        _name = name;
        _value = value;
    }

    public void setName(String name) {
        _name = name;
    }

    public String getName() {
        return _name;
    }

    public void setValue(String value) {
        _value = value;
    }

    public String getValue() {
        return _value;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + ((_name == null) ? 0 : _name.hashCode());
        result = prime * result + ((_value == null) ? 0 : _value.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (!super.equals(obj))
            return false;
        if (getClass() != obj.getClass())
            return false;
        Property other = (Property) obj;
        if (_name == null) {
            if (other._name != null)
                return false;
        } else if (!_name.equals(other._name))
            return false;
        if (_value == null) {
            if (other._value != null)
                return false;
        } else if (!_value.equals(other._value))
            return false;
        return true;
    }

    @Override
    public String toString() {
        return super.toString() + ":" + getName() + "=" + getValue();
    }
}

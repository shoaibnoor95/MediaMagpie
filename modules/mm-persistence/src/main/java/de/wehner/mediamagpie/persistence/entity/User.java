package de.wehner.mediamagpie.persistence.entity;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToMany;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import de.wehner.mediamagpie.persistence.entity.properties.Property;

@Entity
@EntityListeners({ UserGroupRelation.class })
@NamedQueries({ @NamedQuery(name = "getUserLikeName", query = "select u from User as u where LOWER(u._name) like :name"),
        @NamedQuery(name = "getUserByEmail", query = "select u from User as u where LOWER(u._email) like :email") })
public class User extends UniqueBaseName implements UserDetails {

    private static final long serialVersionUID = 1L;

    static class UserGrantedAuthority implements GrantedAuthority {

        private static final long serialVersionUID = 1L;

        private final String _authority;

        public UserGrantedAuthority(String authority) {
            super();
            _authority = authority;
        }

        @Override
        public String getAuthority() {
            return _authority;
        }
    }

    public enum Role {
        GUEST("GUEST"), USER("USER"), ADMIN("ADMIN");

        private final String _name;

        private Role(String name) {
            _name = name;
        }

        public String getName() {
            return _name;
        }
    }

    private Role _role;

    /**
     * The groups are just to organize users, e.g. to find all users of a special company. Currently they are not used.
     */
    @ManyToMany(fetch = FetchType.LAZY, mappedBy = "_users")
    private List<UserGroup> _groups = new ArrayList<UserGroup>();

    @Column(nullable = false)
    private String _email;

    private String _password;

    private String _forename;

    private String _surname;

    /**
     * Holds properties to user specific settings (configurations)
     */
    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "user_fk")
    Set<Property> _settings = new HashSet<Property>();

    public User() {
    }

    public User(String name, String email, Role role) {
        _name = name;
        _email = email;
        _role = role;
    }

    public void setRole(Role role) {
        _role = role;
    }

    public Role getRole() {
        return _role;
    }

    public void setGroups(List<UserGroup> groups) {
        _groups = groups;
    }

    public List<UserGroup> getGroups() {
        return _groups;
    }

    public String getEmail() {
        return _email;
    }

    public void setEmail(String email) {
        _email = email;
    }

    public void setPassword(String password) {
        _password = password;
    }

    public String getPassword() {
        return _password;
    }

    public void setForename(String forename) {
        _forename = forename;
    }

    public String getForename() {
        return _forename;
    }

    public void setSurname(String surname) {
        _surname = surname;
    }

    public String getSurname() {
        return _surname;
    }

    @Override
    public Collection<GrantedAuthority> getAuthorities() {
        Collection<GrantedAuthority> ret = new ArrayList<GrantedAuthority>();
        ret.add(new UserGrantedAuthority("ROLE_" + getRole()));
        return ret;
    }

    @Override
    public String getUsername() {
        return getName();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    public void addSetting(Property property) {
        _settings.add(property);
    }

    public Set<Property> getSettings() {
        return _settings;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }
}

package de.wehner.mediamagpie.common.persistence.entity;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;

@Entity
public class UserGroup extends UniqueBaseName {

    @SuppressWarnings("unused")
    private UserGroup() {
    }

    public UserGroup(String name) {
        _name = name;
    }

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "usergroup_user", joinColumns = @JoinColumn(name = "group_id"), inverseJoinColumns = @JoinColumn(name = "user_id"))
    private List<User> _users = new ArrayList<User>();

    public void setUsers(List<User> users) {
        _users = users;
    }

    public List<User> getUsers() {
        return _users;
    }

    public void addUser(User user) {
        _users.add(user);
    }

}

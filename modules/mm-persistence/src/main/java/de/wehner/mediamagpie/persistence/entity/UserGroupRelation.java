package de.wehner.mediamagpie.persistence.entity;

import java.util.List;

import javax.persistence.PreRemove;

public class UserGroupRelation {

    @PreRemove
    public void removeUserFromGroup(Object object) {
        User user = (User) object;
        List<UserGroup> groups = user.getGroups();
        for (UserGroup userGroup : groups) {
            userGroup.getUsers().remove(user);
        }
    }
}

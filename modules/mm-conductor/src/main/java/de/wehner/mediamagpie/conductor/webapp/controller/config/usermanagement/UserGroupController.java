package de.wehner.mediamagpie.conductor.webapp.controller.config.usermanagement;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.criterion.Order;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttributes;

import de.wehner.mediamagpie.common.persistence.dao.UserDao;
import de.wehner.mediamagpie.common.persistence.entity.User;
import de.wehner.mediamagpie.common.persistence.entity.UserGroup;
import de.wehner.mediamagpie.conductor.persistence.dao.UserGroupDao;
import de.wehner.mediamagpie.conductor.webapp.binder.EntityCollectionEditor;
import de.wehner.mediamagpie.conductor.webapp.controller.commands.UserGroupCommand;


/**
 * @deprecated
 * TODO rwe: currently not used, maybe in admin's user administration
 */
@Controller
@SessionAttributes(value = { "groupCommand" })
public class UserGroupController {

    public static final String LIST_GROUPS_URL = "/admin/groups/listGroups";
    private static final String LIST_GROUPS_VIEW = "/admin/config/usermanagement/listGroups";
    private static final String DELETE_GROUP_URL = "/admin/groups/deleteGroup";
    private static final String LIST_GROUP_MEMBERS_URL = "/admin/group/members/(*:groupId)";
    private static final String LIST_GROUP_MEMBERS_VIEW = "/admin/config/usermanagement/groupMembers";
    private static final String ADD_MEMBERS_URL = "/admin/group/addMembers";
    private static final String ADD_MEMBERS_VIEW = "/admin/config/usermanagement/addGroupMembers";
    private static final String DELETE_MEMBER_URL = "/admin/group/deleteMember/(*:groupId)";
    private static final String GROUP_SUMMARY_URL = "/admin/group/summary/(*:groupId)";
    private static final String GROUP_SUMMARY_VIEW = "/admin/config/usermanagement/groupSummary";
    private static final String ADD_GROUP_URL = "/admin/group/add";
    private static final String EDIT_GROUP_URL = "/admin/group/edit";
    private static final String ADD_GROUP_VIEW = "/admin/config/usermanagement/addGroup";

    private final UserGroupDao _groupDao;
    private final UserDao _userDao;

    @Autowired
    public UserGroupController(UserGroupDao groupDao, UserDao userDao) {
        _groupDao = groupDao;
        _userDao = userDao;
    }

    @InitBinder
    public void initBinder(WebDataBinder binder) {
        binder.registerCustomEditor(List.class, "users", new EntityCollectionEditor<User>(_userDao));
    }

    // ALL / SEARCH USER
    @RequestMapping(method = RequestMethod.GET, value = "/allUsers")
    public String allUsers(@ModelAttribute("groupCommand") UserGroupCommand groupCommand, Model model) {
        List<User> allUsers = _userDao.getAll();
        List<User> usersFromGroup = loadGroup(groupCommand).getUsers();
        List<User> newUsers = extractUsersToAdd(allUsers, usersFromGroup);
        newUsers.removeAll(groupCommand.getUsers());
        groupCommand.getUsers().clear();
        model.addAttribute("users", newUsers);
        return "/admin/config/users/allUsers";
    }

    @RequestMapping(method = RequestMethod.POST, value = "/allUsers")
    public String submitAllUsers(@ModelAttribute("groupCommand") UserGroupCommand groupCommand, Model model) {
        return "redirect:" + ADD_MEMBERS_URL;
    }

    @RequestMapping(method = RequestMethod.GET, value = "/searchUsers")
    public String searchMember(@RequestParam(value = "name", required = true) String name, @ModelAttribute("groupCommand") UserGroupCommand groupCommand, Model model) {
        List<User> allUsers = _userDao.getUserLikeName(name);
        List<User> usersFromGroup = loadGroup(groupCommand).getUsers();
        List<User> newUsers = extractUsersToAdd(allUsers, usersFromGroup);
        newUsers.removeAll(groupCommand.getUsers());
        groupCommand.getUsers().clear();
        model.addAttribute("users", newUsers);
        return "/admin/json/users";
    }

    private List<User> extractUsersToAdd(List<User> allUsers, List<User> usersFromGroup) {
        List<User> newUsers = new ArrayList<User>();
        for (User user : allUsers) {
            if (!usersFromGroup.contains(user)) {
                newUsers.add(user);
            }
        }
        return newUsers;
    }

    @RequestMapping(method = RequestMethod.POST, value = "/searchUsers")
    public String submitSearchUsers(@ModelAttribute("groupCommand") UserGroupCommand groupCommand, Model model) {
        return "redirect:" + ADD_MEMBERS_URL;
    }

    // ADD MEMBER SECTION
    @RequestMapping(method = RequestMethod.GET, value = ADD_MEMBERS_URL)
    public String showAddMembers(@ModelAttribute("groupCommand") UserGroupCommand groupCommand, Model model) {
        return ADD_MEMBERS_VIEW;
    }

    @RequestMapping(method = RequestMethod.POST, value = ADD_MEMBERS_URL)
    public String submitAddMembers(@ModelAttribute("groupCommand") UserGroupCommand groupCommand) {
        UserGroup group = loadGroup(groupCommand);
        List<User> oldUsers = group.getUsers();
        List<User> newUsers = groupCommand.getUsers();
        for (User newUser : newUsers) {
            if (!oldUsers.contains(newUser)) {
                group.addUser(newUser);
            }
        }
        saveGroup(group);
        return "redirect:" + LIST_GROUP_MEMBERS_URL.replace("(*:groupId)", group.getId() + "");
    }

    // DELETE MEMBER SECTION
    @RequestMapping(method = RequestMethod.POST, value = DELETE_MEMBER_URL)
    public String deleteMember(@RequestParam(required = true) final long groupId, @RequestParam(required = true) final Long userId) {
        UserGroup group = _groupDao.getById(groupId);
        User user = _userDao.getById(userId);
        group.getUsers().remove(user);
        return "redirect:" + LIST_GROUP_MEMBERS_URL.replace("(*:groupId)", groupId + "");
    }

    @RequestMapping(method = RequestMethod.GET, value = GROUP_SUMMARY_URL)
    public String showGroupSummary(@RequestParam(required = true) final long groupId, ModelMap model) {
        UserGroup group = _groupDao.getById(groupId);
        model.addAttribute("group", group);
        return GROUP_SUMMARY_VIEW;
    }

    @RequestMapping(method = RequestMethod.GET, value = LIST_GROUP_MEMBERS_URL)
    public String showGroupMembers(@RequestParam(required = true) final long groupId, Model model) {
        UserGroup group = _groupDao.getById(groupId);
        // initial create group command that we can fill with users in searchUsers/allUsers/addUser
        // view
        UserGroupCommand groupCommand = new UserGroupCommand(group.getName());
        groupCommand.setId(group.getId());
        model.addAttribute("groupCommand", groupCommand);
        model.addAttribute("users", group.getUsers());

        return LIST_GROUP_MEMBERS_VIEW;
    }

    // ADD / EDIT /LIST / DELETE GROUP SECTION
    @RequestMapping(method = RequestMethod.GET, value = EDIT_GROUP_URL)
    public String showEditGroup(@RequestParam(required = true) final long id, ModelMap model) {
        UserGroup group = _groupDao.getById(id);
        UserGroupCommand userGroupCommand = new UserGroupCommand(group.getName());
        userGroupCommand.setId(group.getId());
        model.addAttribute("groupCommand", userGroupCommand);
        return ADD_GROUP_VIEW;
    }

    @RequestMapping(method = RequestMethod.GET, value = ADD_GROUP_URL)
    public String showAddGroup(Model model) {
        model.addAttribute("groupCommand", new UserGroupCommand(null));
        return ADD_GROUP_VIEW;
    }

    @RequestMapping(method = RequestMethod.POST, value = { ADD_GROUP_URL, EDIT_GROUP_URL })
    public String submitAddGroup(@ModelAttribute("groupCommand") UserGroupCommand groupCommand) {
        UserGroup group = loadGroup(groupCommand);
        group.setName(groupCommand.getName());
        saveGroup(group);
        return "redirect:" + UserGroupController.LIST_GROUPS_URL;
    }

    @RequestMapping(method = RequestMethod.GET, value = LIST_GROUPS_URL)
    public String listGroups(ModelMap model) {
        List<UserGroup> groups = _groupDao.getAll(Order.asc("_name"), 0, Integer.MAX_VALUE);
        model.addAttribute("groups", groups);
        return LIST_GROUPS_VIEW;
    }

    @RequestMapping(method = RequestMethod.POST, value = DELETE_GROUP_URL)
    public String deleteGroup(@RequestParam(required = true) final long groupId) {
        UserGroup group = _groupDao.getById(groupId);
        _groupDao.makeTransient(group);
        return "redirect:" + LIST_GROUPS_URL;
    }

    // load save entity
    private UserGroup loadGroup(UserGroupCommand groupCommand) {
        Long id = groupCommand.getId();
        UserGroup group = new UserGroup(null);
        if (id != null) {
            group = _groupDao.getById(id);
        }
        return group;
    }

    private void saveGroup(UserGroup group) {
        if (group.getId() == null) {
            _groupDao.makePersistent(group);
        }
    }
}

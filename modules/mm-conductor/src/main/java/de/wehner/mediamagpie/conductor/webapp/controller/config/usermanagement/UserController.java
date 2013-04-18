package de.wehner.mediamagpie.conductor.webapp.controller.config.usermanagement;

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

import de.wehner.mediamagpie.conductor.webapp.binder.EntityCollectionEditor;
import de.wehner.mediamagpie.conductor.webapp.controller.commands.UserCommand;
import de.wehner.mediamagpie.persistence.dao.UserDao;
import de.wehner.mediamagpie.persistence.dao.UserGroupDao;
import de.wehner.mediamagpie.persistence.entity.User;
import de.wehner.mediamagpie.persistence.entity.UserGroup;
import de.wehner.mediamagpie.persistence.entity.User.Role;



/**
 * @deprecated
 * TODO rwe: currently not used, maybe in admin's user administration
 */
@Controller
public class UserController {

    public static final String LIST_USERS_URL = "/admin/users/listUsers";
    public static final String LIST_USERS_VIEW = "/admin/config/usermanagement/listusers";
    public static final String DELETE_USER_URL = "/admin/users/deleteUser";
    public static final String USER_SUMMARY_URL = "/admin/user/summary/(*:userId)";
    public static final String USER_SUMMARY_VIEW = "/admin/config/usermanagement/userSummary";
    public static final String ADD_USER_URL = "/admin/users/add";
    public static final String EDIT_USER_URL = "/admin/users/edit";
    public static final String ADD_USER_VIEW = "/admin/config/usermanagement/addUser";

    private final UserDao _userDao;
    private final UserGroupDao _groupDao;

    @Autowired
    public UserController(UserDao userDao, UserGroupDao groupDao) {
        _userDao = userDao;
        _groupDao = groupDao;
    }

    @InitBinder
    public void initBinder(WebDataBinder binder) {
        binder.registerCustomEditor(List.class, "groups", new EntityCollectionEditor<UserGroup>(_groupDao));
    }

    @RequestMapping(method = RequestMethod.GET, value = LIST_USERS_URL)
    public String listUsers(ModelMap model) {
        List<User> users = _userDao.getAll(Order.asc("_name"), 0, Integer.MAX_VALUE);
        model.addAttribute("users", users);
        return LIST_USERS_VIEW;
    }

    @RequestMapping(method = RequestMethod.POST, value = DELETE_USER_URL)
    public String deleteUser(@RequestParam(required = true) final long id) {
        User user = _userDao.getById(id);
        _userDao.makeTransient(user);
        return "redirect:" + LIST_USERS_URL;
    }

    @RequestMapping(method = RequestMethod.GET, value = USER_SUMMARY_URL)
    public String showUserSummary(@RequestParam(required = true) final long userId, ModelMap model) {
        User user = _userDao.getById(userId);
        model.addAttribute("user", user);
        return USER_SUMMARY_VIEW;
    }

    @RequestMapping(method = RequestMethod.GET, value = ADD_USER_URL)
    public String showAddUser(Model model) {
        model.addAttribute("userCommand", new UserCommand(null, null, null));
        model.addAttribute("roles", Role.values());
        model.addAttribute("nonMemberGroups", _groupDao.getAll());
        return ADD_USER_VIEW;
    }

    @RequestMapping(method = RequestMethod.GET, value = EDIT_USER_URL)
    public String showEditUser(@RequestParam(required = true) final long id, Model model) {
        User user = _userDao.getById(id);
        UserCommand userCommand = new UserCommand(user.getName(), user.getEmail(), user.getRole());
        userCommand.setId(user.getId());
        userCommand.setPassword(user.getPassword());
        userCommand.setPasswordConfirm(user.getPassword());
        userCommand.setGroups(user.getGroups());
        model.addAttribute("userCommand", userCommand);
        model.addAttribute("roles", Role.values());
        List<UserGroup> nonMemberGroups = _groupDao.getAll();
        List<UserGroup> memberGroups = user.getGroups();
        for (UserGroup memberGroup : memberGroups) {
            if (nonMemberGroups.contains(memberGroup)) {
                nonMemberGroups.remove(memberGroup);
            }
        }
        model.addAttribute("nonMemberGroups", nonMemberGroups);
        model.addAttribute("memberGroups", user.getGroups());
        return ADD_USER_VIEW;
    }

    @RequestMapping(method = RequestMethod.POST, value = { ADD_USER_URL, EDIT_USER_URL })
    public String submitAddUser(@ModelAttribute("userCommand") UserCommand userCommand) {
        User user = loadUser(userCommand);
        user.setEmail(userCommand.getEmail());
        user.setName(userCommand.getName());
        user.setPassword(userCommand.getPassword());
        user.setRole(userCommand.getRole());

        List<UserGroup> oldGroups = user.getGroups();
        List<UserGroup> newGroups = userCommand.getGroups();

        // remove from group
        for (UserGroup oldGroup : oldGroups) {
            if (!newGroups.contains(oldGroup)) {
                oldGroup.getUsers().remove(user);
            }
        }
        // add to group
        for (UserGroup newGroup : newGroups) {
            if (!oldGroups.contains(newGroup)) {
                newGroup.getUsers().add(user);
            }
        }

        user.setGroups(userCommand.getGroups());
        saveUser(user);
        return "redirect:" + UserController.LIST_USERS_URL;
    }

    private void saveUser(User user) {
        if (user.getId() == null) {
            _userDao.makePersistent(user);
        }
    }

    private User loadUser(UserCommand userCommand) {
        Long id = userCommand.getId();
        User user = new User();
        if (id != null) {
            user = _userDao.getById(userCommand.getId());
        }
        return user;
    }
}

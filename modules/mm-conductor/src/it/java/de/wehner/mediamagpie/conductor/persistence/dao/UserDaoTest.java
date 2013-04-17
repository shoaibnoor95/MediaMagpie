package de.wehner.mediamagpie.conductor.persistence.dao;

import static org.junit.Assert.*;

import static org.fest.assertions.Assertions.*;

import java.util.List;

import org.junit.Test;

import de.wehner.mediamagpie.persistence.PersistenceService;
import de.wehner.mediamagpie.persistence.UserDao;
import de.wehner.mediamagpie.persistence.entity.User;
import de.wehner.mediamagpie.persistence.entity.UserGroup;
import de.wehner.mediamagpie.persistence.entity.User.Role;
import de.wehner.mediamagpie.persistence.entity.properties.Property;

public class UserDaoTest extends AbstractDaoTest<UserDao> {

    // @BeforeClass
    // public static void setUpClass() {
    // System.setProperty("db.mode", "mysql-it");
    // }

    @Override
    protected UserDao createDao(PersistenceService persistenceService) {
        return new UserDao(persistenceService);
    }

    @Test
    public void testGetById() {
        User user = new User("rwe", "r.wehner@mailing.com", Role.USER);
        getDao().makePersistent(user);
        _persistenceService.flipTransaction();

        User userFromDb = getDao().getById(user.getId());

        assertThat(userFromDb.getName()).isEqualTo(user.getName());
        assertThat(userFromDb.getEmail()).isEqualTo(user.getEmail());
    }

    @Test
    public void testGetByName() {
        User user = new User("", "", Role.USER);
        String name = "user";
        user.setName(name);

        getDao().makePersistent(user);
        _persistenceService.flipTransaction();

        assertNull(getDao().getByName("noUser"));

        User byName = getDao().getByName(name);
        assertNotNull(byName);
        assertEquals(name, byName.getName());
    }

    @Test
    public void testGetByEmail() {
        User user = new User("name", "email@localhost", Role.USER);
        getDao().makePersistent(user);
        _persistenceService.flipTransaction();

        User userFound = getDao().getByEmail("EMAIL@localhost");
        assertThat(userFound).isEqualTo(user);
    }

    @Test
    public void testExists() {
        User user = new User("", "", Role.USER);
        String name = "user";
        user.setName(name);

        getDao().makePersistent(user);
        _persistenceService.flipTransaction();

        assertTrue(getDao().exists(name));
        assertFalse(getDao().exists("noUser"));
    }

    @Test
    public void testGetAll() {
        List<User> allUsers = getDao().getAll();
        assertEquals(0, allUsers.size());

        String name1 = "user1";
        User user1 = new User("", "", Role.USER);
        user1.setName(name1);

        getDao().makePersistent(user1);

        allUsers = getDao().getAll();
        assertEquals(1, allUsers.size());
        assertEquals(user1, allUsers.get(0));

        String name2 = "user2";
        User user2 = new User("", "", Role.USER);
        user2.setName(name2);

        getDao().makePersistent(user2);

        _persistenceService.flipTransaction();

        allUsers = getDao().getAll();
        assertEquals(2, allUsers.size());
        assertTrue(allUsers.contains(user1));
        assertTrue(allUsers.contains(user2));
    }

    @Test
    public void testAddGroup() throws Exception {
        // create user
        User user = new User("foo", "email", Role.USER);
        UserDao dao = getDao();
        dao.makePersistent(user);
        _persistenceService.flipTransaction();
        // create group
        UserGroup userGroup = new UserGroup("group");
        _persistenceService.persist(userGroup);
        _persistenceService.flipTransaction();
        // create user-group relation
        user = dao.getById(user.getId());
        userGroup = _persistenceService.getById(UserGroup.class, userGroup.getId());
        assertNotNull(userGroup);
        assertNotNull(user);
        user.getGroups().add(userGroup);
        _persistenceService.flipTransaction();
        // test relation - THIS SHOULD RETURN ZERO OBJECTS - because only the owner side
        // is
        // responsible for the update!!! The owner side is the UserGroup
        assertEquals(0, _persistenceService.getById(UserGroup.class, userGroup.getId()).getUsers().size());
        user = dao.getById(user.getId());
        List<UserGroup> groups = user.getGroups();
        assertEquals(0, groups.size());
    }

    @Test
    public void testDelete_UserThatIsAssignedToAGroup() throws Exception {
        // create user
        User user = new User("foo", "email", Role.USER);
        UserDao userDao = getDao();
        userDao.makePersistent(user);
        _persistenceService.flipTransaction();
        // create group
        UserGroup userGroup = new UserGroup("group");
        _persistenceService.persist(userGroup);
        _persistenceService.flipTransaction();
        // create user-group relation
        user = userDao.getById(user.getId());
        userGroup = _persistenceService.getById(UserGroup.class, userGroup.getId());
        userGroup.addUser(user);
        _persistenceService.flipTransaction();
        assertEquals(1, _persistenceService.getById(UserGroup.class, userGroup.getId()).getUsers().size());
        user = userDao.getById(user.getId());
        List<UserGroup> groups = user.getGroups();
        assertEquals(1, groups.size());
        _persistenceService.flipTransaction();
        // delete user
        user = userDao.getById(user.getId());
        userDao.makeTransient(user);
        _persistenceService.flipTransaction();
        assertEquals(0, userDao.getAll().size());
        assertEquals(1, _persistenceService.getAll(UserGroup.class).size());
        assertEquals(0, _persistenceService.getById(UserGroup.class, userGroup.getId()).getUsers().size());
    }

    @Test
    public void testSearchUserByName() throws Exception {
        UserDao dao = getDao();
        dao.makePersistent(new User("foo", "email", Role.USER));
        dao.makePersistent(new User("foobar", "email", Role.USER));
        dao.makePersistent(new User("bar", "email", Role.USER));
        dao.makePersistent(new User("meEr", "email", Role.USER));
        _persistenceService.flipTransaction();
        List<User> users = dao.getUserLikeName("foo");
        assertEquals(2, users.size());
        assertTrue(users.get(0).getName().contains("foo"));
        assertTrue(users.get(1).getName().contains("foo"));
        users = dao.getUserLikeName("bar");
        assertEquals(1, users.size());
        assertTrue(users.get(0).getName().contains("bar"));
        users = dao.getUserLikeName("mee");
        assertEquals(1, users.size());
        assertTrue(users.get(0).getName().contains("meEr"));
    }

    @Test
    public void testDelete_VerifyOrphanedPropertiesAreDeletedToo() throws Exception {
        // create user with properties
        User user = new User("foo", "email", Role.USER);
        Property property = new Property("user.media.directories", "/home/foo/medias");
        user.addSetting(property);
        UserDao userDao = getDao();
        userDao.makePersistent(user);
        _persistenceService.flipTransaction();

        // verify property was stored well
        user = userDao.getById(user.getId());
        System.out.println(user.toString());
        assertThat(user.getSettings()).contains(property);
        assertThat(_persistenceService.getAll(Property.class)).hasSize(1);

        // delete user
        userDao.makeTransient(user);
        _persistenceService.flipTransaction();

        // verify, user and property is deleted
        assertEquals(0, userDao.getAll().size());
        assertEquals(0, _persistenceService.getAll(Property.class).size());
    }
}

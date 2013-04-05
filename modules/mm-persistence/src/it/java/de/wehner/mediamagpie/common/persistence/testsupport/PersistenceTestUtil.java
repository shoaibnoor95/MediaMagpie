package de.wehner.mediamagpie.common.persistence.testsupport;

import java.util.List;

import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import de.wehner.mediamagpie.common.persistence.dao.Dao;
import de.wehner.mediamagpie.common.persistence.entity.Album;
import de.wehner.mediamagpie.common.persistence.entity.Base;
import de.wehner.mediamagpie.common.persistence.entity.ImageResizeJobExecution;
import de.wehner.mediamagpie.common.persistence.entity.Media;
import de.wehner.mediamagpie.common.persistence.entity.MediaDeleteJobExecution;
import de.wehner.mediamagpie.common.persistence.entity.MediaTag;
import de.wehner.mediamagpie.common.persistence.entity.ThumbImage;
import de.wehner.mediamagpie.common.persistence.entity.User;
import de.wehner.mediamagpie.common.persistence.entity.UserGroup;
import de.wehner.mediamagpie.common.persistence.entity.properties.Property;
import de.wehner.mediamagpie.persistence.PersistenceService;

public class PersistenceTestUtil {

    public static EntityManagerFactory createEntityManagerFactory() {
        // LocalEntityManagerFactoryBean factoryBean = new org.springframework.orm.jpa.LocalEntityManagerFactoryBean();
        // factoryBean.setPersistenceUnitName(dbMode);
        // factoryBean.afterPropertiesSet();
        // EntityManagerFactory nativeEntityManagerFactory = factoryBean.getNativeEntityManagerFactory();
        // return nativeEntityManagerFactory;
        return Persistence.createEntityManagerFactory("hsql-memory-withoutDS");
    }

    @Deprecated
    public static <T extends Base> void deleteAllEntities(Dao<T> dao) {
        dao.getPersistenceService().beginTransaction();
        List<T> all = dao.getAll();
        for (T t : all) {
            dao.makeTransient(t);
        }
        dao.getPersistenceService().commitTransaction();
    }

    public static <T extends Base> void deleteAllEntities(PersistenceService persistenceService, Class<T> clazz) {
        persistenceService.beginTransaction();
        List<T> all = persistenceService.getAll(clazz);
        for (T t : all) {
            persistenceService.remove(t);
        }
        persistenceService.commitTransaction();
    }

    public static void deleteAll(PersistenceService persistenceService) {
        PersistenceTestUtil.deleteAllEntities(persistenceService, Property.class);
        PersistenceTestUtil.deleteAllEntities(persistenceService, ImageResizeJobExecution.class);
        PersistenceTestUtil.deleteAllEntities(persistenceService, MediaDeleteJobExecution.class);
        PersistenceTestUtil.deleteAllEntities(persistenceService, ThumbImage.class);
        PersistenceTestUtil.deleteAllEntities(persistenceService, Media.class);
        PersistenceTestUtil.deleteAllEntities(persistenceService, MediaTag.class);
        PersistenceTestUtil.deleteAllEntities(persistenceService, Album.class);
        PersistenceTestUtil.deleteAllEntities(persistenceService, UserGroup.class);
        PersistenceTestUtil.deleteAllEntities(persistenceService, User.class);
    }
}

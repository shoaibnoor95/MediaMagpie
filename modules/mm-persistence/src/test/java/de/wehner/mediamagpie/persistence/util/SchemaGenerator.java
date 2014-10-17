package de.wehner.mediamagpie.persistence.util;

import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.Entity;

import org.hibernate.cfg.Configuration;
import org.hibernate.tool.hbm2ddl.SchemaExport;
import org.springframework.core.type.classreading.MetadataReader;

import de.wehner.mediamagpie.core.util.ClassPathUtil;
import de.wehner.mediamagpie.core.util.ClassPathUtil.CandidateCheck;
import de.wehner.mediamagpie.persistence.DatabaseNamingStrategy;

/**
 * Helper class to generate the sql-scripts to setup an initial database.
 * <p>
 * Because, we currently use the property <code>"hibernate.hbm2ddl.auto"="update"</code> it is not necessary to use flyway to run
 * migration/setup scripts for now.
 * </p>
 * 
 * @author Ralf Wehner
 *
 */
public class SchemaGenerator {

    private Configuration cfg;

    public SchemaGenerator(String packageName, Class<?>... classes) throws Exception {
        cfg = new Configuration();

        cfg.setProperty("hibernate.hbm2ddl.auto", "create");
        cfg.setNamingStrategy(new DatabaseNamingStrategy());
        Set<Class<?>> classes2Load = ((classes.length > 0) ? new HashSet<Class<?>>(Arrays.asList(classes)) : null);

        List<Class<?>> allEntities = ClassPathUtil.resolveAllClasses(packageName, new CandidateCheck() {

            @Override
            public boolean isCandidate(MetadataReader metadataReader) {
                try {
                    Class<?> c = Class.forName(metadataReader.getClassMetadata().getClassName());
                    if (c.getAnnotation(Entity.class) != null && !Modifier.isAbstract(c.getModifiers())) {
                        return true;
                    }
                } catch (Throwable e) {
                }
                return false;
            }
        });

        for (Class<?> clazz : allEntities) {
            if (classes2Load == null || classes2Load.contains(clazz)) {
                cfg.addAnnotatedClass(clazz);
            }
        }
    }

    /**
     * Method that actually creates the file.
     * 
     * @param dbDialect
     *            to use
     */
    void generate(Dialect dialect) {
        cfg.setProperty("hibernate.dialect", dialect.getDialectClass());

        SchemaExport export = new SchemaExport(cfg);
        export.setDelimiter(";");
        export.setOutputFile("target/ddl_" + dialect.name().toLowerCase() + ".sql");
        export.execute(true, false, false, false);
    }

    /**
     * @param args
     */
    public static void main(String[] args) throws Exception {
        SchemaGenerator gen = new SchemaGenerator("de.wehner.mediamagpie.persistence.entity");
        gen.generate(Dialect.HSQL);
        gen.generate(Dialect.MYSQL);
    }

    /**
     * Holds the classnames of hibernate dialects for easy reference.
     */
    static enum Dialect {
        ORACLE("org.hibernate.dialect.Oracle10gDialect"), MYSQL("org.hibernate.dialect.MySQLDialect"), HSQL("org.hibernate.dialect.HSQLDialect");

        private String dialectClass;

        private Dialect(String dialectClass) {
            this.dialectClass = dialectClass;
        }

        public String getDialectClass() {
            return dialectClass;
        }
    }
}
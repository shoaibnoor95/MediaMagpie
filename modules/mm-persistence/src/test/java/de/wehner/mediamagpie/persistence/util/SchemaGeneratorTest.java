package de.wehner.mediamagpie.persistence.util;

import org.junit.Test;

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
public class SchemaGeneratorTest {

    @Test
    public void runGenerator() throws Exception {
        SchemaGenerator gen = new SchemaGenerator("de.wehner.mediamagpie.persistence.entity");
        gen.generate(SchemaGenerator.Dialect.MYSQL);
    }
}
package de.wehner.mediamagpie.persistence;

import org.flywaydb.core.Flyway;
import org.junit.Ignore;
import org.junit.Test;

public class FlywayTest {

    @Ignore("just a playground here")
    @Test
    public void testMigrate() {
        Flyway flyway = new Flyway();

        flyway.setDataSource("jdbc:hsqldb:hsql://localhost:9002/xdb2", "sa", null);

        flyway.migrate();
    }
}

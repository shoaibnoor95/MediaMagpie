package de.wehner.mediamagpie.common.fslayer;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import de.wehner.mediamagpie.common.testsupport.MongoTestEnvironment;

public class MongoDbFSLayerTest {

    @Rule
    public MongoTestEnvironment _mongoTestEnvironment = new MongoTestEnvironment();

    @Before
    public void beforeMethod() {
        org.junit.Assume.assumeTrue(_mongoTestEnvironment.getConnection() != null);
    }

    @Test
    public void testMongoStartup() {
        System.out.println("connection to mongodb: " + (_mongoTestEnvironment.getConnection() != null));
    }
}

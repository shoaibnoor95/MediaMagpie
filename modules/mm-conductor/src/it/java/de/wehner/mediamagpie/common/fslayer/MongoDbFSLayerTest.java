package de.wehner.mediamagpie.common.fslayer;

import org.junit.Rule;
import org.junit.Test;

import de.wehner.mediamagpie.common.testsupport.MongoTestEnvironment;

public class MongoDbFSLayerTest {

    @Rule
    public MongoTestEnvironment _mongoTestEnvironment = new MongoTestEnvironment();

    @Test
    public void testMongoStartup() {
        _mongoTestEnvironment.getConnection();
        System.out.println();
    }
}

package de.wehner.mediamagpie.conductor.util;

import static org.fest.assertions.Assertions.*;

import java.io.File;
import java.net.MalformedURLException;

import org.junit.Test;

public class ManifestMetaDataTest {

    @Test
    public void testWithDefaultContructor() throws MalformedURLException {
        ManifestMetaData manifestMetaData = new ManifestMetaData();
        assertThat(manifestMetaData.getVersion()).isEqualTo("unknown");
    }

    @Test
    public void testWithUrlConstructor() throws MalformedURLException {
        // String clazzLocation =
        // "file:/Users/ralfwehner/projects/wehner/mediamagpie/modules/mm-conductor/target/mm-conductor-0.1-SNAPSHOT.jar";
        // URL manifestUrl = new URL("jar:" + clazzLocation + "!/META-INF/MANIFEST.MF");
        File testManifest = new File("src/test/resources/META-INF/MANIFEST.MF");
        ManifestMetaData manifestMetaData = new ManifestMetaData(testManifest.toURI().toURL());
        assertThat(manifestMetaData.getVersion()).isNotEqualTo("unknown");
        assertThat(manifestMetaData.getBuildTime()).isNotEqualTo("unknown");
    }
}

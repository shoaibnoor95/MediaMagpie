package de.wehner.mediamagpie.core.util;

import static org.fest.assertions.Assertions.*;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Enumeration;
import java.util.HashSet;

import org.apache.commons.io.FileUtils;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import de.wehner.mediamagpie.core.testsupport.TestEnvironment;
import de.wehner.mediamagpie.core.util.properties.PropertiesUtil;

public class ClassLocatorTest {

    @Rule
    public TestEnvironment _testEnvironment = new TestEnvironment(getClass());
    private File _testPackagePath;

    @Before
    public void setUp() throws IOException, URISyntaxException {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        String packageName = getClass().getPackage().getName();
        String packagePath = packageName.replace('.', '/');
        _testPackagePath = new File(_testEnvironment.getWorkingDir(), "my path with spaces");

        // build a new test directory like 'target/junit/ClassLocatorTest/my
        // path with spaces/de/wehner/mediamagpie/common/util' and copy all
        // class files into
        _testPackagePath = new File(_testPackagePath, packagePath);
        Enumeration<URL> resourcesOfThisClass = classLoader.getResources(packagePath);
        while (resourcesOfThisClass.hasMoreElements()) {
            URL resourceToPackage = resourcesOfThisClass.nextElement();
            File pathToTestPackage = new File(resourceToPackage.toURI());
            System.out.println("Copy classes from '" + pathToTestPackage.getPath() + "' to '" + _testPackagePath.getPath() + "'.");
            FileUtils.copyDirectory(pathToTestPackage, _testPackagePath);
        }
    }

    @Test
    public void testConvertToUrlAndReadFileBack() throws IOException {
        File pseudoClass = new File(_testPackagePath, "testFile.class");
        FileUtils.write(pseudoClass, "my dummy text.");
        URI uri = pseudoClass.toURI();
        File file = new File(uri);
        assertThat(file.getCanonicalPath()).isEqualTo(pseudoClass.getCanonicalPath());
        assertThat(file).hasSameContentAs(pseudoClass);
    }

    @Test
    public void testLoadDirectory() throws MalformedURLException, IOException {
        ClassLocator classLocator = new ClassLocator();
        HashSet<Class<?>> classes = new HashSet<Class<?>>();
        classLocator.loadDirectory(classes, _testPackagePath.toURI().toURL(), getClass().getPackage().getName());
        assertThat(classes).isNotEmpty();
        assertThat(classes).contains(getClass());
    }

    @Test
    public void testLoadDirectory_ClassFromSubDir() throws MalformedURLException, IOException {
        ClassLocator classLocator = new ClassLocator();
        HashSet<Class<?>> classes = new HashSet<Class<?>>();
        classLocator.loadDirectory(classes, _testPackagePath.toURI().toURL(), getClass().getPackage().getName());
        assertThat(classes).isNotEmpty();
        // BEWARE: This test only works when PropertiesUtils relies in a sub package of ClassLocator!
        assertThat(classes).contains(PropertiesUtil.class);
    }

}

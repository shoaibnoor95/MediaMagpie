package de.wehner.mediamagpie.core.util;

import java.io.File;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.net.JarURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class is able to automatically list classes in classpath. Basic code was taken from
 * http://forum.java.sun.com/thread.jspa?threadID=341935&start=15&tstart=0
 * 
 */
public class ClassLocator {

    private static final Logger LOG = LoggerFactory.getLogger(ClassLocator.class);

    private ClassLoader _classLoader;

    private Set<String> _packageNames = new HashSet<String>();

    private Set<Class<? extends Annotation>> _annotations = new HashSet<Class<? extends Annotation>>();

    public ClassLocator() {
        this(Thread.currentThread().getContextClassLoader());
    }

    public ClassLocator(ClassLoader classLoader) {
        this._classLoader = classLoader;
        if (classLoader == null)
            throw new NullPointerException("class loader is null");
    }

    public void setPackageNames(String... packageNames) {
        _packageNames.addAll(Arrays.asList(packageNames));
    }

    public void setAnnotations(@SuppressWarnings("unchecked") Class<? extends Annotation>... annotations) {
        _annotations.addAll(Arrays.asList(annotations));
    }

    public synchronized Set<Class<?>> findClasses() {
        Set<Class<?>> classes = new HashSet<Class<?>>();
        for (String packageName : _packageNames) {
            String packagePath = packageName.replace('.', '/');
            Enumeration<URL> resources;
            try {
                resources = _classLoader.getResources(packagePath);
                while (resources.hasMoreElements()) {
                    URL resource = resources.nextElement();
                    if (resource.getProtocol().equalsIgnoreCase("file")) {
                        loadDirectory(classes, resource, packageName);
                    } else if (resource.getProtocol().equalsIgnoreCase("jar")) {
                        loadJar(classes, resource, packagePath);
                    } else {
                        LOG.warn("unknown protocol on class resource: " + resource.toExternalForm());
                    }
                }
            } catch (IOException e) {
                LOG.warn("could not load resources from " + packagePath);
            }
        }
        return classes;
    }

    private void loadJar(Set<Class<?>> classes, URL resource, String packagePath) throws IOException {
        JarURLConnection conn = (JarURLConnection) resource.openConnection();
        JarFile jarFile = conn.getJarFile();
        Enumeration<JarEntry> entries = jarFile.entries();
        while (entries.hasMoreElements()) {
            JarEntry entry = entries.nextElement();
            if ((entry.getName().startsWith(packagePath) || entry.getName().startsWith("WEB-INF/classes/" + packagePath))
                    && entry.getName().endsWith(".class")) {
                String className = entry.getName();
                if (className.startsWith("/"))
                    className = className.substring(1);
                className = className.replace('/', '.');
                className = className.substring(0, className.length() - ".class".length());
                Class<?> clazz = getClass(className);
                if (clazz != null) {
                    classes.add(clazz);
                }

            }
        }

    }

    private Class<?> getClass(String className) {
        try {
            Class<?> clazz = _classLoader.loadClass(className);
            if (isCorrectAnnotated(clazz)) {
                return clazz;
            }
        } catch (ClassNotFoundException e) {
            LOG.warn("could not load " + className);
        }
        return null;
    }

    private boolean isCorrectAnnotated(Class<?> clazz) {
        for (Class<? extends Annotation> annotation : _annotations) {
            if (clazz.isAnnotationPresent(annotation)) {
                return true;
            }
        }
        return _annotations.isEmpty();
    }

    /**
     * Collects all classes for a specified package name within a directory on the local file system. If the directory contains sub
     * directories, the classes within the sub directories will be collected as well.
     * 
     * @param classes
     *            A reference to a set were the result will be added
     * @param resource
     *            The URL describing a directory on the local file system were classes within the <code>packageName</code> will be
     *            collected.
     * @param packageName
     *            The package name used to load all classes that are enclosed within
     * @throws IOException
     */
    void loadDirectory(Set<Class<?>> classes, URL resource, String packageName) throws IOException {
        try {
            URI uri = resource.toURI();
            File pathToResource = new File(uri);
            loadDirectory(classes, packageName, pathToResource);
        } catch (URISyntaxException e) {
            throw new IllegalArgumentException("Internal error. The argument 'resource' must be a valid directory on local file sytem.", e);
        }
    }

    private void loadDirectory(Set<Class<?>> classes, String packageName, File directory) throws IOException {
        if (!directory.isDirectory()) {
            throw new IOException("Invalid directory " + directory.getAbsolutePath());
        }
        File[] files = directory.listFiles();
        for (File file : files) {
            if (file.isDirectory()) {
                loadDirectory(classes, packageName + '.' + file.getName(), file);
            } else if (file.getName().endsWith(".class")) {
                String simpleName = file.getName();
                simpleName = simpleName.substring(0, simpleName.length() - ".class".length());
                String className = String.format("%s.%s", packageName, simpleName);
                Class<?> clazz = getClass(className);
                if (clazz != null) {
                    classes.add(clazz);
                }
            }
        }
    }

}

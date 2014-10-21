package de.wehner.mediamagpie.core.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;

public class SearchPathUtil {

    private static final Logger LOG = LoggerFactory.getLogger(SearchPathUtil.class);

    /**
     * Searches for resources in
     * <ul>
     * <li>a) classpath or (prefix <code>classpath:</code>)</li>
     * <li>b) the local file system (no prefix)</li>
     * </ul>
     * When the first resource is found, an InputStream will be returned.
     * 
     * <pre>
     * EG:
     *   byte[] bCP = SearchPathUtil.getBytes("classpath:/html/generic/Welcome.html")
     *   byte[] bFS = SearchPathUtil.getBytes("src/main/resources/html/generic/Welcome.html")
     * </pre>
     * 
     * @param paths
     *            At least one resource location which can be a location within the classpath or a the local file system.
     * @return A InputStream to the first found resource.
     * @throws FileNotFoundException
     */
    public static InputStream openStream(String... paths) throws FileNotFoundException {
        for (String path : paths) {
            if (path.startsWith("classpath:")) {
                String resourceName = path.substring(10);
                InputStream stream = SearchPathUtil.class.getResourceAsStream(resourceName);
                if (stream != null) {
                    LOG.info("Reading from " + path);
                    return stream;
                }
            } else {
                File file = new File(path);
                if (file.exists()) {
                    LOG.info("Reading from " + path);
                    return new FileInputStream(file);
                }
            }
            LOG.debug("Not found: " + path);
        }
        throw new FileNotFoundException("Could not find resource at the following locations: " + Arrays.asList(paths));
    }

    /**
     * Reads bytes from resource. Analog to {@linkplain #openStream(String...)}.
     * 
     * @param paths
     *            At least one or more locations of the resource.
     * @return The content of resource as byte array.
     * @throws IOException
     */
    public static byte[] getBytes(String... paths) throws IOException {
        InputStream inputStream = openStream(paths);
        try {
            return IOUtils.toByteArray(inputStream);
        } catch (IOException e) {
            LOG.error("Can not read from resource '" + Arrays.asList(paths) + "'.", e);
            throw e;
        } finally {
            IOUtils.closeQuietly(inputStream);
        }

    }

    /**
     * Convenient class to find the first existing resource.
     * 
     * @param paths
     *            At least one or more locations of the resource.
     * @return The first found resource location.
     * @throws IllegalArgumentException
     *             If no resource is available.
     */
    public static String findPath(String... paths) {
        for (String path : paths) {
            if (path.startsWith("classpath:")) {
                String resourceName = path.substring(10);
                InputStream stream = SearchPathUtil.class.getResourceAsStream(resourceName);
                try {
                    if (stream != null) {
                        return path;
                    }
                } finally {
                    IOUtils.closeQuietly(stream);
                }

            } else {
                if (new File(path).exists()) {
                    return path;
                }
            }

        }
        throw new IllegalArgumentException("File not found in " + Arrays.asList(paths));
    }

    public static Resource findResource(String... paths) {
        List<Resource> resourcesToTest = new ArrayList<Resource>();
        for (String path : paths) {
            if (isClasspathResource(path)) {
                resourcesToTest.add(new ClassPathResource(removeClasspathFromResource(path), SearchPathUtil.class));
            } else {
                resourcesToTest.add(new FileSystemResource(path));
            }
        }
        return findResource(resourcesToTest.toArray(new Resource[0]));
    }

    public static Resource findResource(Resource... resources) {
        List<String> paths = new ArrayList<String>();
        for (Resource resource : resources) {
            paths.add(resource.getDescription());
            if (resource.exists()) {
                return resource;
            }
        }
        throw new IllegalArgumentException("Resource not found in '" + paths + "'.");
    }

    public static Properties loadProperties(String... path) throws IOException {
        InputStream inputStream = null;
        try {
            inputStream = openStream(path);
            Properties properties = new Properties();
            properties.load(inputStream);
            return properties;
        } finally {
            IOUtils.closeQuietly(inputStream);
        }
    }

    private static boolean isClasspathResource(String path) {
        return path.startsWith("classpath:");
    }

    private static String removeClasspathFromResource(String path) {
        String resourceName = path.substring(10);
        return resourceName;
    }

    public static File[] listFiles(File dir, final String wildcardMatcher) {
        return dir.listFiles(new FilenameFilter() {

            @Override
            public boolean accept(File dir, String name) {
                boolean matches = true;

                if (wildcardMatcher != null) {
                    matches = FilenameUtils.wildcardMatch(name, wildcardMatcher);
                }
                if (matches) {
                    return !isTempSystemFile(name);
                }
                return matches;
            }
        });
    }

    public static boolean isTempSystemFile(String fileName) {
        return fileName.equals(".DS_Store");
    }

}

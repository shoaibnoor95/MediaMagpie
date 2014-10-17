package de.wehner.mediamagpie.core.util;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.core.type.classreading.CachingMetadataReaderFactory;
import org.springframework.core.type.classreading.MetadataReader;
import org.springframework.core.type.classreading.MetadataReaderFactory;

public class ClassPathUtil {

    public interface CandidateCheck {
        boolean isCandidate(MetadataReader metadataReader);
    }

    private static final Logger LOG = LoggerFactory.getLogger(ClassPathUtil.class);

    /**
     * Searches for the first readable <code>Resource</code> within a given classpath.
     * 
     * @param locationPattern
     *            Something like <code>/ssl/keystore.jks</code> or <code>my.package&#47;**&#47;*.class</code>
     * @return
     */
    public static Resource findResourceInClassPath(String locationPattern) {
        String packageSearchPath = ResourcePatternResolver.CLASSPATH_ALL_URL_PREFIX + locationPattern;
        return findResource(packageSearchPath);
    }

    /**
     * Searches for the first readable <code>Resource</code> within a given classpath.
     * 
     * @param locationPattern
     *            A search pattern in format like <code>classpath:test.dat</code>. See also
     *            {@linkplain PathMatchingResourcePatternResolver#getResource(String)}
     * @return
     */
    public static Resource findResource(String locationPattern) {
        ResourcePatternResolver resourcePatternResolver = new PathMatchingResourcePatternResolver();
        try {
            Resource[] resources = resourcePatternResolver.getResources(locationPattern);
            for (Resource resource : resources) {
                if (resource.isReadable()) {
                    LOG.debug("Found resource '" + resource.getURI() + "'");
                    return resource;
                }
            }
        } catch (IOException e) {
            ExceptionUtil.convertToRuntimeException(e);
        }
        throw new RuntimeException("Can not find resource with searchPattern '" + locationPattern + "':");
    }

    public static List<Class<?>> resolveAllClasses(String basePackage, CandidateCheck check) throws IOException, ClassNotFoundException {
        ResourcePatternResolver resourcePatternResolver = new PathMatchingResourcePatternResolver();
        MetadataReaderFactory metadataReaderFactory = new CachingMetadataReaderFactory(resourcePatternResolver);

        List<Class<?>> candidates = new ArrayList<Class<?>>();
        String packageSearchPath = ResourcePatternResolver.CLASSPATH_ALL_URL_PREFIX + basePackage.replace('.', '/') + "/**/*.class";
        Resource[] resources = resourcePatternResolver.getResources(packageSearchPath);
        for (Resource resource : resources) {
            if (resource.isReadable()) {
                MetadataReader metadataReader = metadataReaderFactory.getMetadataReader(resource);
                if (check == null || check.isCandidate(metadataReader)) {
                    candidates.add(Class.forName(metadataReader.getClassMetadata().getClassName()));
                }
            }
        }
        return candidates;
    }

}

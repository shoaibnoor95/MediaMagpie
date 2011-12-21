package de.wehner.mediamagpie.common.util;

import static org.fest.assertions.Assertions.*;

import java.io.File;
import java.io.IOException;

import org.junit.Test;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;

import de.wehner.mediamagpie.common.util.SearchPathUtil;

public class SearchPathUtilTest {
    @Test
    public void testFindResource_TestFile() throws IOException {
        Resource resource = SearchPathUtil.findResource("/src/main/resources/spring/web-application.xml", "src/main/resources/spring/web-application.xml");
        assertThat(resource).isNotNull();
        assertThat(resource).isInstanceOf(FileSystemResource.class);
        assertThat(resource.getFile()).isEqualTo(new File("src/main/resources/spring/web-application.xml"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testFindResource_TestFileNotFound() throws IOException {
        SearchPathUtil.findResource("/src/main/resources/spring/web-application.xmlxxx", "src/main/resources/spring/web-application.xmlxxx");
    }

    @Test
    public void testFindResource_TestFileInClasspath() throws IOException {
        Resource resource = SearchPathUtil.findResource("classpath:/blah/application.xml", "classpath:/spring/web-application.xml");
        assertThat(resource).isNotNull();
        assertThat(resource).isInstanceOf(ClassPathResource.class);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testFindResource_TestFileInClasspathNotFound() throws IOException {
        SearchPathUtil.findResource("classpath:/blah/application.xmlxxx", "classpath:/spring/web-application.xmlxxx");
    }
}
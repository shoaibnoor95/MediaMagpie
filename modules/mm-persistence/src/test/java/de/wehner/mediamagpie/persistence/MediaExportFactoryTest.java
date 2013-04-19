package de.wehner.mediamagpie.persistence;

import static org.fest.assertions.Assertions.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.io.output.ByteArrayOutputStream;
import org.junit.Before;
import org.junit.Test;

import de.wehner.mediamagpie.api.MediaExport;
import de.wehner.mediamagpie.core.testsupport.TestEnvironment;
import de.wehner.mediamagpie.core.util.DigestUtil;
import de.wehner.mediamagpie.persistence.MediaExportFactory;
import de.wehner.mediamagpie.persistence.entity.Media;
import de.wehner.mediamagpie.persistence.entity.MediaTag;

public class MediaExportFactoryTest {

    private static final Date CREATION_DATE = new Date(123456);

    private static final File SRC_TEST_PNG = new File("../mm-core/src/test/resources/images/image1.png");

    private final TestEnvironment _testEnvironment = new TestEnvironment(getClass());

    private Media _media;

    private MediaExportFactory _factory = new MediaExportFactory();

    @Before
    public void setUp() throws IOException {
        _testEnvironment.cleanWorkingDir();

        File mediaResource = new File(_testEnvironment.getWorkingDir(), "testmedia.jpg");
        FileUtils.copyFile(SRC_TEST_PNG, mediaResource);
        _media = new Media(null, "media name", mediaResource.toURI(), CREATION_DATE);
        _media.setTags(Arrays.asList(new MediaTag("tag 1"), new MediaTag("tag 2")));
        _media.setId(5L);
        _media.setHashValue(DigestUtil.computeSha1AsHexString(mediaResource));
    }

    @Test
    public void testCreate_VerifyFileImport() throws IOException {

        MediaExport mediaExport = _factory.create(_media);

        ByteArrayOutputStream byteOS = new ByteArrayOutputStream();
        IOUtils.copy(mediaExport.getInputStream(), byteOS);

        assertThat(byteOS.toByteArray()).isEqualTo(FileUtils.readFileToByteArray(SRC_TEST_PNG));
        assertThat(mediaExport.getOriginalFileName()).isEqualTo("testmedia.jpg");
        assertThat(mediaExport.getLength()).isEqualTo(SRC_TEST_PNG.length());
    }

    @Test
    public void testCreate_VerifyTagConversion() throws FileNotFoundException {

        MediaExport mediaExport = _factory.create(_media);

        List<String> tags = mediaExport.getTags();
        assertThat(tags).contains("tag 1", "tag 2");
    }

    @Test
    public void testCreate_VerifyNameDescriptionEtc() throws IOException {

        MediaExport mediaExport = _factory.create(_media);

        assertThat(mediaExport.getName()).isEqualTo(_media.getName());
        assertThat(mediaExport.getDescription()).isEqualTo(_media.getDescription());
        assertThat(mediaExport.getCreationDate()).isEqualTo(CREATION_DATE);
        assertThat(mediaExport.getMediaId()).isEqualTo(_media.getId() + "");
        assertThat(mediaExport.getOriginalFileName()).isEqualTo(_media.getFileFromUri().getName());
    }

    @Test
    public void testCreate_VerifyUndefinedValuesWillBeUndefinedInMediaExport() throws IOException {
        _media.setName(null);
        _media.setDescription(null);
        
        MediaExport mediaExport = _factory.create(_media);

        assertThat(mediaExport.getName()).isNullOrEmpty();
        assertThat(mediaExport.getDescription()).isNullOrEmpty();
        assertThat(mediaExport.getCreationDate()).isEqualTo(CREATION_DATE);
        assertThat(mediaExport.getMediaId()).isEqualTo(_media.getId() + "");
        assertThat(mediaExport.getOriginalFileName()).isEqualTo(_media.getFileFromUri().getName());
    }

    @Test
    public void testCreate_VerifyHashValue() throws IOException {

        MediaExport mediaExport = _factory.create(_media);

        assertThat(mediaExport.getHashValue()).isEqualTo(_media.getHashValue());
    }
}

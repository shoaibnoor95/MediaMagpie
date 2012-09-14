package de.wehner.mediamagpie.conductor.webapp.media;

import static org.fest.assertions.Assertions.*;

import java.io.File;
import java.io.IOException;
import java.util.Date;

import org.junit.Before;
import org.junit.Test;

import com.drew.metadata.MetadataException;

import de.wehner.mediamagpie.common.persistence.entity.Orientation;

public class PhotoMetadataExtractorTest {

    @Before
    public void setUp() {
    }

    @Test
    public void test_resolveDateTimeOriginal() throws IOException {
        File mediaFile = new File("src/test/resources/images/IMG_0013.JPG");
        PhotoMetadataExtractor metadataExtractor = new PhotoMetadataExtractor(mediaFile.toURI());
        Date dateOfMedia = metadataExtractor.resolveDateTimeOriginal();
        assertThat(dateOfMedia.getTime()).isEqualTo(1250280049000L);
    }

    @Test
    public void test_resolveDateTimeOriginal_ButMediaHasNoOriginDate() throws IOException {
        File mediaFile = new File("src/test/resources/images/1600x4.jpg");
        PhotoMetadataExtractor metadataExtractor = new PhotoMetadataExtractor(mediaFile.toURI());
        Date dateOfMedia = metadataExtractor.resolveDateTimeOriginal();
        assertThat(dateOfMedia).isNull();
    }

    @Test
    public void testGetMetadataFromMedia_Normal() throws IOException, MetadataException {
        File mediaFileNormal = new File("src/test/resources/images/IMG_0013.JPG");
        PhotoMetadataExtractor metadataExtractor = new PhotoMetadataExtractor(mediaFileNormal.toURI());
        Orientation orientation = metadataExtractor.resolveOrientation();
        assertThat(orientation).isEqualTo(Orientation.TOP_LEFT_SIDE);
    }

    @Test
    public void testGetMetadataFromMediaRIGHT_SIDE() throws IOException, MetadataException {
        File mediaFileRightSide = new File("src/test/resources/images/IMG_1414.JPG");
        PhotoMetadataExtractor metadataExtractor = new PhotoMetadataExtractor(mediaFileRightSide.toURI());
        Orientation orientation = metadataExtractor.resolveOrientation();
        assertThat(orientation).isEqualTo(Orientation.RIGHT_SIDE_TOP);
    }

    @Test
    public void testGetMetadataFromExifDatalessMedia() throws IOException, MetadataException {
        File mediaFileRightSide = new File("src/test/resources/images/1600x4.jpg");
        PhotoMetadataExtractor metadataExtractor = new PhotoMetadataExtractor(mediaFileRightSide.toURI());
        Orientation orientation = metadataExtractor.resolveOrientation();
        assertThat(orientation).isEqualTo(Orientation.UNKNOWN);
    }
}

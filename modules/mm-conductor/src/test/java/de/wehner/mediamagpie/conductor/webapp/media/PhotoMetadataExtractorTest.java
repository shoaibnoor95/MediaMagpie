package de.wehner.mediamagpie.conductor.webapp.media;

import static org.fest.assertions.Assertions.*;

import java.io.File;
import java.io.IOException;
import java.util.Date;

import org.apache.commons.lang3.time.DateFormatUtils;
import org.junit.Before;
import org.junit.Test;

import com.drew.metadata.MetadataException;

import de.wehner.mediamagpie.common.persistence.entity.Orientation;
import de.wehner.mediamagpie.conductor.media.PhotoMetadataExtractor;
import de.wehner.mediamagpie.conductor.metadata.CameraMetaData;

public class PhotoMetadataExtractorTest {

    @Before
    public void setUp() {
    }

    @Test
    public void test_resolveDateTimeOriginal() throws IOException {
        File mediaFile = new File("src/test/resources/images/IMG_0013.JPG");
        PhotoMetadataExtractor metadataExtractor = new PhotoMetadataExtractor(mediaFile.toURI());
        Date dateOfMedia = metadataExtractor.resolveDateTimeOriginal();
        String string = DateFormatUtils.ISO_DATETIME_FORMAT.format(dateOfMedia);
        assertThat(string).isEqualTo("2009-08-14T22:00:49");
    }

    @Test
    public void test_resolveDateTimeOriginal_ButMediaHasNoOriginDate() throws IOException {
        File mediaFile = new File("src/test/resources/images/1600x4.jpg");
        PhotoMetadataExtractor metadataExtractor = new PhotoMetadataExtractor(mediaFile.toURI());
        Date dateOfMedia = metadataExtractor.resolveDateTimeOriginal();
        assertThat(dateOfMedia).isNull();
    }

    @Test
    public void test_resolveDateTimeOriginal_ButMediaHasObscurceDate() throws IOException {
        File mediaFile = new File("src/test/resources/images/DSCN0006.JPG");
        PhotoMetadataExtractor metadataExtractor = new PhotoMetadataExtractor(mediaFile.toURI());
        //metadataExtractor.dumpMetadataToStdOut();
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

    @Test
    public void test_createCameraMetaData() throws IOException {
        File mediaFile = new File("src/test/resources/images/IMG_0013.JPG");
        PhotoMetadataExtractor metadataExtractor = new PhotoMetadataExtractor(mediaFile.toURI());
        CameraMetaData cameraMetaData = metadataExtractor.createCameraMetaData();
        System.out.println(cameraMetaData);
        assertThat(cameraMetaData.getExifData().get("Exif Image Height")).isEqualTo("3000 pixels");
    }

    @Test
    public void test_createCameraMetaData_but_PhotoHasNoMetaData() throws IOException {
        File mediaFile = new File("src/test/resources/images/accept.png");
        PhotoMetadataExtractor metadataExtractor = new PhotoMetadataExtractor(mediaFile.toURI());
        CameraMetaData cameraMetaData = metadataExtractor.createCameraMetaData();
        System.out.println(cameraMetaData);
        assertThat(cameraMetaData).isNull();
    }
}

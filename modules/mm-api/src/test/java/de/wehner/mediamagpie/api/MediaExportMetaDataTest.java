package de.wehner.mediamagpie.api;

import static org.fest.assertions.Assertions.*;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;

import org.apache.commons.io.IOUtils;
import org.junit.Before;
import org.junit.Test;

public class MediaExportMetaDataTest {

    private MediaExportMetadata _mediaExportMetaData;

    @Before
    public void setUp() {
        _mediaExportMetaData = new MediaExportMetadata("äöüß Blah");
        _mediaExportMetaData.setDescription("My description contains some special characters like '§$%&ß'.");
        _mediaExportMetaData.setOriginalFileName("original File Name.txt");
        _mediaExportMetaData.setTags(Arrays.asList("tag A", "Tag b"));
    }

    @Test
    public void test_createInputStream() throws IOException {
        InputStream inputStream = _mediaExportMetaData.createInputStream();
        ByteArrayOutputStream bufferOS = new ByteArrayOutputStream();
        IOUtils.copy(inputStream, bufferOS);

        String marshhalledObject = bufferOS.toString("UTF8");
        assertThat(marshhalledObject)
                .isEqualTo(
                        "{\"name\":\"äöüß Blah\",\"description\":\"My description contains some special characters like '§$%&ß'.\",\"originalFileName\":\"original File Name.txt\",\"tags\":[\"tag A\",\"Tag b\"]}");
    }

    @Test
    public void testMarshallRoundRobin() throws IOException {
        InputStream inputStream = _mediaExportMetaData.createInputStream();

        MediaExportMetadata mediaExportMetaDataFromJson = MediaExportMetadata.createInstance(inputStream);

        assertThat(mediaExportMetaDataFromJson).isEqualTo(_mediaExportMetaData);
    }
}

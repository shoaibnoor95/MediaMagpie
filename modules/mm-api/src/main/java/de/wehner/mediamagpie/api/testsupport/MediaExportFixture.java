package de.wehner.mediamagpie.api.testsupport;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.Arrays;
import java.util.Date;

import de.wehner.mediamagpie.api.MediaExport;
import de.wehner.mediamagpie.api.MediaType;

public class MediaExportFixture {

    public static final Date CREATION_DATE = new Date(123456);

    public static MediaExport createMediaExportTestObject(int mediaId, String name, File mediaFile) throws FileNotFoundException {
        MediaExport mediaExport = new MediaExport(name);
        mediaExport.setMediaId("" + mediaId);
        mediaExport.setType(MediaType.PHOTO);
        mediaExport.setHashValue("pseudo-hash-value");
        mediaExport.setInputStream(new FileInputStream(mediaFile));
        mediaExport.setTags(Arrays.asList("Tag AB C", "Schöner Knipsen", "california"));
        mediaExport.setDescription("This is a sample for a description text, \nwhich contains some extra characters like äöü #'ß4§€ and \t blah.");
        mediaExport.setCreationDate(CREATION_DATE);
        return mediaExport;
    }
}

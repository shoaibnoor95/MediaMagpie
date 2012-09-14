package de.wehner.mediamagpie.conductor.webapp.media;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URI;
import java.net.URLConnection;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.drew.imaging.jpeg.JpegMetadataReader;
import com.drew.imaging.jpeg.JpegProcessingException;
import com.drew.metadata.Metadata;
import com.drew.metadata.exif.ExifIFD0Directory;
import com.drew.metadata.exif.ExifSubIFDDirectory;

import de.wehner.mediamagpie.common.persistence.entity.Orientation;

public class PhotoMetadataExtractor {

    private static final Logger LOG = LoggerFactory.getLogger(PhotoMetadataExtractor.class);

    private final Metadata _metadata;

    public PhotoMetadataExtractor(URI mediaFileUri) throws IOException {
        super();
        _metadata = getMetadataFromMedia(mediaFileUri);
    }

    /**
     * This method uses the <code>JpegMetadataReader</code> to read meta informations from only JPEG files.<br/>
     * TODO rwe: plugin-stuff? Better use a separate class like 'MetadataextractorUtil' to get meta informations from media files which is
     * more flexible and can provide meta informations from videos as well.
     * 
     * @param mediaUri
     * @return
     * @throws IOException
     */
    private Metadata getMetadataFromMedia(URI mediaUri) throws IOException {
        String scheme = mediaUri.getScheme();
        if (scheme.equals("file") && new File(mediaUri.getPath()).exists()) {
            File mediaFile = new File(mediaUri.getPath());
            final Set<String> PARSABLE_FILE_EXTENSIONS = new HashSet<String>(Arrays.asList("jpg"));
            if (!PARSABLE_FILE_EXTENSIONS.contains(FilenameUtils.getExtension(mediaFile.getName()).toLowerCase())) {
                // Can not determine file type by file extension, so try to analyze its content
                FileInputStream fileInputStream = null;
                try {
                    fileInputStream = new FileInputStream(mediaFile);
                    String mimeType = URLConnection.guessContentTypeFromStream(new BufferedInputStream(fileInputStream));
                    LOG.info("Found mime type '" + mimeType + "' for file with name '" + mediaUri + "'.");
                    if (!"image/jpeg".equals(mimeType)) {
                        return null;
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    IOUtils.closeQuietly(fileInputStream);
                }
            }
            try {
                return JpegMetadataReader.readMetadata(mediaFile);

            } catch (JpegProcessingException e) {
                LOG.warn("Can not read metadata from media file '" + mediaFile.getPath() + "'.", e);
            }
        }
        return null;
    }

    public Date resolveDateTimeOriginal() {
        if (_metadata != null) {
            // obtain the Exif directory
            ExifSubIFDDirectory directory = _metadata.getDirectory(ExifSubIFDDirectory.class);

            if (directory != null) {
                // query the tag's value
                Date date = directory.getDate(ExifSubIFDDirectory.TAG_DATETIME_ORIGINAL);
                if (date != null) {
                    return date;
                }
            }
        }
        return null;
    }

    public Orientation resolveOrientation() {
        if (_metadata != null) {
            ExifIFD0Directory directory = _metadata.getDirectory(ExifIFD0Directory.class);
            Integer orientationAsInt = directory.getInteger(ExifIFD0Directory.TAG_ORIENTATION);
            // String description = directory.getDescription(ExifIFD0Directory.TAG_ORIENTATION);
            if (orientationAsInt != null) {
                // for mapping see: com.drew.metadata.exif.ExifIFD0Descriptor.getOrientationDescription()
                switch (orientationAsInt) {
                case 1:
                    // 1: return "Top, left side (Horizontal / normal)";
                    return Orientation.TOP_LEFT_SIDE;
                case 6:
                    // 6: return "Right side, top (Rotate 90 CW)"; (um 90 Grad gegen den Uhrzeigersinn gedreht)
                    return Orientation.RIGHT_SIDE_TOP;
                case 8:
                    // 8: return "Left side, bottom (Rotate 270 CW)"; (um 90 Grad gegen im Uhrzeigersinn gedreht)
                    return Orientation.LEFT_SIDE_BOTTOM;
                default:
                    return Orientation.UNKNOWN;
                }
            }
        }
        return Orientation.UNKNOWN;
    }
}

package de.wehner.mediamagpie.conductor.media;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URI;
import java.net.URLConnection;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.drew.imaging.jpeg.JpegMetadataReader;
import com.drew.imaging.jpeg.JpegProcessingException;
import com.drew.metadata.Directory;
import com.drew.metadata.Metadata;
import com.drew.metadata.Tag;
import com.drew.metadata.exif.ExifIFD0Directory;
import com.drew.metadata.exif.ExifSubIFDDirectory;

import de.wehner.mediamagpie.conductor.metadata.CameraMetaData;
import de.wehner.mediamagpie.persistence.entity.Orientation;

public class PhotoMetadataExtractor implements CreationTimeExtractor {

    private static final Logger LOG = LoggerFactory.getLogger(PhotoMetadataExtractor.class);

    private final Metadata _metadata;

    private final GregorianCalendar YEAR_1900 = new GregorianCalendar();

    public PhotoMetadataExtractor(URI mediaFileUri) throws IOException {
        super();
        _metadata = getMetadataFromMedia(mediaFileUri);
        YEAR_1900.set(1900, 0, 0);
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

    /* (non-Javadoc)
     * @see de.wehner.mediamagpie.conductor.media.CreationTimeExtractor#resolveDateTimeOriginal()
     */
    @Override
    public Date resolveDateTimeOriginal() {
        if (_metadata != null) {
            // obtain the Exif directory
            ExifSubIFDDirectory directory = _metadata.getDirectory(ExifSubIFDDirectory.class);

            if (directory != null) {
                // query the tag's value
                // try to find out which tag contains the date of creation
                Integer validTag = ExifSubIFDDirectory.TAG_DATETIME_ORIGINAL;
                String validDateStr = directory.getString(validTag);
                if (!isValidDateFormat(validDateStr)) {
                    validTag = ExifSubIFDDirectory.TAG_DATETIME_DIGITIZED;
                    validDateStr = directory.getString(validTag);
                    if (!isValidDateFormat(validDateStr)) {
                        return null;
                    }
                }

                // try to read date
                Date timeOriginal = directory.getDate(validTag);
                if (timeOriginal != null && timeOriginal.after(YEAR_1900.getTime())) {
                    return timeOriginal;
                }
            }
        }
        return null;
    }

    private boolean isValidDateFormat(String dateStr) {
        // test for invalid date strings, eg: '0000:00:00 00:00:00'
        if (!StringUtils.isEmpty(dateStr)) {
            return !StringUtils.containsOnly(dateStr, "0: ");
        }
        return false;
    }

    public Orientation resolveOrientation() {
        if (_metadata != null) {
            ExifIFD0Directory directory = _metadata.getDirectory(ExifIFD0Directory.class);
            if (directory == null) {
                return Orientation.UNKNOWN;
            }

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

    public CameraMetaData createCameraMetaData() {
        if (_metadata == null) {
            return null;
        }

        CameraMetaData metaData = new CameraMetaData();
        for (Directory directory : _metadata.getDirectories()) {
            if (directory.getClass().getSimpleName().toLowerCase().startsWith("exif")) {
                Collection<Tag> tags = directory.getTags();
                for (Tag tag : tags) {
                    metaData.getExifData().put(tag.getTagName(), tag.getDescription());
                }
                continue;
            }
            String name = directory.getName();
            Collection<Tag> tags = directory.getTags();
            for (Tag tag : tags) {
                metaData.getMetaData().put(name + ", " + tag.getTagName(), tag.getDescription());
            }
        }
        return metaData;
    }

    public void dumpMetadataToStdOut() {
        for (Directory directory : _metadata.getDirectories()) {
            for (Tag tag : directory.getTags()) {
                System.out.println(tag);
            }
        }
    }
}

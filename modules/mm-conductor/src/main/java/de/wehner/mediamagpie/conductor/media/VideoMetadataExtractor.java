package de.wehner.mediamagpie.conductor.media;

import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URI;
import java.nio.channels.FileChannel;
import java.util.Date;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.coremedia.iso.IsoFile;
import com.coremedia.iso.boxes.Box;
import com.coremedia.iso.boxes.MovieBox;
import com.coremedia.iso.boxes.MovieHeaderBox;
import com.googlecode.mp4parser.DataSource;
import com.googlecode.mp4parser.FileDataSourceImpl;

public class VideoMetadataExtractor implements CreationTimeExtractor, Closeable {

    private static final Logger LOG = LoggerFactory.getLogger(VideoMetadataExtractor.class);

    private MovieBox moov;
    private DataSource dataSourceImpl = null;

    public VideoMetadataExtractor(URI mediaUri) throws IOException {
        super();
        String scheme = mediaUri.getScheme();
        if ("file".equalsIgnoreCase(scheme) && new File(mediaUri.getPath()).exists()) {
            FileChannel fc = new FileInputStream(new File(mediaUri)).getChannel();
            dataSourceImpl = new FileDataSourceImpl(fc);

            IsoFile isoFile = new IsoFile(dataSourceImpl/* new File(mediaFileUri).getPath() */);
            moov = isoFile.getMovieBox();
            // dataSourceImpl.close();
        }
    }

    @Override
    public Date resolveDateTimeOriginal() {
        for (Box b : moov.getBoxes()) {
            if (b instanceof MovieHeaderBox) {
                return ((MovieHeaderBox) b).getCreationTime();
            }
        }
        return null;
    }

    /**
     * @return The length of media in seconds. <code>null</code> if the length can not be determined.
     */
    public Float getDuration() {
        for (Box b : moov.getBoxes()) {
            if (b instanceof MovieHeaderBox) {
                float duration = (float) ((MovieHeaderBox) b).getDuration() / (float) ((MovieHeaderBox) b).getTimescale();
                return duration;
            }
        }
        return null;
    }

    @Override
    public void close() throws IOException {
        IOUtils.closeQuietly(dataSourceImpl);
    }

}

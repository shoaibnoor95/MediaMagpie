package de.wehner.mediamagpie.conductor.webapp.services;

import java.io.File;

import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import de.wehner.mediamagpie.conductor.media.FfmpegWrapper;

/**
 * Currrently supported formats (http://www.w3schools.com/TAgs/tag_video.asp):
 * 
 * <pre>
 * Browser  | MP4                                 | WebM  | Ogg
 * ------------------------------------------------------------
 * IE       | YES                                 | NO    | NO
 * Chrome   | YES                                 | YES   | YES
 * Firefox  | NO                                  | YES   | YES
 *            Update: Firefox 21 running on 
 *            Windows 7, Windows 8, Windows 
 *            Vista, and Android now supports MP4     
 * Safari   | YES                                 |NO     | NO
 * Opera    | NO                                  | YES   | YES
 * </pre>
 * 
 * @author ralfwehner
 * 
 */
@Service
public class VideoService {

    public static final Logger LOG = LoggerFactory.getLogger(VideoService.class);

    public static enum VideoFormat {
        /**
         * OGG/Theora, *.ogv
         */
        OGG_Theora("ogv"),
        /**
         * WebM/vp8 , *.webm
         */
        WebM_vp8("webm"),
        /**
         * MP4/h264 , *.mp4
         */
        MP4_h264("mp4");

        private final String _extension;

        private VideoFormat(String extension) {
            _extension = extension;
        }

        public String getExtension() {
            return _extension;
        }
    }

    public File createImageFromVideo(File originImage, File tempMediaPath) {
        File outputFile = new File(tempMediaPath, originImage.getName() + ".img1.jpg");
        FfmpegWrapper ffmpegWrapper = new FfmpegWrapper(originImage.toURI());
        return ffmpegWrapper.createImageFromVideo(outputFile);
    }

    /**
     * Converts a given video into another format.
     * <p>
     * see: http://paulrouget.com/e/converttohtml5video/
     * </p>
     * 
     * <pre>
     * Examples:
     * > ffmpeg -i /Users/ralfwehner/projects/wehner/mediamagpie/modules/mm-conductor/src/main/webapp/static/MVI_2734.MOV -q:a 0 -strict -2 /Users/ralfwehner/projects/wehner/mediamagpie/modules/mm-conductor/src/main/webapp/static/MVI_2734.MP4
     * > ffmpeg -i MVI_2734.MOV -acodec libvorbis -ac 2 -ab 96k -ar 44100 -b 345k -s 640x360 MVI_2734.webm
     * </pre>
     * 
     * @param srcVideo
     * @param destFormat
     * @param widthOrHeight
     * @return
     */
    public File convertVideo(File srcVideo, VideoFormat destFormat, Integer widthOrHeight, File tempMediaPath) {
        String destName = FilenameUtils.getBaseName(srcVideo.getName());

        if (widthOrHeight != null) {
            destName += "x" + widthOrHeight;
        }
        destName += '.' + destFormat.getExtension();
        File outputFile = new File(tempMediaPath, destName);

        // test if destination file already exists
        if (outputFile.exists()) {
            LOG.warn("Output file '{}' already exists. Try to delete it now.", outputFile.getAbsolutePath());
            outputFile.delete();
        }
        FfmpegWrapper ffmpegWrapper = new FfmpegWrapper(srcVideo.toURI());
        
        // start conversion
        return ffmpegWrapper.convertVideo(outputFile, destFormat, widthOrHeight);
    }
}

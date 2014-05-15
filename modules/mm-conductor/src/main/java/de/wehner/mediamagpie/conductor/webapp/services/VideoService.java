package de.wehner.mediamagpie.conductor.webapp.services;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mobile.device.Device;
import org.springframework.stereotype.Service;

import de.wehner.mediamagpie.conductor.media.FfmpegWrapper;
import de.wehner.mediamagpie.persistence.dao.ConvertedVideoDao;
import de.wehner.mediamagpie.persistence.dao.MediaDataProcessingJobExecutionDao;
import de.wehner.mediamagpie.persistence.entity.Media;
import de.wehner.mediamagpie.persistence.entity.Priority;
import de.wehner.mediamagpie.persistence.entity.VideoConversionJobExecution;

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
 * Safari   | YES                                 | NO    | NO
 * Opera    | NO                                  | YES   | YES
 * </pre>
 * 
 * @author ralfwehner
 * 
 */
@Service
public class VideoService {

    public static final String ORIGINAL_SIZE = "original";

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

        private static Map<String, VideoFormat> _extension2VideoFormat;

        private VideoFormat(String extension) {
            _extension = extension;
        }

        public String getExtension() {
            return _extension;
        }

        public static VideoFormat extension2VideoFormat(String extension) {
            if (_extension2VideoFormat == null) {
                _extension2VideoFormat = new HashMap<>();
                for (VideoFormat videoFormat : VideoFormat.values()) {
                    _extension2VideoFormat.put(videoFormat.getExtension(), videoFormat);
                    _extension2VideoFormat.put(videoFormat.getExtension().toUpperCase(), videoFormat);
                }
            }
            return _extension2VideoFormat.get(extension);
        }
    }

    private final ConvertedVideoDao _convertedVideoDao;

    private final MediaDataProcessingJobExecutionDao _videoConversionJobExecutionDao;

    @Autowired
    public VideoService(ConvertedVideoDao convertedVideoDao, MediaDataProcessingJobExecutionDao videoConversionJobExecutionDao) {
        super();
        _convertedVideoDao = convertedVideoDao;
        _videoConversionJobExecutionDao = videoConversionJobExecutionDao;
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

    public static String createLabel(Integer widthOrHeight) {
        if (widthOrHeight == null) {
            return ORIGINAL_SIZE;
        }
        return "" + widthOrHeight;
    }

    /**
     * Creates the link used in html output
     * 
     * @param media
     * @param sizeLabel
     * @param videoFormat
     * @param priority
     * @return something like '/content/videos/123/640.mp4?priority=HIGH'
     */
    static String createLink(Media media, String sizeLabel, VideoFormat videoFormat, Priority priority) {
        if (StringUtils.isEmpty(sizeLabel)) {
            sizeLabel = ORIGINAL_SIZE;
        }
        return String.format("/content/videos/%d/%s.%s?priority=%s", media.getId(), sizeLabel, videoFormat.getExtension(), priority);
    }

    public String getOrCreateVideoUrl(Media media, HttpServletRequest servletRequest, Device device, boolean createJob, Priority priority) {
        Integer width = calculateBestWidth(device);
        VideoFormat videoFormat = getBestFormat(servletRequest.getHeader("User-Agent"));
        String sizeLabel = createLabel(width);
        if (createJob && !_convertedVideoDao.hasData(media, sizeLabel, videoFormat.toString())) {
            addVideoConversionJobExecutionIfNecessary(media, videoFormat, width, priority);
        }
        return createLink(media, sizeLabel, videoFormat, priority);
    }

    public boolean addVideoConversionJobExecutionIfNecessary(Media media, VideoFormat videoFormat, Integer widthOrHeight, Priority priority) {
        if (!_videoConversionJobExecutionDao.hasVideoConversionJob(media.getId(), videoFormat.toString(), widthOrHeight)) {
            VideoConversionJobExecution videoConversionJob = new VideoConversionJobExecution(media, videoFormat.toString(), widthOrHeight);
            if (priority != null) {
                videoConversionJob.setPriority(priority);
            }

            _videoConversionJobExecutionDao.makePersistent(videoConversionJob);
            LOG.info("New job {} for media {} with priority {} added.", videoConversionJob.getClass().getSimpleName(), media.getId(),
                    videoConversionJob.getPriority());
            return true;
        }
        return false;
    }

    public static boolean isPhoto(Media media) {
        return (StringUtils.isEmpty(media.getMediaType()) || media.getMediaType().startsWith(Media.IMAGE_PREFIX));
    }

    private VideoFormat getBestFormat(String userAgent) {
        if (userAgent.contains("Firefox")) {
            return VideoFormat.WebM_vp8;
        } else if (userAgent.contains("Chrome")) {
            return VideoFormat.OGG_Theora;
        }
        return VideoFormat.MP4_h264;
    }

    private Integer calculateBestWidth(Device device) {
        if (device.isNormal()) {
            return null;
        }
        if (device.isMobile()) {
            return 320;
        }
        return 400;
    }

}

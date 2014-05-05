package de.wehner.mediamagpie.conductor.webapp.services;

import java.io.File;

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

    private static final String ORIGINAL_SIZE = "original";

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

    public static String createLink(Media media, String sizeLabel, VideoFormat videoFormat, Priority priority) {
        if (StringUtils.isEmpty(sizeLabel)) {
            sizeLabel = ORIGINAL_SIZE;
        }
        return String.format("/content/videos/%d/%s.%s?priority=%s", media.getId(), sizeLabel, videoFormat.getExtension(), priority);
    }

    public String getOrCreateVideoUrl(Media media, HttpServletRequest servletRequest, Device device, boolean createJob, Priority priority) {
        String userAgent = servletRequest.getHeader("User-Agent");

        Integer width = calculateBestWidth(device);
        String sizeLabel = width == null ? ORIGINAL_SIZE : ("" + width);
        VideoFormat videoFormat = getBestFormat(userAgent);

        if (createJob && !_convertedVideoDao.hasData(media, sizeLabel, videoFormat.toString())) {
            addVideoConversionJobExecutionIfNecessary(media, sizeLabel, videoFormat, priority);
        }
        return createLink(media, sizeLabel, videoFormat, priority);
    }

    private boolean addVideoConversionJobExecutionIfNecessary(Media media, String label, VideoFormat videoFormat, Priority priority) {
        if (StringUtils.isEmpty(label)) {
            throw new IllegalArgumentException("label must not be empty");
        }
        if (!_videoConversionJobExecutionDao.hasResizeJob(media, label)) {
            VideoConversionJobExecution resizeImageJob = new VideoConversionJobExecution(media, label);
            if (priority != null) {
                resizeImageJob.setPriority(priority);
            }

            if (resizeImageJob.getMedia().getId() == null) {
                LOG.error("Media {} has no ID!", media.toString());
            }

            _videoConversionJobExecutionDao.makePersistent(resizeImageJob);
            LOG.info("Resize job for media '" + media.getId() + "' added with priority '" + resizeImageJob.getPriority() + "'.");
            return true;
        }
        return false;
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
        // TODO Auto-generated method stub
        return null;
    }

}

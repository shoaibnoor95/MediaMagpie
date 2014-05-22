package de.wehner.mediamagpie.conductor.webapp.controller;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;
import java.util.concurrent.Callable;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import de.wehner.mediamagpie.conductor.webapp.services.VideoService;
import de.wehner.mediamagpie.conductor.webapp.services.VideoService.VideoFormat;
import de.wehner.mediamagpie.core.util.TimeoutExecutor;
import de.wehner.mediamagpie.persistence.dao.ConvertedVideoDao;
import de.wehner.mediamagpie.persistence.dao.MediaDao;
import de.wehner.mediamagpie.persistence.entity.ConvertedVideo;
import de.wehner.mediamagpie.persistence.entity.Media;
import de.wehner.mediamagpie.persistence.entity.Priority;

@Controller
@RequestMapping({ "/content/videos/{mediaId}" })
public class VideoStreamController {

    public static final Logger LOG = LoggerFactory.getLogger(VideoStreamController.class);

    @Autowired
    private ConvertedVideoDao _convertedVideoDao;

    @Autowired
    private MediaDao _mediaDao;

    @Autowired
    private VideoService _videoService;

    @RequestMapping({ "/{label}.{extension}*" })
    public void streamVideo(@PathVariable final Long mediaId, @PathVariable final String label, @PathVariable final String extension,
            @RequestParam(required = false, value = "priority") String priority, OutputStream outputStream, HttpServletResponse response)
            throws IOException {
        LOG.trace("streaming video for media {} with label '{}'...", mediaId, label);
        final Media media = _mediaDao.getById(mediaId);

        // VideoFormat videoFormat = VideoService.VideoFormat.extension2VideoFormat(extension);
        List<ConvertedVideo> convertedVideos = _convertedVideoDao.getData(media, label, extension, 1);
        ConvertedVideo convertedVideo = null;
        if (convertedVideos != null && !convertedVideos.isEmpty()) {
            try {
                convertedVideo = convertedVideos.get(0);
                ImageController.readFileIntoOutputStream(convertedVideo.getPathToFile(), outputStream);
            } catch (FileNotFoundException e) {
                LOG.info("Remove {} '{}' from db.", ConvertedVideo.class.getSimpleName(), convertedVideo.getId());
                _convertedVideoDao.makeTransient(convertedVideo);
                convertedVideo = null;
            }
        }
        if (convertedVideo == null) {
            // first create a conversion job with high priority
            VideoFormat videoFormat = VideoFormat.extension2VideoFormat(extension);
            Integer widthOrHeight = (label.equals(VideoService.ORIGINAL_SIZE) ? null : Integer.parseInt(label));
            _videoService.addVideoConversionJobExecutionIfNecessary(media, videoFormat, widthOrHeight, Priority.HIGH);

            // try to pull the result
            TimeoutExecutor jobFinishTester = new TimeoutExecutor(1000, 250);
            ByteArrayOutputStream osWithThumbImpage = jobFinishTester.callUntilReturnIsNotNull(new Callable<ByteArrayOutputStream>() {

                @Override
                public ByteArrayOutputStream call() throws Exception {
                    List<ConvertedVideo> availableVideos = _convertedVideoDao.getData(media, label, extension, 1);
                    if (!availableVideos.isEmpty()) {
                        ByteArrayOutputStream os = new ByteArrayOutputStream();
                        try {
                            ImageController.readFileIntoOutputStream(availableVideos.get(0).getPathToFile(), os);
                        } catch (IOException e) {
                            IOUtils.closeQuietly(os);
                            return null;
                        }
                        return os;
                    }
                    return null;
                }
            });

            if (osWithThumbImpage != null) {
                osWithThumbImpage.writeTo(outputStream);
                IOUtils.closeQuietly(osWithThumbImpage);
            }
        }
        LOG.trace("streaming video for media {} with label '{}'...DONE", mediaId, label);
    }

    public static String getBaseRequestMappingUrl() {
        return VideoStreamController.class.getAnnotation(RequestMapping.class).value()[0];
    }

}

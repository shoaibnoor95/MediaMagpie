package de.wehner.mediamagpie.conductor.webapp.controller;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
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

import de.wehner.mediamagpie.common.persistence.entity.Media;
import de.wehner.mediamagpie.common.persistence.entity.Priority;
import de.wehner.mediamagpie.common.persistence.entity.ThumbImage;
import de.wehner.mediamagpie.conductor.persistence.dao.MediaDao;
import de.wehner.mediamagpie.conductor.persistence.dao.ThumbImageDao;
import de.wehner.mediamagpie.conductor.webapp.services.ImageService;
import de.wehner.mediamagpie.conductor.webapp.util.TimeoutExecutor;

@Controller
@RequestMapping({ "/content/images/{mediaId}" })
public class ImageController {
    private static final String SRC_MAIN_WEBAPP_STATIC_IMAGES_UI_ANIM_BASIC_16X16_GIF = "src/main/webapp/static/images/ui-anim_basic_16x16.gif";

    public static final Logger LOG = LoggerFactory.getLogger(ImageController.class);

    @Autowired
    private ThumbImageDao _thumbImageDao;
    @Autowired
    private MediaDao _mediaDao;

    @Autowired
    private ImageService _imageService;

    @RequestMapping({ "/{label}.*" })
    public void streamImageContent(@PathVariable final Long mediaId, @PathVariable final String label,
            @RequestParam(required = false, value = "priority") String priority, OutputStream outputStream, HttpServletResponse response)
            throws IOException {
        LOG.debug("streaming image id: " + mediaId + " with label '" + label + "'...");
        if (label.equals("original")) {
            Media media = _mediaDao.getById(mediaId);
            if (media != null) {
                try {
                    readImageIntoOutputStream(media.getFileFromUri().getPath(), outputStream);
                } catch (FileNotFoundException e) {
                    LOG.warn("Remove Media '" + media.getId() + "' from db.", e);
                    _mediaDao.makeTransient(media);
                    _mediaDao.getPersistenceService().flipTransaction();
                }
            }
        } else {
            ThumbImage thumbImage = _thumbImageDao.getByMediaIdAndLabel(mediaId, label);
            if (thumbImage != null) {
                try {
                    readImageIntoOutputStream(thumbImage.getPathToImage(), outputStream);
                } catch (FileNotFoundException e) {
                    LOG.info("Remove ThumbImage '" + thumbImage.getId() + "' from db.");
                    _thumbImageDao.makeTransient(thumbImage);
                    thumbImage = null;
                }
            }
            if (thumbImage == null) {
                // first create a resize job with high priority
                _imageService.addImageResizeJobExecutionIfNecessary(label, _mediaDao.getById(mediaId), Priority.valueOf(priority));

                // try to pull the result
                TimeoutExecutor jobFinishTester = new TimeoutExecutor(1000, 250);
                byte[] result = jobFinishTester.callUntilReturnIsNotNull(new Callable<byte[]>() {

                    @Override
                    public byte[] call() throws Exception {
                        ThumbImage thumbImage = _thumbImageDao.getByMediaIdAndLabel(mediaId, label);
                        if (thumbImage != null) {
                            ByteArrayOutputStream os = new ByteArrayOutputStream();
                            if (readImageIntoOutputStream(thumbImage.getPathToImage(), os)) {
                                return os.toByteArray();
                            }
                        }
                        return null;
                    }
                });

                if (result != null) {
                    IOUtils.write(result, outputStream);
                } else {
                    // image is not available, maybe we have to wait a little bit longer until the resize job is finished
                    if (!readImageIntoOutputStream(SRC_MAIN_WEBAPP_STATIC_IMAGES_UI_ANIM_BASIC_16X16_GIF, outputStream)) {
                        throw new RuntimeException("Internal error: Can not find picture in path '"
                                + SRC_MAIN_WEBAPP_STATIC_IMAGES_UI_ANIM_BASIC_16X16_GIF + "'.");
                    }
                }
            }
        }
        LOG.debug("streaming image id: " + mediaId + " with label '" + label + "'...DONE");
    }

    private boolean readImageIntoOutputStream(String pathToContent, OutputStream outputStream) throws IOException {
        InputStream inputStream = null;
        boolean wasSuccessful = false;
        try {
            LOG.debug("Try reading file '" + pathToContent + "'.");
            inputStream = new FileInputStream(pathToContent);
            byte[] rawBytes = IOUtils.toByteArray(inputStream);
            outputStream.write(rawBytes);
            wasSuccessful = true;
        } finally {
            IOUtils.closeQuietly(inputStream);
        }
        return wasSuccessful;
    }
}

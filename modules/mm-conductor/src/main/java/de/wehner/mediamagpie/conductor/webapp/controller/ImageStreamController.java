package de.wehner.mediamagpie.conductor.webapp.controller;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import de.wehner.mediamagpie.conductor.webapp.services.ImageService;
import de.wehner.mediamagpie.persistence.dao.MediaDao;
import de.wehner.mediamagpie.persistence.dao.ThumbImageDao;
import de.wehner.mediamagpie.persistence.entity.Media;
import de.wehner.mediamagpie.persistence.entity.Priority;
import de.wehner.mediamagpie.persistence.entity.ThumbImage;

@Controller
@RequestMapping({ "/content/images/{mediaId}" })
public class ImageStreamController {

    // private static final String SRC_MAIN_WEBAPP_STATIC_IMAGES_UI_ANIM_BASIC_16X16_GIF =
    // "src/main/webapp/static/images/ui-anim_basic_16x16.gif";

    public static final Logger LOG = LoggerFactory.getLogger(ImageStreamController.class);

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
        LOG.trace("streaming image id: " + mediaId + " with label '" + label + "'...");
        if (label.equals("original")) {
            Media media = _mediaDao.getById(mediaId);
            if (media != null) {
                try {
                    readFileIntoOutputStream(media.getFileFromUri().getPath(), outputStream);
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
                    readFileIntoOutputStream(thumbImage.getPathToImage(), outputStream);
                } catch (FileNotFoundException e) {
                    LOG.info("Remove ThumbImage '" + thumbImage.getId() + "' from db.");
                    _thumbImageDao.makeTransient(thumbImage);
                    thumbImage = null;
                }
            }

            if (thumbImage == null) {
                // first create a resize job with high priority
                _imageService.addImageResizeJobExecutionIfNecessary(label, _mediaDao.getById(mediaId), Priority.valueOf(priority));

                // // try to pull the result
                // TimeoutExecutor jobFinishTester = new TimeoutExecutor(500, 250);
                // ByteArrayOutputStream osWithThumbImpage = jobFinishTester.callUntilReturnIsNotNull(new Callable<ByteArrayOutputStream>()
                // {
                //
                // @Override
                // public ByteArrayOutputStream call() throws Exception {
                // ThumbImage thumbImage = _thumbImageDao.getByMediaIdAndLabel(mediaId, label);
                // if (thumbImage != null) {
                // ByteArrayOutputStream os = new ByteArrayOutputStream();
                // try {
                // readFileIntoOutputStream(thumbImage.getPathToImage(), os);
                // } catch (IOException e) {
                // IOUtils.closeQuietly(os);
                // return null;
                // }
                // return os;
                // }
                // return null;
                // }
                // });
                //
                // if (osWithThumbImpage != null) {
                // osWithThumbImpage.writeTo(outputStream);
                // IOUtils.closeQuietly(osWithThumbImpage);
                // } else {
                // // image is not available, maybe we have to wait a little bit longer until the resize job is finished
                // try {
                // readFileIntoOutputStream(SRC_MAIN_WEBAPP_STATIC_IMAGES_UI_ANIM_BASIC_16X16_GIF, outputStream);
                // } catch (IOException ex) {
                // throw new RuntimeException("Internal error: Can not find picture in path '"
                // + SRC_MAIN_WEBAPP_STATIC_IMAGES_UI_ANIM_BASIC_16X16_GIF + "'.", ex);
                // }
                // }
                // rwe 24.10.2014: We use a java script solution which tries to reload each image again that fails with 404 error.
                response.setStatus(404);
            }
        }
        LOG.trace("streaming image id: " + mediaId + " with label '" + label + "'...DONE");
    }

    /**
     * Simply reads a file into a given OutputStream.
     * 
     * @param pathToContent
     * @param outputStream
     * @throws IOException
     */
    public static void readFileIntoOutputStream(String pathToContent, OutputStream outputStream) throws IOException {
        InputStream inputStream = null;
        try {
            LOG.trace("Try reading file '" + pathToContent + "'.");
            inputStream = new FileInputStream(pathToContent);
            IOUtils.copy(inputStream, outputStream);
        } catch (IOException e) {
            LOG.error("Can not read file '{}'.", pathToContent, e);
            throw e;
        } finally {
            IOUtils.closeQuietly(inputStream);
        }
    }

    public static String getBaseRequestMappingUrl() {
        return ImageStreamController.class.getAnnotation(RequestMapping.class).value()[0];
    }

}

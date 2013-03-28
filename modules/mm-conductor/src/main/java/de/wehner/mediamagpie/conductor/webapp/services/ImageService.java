package de.wehner.mediamagpie.conductor.webapp.services;

import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang.StringUtils;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.mortbay.log.Log;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import de.wehner.mediamagpie.common.persistence.entity.ImageResizeJobExecution;
import de.wehner.mediamagpie.common.persistence.entity.JobStatus;
import de.wehner.mediamagpie.common.persistence.entity.LifecyleStatus;
import de.wehner.mediamagpie.common.persistence.entity.Media;
import de.wehner.mediamagpie.common.persistence.entity.MediaDeleteJobExecution;
import de.wehner.mediamagpie.common.persistence.entity.Priority;
import de.wehner.mediamagpie.common.persistence.entity.properties.MainConfiguration;
import de.wehner.mediamagpie.common.persistence.entity.properties.UserConfiguration;
import de.wehner.mediamagpie.common.util.ExceptionUtil;
import de.wehner.mediamagpie.conductor.metadata.CameraMetaData;
import de.wehner.mediamagpie.conductor.persistence.dao.ImageResizeJobExecutionDao;
import de.wehner.mediamagpie.conductor.persistence.dao.MediaDao;
import de.wehner.mediamagpie.conductor.persistence.dao.MediaDeleteJobExecutionDao;
import de.wehner.mediamagpie.conductor.persistence.dao.ThumbImageDao;
import de.wehner.mediamagpie.conductor.webapp.controller.commands.MediaThumbCommand;

@Service
public class ImageService {

    public static final Logger LOG = LoggerFactory.getLogger(ImageService.class);

    private final ThumbImageDao _thumbImageDao;
    
    private final MediaDao _mediaDao;

    private final ImageResizeJobExecutionDao _imageResizeJobExecutionDao;

    private final MediaDeleteJobExecutionDao _mediaDeleteJobExecutionDao;

    private final ObjectMapper _mapper;

    @Autowired
    public ImageService(ThumbImageDao imageDao, MediaDao mediaDao, ImageResizeJobExecutionDao imageResizeJobDao,
            MediaDeleteJobExecutionDao mediaDeleteJobDao) {
        super();
        _thumbImageDao = imageDao;
        _mediaDao = mediaDao;
        _imageResizeJobExecutionDao = imageResizeJobDao;
        _mediaDeleteJobExecutionDao = mediaDeleteJobDao;
        _mapper = new ObjectMapper();
    }

    // better use the resizeImageInQueue() method
    @Deprecated
    public static File resizeImage(File originImage, long id, File destPath, int width, int height) {
        try {
            Log.info("Begin resizing image '" + originImage.getPath() + "' to " + width + " x " + height + "...");
            BufferedImage originBitmap = ImageIO.read(originImage);

            if (originBitmap == null) {
                // image can not be loaded - mayby this is not a valid image file
                LOG.error("Image '" + originImage.getPath() + "' seems to be corrupted or is no image.");
                return null;
            }

            int thumbWidth = originBitmap.getWidth();
            int thumbHeight = originBitmap.getHeight();
            float minRatio = 1.0f;
            if (originBitmap.getWidth() >= width || originBitmap.getHeight() >= height) {
                float ratioX = (float) width / originBitmap.getWidth();
                float ratioY = (float) height / originBitmap.getHeight();

                minRatio = Math.min(ratioX, ratioY);

                thumbWidth = (int) Math.max(1, originBitmap.getWidth() * minRatio);
                thumbHeight = (int) Math.max(1, originBitmap.getHeight() * minRatio);
            }

            BufferedImage resizedBitmap = new BufferedImage(thumbWidth, thumbHeight, BufferedImage.TYPE_INT_RGB);
            Graphics2D graphics2D = resizedBitmap.createGraphics();
            try {
                graphics2D.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
                graphics2D.drawRenderedImage(originBitmap, AffineTransform.getScaleInstance(minRatio, minRatio));
                File thumbImagePath = buildThumbImagePath(originImage, id, destPath, thumbWidth, thumbHeight);
                ImageIO.write(resizedBitmap, FilenameUtils.getExtension(thumbImagePath.getPath()), thumbImagePath);
                Log.info("Begin resizing image... finished. Resized image into file '" + thumbImagePath.getPath() + "'.");
                return thumbImagePath;
            } finally {
                graphics2D.dispose();
            }
        } catch (Throwable e) {
            Log.warn("Exception arised during image resizing.", e);
            throw new RuntimeException(e);
        }
    }

    public static File resizeImageInQueue(File originImage, long id, File destPath, int width, int height, int necessaryRotation) {
        try {
            Log.info("Begin resizing image '" + originImage.getPath() + "' to " + width + " x " + height + " with rotation " + necessaryRotation + "...");
            BufferedImage originBitmap = ImageIO.read(originImage);
            if (originBitmap == null) {
                // image can not be loaded - mayby this is not a valid image file
                LOG.error("Image '" + originImage.getPath() + "' seems to be corrupted or is no image.");
                return null;
            }

            // scale image
            Dimension newDimension = computeNewDimension(originBitmap, width, height);
            BufferedImage newImage = resizeImageWithAffineTransform(originBitmap, newDimension);

            // rotate if necessary
            if (necessaryRotation != 0) {
                newImage = rotateImage(newImage, necessaryRotation);
            }

            // save result to file
            File thumbImagePath = buildThumbImagePath(originImage, id, destPath, newImage.getWidth(), newImage.getHeight());
            ImageIO.write(newImage, FilenameUtils.getExtension(thumbImagePath.getPath()), thumbImagePath);
            Log.info("Begin resizing image... finished. Resized image into file '" + thumbImagePath.getPath() + "'.");
            return thumbImagePath;
        } catch (Throwable e) {
            Log.warn("Exception arised during image resizing.", e);
            throw new RuntimeException(e);
        }
    }

    public static Dimension computeNewDimension(BufferedImage originBitmap, int width, int height) {
        int newWidth = originBitmap.getWidth();
        int newHeight = originBitmap.getHeight();
        float minRatio = 1.0f;
        if (originBitmap.getWidth() >= width || originBitmap.getHeight() >= height) {
            float ratioX = (float) width / originBitmap.getWidth();
            float ratioY = (float) height / originBitmap.getHeight();
            minRatio = Math.min(ratioX, ratioY);
            newWidth = (int) Math.max(1, originBitmap.getWidth() * minRatio);
            newHeight = (int) Math.max(1, originBitmap.getHeight() * minRatio);
        }
        return new Dimension(newWidth, newHeight);
    }

    /**
     * Resizes an image to new dimensions. See: http://stackoverflow.com/questions/4787066/how-to-resize-and-rotate-an-image
     * 
     * @param srcBImage
     *            The source image
     * @param newSize
     *            The new with and height
     * @return The resized image as <code>BufferedImage</code>
     */
    public static BufferedImage resizeImageWithAffineTransform(BufferedImage srcBImage, Dimension newSize) {
        BufferedImage bdest;
        // scale the image
        int w = newSize.width;
        int h = newSize.height;
        bdest = new BufferedImage(w, h, srcBImage.getType());
        Graphics2D g = bdest.createGraphics();
        AffineTransform at = AffineTransform.getScaleInstance((double) w / srcBImage.getWidth(), (double) h / srcBImage.getHeight());
        g.drawRenderedImage(srcBImage, at);
        g.dispose();
        return bdest;
    }

    /**
     * The code is based on an example on: http://stackoverflow.com/questions/4787066/how-to-resize-and-rotate-an-image
     * 
     * @param srcBImage
     * @param angle
     *            The angle in degree
     * @return
     */
    public static BufferedImage rotateImage(BufferedImage srcBImage, double angle) {
        angle = Math.toRadians(angle);
        double sin = Math.abs(Math.sin(angle));
        double cos = Math.abs(Math.cos(angle));
        int w = srcBImage.getWidth(), h = srcBImage.getHeight();
        int neww = (int) Math.floor(w * cos + h * sin);
        int newh = (int) Math.floor(h * cos + w * sin);
        BufferedImage result = new BufferedImage(neww, newh, srcBImage.getType());
        Graphics2D g = result.createGraphics();
        g.translate((neww - w) / 2, (newh - h) / 2);
        g.rotate(angle, w / 2, h / 2);
        g.drawRenderedImage(srcBImage, null);
        g.dispose();
        return result;
    }

    private static File buildThumbImagePath(File originFileName, long id, File destPath, int width, int height) {
        String pathOriginal = originFileName.getPath();
        String thumbFileName = String.format("%s_%d_%dx%d.%s", FilenameUtils.getBaseName(pathOriginal), id, width, height,
                FilenameUtils.getExtension(pathOriginal));
        if (!destPath.exists()) {
            try {
                FileUtils.forceMkdir(destPath);
            } catch (IOException e) {
                throw ExceptionUtil.convertToRuntimeException(e);
            }
        }
        return new File(destPath, thumbFileName);
    }

    public String getOrCreateImageUrl(Media media, Integer size) {
        return getOrCreateImageUrl(media, size, true, Priority.NORMAL);
    }

    public String getOrCreateImageUrl(Media media, Integer size, boolean createJob, Priority priority) {
        String label = (size != null) ? size.toString() : null;
        if (label != null && createJob && !_thumbImageDao.hasData(media, label)) {
            addImageResizeJobExecutionIfNecessary(label, media, priority);
        }
        return createLink(media, label, priority);
    }

    public boolean addImageResizeJobExecutionIfNecessary(String label, Media media, Priority priority) {
        if (StringUtils.isEmpty(label)) {
            throw new IllegalArgumentException("label must not be empty");
        }
        if (!_imageResizeJobExecutionDao.hasResizeJob(media, label)) {
            ImageResizeJobExecution resizeImageJob = new ImageResizeJobExecution(media, label);
            if (priority != null) {
                resizeImageJob.setPriority(priority);
            }
            _imageResizeJobExecutionDao.makePersistent(resizeImageJob);
            LOG.info("Resize job for media '" + media.getId() + "' added with priority '" + resizeImageJob.getPriority() + "'.");
            return true;
        }
        return false;
    }

    public String createLink(Media media, String imageLabel, Priority priority) {
        String label = imageLabel == null ? "original" : imageLabel;
        File mediaOriginalFile = media.getFileFromUri();
        return String.format("/content/images/%d/%s.%s?priority=%s", media.getId(), label, FilenameUtils.getExtension(mediaOriginalFile.getPath()),
                priority);
    }

    public boolean addDeleteJobIfNecessary(Media media) {
        if (!_mediaDeleteJobExecutionDao.hasJob(media)) {
            // finish all image resize jobs before
            for (ImageResizeJobExecution resizeJob : _imageResizeJobExecutionDao.getJobsByMedia(media)) {
                resizeJob.setJobStatus(JobStatus.STOPPING);
                _imageResizeJobExecutionDao.makePersistent(resizeJob);
            }

            // add new delete job for this media
            MediaDeleteJobExecution jobExecution = new MediaDeleteJobExecution(media);
            jobExecution.setPriority(Priority.LOW);
            _mediaDeleteJobExecutionDao.makePersistent(jobExecution);
            LOG.info("Delete job for media '" + media.getId() + "' added with priority '" + jobExecution.getPriority() + "'.");
            return true;
        }
        return false;
    }

    /**
     * This method removes a media entity from database.
     * 
     * @param media
     */
    public void deleteMediaCompletely(Media media) {
        deleteMediaCompletely(Arrays.asList(media));
    }

    /**
     * This method removes multiple media entities from database.
     * 
     * @param medias
     */
    public void deleteMediaCompletely(List<Media> medias) {
        for (Media media : medias) {
            LOG.debug("Mark media '" + media + "' with uri '" + media.getUri() + "' for removal from database.");
            media.setLifeCycleStatus(LifecyleStatus.MarkedForErasure);
            _mediaDao.makePersistent(media);
            addDeleteJobIfNecessary(media);
        }
    }

    public MediaThumbCommand createMediaThumbCommand(Media media, MainConfiguration mainConfiguration, UserConfiguration userConfiguration,
            HttpServletRequest request) {
        int thumbSize = mainConfiguration.getDefaultThumbSize();
        if (userConfiguration != null) {
            thumbSize = userConfiguration.getThumbImageSize();
        }

        MediaThumbCommand mediaThumbCommand = new MediaThumbCommand(media);
        mediaThumbCommand.setId(media.getId());
        final String contextPath = request.getContextPath();
        final String thumbImageUrl = getOrCreateImageUrl(media, thumbSize, true, Priority.NORMAL);
        mediaThumbCommand.setUrlThumbImage(contextPath + thumbImageUrl);
        final int thumbDetailMediumSize = mainConfiguration.getDefaultGalleryDetailThumbSize();
        final String thumbDetailImageUrl = getOrCreateImageUrl(media, thumbDetailMediumSize, false, Priority.LOW);
        mediaThumbCommand.setUrlThumbDetail(contextPath + thumbDetailImageUrl);
        String downloadUrl = getOrCreateImageUrl(media, null, false, Priority.LOW);
        mediaThumbCommand.setUrlDownload(contextPath + downloadUrl);
        String title = !StringUtils.isEmpty(media.getName()) ? media.getName() : ("<" + media.getFileFromUri().getName() + ">");
        mediaThumbCommand.setTitle(title);
        mediaThumbCommand.setDescription(media.getDescription());

        final String jsonCameraMetaData = media.getCameraMetaData();
        if (!StringUtils.isEmpty(jsonCameraMetaData) && !"null".equals(jsonCameraMetaData)) {
            try {
                CameraMetaData cameraMetaData = _mapper.readValue(jsonCameraMetaData, CameraMetaData.class);
                mediaThumbCommand.setCameraMetaData(cameraMetaData);
            } catch (JsonParseException e) {
                LOG.error("Internal error", e);
                e.printStackTrace();
            } catch (JsonMappingException e) {
                LOG.error("Internal error", e);
            } catch (IOException e) {
                LOG.error("Internal error", e);
            }
        }

        return mediaThumbCommand;
    }
}

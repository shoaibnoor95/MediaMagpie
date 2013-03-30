package de.wehner.mediamagpie.conductor.webapp.services;

import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.awt.image.renderable.ParameterBlock;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;

import javax.imageio.ImageIO;
import javax.media.jai.JAI;
import javax.media.jai.OpImage;
import javax.media.jai.RenderedOp;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.mortbay.log.Log;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StopWatch;

import com.sun.media.jai.codec.SeekableStream;

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
import de.wehner.mediamagpie.conductor.webapp.media.process.ImageProcessorImageIO;
import de.wehner.mediamagpie.conductor.webapp.media.process.ImageProcessorJAI;

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

    /**** TODO rwe: testing JAI *********************/
    /**
     * The JAI.create action name for handling a stream.
     */
    private static final String JAI_STREAM_ACTION = "stream";

    /**
     * The JAI.create action name for handling a resizing using a subsample averaging technique.
     */
    private static final String JAI_SUBSAMPLE_AVERAGE_ACTION = "SubsampleAverage";

    /**
     * The JAI.create encoding format name for JPEG.
     */
    private static final String JAI_ENCODE_FORMAT_JPEG = "JPEG";

    /**
     * The JAI.create action name for encoding image data.
     */
    private static final String JAI_ENCODE_ACTION = "encode";

    /**
     * The http content type/mime-type for JPEG images.
     */
    private static final String JPEG_CONTENT_TYPE = "image/jpeg";

    private int mMaxWidth = 800;

    private int mMaxWidthThumbnail = 150;

    /**
     * This method takes in an image as a byte array (currently supports GIF, JPG, PNG and possibly other formats) and resizes it to have a
     * width no greater than the pMaxWidth parameter in pixels. It converts the image to a standard quality JPG and returns the byte array
     * of that JPG image.
     * 
     * @param pImageData
     *            the image data.
     * @param pMaxWidth
     *            the max width in pixels, 0 means do not scale.
     * @return the resized JPG image.
     * @throws IOException
     *             if the image could not be manipulated correctly.
     */
    private byte[] resizeImageAsJPG(byte[] pImageData, int pMaxWidth) throws IOException {
        InputStream imageInputStream = new ByteArrayInputStream(pImageData);
        // read in the original image from an input stream
        SeekableStream seekableImageStream = SeekableStream.wrapInputStream(imageInputStream, true);
        RenderedOp originalImage = JAI.create(JAI_STREAM_ACTION, seekableImageStream);
        ((OpImage) originalImage.getRendering()).setTileCache(null);
        int origImageWidth = originalImage.getWidth();
        // now resize the image
        double scale = 1.0;
        if (pMaxWidth > 0 && origImageWidth > pMaxWidth) {
            scale = (double) pMaxWidth / originalImage.getWidth();
        }
        ParameterBlock paramBlock = new ParameterBlock();
        paramBlock.addSource(originalImage); // The source image
        paramBlock.add(scale); // The xScale
        paramBlock.add(scale); // The yScale
        paramBlock.add(0.0); // The x translation
        paramBlock.add(0.0); // The y translation

        RenderingHints qualityHints = new RenderingHints(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);

        RenderedOp resizedImage = JAI.create(JAI_SUBSAMPLE_AVERAGE_ACTION, paramBlock, qualityHints);

        // lastly, write the newly-resized image to an output stream, in a specific encoding
        ByteArrayOutputStream encoderOutputStream = new ByteArrayOutputStream();
        JAI.create(JAI_ENCODE_ACTION, resizedImage, encoderOutputStream, JAI_ENCODE_FORMAT_JPEG, null);
        // Export to Byte Array
        byte[] resizedImageByteArray = encoderOutputStream.toByteArray();
        return resizedImageByteArray;
    }

    public static File resizeImageInQueue(File originImage, long id, File destPath, int width, int height, int necessaryRotation) {
        Log.info("Begin resizing image '" + originImage.getPath() + "' to " + width + " x " + height + " with rotation " + necessaryRotation + "...");
        StopWatch stopWatch = new StopWatch();
        try {
            // scale image
            stopWatch.start("resize direct API (" + width + "/" + height + ")");
            ImageProcessorImageIO imageResizeByImageIO = new ImageProcessorImageIO(originImage);
            BufferedImage resizedImage = imageResizeByImageIO.resize(width, height);
            // rotate if necessary
            if (necessaryRotation != 0) {
                resizedImage = rotateImage(resizedImage, necessaryRotation);
            }

            // save result to file
            File thumbImagePath = buildThumbImagePath(originImage, id, destPath, resizedImage.getWidth(), resizedImage.getHeight());
            ImageIO.write(resizedImage, FilenameUtils.getExtension(thumbImagePath.getPath()), thumbImagePath);
            stopWatch.stop();
            Log.info("Begin resizing image... finished. Resized image into file '" + thumbImagePath.getPath() + "'.");
            return thumbImagePath;
        } catch (Throwable e) {
            stopWatch.stop();
            Log.warn("Exception arised during image resizing. Try JAI library for processing...", e);

            try {
                stopWatch.start("resize JAI (" + width + "/" + height + ")");
                FileInputStream is = new FileInputStream(originImage);
                ImageProcessorJAI imageProcessorJAI = new ImageProcessorJAI(is);
                ByteArrayOutputStream resizedImage = imageProcessorJAI.resize(width, height);
                File thumbImagePath = buildThumbImagePath(originImage, id, destPath, imageProcessorJAI.getProcessedDimension().width,
                        imageProcessorJAI.getProcessedDimension().height);
                IOUtils.closeQuietly(is);
                FileUtils.writeByteArrayToFile(thumbImagePath, resizedImage.toByteArray());
                stopWatch.stop();
                Log.info("Begin resizing image... finished. Resized image into file '" + thumbImagePath.getPath() + "'.");
                return thumbImagePath;
            } catch (Throwable e2) {
                throw new RuntimeException(e2);
            }
        } finally {
            System.out.println(stopWatch.prettyPrint());
        }
    }

    public static Dimension computeNewDimension(int origWidth, int origHeight, int width, int height) {
        float minRatio = 1.0f;
        if (origWidth >= width || origHeight >= height) {
            float ratioX = (float) width / origWidth;
            float ratioY = (float) height / origHeight;
            minRatio = Math.min(ratioX, ratioY);
            origWidth = (int) Math.max(1, origWidth * minRatio);
            origHeight = (int) Math.max(1, origHeight * minRatio);
        }
        return new Dimension(origWidth, origHeight);
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

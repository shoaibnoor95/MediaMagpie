package de.wehner.mediamagpie.conductor.webapp.services;

import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StopWatch;

import de.wehner.mediamagpie.api.MediaStorageInfo;
import de.wehner.mediamagpie.api.MediaType;
import de.wehner.mediamagpie.aws.s3.S3ClientFacade;
import de.wehner.mediamagpie.aws.s3.S3MediaExportRepository;
import de.wehner.mediamagpie.conductor.metadata.CameraMetaData;
import de.wehner.mediamagpie.conductor.webapp.controller.commands.MediaThumbCommand;
import de.wehner.mediamagpie.conductor.webapp.processor.AbstractImageProcessor;
import de.wehner.mediamagpie.conductor.webapp.processor.ImageProcessorFactory;
import de.wehner.mediamagpie.core.util.ExceptionUtil;
import de.wehner.mediamagpie.persistence.dao.MediaDao;
import de.wehner.mediamagpie.persistence.dao.MediaDataProcessingJobExecutionDao;
import de.wehner.mediamagpie.persistence.dao.MediaDeleteJobExecutionDao;
import de.wehner.mediamagpie.persistence.dao.ThumbImageDao;
import de.wehner.mediamagpie.persistence.entity.CloudMediaDeleteJobExecution;
import de.wehner.mediamagpie.persistence.entity.CloudSyncJobExecution.CloudType;
import de.wehner.mediamagpie.persistence.entity.ImageResizeJobExecution;
import de.wehner.mediamagpie.persistence.entity.JobStatus;
import de.wehner.mediamagpie.persistence.entity.LifecyleStatus;
import de.wehner.mediamagpie.persistence.entity.Media;
import de.wehner.mediamagpie.persistence.entity.MediaDeleteJobExecution;
import de.wehner.mediamagpie.persistence.entity.Priority;
import de.wehner.mediamagpie.persistence.entity.VideoConversionJobExecution;
import de.wehner.mediamagpie.persistence.entity.properties.MainConfiguration;
import de.wehner.mediamagpie.persistence.entity.properties.UserConfiguration;

@Service
public class ImageService {

    private static final String ORIGINAL_SIZE = "original";

    public static final Logger LOG = LoggerFactory.getLogger(ImageService.class);

    private final ThumbImageDao _thumbImageDao;

    private final MediaDao _mediaDao;

    private final MediaDataProcessingJobExecutionDao _jobExecutionDao;

    private final MediaDeleteJobExecutionDao _mediaDeleteJobExecutionDao;

    private final ObjectMapper _mapper;

    private final List<ImageProcessorFactory> _imageProcessorFactories;

    @Autowired
    public ImageService(ThumbImageDao imageDao, MediaDao mediaDao, MediaDataProcessingJobExecutionDao jobDao,
            MediaDeleteJobExecutionDao mediaDeleteJobDao, List<ImageProcessorFactory> imageProcessors) {
        super();
        _thumbImageDao = imageDao;
        _mediaDao = mediaDao;
        _jobExecutionDao = jobDao;
        _mediaDeleteJobExecutionDao = mediaDeleteJobDao;
        _mapper = new ObjectMapper();
        _imageProcessorFactories = new ArrayList<ImageProcessorFactory>(imageProcessors);
        // sort processors to get the fastes first
        Collections.sort(_imageProcessorFactories, new Comparator<ImageProcessorFactory>() {

            @Override
            public int compare(ImageProcessorFactory o1, ImageProcessorFactory o2) {
                return (o1.getPerformanceIndex() - o2.getPerformanceIndex());
            }
        });
    }

    // the original resizing algorithm
    @Deprecated
    public static File resizeImageWithImageIO(File originImage, long id, File destPath, int width, int height) {
        try {
            LOG.info("Begin resizing image '" + originImage.getPath() + "' to " + width + " x " + height + "...");
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
                File thumbImagePath = suggestThumbImageFile(originImage, id, destPath, thumbWidth, thumbHeight);
                ImageIO.write(resizedBitmap, FilenameUtils.getExtension(thumbImagePath.getPath()), thumbImagePath);
                LOG.info("Begin resizing image... finished. Resized image into file '" + thumbImagePath.getPath() + "'.");
                return thumbImagePath;
            } finally {
                graphics2D.dispose();
            }
        } catch (Throwable e) {
            LOG.warn("Exception arised during image resizing.", e);
            throw new RuntimeException(e);
        }
    }

    public File resizeImage(File originImage, long mediaId, File destPath, int width, int height, int necessaryRotation) {
        LOG.info("Begin resizing image '" + originImage.getPath() + "' to " + width + " x " + height + " with rotation " + necessaryRotation + "...");
        StopWatch stopWatch = new StopWatch();

        for (ImageProcessorFactory imageProcessorFactory : _imageProcessorFactories) {
            AbstractImageProcessor imageProcessor = null;
            try {
                // scale image
                stopWatch.start(imageProcessorFactory.getClass().getSimpleName() + ": resize direct API (" + width + "/" + height + ")");
                String extension = originImage.getName().toUpperCase();
                if (!extension.endsWith(".JPG")) {
                    LOG.warn("Detected file name {} without clear image extension.", extension);
                }
                imageProcessor = imageProcessorFactory.createProcessor(originImage);
                imageProcessor.resize(width, height);
                // rotate if necessary
                if (necessaryRotation != 0) {
                    imageProcessor.rotateImage(necessaryRotation);
                }

                // save result to file
                Dimension imageDimension = imageProcessor.getProcessedImageDimension();
                File thumbImagePath = suggestThumbImageFile(originImage, mediaId, destPath, imageDimension.width, imageDimension.height);
                thumbImagePath = imageProcessor.write(thumbImagePath);
                stopWatch.stop();
                LOG.info("Begin resizing image... finished. Resized image into file '" + thumbImagePath.getPath() + "'.");
                return thumbImagePath;
            } catch (Throwable e) {
                stopWatch.stop();
                LOG.debug("Exception arised during image resizing. Try next image processor...", e);
            } finally {
                // System.out.println(stopWatch.prettyPrint());
                IOUtils.closeQuietly(imageProcessor);
            }
        }
        throw new RuntimeException("No image processor can resize an image with original file '" + originImage.getPath() + "'.");
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

    private static File suggestThumbImageFile(File originFileName, long id, File destPath, int width, int height) {
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
        String sizeLabel = (size != null) ? size.toString() : null;
        if (sizeLabel != null && createJob && !_thumbImageDao.hasData(media, sizeLabel)) {
            addImageResizeJobExecutionIfNecessary(sizeLabel, media, priority);
        }
        return createLink(media, sizeLabel, priority);
    }

    public boolean addImageResizeJobExecutionIfNecessary(String label, Media media, Priority priority) {
        if (StringUtils.isEmpty(label)) {
            throw new IllegalArgumentException("label must not be empty");
        }
        if (!_jobExecutionDao.hasResizeJob(media, label)) {
            ImageResizeJobExecution resizeImageJob = new ImageResizeJobExecution(media, label);
            if (priority != null) {
                resizeImageJob.setPriority(priority);
            }

            // rwe: try to find out mystery org.hibernate.exception.ConstraintViolationException when persisting the resize job
            if (resizeImageJob.getMedia().getId() == null) {
                LOG.error("Media {} has no ID!", media.toString());
            }

            _jobExecutionDao.makePersistent(resizeImageJob);
            LOG.info("Added Image/Video resize job ({}) for media '{}' added with priority '{}'.", resizeImageJob.getId(), media.getId(),
                    resizeImageJob.getPriority());
            return true;
        }
        return false;
    }

    public static String createLink(Media media, String imageLabel, Priority priority) {
        String label = imageLabel == null ? ORIGINAL_SIZE : imageLabel;
        File mediaOriginalFile = media.getFileFromUri();
        return String.format("/content/images/%d/%s.%s?priority=%s", media.getId(), label, FilenameUtils.getExtension(mediaOriginalFile.getPath()),
                priority);
    }

    public boolean addDeleteJobIfNecessary(Media media) {
        if (!_mediaDeleteJobExecutionDao.hasJobForDelete(media)) {
            // finish all image resize and video conversion jobs before
            for (ImageResizeJobExecution resizeJob : _jobExecutionDao.getJobsByMedia(media, Integer.MAX_VALUE, ImageResizeJobExecution.class)) {
                LOG.debug("Change status of job '{}' from '{}' -> 'STOPPING'.", resizeJob.getClass().getSimpleName(), resizeJob.getJobStatus());
                resizeJob.setJobStatus(JobStatus.STOPPING);
                _jobExecutionDao.makePersistent(resizeJob);
            }
            for (VideoConversionJobExecution job : _jobExecutionDao.getJobsByMediaId(media.getId(), Integer.MAX_VALUE, VideoConversionJobExecution.class)) {
                LOG.debug("Change status of job '{}' from '{}' -> 'STOPPING'.", job.getClass().getSimpleName(), job.getJobStatus());
                job.setJobStatus(JobStatus.STOPPING);
                _jobExecutionDao.makePersistent(job);
            }

            // add new delete job for this media
            MediaDeleteJobExecution mediaDeleteJobExecution = new MediaDeleteJobExecution(media);
            mediaDeleteJobExecution.setPriority(Priority.LOW);
            _mediaDeleteJobExecutionDao.makePersistent(mediaDeleteJobExecution);
            LOG.info("Added delete job for media '{}' added with priority '{}'.", media.getId(), mediaDeleteJobExecution.getPriority());

            // delete media from cloud (s3)
            S3MediaExportRepository s3MediaExportRepository = new S3MediaExportRepository((S3ClientFacade) null);
            MediaStorageInfo fileNameInfo = s3MediaExportRepository.getMediaStorageInfo(media.getOwner().getName(), MediaType.PHOTO, media.getHashValue(),
                    media.getOriginalFileName());
            String bucketName = s3MediaExportRepository.buildBucketTypeName(MediaType.PHOTO);
            CloudMediaDeleteJobExecution cloudMediaDeleteJobExecution = new CloudMediaDeleteJobExecution(bucketName, fileNameInfo, CloudType.S3,
                    media.getOwner());
            cloudMediaDeleteJobExecution.setPriority(Priority.LOW);
            _mediaDeleteJobExecutionDao.makePersistent(cloudMediaDeleteJobExecution);
            LOG.debug("Added cloud delete job for media '" + media.getId() + "' with priority '" + cloudMediaDeleteJobExecution.getPriority() + "'.");
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
            LOG.debug("Mark media '" + media + "' with uri '" + media.getUri() + "' for removal from database and filesystem.");
            media.setLifeCycleStatus(LifecyleStatus.MarkedForErasure);
            addDeleteJobIfNecessary(media);
            _mediaDao.makePersistent(media);
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

        // add camera meta data for photos only
        if (!(media.getMediaType() != null && media.getMediaType().startsWith(Media.VIDEO_PREFIX))) {
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
        }
        return mediaThumbCommand;
    }
}

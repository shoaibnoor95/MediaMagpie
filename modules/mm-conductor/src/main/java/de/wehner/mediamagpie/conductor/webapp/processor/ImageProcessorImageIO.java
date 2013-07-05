package de.wehner.mediamagpie.conductor.webapp.processor;

import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import javax.imageio.ImageIO;

import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ImageProcessorImageIO extends AbstractImageProcessor {

    private static final Logger LOG = LoggerFactory.getLogger(ImageProcessorImageIO.class);

    private BufferedImage originBitmap;
    private BufferedImage processedImage;

    public ImageProcessorImageIO(File originImage) throws IOException {
        originBitmap = ImageIO.read(originImage);
    }

    public ImageProcessorImageIO(InputStream is) throws IOException {
        originBitmap = ImageIO.read(is);
    }

    @Override
    public int getOriginalWidth() {
        return originBitmap.getWidth();
    }

    @Override
    public int getOriginalHeight() {
        return originBitmap.getHeight();
    }

    public void resize(int width, int height) {
        int origWidth = originBitmap.getWidth();
        int origHeight = originBitmap.getHeight();
        Dimension newDimension = computeNewDimension(origWidth, origHeight, width, height);
        processedImage = resizeImageWithAffineTransform(getImageToProcess(), newDimension);
    }

    @Override
    public void rotateImage(int angle) {
        processedImage = rotateImage(getImageToProcess(), angle);
    }

    @Override
    public Dimension getProcessedImageDimension() {
        return new Dimension(processedImage.getWidth(), processedImage.getHeight());
    }

    @Override
    public void write(File destFile) throws IOException {
        boolean write = ImageIO.write(processedImage, FilenameUtils.getExtension(destFile.getPath()), destFile);
        if (!write) {
            LOG.error("Can not write image into file '" + destFile.getPath() + "'.");
        }
    }

    @Override
    public void close() throws IOException {
        // nothing to do here
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

    private BufferedImage getImageToProcess() {
        if (processedImage != null) {
            return processedImage;
        }
        return originBitmap;
    }

}

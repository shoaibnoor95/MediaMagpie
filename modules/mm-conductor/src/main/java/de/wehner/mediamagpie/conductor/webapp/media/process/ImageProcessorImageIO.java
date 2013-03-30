package de.wehner.mediamagpie.conductor.webapp.media.process;

import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

public class ImageProcessorImageIO extends AbstractImageProcessor {

    private BufferedImage originBitmap;

    public ImageProcessorImageIO(File originImage) throws IOException {
        originBitmap = ImageIO.read(originImage);
    }

    @Override
    public int getOriginalWidth() {
        return originBitmap.getWidth();
    }

    @Override
    public int getOriginalHeight() {
        return originBitmap.getHeight();
    }

    public BufferedImage resize(int width, int height) {
        int origWidth = originBitmap.getWidth();
        int origHeight = originBitmap.getHeight();
        Dimension newDimension = computeNewDimension(origWidth, origHeight, width, height);
        BufferedImage newImage = resizeImageWithAffineTransform(originBitmap, newDimension);
        return newImage;
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


}

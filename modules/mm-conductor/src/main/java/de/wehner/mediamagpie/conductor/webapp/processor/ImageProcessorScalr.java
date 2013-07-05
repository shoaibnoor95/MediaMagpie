package de.wehner.mediamagpie.conductor.webapp.processor;

import java.awt.Dimension;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import org.imgscalr.Scalr;
import org.imgscalr.Scalr.Method;
import org.imgscalr.Scalr.Mode;

/**
 * Currently not supported.
 * 
 * @author ralfwehner
 * 
 */
@Deprecated
public class ImageProcessorScalr extends AbstractImageProcessor {

    private BufferedImage originBitmap;

    public ImageProcessorScalr(File originImage) throws IOException {
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

    public BufferedImage resizeInternal(int width, int height) {
        int origWidth = originBitmap.getWidth();
        int origHeight = originBitmap.getHeight();
        Dimension newDimension = computeNewDimension(origWidth, origHeight, width, height);
        BufferedImage newImageScalr = Scalr.resize(originBitmap, Method.AUTOMATIC, Mode.AUTOMATIC, newDimension.width, newDimension.height);
        return newImageScalr;
    }

    @Override
    public void rotateImage(int angle) {
        // TODO Auto-generated method stub

    }

    @Override
    public void write(File thumbImagePath) throws IOException {
        // TODO Auto-generated method stub

    }

    @Override
    public Dimension getProcessedImageDimension() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void resize(int width, int height) {
        // TODO Auto-generated method stub

    }

    @Override
    public void close() throws IOException {
        // TODO Auto-generated method stub
        
    }
}

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

/**
 * Installation on Mac OS X:
 * 
 * <pre>
 * sudo port install ImageMagick
 * </pre>
 * 
 * @author ralfwehner
 * 
 */
public class ImageProcessorImageMagick extends AbstractImageProcessor {

    private static final Logger LOG = LoggerFactory.getLogger(ImageProcessorImageMagick.class);

    @Override
    public void close() throws IOException {
        // TODO Auto-generated method stub

    }

    @Override
    public int getOriginalWidth() {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public int getOriginalHeight() {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public void resize(int width, int height) {
        // TODO Auto-generated method stub

    }

    @Override
    public void rotateImage(int angle) {
        // TODO Auto-generated method stub

    }

    @Override
    public File write(File thumbImagePath) throws IOException {
        // TODO Auto-generated method stub
        return thumbImagePath;
    }

    @Override
    public Dimension getProcessedImageDimension() {
        // TODO Auto-generated method stub
        return null;
    }

}

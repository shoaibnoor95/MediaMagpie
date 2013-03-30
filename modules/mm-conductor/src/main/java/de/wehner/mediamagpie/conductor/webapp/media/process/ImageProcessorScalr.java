package de.wehner.mediamagpie.conductor.webapp.media.process;

import java.awt.Dimension;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import org.imgscalr.Scalr;
import org.imgscalr.Scalr.Method;
import org.imgscalr.Scalr.Mode;

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

    public BufferedImage resize(int width, int height) {
        int origWidth = originBitmap.getWidth();
        int origHeight = originBitmap.getHeight();
        Dimension newDimension = computeNewDimension(origWidth, origHeight, width, height);
        BufferedImage newImageScalr = Scalr.resize(originBitmap, Method.AUTOMATIC, Mode.AUTOMATIC, newDimension.width, newDimension.height);
        return newImageScalr;
    }
}

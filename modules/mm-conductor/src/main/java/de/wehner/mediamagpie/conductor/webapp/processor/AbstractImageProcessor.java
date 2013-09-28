package de.wehner.mediamagpie.conductor.webapp.processor;

import java.awt.Dimension;
import java.io.Closeable;
import java.io.File;
import java.io.IOException;

public abstract class AbstractImageProcessor implements Closeable {

    public abstract int getOriginalWidth();

    public abstract int getOriginalHeight();

    public abstract void resize(int width, int height);

    public abstract void rotateImage(int angle);

    public abstract File write(File suggestedThumbImagePath) throws IOException;

    public abstract Dimension getProcessedImageDimension();

    public AbstractImageProcessor() {
        super();
    }

    protected Dimension computeNewDimension(int origWidth, int origHeight, int width, int height) {
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

}
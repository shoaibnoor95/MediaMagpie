package de.wehner.mediamagpie.conductor.webapp.media.process;

import java.awt.Dimension;

public abstract class AbstractImageProcessor {

    public abstract int getOriginalWidth();
    public abstract int getOriginalHeight();
    
    public Dimension computeNewDimension(int origWidth, int origHeight, int width, int height) {
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

    public AbstractImageProcessor() {
        super();
    }

}
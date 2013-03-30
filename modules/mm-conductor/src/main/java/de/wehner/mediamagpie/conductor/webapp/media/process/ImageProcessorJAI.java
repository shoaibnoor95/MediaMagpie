package de.wehner.mediamagpie.conductor.webapp.media.process;

import java.awt.Dimension;
import java.awt.RenderingHints;
import java.awt.image.renderable.ParameterBlock;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;

import javax.media.jai.JAI;
import javax.media.jai.OpImage;
import javax.media.jai.RenderedOp;

import org.apache.commons.io.IOUtils;

import com.sun.media.jai.codec.SeekableStream;

public class ImageProcessorJAI extends AbstractImageProcessor {

    private final RenderedOp originalImage;

    private Dimension processedDimension = new Dimension();

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

    public ImageProcessorJAI(FileInputStream is) {
        SeekableStream seekableImageStream = SeekableStream.wrapInputStream(is, true);
        originalImage = JAI.create(JAI_STREAM_ACTION, seekableImageStream);
    }

    public ByteArrayOutputStream resize(int width, int height) {
        ((OpImage) originalImage.getRendering()).setTileCache(null);
        int origWidth = originalImage.getWidth();
        int origHeight = originalImage.getHeight();
        Dimension newDimension = computeNewDimension(origWidth, origHeight, width, height);
        // now resize the image
        double scale = (double) newDimension.width / (double) origWidth;
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
        processedDimension.width = (int) (origWidth * scale);
        processedDimension.height = (int) (origHeight * scale);
        return encoderOutputStream;
    }

    @Override
    public int getOriginalWidth() {
        return originalImage.getWidth();
    }

    @Override
    public int getOriginalHeight() {
        return originalImage.getHeight();
    }

    public Dimension getProcessedDimension() {
        return processedDimension;
    }

}

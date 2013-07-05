package de.wehner.mediamagpie.conductor.webapp.processor;

import java.awt.Dimension;
import java.awt.RenderingHints;
import java.awt.image.renderable.ParameterBlock;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import javax.media.jai.JAI;
import javax.media.jai.OpImage;
import javax.media.jai.RenderedOp;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;

import com.sun.media.jai.codec.SeekableStream;

import de.wehner.mediamagpie.core.util.ExceptionUtil;

public class ImageProcessorJAI extends AbstractImageProcessor {

    private InputStream is;

    private final RenderedOp originalImage;

    private ByteArrayOutputStream processedImage;

    /**
     * Only used for rotation
     */
    private ImageProcessorImageIO childImageProcessor;

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

    public ImageProcessorJAI(File inImageFile) throws FileNotFoundException {
        is = new FileInputStream(inImageFile);
        SeekableStream seekableImageStream = SeekableStream.wrapInputStream(is, true);
        originalImage = JAI.create(JAI_STREAM_ACTION, seekableImageStream);
    }

    /**
     * Resizes an image and writes it as jpeg
     * 
     * @param width
     * @param height
     * @return
     */
    ByteArrayOutputStream resizeInternal(int width, int height) {
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

    @Override
    public void resize(int width, int height) {
        processedImage = resizeInternal(width, height);
    }

    @Override
    public void rotateImage(int angle) {
        try {
            ByteArrayInputStream is = new ByteArrayInputStream(processedImage.toByteArray());
            childImageProcessor = new ImageProcessorImageIO(is);
            IOUtils.closeQuietly(is);
        } catch (IOException e) {
            throw ExceptionUtil.convertToRuntimeException(e);
        }
        childImageProcessor.rotateImage(angle);
    }

    @Override
    public void write(File thumbImagePath) throws IOException {
        if (childImageProcessor != null) {
            childImageProcessor.write(thumbImagePath);
        } else {
            FileUtils.writeByteArrayToFile(thumbImagePath, processedImage.toByteArray());
        }
    }

    @Override
    public Dimension getProcessedImageDimension() {
        if (childImageProcessor != null) {
            return childImageProcessor.getProcessedImageDimension();
        }
        return processedDimension;
    }

    @Override
    public void close() throws IOException {
        if (childImageProcessor != null) {
            childImageProcessor.close();
        }
        IOUtils.closeQuietly(is);
    }

}

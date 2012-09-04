package de.wehner.mediamagpie.common.simplenio.file;

import org.fest.util.Files;

/**
 * An object that configures how to open or create a file.
 *
 * <p> Objects of this type are used by methods such as {@link
 * Files#newOutputStream(MMPath,MMOpenOption[]) newOutputStream}, {@link
 * Files#newByteChannel newByteChannel}, {@link
 * java.nio.channels.FileChannel#open FileChannel.open}, and {@link
 * java.nio.channels.AsynchronousFileChannel#open AsynchronousFileChannel.open}
 * when opening or creating a file.
 *
 * <p> The {@link MMStandardOpenOption} enumeration type defines the
 * <i>standard</i> options.
 *
 */
public interface MMOpenOption {

}

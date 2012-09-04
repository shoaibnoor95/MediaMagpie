package de.wehner.mediamagpie.common.simplenio.file;

import java.io.Closeable;
import java.io.IOException;
import java.util.Set;

import de.wehner.mediamagpie.common.simplenio.file.spi.MMFileSystemProvider;

/**
 * Provides an interface to a file system and is the factory for objects to access files and other objects in the file system.
 * 
 * <p>
 * The default file system, obtained by invoking the {@link MMFileSystems#getDefault FileSystems.getDefault} method, provides access to the
 * file system that is accessible to the Java virtual machine. The {@link MMFileSystems} class defines methods to create file systems that
 * provide access to other types of (custom) file systems.
 * 
 * <p>
 * A file system is the factory for several types of objects:
 * 
 * <ul>
 * <li>
 * <p>
 * The {@link #getPath getPath} method converts a system dependent <em>path string</em>, returning a {@link Path} object that may be used to
 * locate and access a file.
 * </p>
 * </li>
 * <li>
 * <p>
 * The {@link #getPathMatcher getPathMatcher} method is used to create a {@link PathMatcher} that performs match operations on paths.
 * </p>
 * </li>
 * <li>
 * <p>
 * The {@link #getFileStores getFileStores} method returns an iterator over the underlying {@link FileStore file-stores}.
 * </p>
 * </li>
 * <li>
 * <p>
 * The {@link #getUserPrincipalLookupService getUserPrincipalLookupService} method returns the {@link UserPrincipalLookupService} to lookup
 * users or groups by name.
 * </p>
 * </li>
 * <li>
 * <p>
 * The {@link #newWatchService newWatchService} method creates a {@link WatchService} that may be used to watch objects for changes and
 * events.
 * </p>
 * </li>
 * </ul>
 * 
 * <p>
 * File systems vary greatly. In some cases the file system is a single hierarchy of files with one top-level root directory. In other cases
 * it may have several distinct file hierarchies, each with its own top-level root directory. The {@link #getRootDirectories
 * getRootDirectories} method may be used to iterate over the root directories in the file system. A file system is typically composed of
 * one or more underlying {@link FileStore file-stores} that provide the storage for the files. Theses file stores can also vary in the
 * features they support, and the file attributes or <em>meta-data</em> that they associate with files.
 * 
 * <p>
 * A file system is open upon creation and can be closed by invoking its {@link #close() close} method. Once closed, any further attempt to
 * access objects in the file system cause {@link ClosedFileSystemException} to be thrown. File systems created by the default
 * {@link MMFileSystemProviderFactory provider} cannot be closed.
 * 
 * <p>
 * A {@code FileSystem} can provide read-only or read-write access to the file system. Whether or not a file system provides read-only
 * access is established when the {@code FileSystem} is created and can be tested by invoking its {@link #isReadOnly() isReadOnly} method.
 * Attempts to write to file stores by means of an object associated with a read-only file system throws {@link ReadOnlyFileSystemException}.
 * 
 * <p>
 * File systems are safe for use by multiple concurrent threads. The {@link #close close} method may be invoked at any time to close a file
 * system but whether a file system is <i>asynchronously closeable</i> is provider specific and therefore unspecified. In other words, if a
 * thread is accessing an object in a file system, and another thread invokes the {@code close} method then it may require to block until
 * the first operation is complete. Closing a file system causes all open channels, watch services, and other {@link Closeable closeable}
 * objects associated with the file system to be closed.
 * 
 * @since 1.7
 */

public abstract class MMFileSystem implements Closeable {
    /**
     * Initializes a new instance of this class.
     */
    protected MMFileSystem() {
    }

    /**
     * Returns the provider that created this file system.
     * 
     * @return The provider that created this file system.
     */
    public abstract MMFileSystemProvider provider();

    /**
     * Closes this file system.
     * 
     * <p>
     * After a file system is closed then all subsequent access to the file system, either by methods defined by this class or on objects
     * associated with this file system, throw {@link ClosedFileSystemException}. If the file system is already closed then invoking this
     * method has no effect.
     * 
     * <p>
     * Closing a file system will close all open {@link java.nio.channels.Channel channels}, {@link DirectoryStream directory-streams},
     * {@link WatchService watch-service}, and other closeable objects associated with this file system. The
     * {@link MMFileSystems#getDefault default} file system cannot be closed.
     * 
     * @throws IOException
     *             If an I/O error occurs
     * @throws UnsupportedOperationException
     *             Thrown in the case of the default file system
     */
    @Override
    public abstract void close() throws IOException;

    /**
     * Tells whether or not this file system is open.
     * 
     * <p>
     * File systems created by the default provider are always open.
     * 
     * @return {@code true} if, and only if, this file system is open
     */
    public abstract boolean isOpen();

    /**
     * Returns the name separator, represented as a string.
     * 
     * <p>
     * The name separator is used to separate names in a path string. An implementation may support multiple name separators in which case
     * this method returns an implementation specific <em>default</em> name separator. This separator is used when creating path strings by
     * invoking the {@link Path#toString() toString()} method.
     * 
     * <p>
     * In the case of the default provider, this method returns the same separator as {@link java.io.File#separator}.
     * 
     * @return The name separator
     */
    public abstract String getSeparator();

    /**
     * Converts a path string, or a sequence of strings that when joined form a path string, to a {@code Path}. If {@code more} does not
     * specify any elements then the value of the {@code first} parameter is the path string to convert. If {@code more} specifies one or
     * more elements then each non-empty string, including {@code first}, is considered to be a sequence of name elements (see {@link Path})
     * and is joined to form a path string. The details as to how the Strings are joined is provider specific but typically they will be
     * joined using the {@link #getSeparator name-separator} as the separator. For example, if the name separator is "{@code /}" and
     * {@code getPath("/foo","bar","gus")} is invoked, then the path string {@code "/foo/bar/gus"} is converted to a {@code Path}. A
     * {@code Path} representing an empty path is returned if {@code first} is the empty string and {@code more} does not contain any
     * non-empty strings.
     * 
     * <p>
     * The parsing and conversion to a path object is inherently implementation dependent. In the simplest case, the path string is
     * rejected, and {@link InvalidPathException} thrown, if the path string contains characters that cannot be converted to characters that
     * are <em>legal</em> to the file store. For example, on UNIX systems, the NUL (&#92;u0000) character is not allowed to be present in a
     * path. An implementation may choose to reject path strings that contain names that are longer than those allowed by any file store,
     * and where an implementation supports a complex path syntax, it may choose to reject path strings that are <em>badly
     * formed</em>.
     * 
     * <p>
     * In the case of the default provider, path strings are parsed based on the definition of paths at the platform or virtual file system
     * level. For example, an operating system may not allow specific characters to be present in a file name, but a specific underlying
     * file store may impose different or additional restrictions on the set of legal characters.
     * 
     * <p>
     * This method throws {@link InvalidPathException} when the path string cannot be converted to a path. Where possible, and where
     * applicable, the exception is created with an {@link InvalidPathException#getIndex index} value indicating the first position in the
     * {@code path} parameter that caused the path string to be rejected.
     * 
     * @param first
     *            the path string or initial part of the path string
     * @param more
     *            additional strings to be joined to form the path string
     * 
     * @return the resulting {@code Path}
     * 
     * @throws InvalidPathException
     *             If the path string cannot be converted
     */
    public abstract MMPath getPath(String first, String... more);
}

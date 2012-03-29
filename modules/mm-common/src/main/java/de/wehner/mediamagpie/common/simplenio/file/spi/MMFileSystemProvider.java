package de.wehner.mediamagpie.common.simplenio.file.spi;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.nio.channels.Channels;
import java.nio.channels.SeekableByteChannel;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.nio.file.attribute.FileAttribute;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import de.wehner.mediamagpie.common.simplenio.file.MMFileSystem;
import de.wehner.mediamagpie.common.simplenio.file.MMFiles;
import de.wehner.mediamagpie.common.simplenio.file.MMNoSuchFileException;
import de.wehner.mediamagpie.common.simplenio.file.MMOpenOption;
import de.wehner.mediamagpie.common.simplenio.file.MMPath;
import de.wehner.mediamagpie.common.simplenio.file.MMStandardOpenOption;
import de.wehner.mediamagpie.common.simplenio.file.attribute.MMBasicFileAttributes;

public abstract class MMFileSystemProvider {

    /**
     * Returns an existing {@code FileSystem} created by this provider.
     * 
     * <p>
     * This method returns a reference to a {@code FileSystem} that was created by invoking the {@link #newFileSystem(URI,Map)
     * newFileSystem(URI,Map)} method. File systems created the {@link #newFileSystem(Path,Map) newFileSystem(Path,Map)} method are not
     * returned by this method. The file system is identified by its {@code URI}. Its exact form is highly provider dependent. In the
     * case of the default provider the URI's path component is {@code "/"} and the authority, query and fragment components are
     * undefined (Undefined components are represented by {@code null}).
     * 
     * <p>
     * Once a file system created by this provider is {@link java.nio.file.FileSystem#close closed} it is provider-dependent if this
     * method returns a reference to the closed file system or throws {@link FileSystemNotFoundException}. If the provider allows a new
     * file system to be created with the same URI as a file system it previously created then this method throws the exception if
     * invoked after the file system is closed (and before a new instance is created by the {@link #newFileSystem newFileSystem}
     * method).
     * 
     * <p>
     * If a security manager is installed then a provider implementation may require to check a permission before returning a reference
     * to an existing file system. In the case of the {@link FileSystems#getDefault default} file system, no permission check is
     * required.
     * 
     * @param uri
     *            URI reference
     * 
     * @return The file system
     * 
     * @throws IllegalArgumentException
     *             If the pre-conditions for the {@code uri} parameter aren't met
     * @throws FileSystemNotFoundException
     *             If the file system does not exist
     * @throws SecurityException
     *             If a security manager is installed and it denies an unspecified permission.
     */
    public abstract MMFileSystem getFileSystem(URI uri);

    /**
     * Return a {@code Path} object by converting the given {@link URI}. The resulting {@code Path} is associated with a
     * {@link FileSystem} that already exists or is constructed automatically.
     * 
     * <p>
     * The exact form of the URI is file system provider dependent. In the case of the default provider, the URI scheme is
     * {@code "file"} and the given URI has a non-empty path component, and undefined query, and fragment components. The resulting
     * {@code Path} is associated with the default {@link FileSystems#getDefault default} {@code FileSystem}.
     * 
     * <p>
     * If a security manager is installed then a provider implementation may require to check a permission. In the case of the
     * {@link FileSystems#getDefault default} file system, no permission check is required.
     * 
     * @param uri
     *            The URI to convert
     * 
     * @throws IllegalArgumentException
     *             If the URI scheme does not identify this provider or other preconditions on the uri parameter do not hold
     * @throws FileSystemNotFoundException
     *             The file system, identified by the URI, does not exist and cannot be created automatically
     * @throws SecurityException
     *             If a security manager is installed and it denies an unspecified permission.
     */
    public abstract MMPath getPath(URI uri);

    /**
     * Opens or creates a file, returning a seekable byte channel to access the file. This method works in exactly the manner specified
     * by the {@link Files#newByteChannel(Path,Set,FileAttribute[])} method.
     * 
     * @param path
     *            the path to the file to open or create
     * @param options
     *            options specifying how the file is opened
     * @param attrs
     *            an optional list of file attributes to set atomically when creating the file
     * 
     * @return a new seekable byte channel
     * 
     * @throws IllegalArgumentException
     *             if the set contains an invalid combination of options
     * @throws UnsupportedOperationException
     *             if an unsupported open option is specified or the array contains attributes that cannot be set atomically when
     *             creating the file
     * @throws FileAlreadyExistsException
     *             if a file of that name already exists and the {@link StandardOpenOption#CREATE_NEW CREATE_NEW} option is specified
     *             <i>(optional specific exception)</i>
     * @throws IOException
     *             if an I/O error occurs
     * @throws SecurityException
     *             In the case of the default provider, and a security manager is installed, the
     *             {@link SecurityManager#checkRead(String) checkRead} method is invoked to check read access to the path if the file
     *             is opened for reading. The {@link SecurityManager#checkWrite(String) checkWrite} method is invoked to check write
     *             access to the path if the file is opened for writing. The {@link SecurityManager#checkDelete(String) checkDelete}
     *             method is invoked to check delete access if the file is opened with the {@code DELETE_ON_CLOSE} option.
     */
    public abstract SeekableByteChannel newByteChannel(MMPath path, Set<? extends MMOpenOption> options) throws IOException;

    /**
     * Opens a file, returning an input stream to read from the file. This method works in exactly the manner specified by the
     * {@link Files#newInputStream} method.
     * 
     * <p>
     * The default implementation of this method opens a channel to the file as if by invoking the {@link #newByteChannel} method and
     * constructs a stream that reads bytes from the channel. This method should be overridden where appropriate.
     * 
     * @param path
     *            the path to the file to open
     * @param options
     *            options specifying how the file is opened
     * 
     * @return a new input stream
     * 
     * @throws IllegalArgumentException
     *             if an invalid combination of options is specified
     * @throws UnsupportedOperationException
     *             if an unsupported option is specified
     * @throws IOException
     *             if an I/O error occurs
     * @throws SecurityException
     *             In the case of the default provider, and a security manager is installed, the
     *             {@link SecurityManager#checkRead(String) checkRead} method is invoked to check read access to the file.
     */
    public InputStream newInputStream(MMPath path, MMOpenOption... options) throws IOException {
        if (options.length > 0) {
            for (MMOpenOption opt : options) {
                if (opt != MMStandardOpenOption.READ)
                    throw new UnsupportedOperationException("'" + opt + "' not allowed");
            }
            return Channels.newInputStream(MMFiles.newByteChannel(path, options));
        } else {
            return Channels.newInputStream(MMFiles.newByteChannel(path, MMStandardOpenOption.READ));
        }
    }

    /**
     * Opens or creates a file, returning an output stream that may be used to write bytes to the file. This method works in exactly
     * the manner specified by the {@link Files#newOutputStream} method.
     * 
     * <p>
     * The default implementation of this method opens a channel to the file as if by invoking the {@link #newByteChannel} method and
     * constructs a stream that writes bytes to the channel. This method should be overridden where appropriate.
     * 
     * @param path
     *            the path to the file to open or create
     * @param options
     *            options specifying how the file is opened
     * 
     * @return a new output stream
     * 
     * @throws IllegalArgumentException
     *             if {@code options} contains an invalid combination of options
     * @throws UnsupportedOperationException
     *             if an unsupported option is specified
     * @throws IOException
     *             if an I/O error occurs
     * @throws SecurityException
     *             In the case of the default provider, and a security manager is installed, the
     *             {@link SecurityManager#checkWrite(String) checkWrite} method is invoked to check write access to the file. The
     *             {@link SecurityManager#checkDelete(String) checkDelete} method is invoked to check delete access if the file is
     *             opened with the {@code DELETE_ON_CLOSE} option.
     */
    public OutputStream newOutputStream(MMPath path, MMOpenOption... options) throws IOException {
        int len = options.length;
        Set<MMOpenOption> opts = new HashSet<MMOpenOption>(len + 3);
        if (len == 0) {
            opts.add(MMStandardOpenOption.CREATE);
            opts.add(MMStandardOpenOption.TRUNCATE_EXISTING);
        } else {
            for (MMOpenOption opt : options) {
                if (opt == MMStandardOpenOption.READ)
                    throw new IllegalArgumentException("READ not allowed");
                opts.add(opt);
            }
        }
        opts.add(MMStandardOpenOption.WRITE);
        return Channels.newOutputStream(newByteChannel(path, opts));
    }

    /**
     * Creates a new directory. This method works in exactly the manner specified by the {@link Files#createDirectory} method.
     * 
     * @param dir
     *            the directory to create
     * @param attrs
     *            an optional list of file attributes to set atomically when creating the directory
     * 
     * @throws UnsupportedOperationException
     *             if the array contains an attribute that cannot be set atomically when creating the directory
     * @throws FileAlreadyExistsException
     *             if a directory could not otherwise be created because a file of that name already exists <i>(optional specific
     *             exception)</i>
     * @throws IOException
     *             if an I/O error occurs or the parent directory does not exist
     * @throws SecurityException
     *             In the case of the default provider, and a security manager is installed, the
     *             {@link SecurityManager#checkWrite(String) checkWrite} method is invoked to check write access to the new directory.
     */
    public abstract void createDirectory(MMPath dir) throws IOException;

    // /**
    // * Return a {@code Path} object by converting the given {@link URI}. The resulting {@code Path} is associated with a {@link
    // FileSystem}
    // * that already exists or is constructed automatically.
    // *
    // * <p>
    // * The exact form of the URI is file system provider dependent. In the case of the default provider, the URI scheme is {@code
    // "file"}
    // * and the given URI has a non-empty path component, and undefined query, and fragment components. The resulting {@code Path} is
    // * associated with the default {@link FileSystems#getDefault default} {@code FileSystem}.
    // *
    // * <p>
    // * If a security manager is installed then a provider implementation may require to check a permission. In the case of the
    // * {@link FileSystems#getDefault default} file system, no permission check is required.
    // *
    // * @param uri
    // * The URI to convert
    // *
    // * @throws IllegalArgumentException
    // * If the URI scheme does not identify this provider or other preconditions on the uri parameter do not hold
    // * @throws FileSystemNotFoundException
    // * The file system, identified by the URI, does not exist and cannot be created automatically
    // * @throws SecurityException
    // * If a security manager is installed and it denies an unspecified permission.
    // */
    // public abstract MMPath getPath(URI uri);

    /**
     * Reads a file's attributes as a bulk operation. This method works in exactly the manner specified by the
     * {@link Files#readAttributes(Path,Class,LinkOption[])} method.
     * 
     * @param path
     *            the path to the file
     * @param type
     *            the {@code Class} of the file attributes required to read
     * @param options
     *            options indicating how symbolic links are handled
     * 
     * @return the file attributes
     * 
     * @throws UnsupportedOperationException
     *             if an attributes of the given type are not supported
     * @throws IOException
     *             if an I/O error occurs
     * @throws SecurityException
     *             In the case of the default provider, a security manager is installed, its {@link SecurityManager#checkRead(String)
     *             checkRead} method is invoked to check read access to the file
     */
    public abstract <A extends MMBasicFileAttributes> A readAttributes(MMPath path, Class<A> type) throws IOException;

    /**
     * Deletes a file. This method works in exactly the manner specified by the {@link Files#delete} method.
     * 
     * @param path
     *            the path to the file to delete
     * 
     * @throws MMNoSuchFileException
     *             if the file does not exist <i>(optional specific exception)</i>
     * @throws DirectoryNotEmptyException
     *             if the file is a directory and could not otherwise be deleted because the directory is not empty <i>(optional
     *             specific exception)</i>
     * @throws IOException
     *             if an I/O error occurs
     * @throws SecurityException
     *             In the case of the default provider, and a security manager is installed, the
     *             {@link SecurityManager#checkDelete(String)} method is invoked to check delete access to the file
     */
    public abstract void delete(MMPath path) throws IOException;

    /**
     * Deletes a file if it exists. This method works in exactly the manner specified by the {@link Files#deleteIfExists} method.
     * 
     * <p>
     * The default implementation of this method simply invokes {@link #delete} ignoring the {@code NoSuchFileException} when the file
     * does not exist. It may be overridden where appropriate.
     * 
     * @param path
     *            the path to the file to delete
     * 
     * @return {@code true} if the file was deleted by this method; {@code false} if the file could not be deleted because it did not
     *         exist
     * 
     * @throws DirectoryNotEmptyException
     *             if the file is a directory and could not otherwise be deleted because the directory is not empty <i>(optional
     *             specific exception)</i>
     * @throws IOException
     *             if an I/O error occurs
     * @throws SecurityException
     *             In the case of the default provider, and a security manager is installed, the
     *             {@link SecurityManager#checkDelete(String)} method is invoked to check delete access to the file
     */
    public boolean deleteIfExists(MMPath path) throws IOException {
        try {
            delete(path);
            return true;
        } catch (MMNoSuchFileException ignore) {
            return false;
        }
    }

    /**
     * Copy a file to a target file. This method works in exactly the manner specified by the
     * {@link Files#copy(Path,Path,CopyOption[])} method except that both the source and target paths must be associated with this
     * provider.
     * 
     * @param source
     *            the path to the file to copy
     * @param target
     *            the path to the target file
     * @param options
     *            options specifying how the copy should be done
     * 
     * @throws UnsupportedOperationException
     *             if the array contains a copy option that is not supported
     * @throws FileAlreadyExistsException
     *             if the target file exists but cannot be replaced because the {@code REPLACE_EXISTING} option is not specified
     *             <i>(optional specific exception)</i>
     * @throws DirectoryNotEmptyException
     *             the {@code REPLACE_EXISTING} option is specified but the file cannot be replaced because it is a non-empty directory
     *             <i>(optional specific exception)</i>
     * @throws IOException
     *             if an I/O error occurs
     * @throws SecurityException
     *             In the case of the default provider, and a security manager is installed, the
     *             {@link SecurityManager#checkRead(String) checkRead} method is invoked to check read access to the source file, the
     *             {@link SecurityManager#checkWrite(String) checkWrite} is invoked to check write access to the target file. If a
     *             symbolic link is copied the security manager is invoked to check {@link LinkPermission}{@code ("symbolic")}.
     */
    public abstract void copy(MMPath source, MMPath target) throws IOException;

    /**
     * Move or rename a file to a target file. This method works in exactly the manner specified by the {@link Files#move} method
     * except that both the source and target paths must be associated with this provider.
     * 
     * @param source
     *            the path to the file to move
     * @param target
     *            the path to the target file
     * @param options
     *            options specifying how the move should be done
     * 
     * @throws UnsupportedOperationException
     *             if the array contains a copy option that is not supported
     * @throws FileAlreadyExistsException
     *             if the target file exists but cannot be replaced because the {@code REPLACE_EXISTING} option is not specified
     *             <i>(optional specific exception)</i>
     * @throws DirectoryNotEmptyException
     *             the {@code REPLACE_EXISTING} option is specified but the file cannot be replaced because it is a non-empty directory
     *             <i>(optional specific exception)</i>
     * @throws AtomicMoveNotSupportedException
     *             if the options array contains the {@code ATOMIC_MOVE} option but the file cannot be moved as an atomic file system
     *             operation.
     * @throws IOException
     *             if an I/O error occurs
     * @throws SecurityException
     *             In the case of the default provider, and a security manager is installed, the
     *             {@link SecurityManager#checkWrite(String) checkWrite} method is invoked to check write access to both the source and
     *             target file.
     */
    public abstract void move(MMPath source, MMPath target) throws IOException;
}

package de.wehner.mediamagpie.common.simplenio.file;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.Collections;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.apache.commons.io.IOUtils;

import de.wehner.mediamagpie.common.simplenio.channels.MMSeekableByteChannel;
import de.wehner.mediamagpie.common.simplenio.file.attribute.MMBasicFileAttributes;
import de.wehner.mediamagpie.common.simplenio.file.spi.MMFileSystemProvider;

public class MMFiles {

    private MMFiles() {
    }

    /**
     * Returns the {@code FileSystemProvider} to delegate to.
     */
    private static MMFileSystemProvider provider(MMPath path) {
        return path.getFileSystem().provider();
    }

    // -- File contents --

    /**
     * Opens a file, returning an input stream to read from the file. The stream will not be buffered, and is not required to support
     * the {@link InputStream#mark mark} or {@link InputStream#reset reset} methods. The stream will be safe for access by multiple
     * concurrent threads. Reading commences at the beginning of the file. Whether the returned stream is <i>asynchronously
     * closeable</i> and/or <i>interruptible</i> is highly file system provider specific and therefore not specified.
     * 
     * <p>
     * The {@code options} parameter determines how the file is opened. If no options are present then it is equivalent to opening the
     * file with the {@link StandardOpenOption#READ READ} option. In addition to the {@code READ} option, an implementation may also
     * support additional implementation specific options.
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
    public static InputStream newInputStream(MMPath path) throws IOException {
        return provider(path).newInputStream(path);
    }

    /**
     * Opens or creates a file, returning an output stream that may be used to write bytes to the file. The resulting stream will not
     * be buffered. The stream will be safe for access by multiple concurrent threads. Whether the returned stream is <i>asynchronously
     * closeable</i> and/or <i>interruptible</i> is highly file system provider specific and therefore not specified.
     * 
     * <p>
     * This method opens or creates a file in exactly the manner specified by the {@link #newByteChannel(Path,Set,FileAttribute[])
     * newByteChannel} method with the exception that the {@link StandardOpenOption#READ READ} option may not be present in the array
     * of options. If no options are present then this method works as if the {@link StandardOpenOption#CREATE CREATE},
     * {@link StandardOpenOption#TRUNCATE_EXISTING TRUNCATE_EXISTING}, and {@link StandardOpenOption#WRITE WRITE} options are present.
     * In other words, it opens the file for writing, creating the file if it doesn't exist, or initially truncating an existing
     * {@link #isRegularFile regular-file} to a size of {@code 0} if it exists.
     * 
     * <p>
     * <b>Usage Examples:</b>
     * 
     * <pre>
     *     Path path = ...
     * 
     *     // truncate and overwrite an existing file, or create the file if
     *     // it doesn't initially exist
     *     OutputStream out = Files.newOutputStream(path);
     * 
     *     // append to an existing file, fail if the file does not exist
     *     out = Files.newOutputStream(path, APPEND);
     * 
     *     // append to an existing file, create file if it doesn't initially exist
     *     out = Files.newOutputStream(path, CREATE, APPEND);
     * 
     *     // always create new file, failing if it already exists
     *     out = Files.newOutputStream(path, CREATE_NEW);
     * </pre>
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
    public static OutputStream newOutputStream(MMPath path) throws IOException {
        return provider(path).newOutputStream(path);
    }

    // -- Directories --

    /**
     * Opens a directory, returning a {@link DirectoryStream} to iterate over all entries in the directory. The elements returned by
     * the directory stream's {@link DirectoryStream#iterator iterator} are of type {@code Path}, each one representing an entry in the
     * directory. The {@code Path} objects are obtained as if by {@link Path#resolve(Path) resolving} the name of the directory entry
     * against {@code dir}.
     * 
     * <p>
     * When not using the try-with-resources construct, then directory stream's {@code close} method should be invoked after iteration
     * is completed so as to free any resources held for the open directory.
     * 
     * <p>
     * When an implementation supports operations on entries in the directory that execute in a race-free manner then the returned
     * directory stream is a {@link SecureDirectoryStream}.
     * 
     * @param dir
     *            the path to the directory
     * 
     * @return a new and open {@code DirectoryStream} object
     * 
     * @throws NotDirectoryException
     *             if the file could not otherwise be opened because it is not a directory <i>(optional specific exception)</i>
     * @throws IOException
     *             if an I/O error occurs
     * @throws SecurityException
     *             In the case of the default provider, and a security manager is installed, the
     *             {@link SecurityManager#checkRead(String) checkRead} method is invoked to check read access to the directory.
     */
    public static MMDirectoryStream<MMPath> newDirectoryStream(MMPath dir) throws IOException {
        return provider(dir).newDirectoryStream(dir, new MMDirectoryStream.Filter<MMPath>() {
            @Override
            public boolean accept(MMPath entry) {
                return true;
            }
        });
    }

    /**
     * Opens a directory, returning a {@link DirectoryStream} to iterate over the entries in the directory. The elements returned by
     * the directory stream's {@link DirectoryStream#iterator iterator} are of type {@code Path}, each one representing an entry in the
     * directory. The {@code Path} objects are obtained as if by {@link Path#resolve(Path) resolving} the name of the directory entry
     * against {@code dir}. The entries returned by the iterator are filtered by the given {@link DirectoryStream.Filter filter}.
     * 
     * <p>
     * When not using the try-with-resources construct, then directory stream's {@code close} method should be invoked after iteration
     * is completed so as to free any resources held for the open directory.
     * 
     * <p>
     * Where the filter terminates due to an uncaught error or runtime exception then it is propagated to the
     * {@link Iterator#hasNext() hasNext} or {@link Iterator#next() next} method. Where an {@code IOException} is thrown, it results in
     * the {@code hasNext} or {@code next} method throwing a {@link DirectoryIteratorException} with the {@code IOException} as the
     * cause.
     * 
     * <p>
     * When an implementation supports operations on entries in the directory that execute in a race-free manner then the returned
     * directory stream is a {@link SecureDirectoryStream}.
     * 
     * <p>
     * <b>Usage Example:</b> Suppose we want to iterate over the files in a directory that are larger than 8K.
     * 
     * <pre>
     *     DirectoryStream.Filter&lt;Path&gt; filter = new DirectoryStream.Filter&lt;Path&gt;() {
     *         public boolean accept(Path file) throws IOException {
     *             return (Files.size(file) > 8192L);
     *         }
     *     };
     *     Path dir = ...
     *     try (DirectoryStream&lt;Path&gt; stream = Files.newDirectoryStream(dir, filter)) {
     *         :
     *     }
     * </pre>
     * 
     * @param dir
     *            the path to the directory
     * @param filter
     *            the directory stream filter
     * 
     * @return a new and open {@code DirectoryStream} object
     * 
     * @throws NotDirectoryException
     *             if the file could not otherwise be opened because it is not a directory <i>(optional specific exception)</i>
     * @throws IOException
     *             if an I/O error occurs
     * @throws SecurityException
     *             In the case of the default provider, and a security manager is installed, the
     *             {@link SecurityManager#checkRead(String) checkRead} method is invoked to check read access to the directory.
     */
    public static MMDirectoryStream<MMPath> newDirectoryStream(MMPath dir, MMDirectoryStream.Filter<? super MMPath> filter) throws IOException {
        return provider(dir).newDirectoryStream(dir, filter);
    }

    /**
     * Opens or creates a file, returning a seekable byte channel to access the file.
     * 
     * <p>
     * This method opens or creates a file in exactly the manner specified by the {@link #newByteChannel(Path,Set,FileAttribute[])
     * newByteChannel} method.
     * 
     * @param path
     *            the path to the file to open or create
     * @param options
     *            options specifying how the file is opened
     * 
     * @return a new seekable byte channel
     * 
     * @throws IllegalArgumentException
     *             if the set contains an invalid combination of options
     * @throws UnsupportedOperationException
     *             if an unsupported open option is specified
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
     * 
     * @see java.nio.channels.FileChannel#open(Path,OpenOption[])
     */
    // public static MMSeekableByteChannel newByteChannel(MMPath path) throws IOException {
    // return provider(path).newByteChannel(path);
    // }

    public static MMPath createDirectory(MMPath dir) throws IOException {
        provider(dir).createDirectory(dir);
        return dir;
    }

    public static MMPath createFile(MMPath path) throws IOException {
        EnumSet<MMStandardOpenOption> options = EnumSet.<MMStandardOpenOption> of(MMStandardOpenOption.CREATE_NEW,
                MMStandardOpenOption.WRITE);
        newByteChannel(path, options).close();
        return path;
    }

    /**
     * Opens or creates a file, returning a seekable byte channel to access the file.
     * 
     * <p>
     * The {@code options} parameter determines how the file is opened. The {@link StandardOpenOption#READ READ} and
     * {@link StandardOpenOption#WRITE WRITE} options determine if the file should be opened for reading and/or writing. If neither
     * option (or the {@link StandardOpenOption#APPEND APPEND} option) is present then the file is opened for reading. By default
     * reading or writing commence at the beginning of the file.
     * 
     * <p>
     * In the addition to {@code READ} and {@code WRITE}, the following options may be present:
     * 
     * <table border=1 cellpadding=5 summary="">
     * <tr>
     * <th>Option</th>
     * <th>Description</th>
     * </tr>
     * <tr>
     * <td> {@link StandardOpenOption#APPEND APPEND}</td>
     * <td>If this option is present then the file is opened for writing and each invocation of the channel's {@code write} method
     * first advances the position to the end of the file and then writes the requested data. Whether the advancement of the position
     * and the writing of the data are done in a single atomic operation is system-dependent and therefore unspecified. This option may
     * not be used in conjunction with the {@code READ} or {@code TRUNCATE_EXISTING} options.</td>
     * </tr>
     * <tr>
     * <td> {@link StandardOpenOption#TRUNCATE_EXISTING TRUNCATE_EXISTING}</td>
     * <td>If this option is present then the existing file is truncated to a size of 0 bytes. This option is ignored when the file is
     * opened only for reading.</td>
     * </tr>
     * <tr>
     * <td> {@link StandardOpenOption#CREATE_NEW CREATE_NEW}</td>
     * <td>If this option is present then a new file is created, failing if the file already exists or is a symbolic link. When
     * creating a file the check for the existence of the file and the creation of the file if it does not exist is atomic with respect
     * to other file system operations. This option is ignored when the file is opened only for reading.</td>
     * </tr>
     * <tr>
     * <td > {@link StandardOpenOption#CREATE CREATE}</td>
     * <td>If this option is present then an existing file is opened if it exists, otherwise a new file is created. This option is
     * ignored if the {@code CREATE_NEW} option is also present or the file is opened only for reading.</td>
     * </tr>
     * <tr>
     * <td > {@link StandardOpenOption#DELETE_ON_CLOSE DELETE_ON_CLOSE}</td>
     * <td>When this option is present then the implementation makes a <em>best effort</em> attempt to delete the file when closed by
     * the {@link SeekableByteChannel#close close} method. If the {@code close} method is not invoked then a <em>best effort</em>
     * attempt is made to delete the file when the Java virtual machine terminates.</td>
     * </tr>
     * <tr>
     * <td>{@link StandardOpenOption#SPARSE SPARSE}</td>
     * <td>When creating a new file this option is a <em>hint</em> that the new file will be sparse. This option is ignored when not
     * creating a new file.</td>
     * </tr>
     * <tr>
     * <td> {@link StandardOpenOption#SYNC SYNC}</td>
     * <td>Requires that every update to the file's content or metadata be written synchronously to the underlying storage device. (see
     * <a href="package-summary.html#integrity"> Synchronized I/O file integrity</a>).</td>
     * <tr>
     * <tr>
     * <td> {@link StandardOpenOption#DSYNC DSYNC}</td>
     * <td>Requires that every update to the file's content be written synchronously to the underlying storage device. (see <a
     * href="package-summary.html#integrity"> Synchronized I/O file integrity</a>).</td>
     * </tr>
     * </table>
     * 
     * <p>
     * An implementation may also support additional implementation specific options.
     * 
     * <p>
     * The {@code attrs} parameter is optional {@link FileAttribute file-attributes} to set atomically when a new file is created.
     * 
     * <p>
     * In the case of the default provider, the returned seekable byte channel is a {@link java.nio.channels.FileChannel}.
     * 
     * <p>
     * <b>Usage Examples:</b>
     * 
     * <pre>
     *     Path path = ...
     * 
     *     // open file for reading
     *     ReadableByteChannel rbc = Files.newByteChannel(path, EnumSet.of(READ)));
     * 
     *     // open file for writing to the end of an existing file, creating
     *     // the file if it doesn't already exist
     *     WritableByteChannel wbc = Files.newByteChannel(path, EnumSet.of(CREATE,APPEND));
     * 
     *     // create file with initial permissions, opening it for both reading and writing
     *     {@code FileAttribute<<SetPosixFilePermission>> perms = ...}
     *     SeekableByteChannel sbc = Files.newByteChannel(path, EnumSet.of(CREATE_NEW,READ,WRITE), perms);
     * </pre>
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
     * 
     * @see java.nio.channels.FileChannel#open(Path,Set,FileAttribute[])
     */
    public static MMSeekableByteChannel newByteChannel(MMPath path, Set<? extends MMOpenOption> options) throws IOException {
        return provider(path).newByteChannel(path, options);
    }

    /**
     * Opens or creates a file, returning a seekable byte channel to access the file.
     * 
     * <p>
     * This method opens or creates a file in exactly the manner specified by the {@link #newByteChannel(Path,Set,FileAttribute[])
     * newByteChannel} method.
     * 
     * @param path
     *            the path to the file to open or create
     * @param options
     *            options specifying how the file is opened
     * 
     * @return a new seekable byte channel
     * 
     * @throws IllegalArgumentException
     *             if the set contains an invalid combination of options
     * @throws UnsupportedOperationException
     *             if an unsupported open option is specified
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
     * 
     * @see java.nio.channels.FileChannel#open(Path,OpenOption[])
     */
    public static MMSeekableByteChannel newByteChannel(MMPath path, MMOpenOption... options) throws IOException {
        Set<MMOpenOption> set = new HashSet<MMOpenOption>(options.length);
        Collections.addAll(set, options);
        return newByteChannel(path, set);
    }

    /**
     * Copies all bytes from a file to an output stream.
     * 
     * <p>
     * If an I/O error occurs reading from the file or writing to the output stream, then it may do so after some bytes have been read
     * or written. Consequently the output stream may be in an inconsistent state. It is strongly recommended that the output stream be
     * promptly closed if an I/O error occurs.
     * 
     * <p>
     * This method may block indefinitely writing to the output stream (or reading from the file). The behavior for the case that the
     * output stream is <i>asynchronously closed</i> or the thread interrupted during the copy is highly output stream and file system
     * provider specific and therefore not specified.
     * 
     * <p>
     * Note that if the given output stream is {@link java.io.Flushable} then its {@link java.io.Flushable#flush flush} method may need
     * to invoked after this method completes so as to flush any buffered output.
     * 
     * @param source
     *            the path to the file
     * @param out
     *            the output stream to write to
     * 
     * @return the number of bytes read or written
     * 
     * @throws IOException
     *             if an I/O error occurs when reading or writing
     * @throws SecurityException
     *             In the case of the default provider, and a security manager is installed, the
     *             {@link SecurityManager#checkRead(String) checkRead} method is invoked to check read access to the file.
     */
    public static long copy(MMPath source, OutputStream out) throws IOException {
        // ensure not null before opening file
        if (out == null) {
            throw new IllegalArgumentException("The parameter 'out' must not be null.");
        }

        InputStream in = newInputStream(source);
        return IOUtils.copy(in, out);
    }

    /**
     * Read all the bytes from an input stream. The {@code initialSize} parameter indicates the initial size of the byte[] to allocate.
     */
    private static byte[] read(InputStream source, int initialSize) throws IOException {
        int capacity = initialSize;
        byte[] buf = new byte[capacity];
        int nread = 0;
        int rem = buf.length;
        int n;
        // read to EOF which may read more or less than initialSize (eg: file
        // is truncated while we are reading)
        while ((n = source.read(buf, nread, rem)) > 0) {
            nread += n;
            rem -= n;
            assert rem >= 0;
            if (rem == 0) {
                // need larger buffer
                int newCapacity = capacity << 1;
                if (newCapacity < 0) {
                    if (capacity == Integer.MAX_VALUE)
                        throw new OutOfMemoryError("Required array size too large");
                    newCapacity = Integer.MAX_VALUE;
                }
                rem = newCapacity - capacity;
                buf = Arrays.copyOf(buf, newCapacity);
                capacity = newCapacity;
            }
        }
        return (capacity == nread) ? buf : Arrays.copyOf(buf, nread);
    }

    /**
     * Returns the size of a file (in bytes). The size may differ from the actual size on the file system due to compression, support
     * for sparse files, or other reasons. The size of files that are not {@link #isRegularFile regular} files is implementation
     * specific and therefore unspecified.
     * 
     * @param path
     *            the path to the file
     * 
     * @return the file size, in bytes
     * 
     * @throws IOException
     *             if an I/O error occurs
     * @throws SecurityException
     *             In the case of the default provider, and a security manager is installed, its
     *             {@link SecurityManager#checkRead(String) checkRead} method denies read access to the file.
     * 
     * @see BasicFileAttributes#size
     */
    public static long size(MMPath path) throws IOException {
        MMBasicFileAttributes attributes = readAttributes(path, MMBasicFileAttributes.class);
        return attributes.size();
    }

    /**
     * Tests whether a file exists.
     * 
     * <p>
     * The {@code options} parameter may be used to indicate how symbolic links are handled for the case that the file is a symbolic
     * link. By default, symbolic links are followed. If the option {@link LinkOption#NOFOLLOW_LINKS NOFOLLOW_LINKS} is present then
     * symbolic links are not followed.
     * 
     * <p>
     * Note that the result of this method is immediately outdated. If this method indicates the file exists then there is no guarantee
     * that a subsequence access will succeed. Care should be taken when using this method in security sensitive applications.
     * 
     * @param path
     *            the path to the file to test
     * @param options
     *            options indicating how symbolic links are handled .
     * @return {@code true} if the file exists; {@code false} if the file does not exist or its existence cannot be determined.
     * 
     * @throws SecurityException
     *             In the case of the default provider, the {@link SecurityManager#checkRead(String)} is invoked to check read access
     *             to the file.
     * 
     * @see #notExists
     */
    public static boolean exists(MMPath path) {
        try {
            // attempt to read attributes without following links
            readAttributes(path, MMBasicFileAttributes.class);
            // file exists
            return true;
        } catch (IOException x) {
            // does not exist or unable to determine if file exists
            return false;
        }
    }

    /**
     * Tests whether a file is a directory.
     * 
     * <p>
     * The {@code options} array may be used to indicate how symbolic links are handled for the case that the file is a symbolic link.
     * By default, symbolic links are followed and the file attribute of the final target of the link is read. If the option
     * {@link LinkOption#NOFOLLOW_LINKS NOFOLLOW_LINKS} is present then symbolic links are not followed.
     * 
     * <p>
     * Where is it required to distinguish an I/O exception from the case that the file is not a directory then the file attributes can
     * be read with the {@link #readAttributes(Path,Class,LinkOption[]) readAttributes} method and the file type tested with the
     * {@link BasicFileAttributes#isDirectory} method.
     * 
     * @param path
     *            the path to the file to test
     * @param options
     *            options indicating how symbolic links are handled
     * 
     * @return {@code true} if the file is a directory; {@code false} if the file does not exist, is not a directory, or it cannot be
     *         determined if the file is a directory or not.
     * 
     * @throws SecurityException
     *             In the case of the default provider, and a security manager is installed, its
     *             {@link SecurityManager#checkRead(String) checkRead} method denies read access to the file.
     */
    public static boolean isDirectory(MMPath path) {
        try {
            return readAttributes(path, MMBasicFileAttributes.class).isDirectory();
        } catch (IOException ioe) {
            return false;
        }
    }

    /**
     * Move or rename a file to a target file.
     * 
     * <p>
     * By default, this method attempts to move the file to the target file, failing if the target file exists except if the source and
     * target are the {@link #isSameFile same} file, in which case this method has no effect. If the file is a symbolic link then the
     * symbolic link itself, not the target of the link, is moved. This method may be invoked to move an empty directory. In some
     * implementations a directory has entries for special files or links that are created when the directory is created. In such
     * implementations a directory is considered empty when only the special entries exist. When invoked to move a directory that is
     * not empty then the directory is moved if it does not require moving the entries in the directory. For example, renaming a
     * directory on the same {@link FileStore} will usually not require moving the entries in the directory. When moving a directory
     * requires that its entries be moved then this method fails (by throwing an {@code IOException}). To move a <i>file tree</i> may
     * involve copying rather than moving directories and this can be done using the {@link #copy copy} method in conjunction with the
     * {@link #walkFileTree Files.walkFileTree} utility method.
     * 
     * <p>
     * The {@code options} parameter may include any of the following:
     * 
     * <table border=1 cellpadding=5 summary="">
     * <tr>
     * <th>Option</th>
     * <th>Description</th>
     * </tr>
     * <tr>
     * <td> {@link StandardCopyOption#REPLACE_EXISTING REPLACE_EXISTING}</td>
     * <td>If the target file exists, then the target file is replaced if it is not a non-empty directory. If the target file exists
     * and is a symbolic link, then the symbolic link itself, not the target of the link, is replaced.</td>
     * </tr>
     * <tr>
     * <td> {@link StandardCopyOption#ATOMIC_MOVE ATOMIC_MOVE}</td>
     * <td>The move is performed as an atomic file system operation and all other options are ignored. If the target file exists then
     * it is implementation specific if the existing file is replaced or this method fails by throwing an {@link IOException}. If the
     * move cannot be performed as an atomic file system operation then {@link AtomicMoveNotSupportedException} is thrown. This can
     * arise, for example, when the target location is on a different {@code FileStore} and would require that the file be copied, or
     * target location is associated with a different provider to this object.</td>
     * </table>
     * 
     * <p>
     * An implementation of this interface may support additional implementation specific options.
     * 
     * <p>
     * Where the move requires that the file be copied then the {@link BasicFileAttributes#lastModifiedTime last-modified-time} is
     * copied to the new file. An implementation may also attempt to copy other file attributes but is not required to fail if the file
     * attributes cannot be copied. When the move is performed as a non-atomic operation, and a {@code IOException} is thrown, then the
     * state of the files is not defined. The original file and the target file may both exist, the target file may be incomplete or
     * some of its file attributes may not been copied from the original file.
     * 
     * <p>
     * <b>Usage Examples:</b> Suppose we want to rename a file to "newname", keeping the file in the same directory:
     * 
     * <pre>
     *     Path source = ...
     *     Files.move(source, source.resolveSibling("newname"));
     * </pre>
     * 
     * Alternatively, suppose we want to move a file to new directory, keeping the same file name, and replacing any existing file of
     * that name in the directory:
     * 
     * <pre>
     *     Path source = ...
     *     Path newdir = ...
     *     Files.move(source, newdir.resolve(source.getFileName()), REPLACE_EXISTING);
     * </pre>
     * 
     * @param source
     *            the path to the file to move
     * @param target
     *            the path to the target file (may be associated with a different provider to the source path)
     * @param options
     *            options specifying how the move should be done
     * 
     * @return the path to the target file
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
    public static MMPath move(MMPath source, MMPath target) throws IOException {
        MMFileSystemProvider provider = provider(source);
        if (provider(target) == provider) {
            // same provider
            provider.move(source, target);
        } else {
            // different providers
            // CopyMoveHelper.moveToForeignTarget(source, target, options);
            throw new IllegalArgumentException("source and target must be of same type!");
        }
        return target;
    }

    /**
     * Reads a file's attributes as a bulk operation.
     * 
     * <p>
     * The {@code type} parameter is the type of the attributes required and this method returns an instance of that type if supported.
     * All implementations support a basic set of file attributes and so invoking this method with a {@code type} parameter of
     * {@code BasicFileAttributes.class} will not throw {@code UnsupportedOperationException}.
     * 
     * <p>
     * The {@code options} array may be used to indicate how symbolic links are handled for the case that the file is a symbolic link.
     * By default, symbolic links are followed and the file attribute of the final target of the link is read. If the option
     * {@link LinkOption#NOFOLLOW_LINKS NOFOLLOW_LINKS} is present then symbolic links are not followed.
     * 
     * <p>
     * It is implementation specific if all file attributes are read as an atomic operation with respect to other file system
     * operations.
     * 
     * <p>
     * <b>Usage Example:</b> Suppose we want to read a file's attributes in bulk:
     * 
     * <pre>
     *    Path path = ...
     *    BasicFileAttributes attrs = Files.readAttributes(path, BasicFileAttributes.class);
     * </pre>
     * 
     * Alternatively, suppose we want to read file's POSIX attributes without following symbolic links:
     * 
     * <pre>
     * PosixFileAttributes attrs = Files.readAttributes(path, PosixFileAttributes.class, NOFOLLOW_LINKS);
     * </pre>
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
     *             checkRead} method is invoked to check read access to the file. If this method is invoked to read security sensitive
     *             attributes then the security manager may be invoke to check for additional permissions.
     */
    public static <A extends MMBasicFileAttributes> A readAttributes(MMPath path, Class<A> type) throws IOException {
        return provider(path).readAttributes(path, type);
    }

    /**
     * Deletes a file.
     * 
     * <p>
     * An implementation may require to examine the file to determine if the file is a directory. Consequently this method may not be
     * atomic with respect to other file system operations. If the file is a symbolic link then the symbolic link itself, not the final
     * target of the link, is deleted.
     * 
     * <p>
     * If the file is a directory then the directory must be empty. In some implementations a directory has entries for special files
     * or links that are created when the directory is created. In such implementations a directory is considered empty when only the
     * special entries exist. This method can be used with the {@link #walkFileTree walkFileTree} method to delete a directory and all
     * entries in the directory, or an entire <i>file-tree</i> where required.
     * 
     * <p>
     * On some operating systems it may not be possible to remove a file when it is open and in use by this Java virtual machine or
     * other programs.
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
    public static void delete(MMPath path) throws IOException {
        provider(path).delete(path);
    }

    /**
     * Deletes a file if it exists.
     * 
     * <p>
     * As with the {@link #delete(MMPath) delete(MMPath)} method, an implementation may need to examine the file to determine if the
     * file is a directory. Consequently this method may not be atomic with respect to other file system operations. If the file is a
     * symbolic link, then the symbolic link itself, not the final target of the link, is deleted.
     * 
     * <p>
     * If the file is a directory then the directory must be empty. In some implementations a directory has entries for special files
     * or links that are created when the directory is created. In such implementations a directory is considered empty when only the
     * special entries exist.
     * 
     * <p>
     * On some operating systems it may not be possible to remove a file when it is open and in use by this Java virtual machine or
     * other programs.
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
     *             {@link SecurityManager#checkDelete(String)} method is invoked to check delete access to the file.
     */
    // public static boolean deleteIfExists(MMPath path) throws IOException {
    // return provider(path).deleteIfExists(path);
    // }
}

package de.wehner.mediamagpie.common.simplenio.file.spi;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.channels.Channels;
import java.nio.channels.SeekableByteChannel;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.nio.file.attribute.FileAttribute;
import java.util.HashSet;
import java.util.Set;

import de.wehner.mediamagpie.common.simplenio.file.MMFiles;
import de.wehner.mediamagpie.common.simplenio.file.MMPath;

public abstract class MMFileSystemProvider {

    /**
     * Opens a file, returning an input stream to read from the file. This
     * method works in exactly the manner specified by the {@link
     * Files#newInputStream} method.
     *
     * <p> The default implementation of this method opens a channel to the file
     * as if by invoking the {@link #newByteChannel} method and constructs a
     * stream that reads bytes from the channel. This method should be overridden
     * where appropriate.
     *
     * @param   path
     *          the path to the file to open
     * @param   options
     *          options specifying how the file is opened
     *
     * @return  a new input stream
     *
     * @throws  IllegalArgumentException
     *          if an invalid combination of options is specified
     * @throws  UnsupportedOperationException
     *          if an unsupported option is specified
     * @throws  IOException
     *          if an I/O error occurs
     * @throws  SecurityException
     *          In the case of the default provider, and a security manager is
     *          installed, the {@link SecurityManager#checkRead(String) checkRead}
     *          method is invoked to check read access to the file.
     */
    public InputStream newInputStream(MMPath path)
        throws IOException
    {
        return Channels.newInputStream(MMFiles.newByteChannel(path));
    }

    /**
     * Opens or creates a file, returning an output stream that may be used to
     * write bytes to the file. This method works in exactly the manner
     * specified by the {@link Files#newOutputStream} method.
     *
     * <p> The default implementation of this method opens a channel to the file
     * as if by invoking the {@link #newByteChannel} method and constructs a
     * stream that writes bytes to the channel. This method should be overridden
     * where appropriate.
     *
     * @param   path
     *          the path to the file to open or create
     * @param   options
     *          options specifying how the file is opened
     *
     * @return  a new output stream
     *
     * @throws  IllegalArgumentException
     *          if {@code options} contains an invalid combination of options
     * @throws  UnsupportedOperationException
     *          if an unsupported option is specified
     * @throws  IOException
     *          if an I/O error occurs
     * @throws  SecurityException
     *          In the case of the default provider, and a security manager is
     *          installed, the {@link SecurityManager#checkWrite(String) checkWrite}
     *          method is invoked to check write access to the file. The {@link
     *          SecurityManager#checkDelete(String) checkDelete} method is
     *          invoked to check delete access if the file is opened with the
     *          {@code DELETE_ON_CLOSE} option.
     */
    public OutputStream newOutputStream(MMPath path)
        throws IOException
    {
        return Channels.newOutputStream(newByteChannel(path));
    }

    /**
     * Opens or creates a file, returning a seekable byte channel to access the
     * file. This method works in exactly the manner specified by the {@link
     * Files#newByteChannel(Path,Set,FileAttribute[])} method.
     *
     * @param   path
     *          the path to the file to open or create
     * @param   options
     *          options specifying how the file is opened
     * @param   attrs
     *          an optional list of file attributes to set atomically when
     *          creating the file
     *
     * @return  a new seekable byte channel
     *
     * @throws  IllegalArgumentException
     *          if the set contains an invalid combination of options
     * @throws  UnsupportedOperationException
     *          if an unsupported open option is specified or the array contains
     *          attributes that cannot be set atomically when creating the file
     * @throws  FileAlreadyExistsException
     *          if a file of that name already exists and the {@link
     *          StandardOpenOption#CREATE_NEW CREATE_NEW} option is specified
     *          <i>(optional specific exception)</i>
     * @throws  IOException
     *          if an I/O error occurs
     * @throws  SecurityException
     *          In the case of the default provider, and a security manager is
     *          installed, the {@link SecurityManager#checkRead(String) checkRead}
     *          method is invoked to check read access to the path if the file is
     *          opened for reading. The {@link SecurityManager#checkWrite(String)
     *          checkWrite} method is invoked to check write access to the path
     *          if the file is opened for writing. The {@link
     *          SecurityManager#checkDelete(String) checkDelete} method is
     *          invoked to check delete access if the file is opened with the
     *          {@code DELETE_ON_CLOSE} option.
     */
    public abstract SeekableByteChannel newByteChannel(MMPath path) throws IOException;
    
    /**
     * Creates a new directory. This method works in exactly the manner
     * specified by the {@link Files#createDirectory} method.
     *
     * @param   dir
     *          the directory to create
     * @param   attrs
     *          an optional list of file attributes to set atomically when
     *          creating the directory
     *
     * @throws  UnsupportedOperationException
     *          if the array contains an attribute that cannot be set atomically
     *          when creating the directory
     * @throws  FileAlreadyExistsException
     *          if a directory could not otherwise be created because a file of
     *          that name already exists <i>(optional specific exception)</i>
     * @throws  IOException
     *          if an I/O error occurs or the parent directory does not exist
     * @throws  SecurityException
     *          In the case of the default provider, and a security manager is
     *          installed, the {@link SecurityManager#checkWrite(String) checkWrite}
     *          method is invoked to check write access to the new directory.
     */
    public abstract void createDirectory(MMPath dir)
        throws IOException;

}

package de.wehner.mediamagpie.conductor.fslayer;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;

/**
 * Abstraction to a file which can be served from normal file system or mongoDB or S3 etc. TODO rwe...
 */
public interface IFile {

    /**
     * Provides the URI that can be stored in {@linkplain Media} and used to reload the file from {@linkplain IFSLayer}.
     * 
     * @return
     */
    URI getUri();

    /**
     * Provides the path as string of this file or directory.
     * 
     * @return
     */
    String getPath();

    /**
     * Equals to <code>File.getName()</code>
     * 
     * @return
     */
    String getName();

    IFile getParentFile();

    /**
     * @return <code>true</code> if this object represents a directory. (Equals to <code>File</code>)
     */
    boolean isDirectory();

    /**
     * Equals like <code>File.listFiles()</code> method.
     * 
     * @return
     */
    IFile[] listFiles();

    /**
     * Provides an input stream used to read from file.
     * 
     * @return
     * @throws FileNotFoundException
     *             In case file does not exists or is not readable
     */
    InputStream getInputStream() throws FileNotFoundException;

    /**
     * Provides an OutputStream used to write binary data to file. When the OutputStream is used a previous content of this object will be
     * overridden.
     * 
     * @return
     * @throws FileNotFoundException
     *             If file is not writable
     */
    OutputStream getOutputStream() throws FileNotFoundException;

    /**
     * Tests if a file of directory exists
     * 
     * @return
     */
    boolean exists();

    URI toURI();

}

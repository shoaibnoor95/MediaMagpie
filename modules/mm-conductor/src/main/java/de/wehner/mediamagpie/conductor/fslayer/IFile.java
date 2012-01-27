package de.wehner.mediamagpie.conductor.fslayer;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;

/**
 * Abstraction to a file which can be served from normal file system or mongoDB or S3 etc. TODO rwe...
 */
public interface IFile {

    /**
     * Provides the URI that can be stored in {@linkplain Media} and used to reload the file from {@linkplain FSLayer}.
     * 
     * @return
     */
    URI getUri();

    /**
     * Provides an input stream used to read from file.
     * 
     * @return
     */
    InputStream getInputStream();

    /**
     * Provides an OutputStream used to write binary data to file. When the OutputStream is used a previous content of this object will be
     * overridden.
     * 
     * @return
     */
    OutputStream getOutputStream();

}

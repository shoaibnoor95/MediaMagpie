package de.wehner.mediamagpie.common.fslayer;

import java.io.File;
import java.io.IOException;

public interface IFSLayer {

    /**
     * Creates a new IFile.
     * 
     * @param parentFile
     *            provides the path for the new file
     * @param name
     *            provides the name for the new file
     * @return
     */
    IFile createFile(IFile parentFile, String name);

    /**
     * Creates a new <code>IFile</code> for relevant IFSLayer implementation.
     * 
     * @param path
     *            The Path
     * @param fileName
     *            the file name
     * @return The proper <code>IFile</code> implementation of this file system implementation.
     */
    IFile createFile(File path, String fileName);

    /**
     * Creates a new <code>IFile</code> for relevant IFSLayer implementation.
     * 
     * @param path
     *            The Path
     * @param fileName
     *            the file name
     * @return The proper <code>IFile</code> implementation of this file system implementation.
     */
    IFile createFile(String path, String fileName);

    /**
     * Creates a new empty File on file system.
     * 
     * @param tempFile
     *            The file describing the new file name.
     */
    void createFile(IFile tempFile);

    // /**
    // * Provides the schema, this FSLayer implements. EG: 'file', 'mongoDB', etc.
    // *
    // * @return
    // */
    // String getSchema();

    /**
     * Creates a new IFile object.
     * 
     * @param filePath
     *            The path of new file. This is normally a unique name of this file. In case of local file system, it would be the path/name
     *            like '/user/rwe/pictures/img002.jpg'. The pathOfFile is not an URI and does NOT contain the schema name.
     * @return
     */
    IFile createFile(String filePath);

    /**
     * Creates a new directory instance.
     * 
     * @param path
     *            The path dir directory, like /home/rwe/medias/data/users/
     * @return Dir concreate <code>IDir</code> implementation
     */
    IFile createDir(String path);

    

    // /**
    // * Finds all files that are located within a given path.
    // *
    // * @param path
    // * The path to search files within. This will be used like the path in normal file sytems.
    // * @return
    // */
    // List<IFile> listFiles(String path);

    // /**
    // * Creates a directory with given path.
    // *
    // * @param path
    // * The directory
    // */
    // void mkDir(String path);

    // /**
    // * Deletes a file from system. // TODO rwe: Maybe it will be better to use a String or URI instead of IFile
    // *
    // * @param fileToDelete
    // */
    // void deleteFile(IFile fileToDelete);

    /**
     * Create on ore more directories with specified path name. When the method will be leaved without an exception you can be sure the
     * directories are created on the FS.
     * 
     * @param path
     *            The directory name or the nested directory name.
     * @throws IOException
     *             when one directory can't be created.
     */
    void forceMkdir(IFile path) throws IOException;
}

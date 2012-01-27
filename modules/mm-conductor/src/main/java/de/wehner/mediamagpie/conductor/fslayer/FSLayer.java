package de.wehner.mediamagpie.conductor.fslayer;

import java.util.List;

public interface FSLayer {

    /**
     * Provides the schema, this FSLayer implements. EG: 'file', 'mongoDB', etc.
     * 
     * @return
     */
    String getSchema();

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
     * Finds all files that are located within a given path.
     * 
     * @param path
     *            The path to search files within. This will be used like the path in normal file sytems.
     * @return
     */
    List<IFile> listFiles(String path);

    /**
     * Deletes a file from system. // TODO rwe: Maybe it will be better to use a String or URI instead of IFile
     * 
     * @param fileToDelete
     */
    void deleteFile(IFile fileToDelete);
}

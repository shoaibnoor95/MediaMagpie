package de.wehner.mediamagpie.common.simplenio.file;


/**
 * Checked exception thrown when a file system operation fails because a directory is not empty.
 * 
 */
public class MMDirectoryNotEmptyException extends MMFileSystemException {
    
    static final long serialVersionUID = 3056667871802779003L;

    /**
     * Constructs an instance of this class.
     * 
     * @param dir
     *            a string identifying the directory or {@code null} if not known
     */
    public MMDirectoryNotEmptyException(String dir) {
        super(dir);
    }
}

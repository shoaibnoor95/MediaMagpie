package de.wehner.mediamagpie.common.simplenio.file;

public class FileSystemAlreadyExistsException extends RuntimeException {
    
    static final long serialVersionUID = -5438419127181131148L;

    /**
     * Constructs an instance of this class.
     */
    public FileSystemAlreadyExistsException() {
    }

    /**
     * Constructs an instance of this class.
     * 
     * @param msg
     *            the detail message
     */
    public FileSystemAlreadyExistsException(String msg) {
        super(msg);
    }
}

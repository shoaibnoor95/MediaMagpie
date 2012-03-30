package de.wehner.mediamagpie.common.simplenio.file;

/**
 * Runtime exception thrown when a file system cannot be found.
 */

public class MMFileSystemNotFoundException extends RuntimeException {
    static final long serialVersionUID = 7999581764446402397L;

    /**
     * Constructs an instance of this class.
     */
    public MMFileSystemNotFoundException() {
    }

    /**
     * Constructs an instance of this class.
     * 
     * @param msg
     *            the detail message
     */
    public MMFileSystemNotFoundException(String msg) {
        super(msg);
    }
}

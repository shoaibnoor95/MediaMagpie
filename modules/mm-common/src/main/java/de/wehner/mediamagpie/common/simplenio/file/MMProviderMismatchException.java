package de.wehner.mediamagpie.common.simplenio.file;

/**
 * Unchecked exception thrown when an attempt is made to invoke a method on an object created by one file system provider with a parameter
 * created by a different file system provider.
 */
public class MMProviderMismatchException extends java.lang.IllegalArgumentException {
    
    static final long serialVersionUID = 4990847485741612530L;

    /**
     * Constructs an instance of this class.
     */
    public MMProviderMismatchException() {
    }

    /**
     * Constructs an instance of this class.
     * 
     * @param msg
     *            the detail message
     */
    public MMProviderMismatchException(String msg) {
        super(msg);
    }
}

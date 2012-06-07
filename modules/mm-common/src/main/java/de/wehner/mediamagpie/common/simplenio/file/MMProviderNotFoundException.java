package de.wehner.mediamagpie.common.simplenio.file;

public class MMProviderNotFoundException extends RuntimeException {

    static final long serialVersionUID = -1L;

    /**
     * Constructs an instance of this class.
     */
    public MMProviderNotFoundException() {
    }

    /**
     * Constructs an instance of this class.
     * 
     * @param msg
     *            the detail message
     */
    public MMProviderNotFoundException(String msg) {
        super(msg);
    }
}

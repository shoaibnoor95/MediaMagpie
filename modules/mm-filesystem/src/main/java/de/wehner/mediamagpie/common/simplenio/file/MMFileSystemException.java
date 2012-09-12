package de.wehner.mediamagpie.common.simplenio.file;

import java.io.IOException;

public class MMFileSystemException extends IOException {
    static final long serialVersionUID = -3055425747967319812L;

    private final String file;
    private final String other;

    /**
     * Constructs an instance of this class. This constructor should be used when an operation involving one file fails and there isn't any
     * additional information to explain the reason.
     * 
     * @param file
     *            a string identifying the file or {@code null} if not known.
     */
    public MMFileSystemException(String file) {

        super((String) null);
        this.file = file;
        this.other = null;
    }

    /**
     * Constructs an instance of this class. This constructor should be used when an operation involving two files fails, or there is
     * additional information to explain the reason.
     * 
     * @param file
     *            a string identifying the file or {@code null} if not known.
     * @param other
     *            a string identifying the other file or {@code null} if there isn't another file or if not known
     * @param reason
     *            a reason message with additional information or {@code null}
     */
    public MMFileSystemException(String file, String other, String reason) {
        super(reason);
        this.file = file;
        this.other = other;
    }

    /**
     * Returns the file used to create this exception.
     * 
     * @return the file (can be {@code null})
     */
    public String getFile() {
        return file;
    }

    /**
     * Returns the other file used to create this exception.
     * 
     * @return the other file (can be {@code null})
     */
    public String getOtherFile() {
        return other;
    }

    /**
     * Returns the string explaining why the file system operation failed.
     * 
     * @return the string explaining why the file system operation failed
     */
    public String getReason() {
        return super.getMessage();
    }

    /**
     * Returns the detail message string.
     */
    @Override
    public String getMessage() {
        if (file == null && other == null)
            return getReason();
        StringBuilder sb = new StringBuilder();
        if (file != null)
            sb.append(file);
        if (other != null) {
            sb.append(" -> ");
            sb.append(other);
        }
        if (getReason() != null) {
            sb.append(": ");
            sb.append(getReason());
        }
        return sb.toString();
    }
}
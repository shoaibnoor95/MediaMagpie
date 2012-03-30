package de.wehner.mediamagpie.common.simplenio.file;

public enum MMStandardOpenOption implements MMOpenOption {
    /**
     * Open for read access.
     */
    READ,

    /**
     * Open for write access.
     */
    WRITE,

    /**
     * If the file is opened for {@link #WRITE} access then bytes will be written to the end of the file rather than the beginning.
     * 
     * <p>
     * If the file is opened for write access by other programs, then it is file system specific if writing to the end of the file is
     * atomic.
     */
    APPEND,

    /**
     * If the file already exists and it is opened for {@link #WRITE} access, then its length is truncated to 0. This option is ignored
     * if the file is opened only for {@link #READ} access.
     */
    TRUNCATE_EXISTING,

    /**
     * Create a new file if it does not exist. This option is ignored if the {@link #CREATE_NEW} option is also set. The check for the
     * existence of the file and the creation of the file if it does not exist is atomic with respect to other file system operations.
     */
    CREATE,

    /**
     * Create a new file, failing if the file already exists. The check for the existence of the file and the creation of the file if
     * it does not exist is atomic with respect to other file system operations.
     */
    CREATE_NEW

}

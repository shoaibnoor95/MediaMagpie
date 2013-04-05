package de.wehner.mediamagpie.common.fs;

import java.io.File;

/**
 * TODO rwe: Rename this class because it is more a configuration than an File System.
 * @author ralfwehner
 *
 */
public class ApplicationFS {

    private final File _tempMediaPath;

    public ApplicationFS(String tempMediaPath) {
        this(new File(tempMediaPath));
    }

    public ApplicationFS(File tempMediaPath) {
        super();
        _tempMediaPath = tempMediaPath;
    }

    public File getTempMediaPath() {
        return _tempMediaPath;
    }
}

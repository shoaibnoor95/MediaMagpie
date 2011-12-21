package de.wehner.mediamagpie.common.fs;

import java.io.File;

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

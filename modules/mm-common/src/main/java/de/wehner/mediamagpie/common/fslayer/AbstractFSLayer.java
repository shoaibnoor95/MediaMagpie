package de.wehner.mediamagpie.common.fslayer;

import java.io.File;

public abstract class AbstractFSLayer implements IFSLayer {

    @Override
    public IFile createFile(IFile parentFile, String name) {
        return createFile(parentFile.getPath(), name);
    }

    @Override
    public IFile createFile(File parentFile, String fileName) {
        return createFile(parentFile.getPath(), fileName);
    }

    @Override
    public void createFile(IFile file) {
        createFile(file.getPath());
    }

}

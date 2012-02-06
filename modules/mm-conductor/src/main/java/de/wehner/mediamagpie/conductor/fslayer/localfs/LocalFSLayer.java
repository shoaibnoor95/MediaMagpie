package de.wehner.mediamagpie.conductor.fslayer.localfs;

import java.io.File;
import java.util.List;

import org.springframework.stereotype.Component;

import de.wehner.mediamagpie.conductor.fslayer.IFSLayer;
import de.wehner.mediamagpie.conductor.fslayer.IFile;

@Component
public class LocalFSLayer implements IFSLayer {

    @Override
    public IFile createFile(String path, String fileName) {
        return new LocalFSFile(new File(path), fileName);
    }

    @Override
    public String getSchema() {
        return new File("").toURI().getScheme();
    }

    @Override
    public IFile createDir(String path) {
        return new LocalFSFile(path, true);
    }

    @Override
    public IFile createFile(String filePath) {
        return new LocalFSFile(filePath, false);
    }

    @Override
    public List<IFile> listFiles(String path) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void mkDir(String path) {
        // TODO Auto-generated method stub

    }

    @Override
    public void deleteFile(IFile fileToDelete) {
        // TODO Auto-generated method stub

    }

}

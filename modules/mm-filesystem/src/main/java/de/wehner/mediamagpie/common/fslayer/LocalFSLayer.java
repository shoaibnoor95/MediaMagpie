package de.wehner.mediamagpie.common.fslayer;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Deprecated
@Component
@Profile({ "local", "default" })
public class LocalFSLayer extends AbstractFSLayer implements IFSLayer {

    @Override
    public IFile createFile(String path, String fileName) {
        return new LocalFSFile(new File(path), fileName);
    }

//    @Override
//    public String getSchema() {
//        return new File("").toURI().getScheme();
//    }

    @Override
    public IFile createFile(String filePath) {
        return new LocalFSFile(new File(filePath), false);
    }

    // @Override
    // public List<IFile> listFiles(String path) {
    // // TODO Auto-generated method stub
    // return null;
    // }

    // @Override
    // public void mkDir(String path) {
    // // TODO Auto-generated method stub
    //
    // }
    //
    // @Override
    // public void deleteFile(IFile fileToDelete) {
    // // TODO Auto-generated method stub
    //
    // }

}

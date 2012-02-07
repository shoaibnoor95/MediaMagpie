package de.wehner.mediamagpie.common.fslayer;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

public class LocalFSFile extends AbstractFile implements IFile {

    private final File _file;

    /**
     * Creates af file or directory.
     * 
     * @param path
     * @param isDir
     * @deprecated TODO rwe this method
     */
    public LocalFSFile(String path, boolean isDir) {
        this(new File(path), isDir);
    }

    public LocalFSFile(File parent, String fileOrPath) {
        this(new File(parent, fileOrPath), false);
    }

    public LocalFSFile(File file, boolean isDir) {
        super(new LocalFSLayer());
        _file = file;
    }

    @Override
    public URI getUri() {
        return _file.toURI();
    }

    @Override
    public InputStream getInputStream() throws FileNotFoundException {
        FileInputStream is = new FileInputStream(_file);
        return is;
    }

    @Override
    public OutputStream getOutputStream() throws FileNotFoundException {
        return new FileOutputStream(_file);
    }

    @Override
    public String getPath() {
        return _file.getPath();
    }

    @Override
    public String getName() {
        return _file.getName();
    }

    @Override
    public IFile getParentFile() {
        String parentDir = _file.getParent();
        return new LocalFSFile(new File(parentDir), true);
    }

    @Override
    public boolean isDirectory() {
        return _file.isDirectory();
    }

    @Override
    public void createNewFile() throws IOException {
        _file.createNewFile();
    }

    @Override
    public IFile[] listFiles() {
        File[] listFiles = _file.listFiles();
        List<LocalFSFile> itemsInDir = new ArrayList<LocalFSFile>();
        for (File file : listFiles) {
            itemsInDir.add(new LocalFSFile(file, false));
        }
        return itemsInDir.toArray(new IFile[0]);
    }

    @Override
    public boolean exists() {
        return _file.exists();
    }

    @Override
    public URI toURI() {
        return _file.toURI();
    }

    public File getFile() {
        return _file;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((_file == null) ? 0 : _file.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        LocalFSFile other = (LocalFSFile) obj;
        if (_file == null) {
            if (other._file != null)
                return false;
        } else if (!_file.equals(other._file))
            return false;
        return true;
    }

    @Override
    public String toString() {
        return ReflectionToStringBuilder.toString(this, ToStringStyle.SHORT_PREFIX_STYLE);
    }

    @Override
    public long length() {
        return _file.length();
    }

    @Override
    public void delete() {
        _file.delete();
    }

    @Override
    public File toFile() {
        return _file;
    }
}

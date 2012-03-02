package de.wehner.mediamagpie.common.fslayer;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;

public class MongoDbFile implements IFile {

    private final String _path;
    // TODO: move the content to db layer
    private byte[] _content;
    
    private final MongoDbFSLayer _mongoDbFSLayer;

    public MongoDbFile(MongoDbFSLayer mongoDbFSLayer, String filePath) {
        _mongoDbFSLayer = mongoDbFSLayer;
        _path = filePath;
    }

    public String getPath() {
        return _path;
    }

    @Override
    public URI getUri() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String getName() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public IFile getParentFile() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public boolean isDirectory() {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public IFile[] listFiles() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public InputStream getInputStream() throws FileNotFoundException {
        
        return new ByteArrayInputStream(_content);
    }

    @Override
    public OutputStream getOutputStream() throws FileNotFoundException {
        final ByteArrayOutputStream os = new ByteArrayOutputStream() {

            @Override
            public void close() throws IOException {
                super.close();
                // TODO rwe: This works only for non-big files. If we want to handle big files, we have to splitt the content into multiple
                // mongo db file objects.
                this.flush();
                byte[] data = this.toByteArray();
                _content = new byte[data.length];
                System.arraycopy(data, 0, _content, 0, data.length);
                
                // write to db
                _mongoDbFSLayer.
            }
        };
        return os;
    }

    @Override
    public boolean exists() {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public URI toURI() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void createNewFile() throws IOException {
        // TODO Auto-generated method stub

    }

    @Override
    public long length() {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public void delete() {
        // TODO Auto-generated method stub

    }

    @Override
    public File toFile() {
        // TODO Auto-generated method stub
        return null;
    }

}

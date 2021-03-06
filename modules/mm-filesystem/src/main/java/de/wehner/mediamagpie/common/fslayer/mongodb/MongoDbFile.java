package de.wehner.mediamagpie.common.fslayer.mongodb;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.wehner.mediamagpie.common.fslayer.IFile;
import de.wehner.mediamagpie.common.fslayer.mongodb.MongoDbFileDescriptor.Type;

/**
 * Implementation of <code>IFile</code> were the file will be stored within a mongoDB.
 * 
 * @author ralfwehner
 * 
 */
public class MongoDbFile implements IFile {

    private static final Logger LOG = LoggerFactory.getLogger(MongoDbFile.class);

    /**
     * Contains a unique id for this file. Compared to a file on local file system, this will be the path and the file name.
     */
    private final String _path;

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
        MongoDbFileDescriptor desc = getMongoDbFileDescriptor();
        if (desc != null) {
            // TODO rwe: simplification: First use only on file data object
            ByteArrayInputStream inputStream = new ByteArrayInputStream(desc.getData().get(0).getContent());
            return inputStream;
        } else {
            // internal error
            LOG.error("internal error: MongoDbFileDescriptor for '" + _path + "' does not exist.");
        }
        return null;
    }

    @Override
    public OutputStream getOutputStream() throws FileNotFoundException {
        final ByteArrayOutputStream os = new ByteArrayOutputStream() {

            @Override
            public void close() throws IOException {
                super.close();
                // TODO rwe: This works only for non-big files. If we want to handle big files, we have to split the content into multiple
                // mongo db file objects.
                this.flush();
                byte[] data = this.toByteArray();

                MongoDbFileDescriptor mongoDbFileDescriptor = getMongoDbFileDescriptor();
                MongoDbFileData mongoDbFileData = new MongoDbFileData(null, _path, data);
                if (mongoDbFileDescriptor == null) {
                    mongoDbFileDescriptor = new MongoDbFileDescriptor(_path, Type.FILE);
                    mongoDbFileDescriptor.setData(Arrays.asList(mongoDbFileData));
                } else {
                    mongoDbFileDescriptor.setData(Arrays.asList(mongoDbFileData));
                }
                // write to db
                getDao().saveOrUpdate(mongoDbFileDescriptor);
            }
        };
        return os;
    }

    @Override
    public boolean exists() {
        return (getDao().findByPath(_path) != null);
    }

    @Override
    public URI toURI() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void createNewFile() throws IOException {
        MongoDbFileDescriptor mongoDbFileDescriptor = new MongoDbFileDescriptor(_path, Type.FILE);
        // write to db
        getDao().saveOrUpdate(mongoDbFileDescriptor);
    }

    @Override
    public void createDir() {
        // does the directory already exist?
        MongoDbFileDescriptor dir = getMongoDbFileDescriptor();
        if (dir != null) {
            LOG.warn("Directory '" + dir + "' does already exists.");
        }
        MongoDbFileDescriptor mongoDbFileDescriptor = new MongoDbFileDescriptor(_path, Type.DIR);
        // write to db
        getDao().saveOrUpdate(mongoDbFileDescriptor);
    }

    @Override
    public void forceMkdir() throws IOException {
        // TODO Auto-generated method stub
    }

    @Override
    public long length() {
        MongoDbFileDescriptor fileDescriptor = getMongoDbFileDescriptor();
        if (fileDescriptor != null) {
            List<MongoDbFileData> data = fileDescriptor.getData();
            if (data == null || data.size() == 0) {
                return 0;
            }
            // TODO rwe: assumption, we have only one!
            return data.get(0).getContent().length;
        }
        return 0;
    }

    @Override
    public void delete() {
        MongoDbFileDescriptor fileDescriptor = getMongoDbFileDescriptor();
        if (fileDescriptor != null) {
            getDao().delete(fileDescriptor);
        } else {
            LOG.warn("Can not delete file '" + _path + "' because it does not exist.");
        }
    }

    private MongoDbFileDescriptorDao getDao() {
        return _mongoDbFSLayer.getMongoDbFileDescriptorDao();
    }

    private MongoDbFileDescriptor getMongoDbFileDescriptor() {
        MongoDbFileDescriptor fileDescriptor = getDao().findByPath(_path);
        return fileDescriptor;
    }

    @Override
    public File toFile() {
        // only relevant for local file system files
        return null;
    }

}

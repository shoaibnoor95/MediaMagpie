package de.wehner.mediamagpie.common.fslayer.mongodb;

import java.io.File;
import java.io.IOException;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import de.wehner.mediamagpie.common.fslayer.AbstractFSLayer;
import de.wehner.mediamagpie.common.fslayer.IFSLayer;
import de.wehner.mediamagpie.common.fslayer.IFile;

@Component
@Profile({ "local-mongo", "cloud" })
public class MongoDbFSLayer extends AbstractFSLayer implements IFSLayer {

    private final MongoDbFileDescriptorDao _mongoDbFileDescriptorDao;

    @Autowired
    public MongoDbFSLayer(MongoDbFileDescriptorDao mongoTemplate) {
        super();
        _mongoDbFileDescriptorDao = mongoTemplate;
    }

    @Override
    public IFile createFile(String path, String fileName) {
        File normalizer = new File(path, fileName);
        return new MongoDbFile(this, normalizer.getPath());
    }

    // @Override
    // public String getSchema() {
    // // TODO Auto-generated method stub
    // return null;
    // }

    @Override
    public IFile createFile(String filePath) {
        if (StringUtils.isEmpty(filePath)) {
            throw new IllegalArgumentException("The argument filePath must not be empty.");
        }
        File normalizer = new File(filePath);
        MongoDbFile mongoDbFile = new MongoDbFile(this, normalizer.getPath());
        return mongoDbFile;
    }

    // @Override
    // public String getSchema() {
    // // TODO Auto-generated method stub
    // return null;
    // }

    @Override
    public IFile createDir(String path) {
        if (StringUtils.isEmpty(path)) {
            throw new IllegalArgumentException("The argument path must not be empty.");
        }
        File normalizer = new File(path);
        // does the directory already exist?
        MongoDbFileDescriptor dir = _mongoDbFileDescriptorDao.findByPath(normalizer.getPath());
        if (dir != null) {
            return dir;
        }
        MongoDbFile mongoDbFile = new MongoDbFile(this, normalizer.getPath());
        return null;
    }

    @Override
    public void forceMkdir(IFile path) throws IOException {
        // TODO Auto-generated method stub

    }

    MongoDbFileDescriptorDao getMongoDbFileDescriptorDao() {
        return _mongoDbFileDescriptorDao;
    }
}

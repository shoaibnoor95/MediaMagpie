package de.wehner.mediamagpie.common.fslayer;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Component;

@Component
@Profile({ "local-mongo", "cloud" })
public class MongoDbFSLayer extends AbstractFSLayer implements IFSLayer {

    private final MongoTemplate _mongoTemplate;

    @Autowired
    public MongoDbFSLayer(MongoTemplate mongoTemplate) {
        super();
        _mongoTemplate = mongoTemplate;
    }

    @Override
    public IFile createFile(String path, String fileName) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String getSchema() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public IFile createDir(String path) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public IFile createFile(String filePath) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void forceMkdir(IFile path) throws IOException {
        // TODO Auto-generated method stub

    }

}

package de.wehner.mediamagpie.common.fslayer.mongodb;

import static org.springframework.data.mongodb.core.query.Criteria.where;

import java.io.File;
import java.io.IOException;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Component;

import de.wehner.mediamagpie.common.fslayer.AbstractFSLayer;
import de.wehner.mediamagpie.common.fslayer.IFSLayer;
import de.wehner.mediamagpie.common.fslayer.IFile;

@Component
@Profile({ "local-mongo", "cloud" })
public class MongoDbFSLayer extends AbstractFSLayer implements IFSLayer {

    private final MongoOperations _mongoOperation;

    @Autowired
    public MongoDbFSLayer(MongoTemplate mongoTemplate) {
        super();
        _mongoOperation = mongoTemplate;
    }

    @Override
    public IFile createFile(String path, String fileName) {
        File normalizer = new File(path, fileName);
        return new MongoDbFile(this, normalizer.getPath());
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
        if (StringUtils.isEmpty(filePath)) {
            throw new IllegalArgumentException("The filePath must not be empty.");
        }
        File normalizer = new File(filePath);
        MongoDbFile mongoDbFile = new MongoDbFile(this, normalizer.getPath());
        return mongoDbFile;
    }

    @Override
    public void forceMkdir(IFile path) throws IOException {
        // TODO Auto-generated method stub

    }

    // Maybe we will store this into a separate service
    void save(MongoDbFileData fileData) {
        _mongoOperation.save(fileData);
    }

    MongoDbFileData findByPath(String path){
        Query query = new Query(where("_path").is(path));
        MongoDbFileData findOne = _mongoOperation.findOne(query, MongoDbFileData.class);
        return findOne;
    }
}

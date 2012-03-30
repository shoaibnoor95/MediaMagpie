package de.wehner.mediamagpie.common.fslayer.mongodb;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.repository.support.SimpleMongoRepository;
import org.springframework.stereotype.Component;

import de.wehner.mediamagpie.common.util.CollectionUtil;

@Component
@Profile({ "local-mongo", "cloud" })
public class MongoDbFileDataDao {

    public final String COLLECTION = "mongoDbFileData";

    /**
     * The MongoTemplate
     */
    private final MongoOperations _mongoOperation;

    /**
     * The spring's Repository support
     */
    private final SimpleMongoRepository<MongoDbFileData, String> _fileDataRepository;

    @Autowired
    public MongoDbFileDataDao(MongoTemplate mongoTemplate) {
        super();
        _mongoOperation = mongoTemplate;
        _fileDataRepository = new SimpleMongoRepository<MongoDbFileData, String>(
                new org.springframework.data.mongodb.repository.query.MongoEntityInformation<MongoDbFileData, String>() {

                    @Override
                    public boolean isNew(MongoDbFileData entity) {
                        // TODO Auto-generated method stub
                        return false;
                    }

                    @Override
                    public String getId(MongoDbFileData entity) {
                        return entity.getId();
                    }

                    @Override
                    public Class<String> getIdType() {
                        return String.class;
                    }

                    @Override
                    public Class<MongoDbFileData> getJavaType() {
                        return MongoDbFileData.class;
                    }

                    @Override
                    public String getCollectionName() {
                        return COLLECTION;
                    }

                    @Override
                    public String getIdAttribute() {
                        return "_id";
                    }
                }, _mongoOperation);
    }

    public void update(List<MongoDbFileData> existingData, List<MongoDbFileData> newData) {
        if (CollectionUtil.isEmpty(newData)) {
            if (!CollectionUtil.isEmpty(existingData)) {
                // delete all existing data
                _fileDataRepository.delete(existingData);
            }
        } else {
            // we have new data to update
            if (CollectionUtil.isEmpty(existingData)) {
                throw new RuntimeException("Internal error: No existing list is present to add new data");
            }
            // update existing data or add new one
            for (int i = 0; i < newData.size(); i++) {
                if (existingData.size() > i) {
                    // replace existing data element with new data
                    existingData.get(i).setContent(newData.get(i).getContent());
                } else {
                    // add new element to existing data
                    existingData.add(newData.get(i));
                }
                _fileDataRepository.save(existingData.get(i));
            }
            // remove obsolete data
            for (int i = newData.size(); i < existingData.size(); i++) {
                _fileDataRepository.delete(existingData.get(i));
            }
        }
    }

}

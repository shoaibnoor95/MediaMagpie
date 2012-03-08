package de.wehner.mediamagpie.common.fslayer.mongodb;

import static org.springframework.data.mongodb.core.query.Criteria.where;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.repository.support.SimpleMongoRepository;
import org.springframework.stereotype.Component;

@Component
@Profile({ "local-mongo", "cloud" })
public class MongoDbFileDescriptorDao {

    public final String COLLECTION = "mongoDbFileDescriptor";

    private final MongoOperations _mongoOperation;
    /**
     * TODO rwe: checkout if that makes sense to use
     */
    private final SimpleMongoRepository<MongoDbFileDescriptor, String> _fileDescriptorRepository;

    @Autowired
    public MongoDbFileDescriptorDao(MongoTemplate mongoTemplate) {
        super();
        _mongoOperation = mongoTemplate;
        _fileDescriptorRepository = new SimpleMongoRepository<MongoDbFileDescriptor, String>(
                new org.springframework.data.mongodb.repository.query.MongoEntityInformation<MongoDbFileDescriptor, String>() {

                    @Override
                    public boolean isNew(MongoDbFileDescriptor entity) {
                        // TODO Auto-generated method stub
                        return false;
                    }

                    @Override
                    public String getId(MongoDbFileDescriptor entity) {
                        return entity.getId();
                    }

                    @Override
                    public Class<String> getIdType() {
                        return String.class;
                    }

                    @Override
                    public Class<MongoDbFileDescriptor> getJavaType() {
                        return MongoDbFileDescriptor.class;
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

    public void saveOrUpdate(MongoDbFileDescriptor descriptor) {
        MongoDbFileDescriptor byPath = findByPath(descriptor.getPath());
        if (byPath != null) {
            // update
            // see 'Upserting' in
            // http://static.springsource.org/spring-data/data-mongodb/docs/current/reference/html/#mongo-template.save-update-remove
        } else {
            // insert new
            if (descriptor.getData() != null) {
                for (MongoDbFileData data : descriptor.getData()) {
                    _mongoOperation.save(data);
                }
            }
            _mongoOperation.save(descriptor, COLLECTION);
        }
    }

    public MongoDbFileDescriptor findByPath(String path) {
        Query query = new Query(where("_path").is(path));
        MongoDbFileDescriptor findOne = _mongoOperation.findOne(query, MongoDbFileDescriptor.class, COLLECTION);
        return findOne;
    }

    public void delete(MongoDbFileDescriptor fileDescriptor) {
        _fileDescriptorRepository.delete(fileDescriptor);
    }

}

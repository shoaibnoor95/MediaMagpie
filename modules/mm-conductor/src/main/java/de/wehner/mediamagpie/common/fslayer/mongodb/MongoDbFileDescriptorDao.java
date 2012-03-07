package de.wehner.mediamagpie.common.fslayer.mongodb;

import static org.springframework.data.mongodb.core.query.Criteria.where;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Component;

@Component
@Profile({ "local-mongo", "cloud" })
public class MongoDbFileDescriptorDao {

    private final MongoOperations _mongoOperation;

    @Autowired
    public MongoDbFileDescriptorDao(MongoTemplate mongoTemplate) {
        super();
        _mongoOperation = mongoTemplate;
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
            _mongoOperation.save(descriptor);
        }
    }

    public MongoDbFileDescriptor findByPath(String path) {
        Query query = new Query(where("_path").is(path));
        MongoDbFileDescriptor findOne = _mongoOperation.findOne(query, MongoDbFileDescriptor.class);
        return findOne;
    }

}

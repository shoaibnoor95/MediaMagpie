package de.wehner.mediamagpie.common.simplenio.fs;

import java.io.IOException;

import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.repository.support.SimpleMongoRepository;

import de.wehner.mediamagpie.common.fslayer.mongodb.MongoDbFileDescriptor;
import de.wehner.mediamagpie.common.simplenio.file.MMFileSystem;
import de.wehner.mediamagpie.common.simplenio.file.MMPath;
import de.wehner.mediamagpie.common.simplenio.file.spi.MMFileSystemProvider;

/**
 * See also <code>MongoDbFileDescriptorDao</code> and <code>MongoDbFSLayer</code>
 * 
 * @author ralfwehner
 *
 */
public class MMMongoFileSystem extends MMFileSystem {

    private final MMMongoFileSystemProvider _provider;

    public final String COLLECTION = "mongoDbFileDescriptor";

    /**
     * The MongoTemplate
     */
    private final MongoOperations _mongoOperation;
    
    /**
     * The spring's Repository support
     */
    private SimpleMongoRepository<MongoDbFileDescriptor, String> _fileDescriptorRepository;

    public MMMongoFileSystem(MMMongoFileSystemProvider provider, MongoOperations mongoOperation) {
        super();
        _provider = provider;
        _mongoOperation = mongoOperation;
    }

    @Override
    public MMFileSystemProvider provider() {
        return _provider;
    }

    @Override
    public void close() throws IOException {
        // TODO Auto-generated method stub

    }

    @Override
    public boolean isOpen() {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public String getSeparator() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public MMPath getPath(String first, String... more) {
        StringBuilder path = new StringBuilder(first);
        for (String element : more) {
            path.append('/').append(element);
        }
        return new MMMongoPath(this,  path.toString());
    }

}

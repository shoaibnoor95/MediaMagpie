package de.wehner.mediamagpie.common.simplenio.fs;

import java.io.IOException;
import java.net.URI;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Component;

import de.wehner.mediamagpie.common.simplenio.channels.MMSeekableByteChannel;
import de.wehner.mediamagpie.common.simplenio.file.MMDirectoryStream;
import de.wehner.mediamagpie.common.simplenio.file.MMDirectoryStream.Filter;
import de.wehner.mediamagpie.common.simplenio.file.MMFileSystem;
import de.wehner.mediamagpie.common.simplenio.file.MMOpenOption;
import de.wehner.mediamagpie.common.simplenio.file.MMPath;
import de.wehner.mediamagpie.common.simplenio.file.attribute.MMBasicFileAttributes;

@Component
@Profile({ "local-mongo", "cloud" })
public class MMMongoFileSystemProvider extends MMAbstractFileSystemProvider  {

    private final MongoOperations _mongoOperation;

    private final MMMongoFileSystem _theFileSystem;

    @Autowired
    public MMMongoFileSystemProvider(MongoTemplate mongoTemplate){
        _mongoOperation = mongoTemplate;
        _theFileSystem = new MMMongoFileSystem(this, _mongoOperation);
    }
    
    @Override
    public final String getScheme()
    {
      return "mongo";
    }

    @Override
    boolean implDelete(MMPath paramPath, boolean paramBoolean) throws IOException {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public MMFileSystem getFileSystem(URI uri) {
        checkUri(uri);
        return _theFileSystem;
    }

    private void checkUri(URI paramURI) {
        if (!paramURI.getScheme().equalsIgnoreCase(getScheme()))
            throw new IllegalArgumentException("URI does not match this provider");
        if (paramURI.getAuthority() != null)
            throw new IllegalArgumentException("Authority component present");
//        if (paramURI.getPath() == null)
//            throw new IllegalArgumentException("Path component is undefined");
//        if (!paramURI.getPath().equals("/"))
//            throw new IllegalArgumentException("Path component should be '/'");
        if (!StringUtils.isEmpty(paramURI.getPath()))
            throw new IllegalArgumentException("Path component present");
        if (paramURI.getQuery() != null)
            throw new IllegalArgumentException("Query component present");
        if (paramURI.getFragment() != null)
            throw new IllegalArgumentException("Fragment component present");
    }

    @Override
    public MMPath getPath(URI uri) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public MMSeekableByteChannel newByteChannel(MMPath path, Set<? extends MMOpenOption> options) throws IOException {
        MMMongoPath mongoPath = MMMongoPath.toMongoPath(path);
        return MMMongoDbFileChannelFactory.newFileChannel(mongoPath, options);
    }

    @Override
    public void createDirectory(MMPath dir) throws IOException {
        // TODO Auto-generated method stub
        
    }

    @Override
    public MMDirectoryStream<MMPath> newDirectoryStream(MMPath dir, Filter<? super MMPath> filter) throws IOException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public <A extends MMBasicFileAttributes> A readAttributes(MMPath path, Class<A> type) throws IOException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void copy(MMPath source, MMPath target) throws IOException {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void move(MMPath source, MMPath target) throws IOException {
        // TODO Auto-generated method stub
        
    }
}

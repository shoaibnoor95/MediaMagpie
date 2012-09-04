package de.wehner.mediamagpie.common.fslayer.mongodb;

import org.apache.commons.lang.builder.ReflectionToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document
public class MongoDbFileData {

    @Id
    private String _id;
    
    /**
     * Maybe we want to delete this, because it is no more needed and only useful in testing phase
     */
    private String _path;
    private byte[] _content;

    public MongoDbFileData(String id, String path, byte[] content) {
        super();
        _id = id;
        _path = path;
        _content = content;
    }

    public String getId() {
        return _id;
    }

    public void setId(String id) {
        _id = id;
    }

    public String getPath() {
        return _path;
    }

    public void setPath(String path) {
        _path = path;
    }

    public byte[] getContent() {
        return _content;
    }

    public void setContent(byte[] content) {
        _content = content;
    }

    @Override
    public String toString() {
        return ReflectionToStringBuilder.toString(this, ToStringStyle.SHORT_PREFIX_STYLE);
    }

}

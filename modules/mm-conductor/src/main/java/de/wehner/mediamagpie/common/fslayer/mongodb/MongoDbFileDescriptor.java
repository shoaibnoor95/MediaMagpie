package de.wehner.mediamagpie.common.fslayer.mongodb;

import java.util.List;

import org.apache.commons.lang.builder.ReflectionToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

@Document
public class MongoDbFileDescriptor {

    public static enum Type {
        FILE, DIR
    };

    @Id
    private String _id;
    @Indexed(unique = true)
    private String _path;
    private final Type _type;
    @DBRef
    private List<MongoDbFileData> _data;

    public MongoDbFileDescriptor(String path, Type type) {
        super();
        _path = path;
        _type = type;
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

    public Type getType() {
        return _type;
    }

    public List<MongoDbFileData> getData() {
        return _data;
    }

    public void setData(List<MongoDbFileData> data) {
        _data = data;
    }

    @Override
    public String toString() {
        return ReflectionToStringBuilder.toString(this, ToStringStyle.SHORT_PREFIX_STYLE);
    }

}

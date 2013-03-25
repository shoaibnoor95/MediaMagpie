package de.wehner.mediamagpie.api;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;

public class MediaExportMetadata {

    private String _name;

    private String _description;

    private String _originalFileName;

    private List<String> _tags = new LinkedList<String>();

    private static transient ObjectMapper _mapper = new ObjectMapper();

    public MediaExportMetadata() {
        this(null);
    }

    public MediaExportMetadata(String name) {
        super();
        _name = name;
    }

    public String getDescription() {
        return _description;
    }

    public void setDescription(String description) {
        _description = description;
    }

    public String getOriginalFileName() {
        return _originalFileName;
    }

    public void setOriginalFileName(String originalFileName) {
        _originalFileName = originalFileName;
    }

    public List<String> getTags() {
        return _tags;
    }

    public void setTags(List<String> tags) {
        _tags = tags;
    }

    public String getName() {
        return _name;
    }

    public void setName(String name) {
        _name = name;
    }

    public InputStream createInputStream() {
        Writer stringWriter = new StringWriter();
        try {
            _mapper.writeValue(stringWriter, this);
        } catch (JsonGenerationException e) {
            throw new RuntimeException(e);
        } catch (JsonMappingException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        try {
            return new ByteArrayInputStream(stringWriter.toString().getBytes("UTF8"));
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }

    public static MediaExportMetadata createInstance(InputStream src) {
        try {
            return _mapper.readValue(src, MediaExportMetadata.class);
        } catch (JsonParseException e) {
            throw new RuntimeException(e);
        } catch (JsonMappingException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((_description == null) ? 0 : _description.hashCode());
        result = prime * result + ((_name == null) ? 0 : _name.hashCode());
        result = prime * result + ((_originalFileName == null) ? 0 : _originalFileName.hashCode());
        result = prime * result + ((_tags == null) ? 0 : _tags.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        MediaExportMetadata other = (MediaExportMetadata) obj;
        if (_description == null) {
            if (other._description != null)
                return false;
        } else if (!_description.equals(other._description))
            return false;
        if (_name == null) {
            if (other._name != null)
                return false;
        } else if (!_name.equals(other._name))
            return false;
        if (_originalFileName == null) {
            if (other._originalFileName != null)
                return false;
        } else if (!_originalFileName.equals(other._originalFileName))
            return false;
        if (_tags == null) {
            if (other._tags != null)
                return false;
        } else if (!_tags.equals(other._tags))
            return false;
        return true;
    }

    @Override
    public String toString() {
        return ReflectionToStringBuilder.toString(this, ToStringStyle.SHORT_PREFIX_STYLE);
    }

}

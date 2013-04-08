package de.wehner.mediamagpie.api;

import java.io.InputStream;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * This is a container class to encapsulate a <code>Media</code> entity for export or import operations.
 * 
 * @author ralfwehner
 * 
 */
public class MediaExport {

    private final String _name;

    private String _mediaId;

    private InputStream _inputStream;

    private Long _length;

    /**
     * The sha1 hash value of media encoded in base 64
     */
    private String _hashValue;

    private Date _creationDate;

    private String _description;

    private String _originalFileName;

    private List<String> _tags = new LinkedList<String>();

    private MediaType _type = MediaType.UNKNOWN;

    public MediaExport(String name) {
        _name = name;
    }

    public MediaExportMetadata createMediaExportMetadata() {
        MediaExportMetadata mediaExportMetaData = new MediaExportMetadata(_name);
        mediaExportMetaData.setDescription(_description);
        mediaExportMetaData.setOriginalFileName(_originalFileName);
        mediaExportMetaData.setTags(_tags);
        return mediaExportMetaData;
    }

    public String getName() {
        return _name;
    }

    public String getMediaId() {
        return _mediaId;
    }

    public void setMediaId(String mediaId) {
        _mediaId = mediaId;
    }

    public void setInputStream(InputStream fileInputStream) {
        _inputStream = fileInputStream;
    }

    public InputStream getInputStream() {
        return _inputStream;
    }

    public Long getLength() {
        return _length;
    }

    public void setLength(Long length) {
        _length = length;
    }

    public String getHashValue() {
        return _hashValue;
    }

    public void setHashValue(String hashValue) {
        _hashValue = hashValue;
    }

    public Date getCreationDate() {
        return _creationDate;
    }

    public void setCreationDate(Date creationDate) {
        _creationDate = creationDate;
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

    public MediaType getType() {
        return _type;
    }

    public void setType(MediaType type) {
        _type = type;
    }

    @Override
    public String toString() {
        return ReflectionToStringBuilder.toString(this, ToStringStyle.SHORT_PREFIX_STYLE);
    }

}

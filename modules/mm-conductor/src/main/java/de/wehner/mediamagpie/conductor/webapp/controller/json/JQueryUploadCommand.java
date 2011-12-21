package de.wehner.mediamagpie.conductor.webapp.controller.json;

import org.apache.commons.lang.builder.ReflectionToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

/**
 * Used in combination with the @RequestMapping annotation to get a JSON object at final result.
 * 
 * @author ralfwehner
 * 
 */
public class JQueryUploadCommand {

    private final String _name;
    private final int _size;
    private String _url;
    private String _thumbnail_url;
    private String _delete_url;
    private final String _delete_type;

    public JQueryUploadCommand(String name, int size, String url, String thumbnailUrl, String deleteUrl, String deleteType) {
        super();
        _name = name;
        _size = size;
        _url = url;
        _thumbnail_url = thumbnailUrl;
        _delete_url = deleteUrl;
        _delete_type = deleteType;
    }

    public String getName() {
        return _name;
    }

    public int getSize() {
        return _size;
    }

    public String getUrl() {
        return _url;
    }

    public String getThumbnail_Url() {
        return _thumbnail_url;
    }

    public String getDelete_url() {
        return _delete_url;
    }

    public String getDelete_type() {
        return _delete_type;
    }

    public String getThumbnail_url() {
        return _thumbnail_url;
    }

    public void setThumbnail_url(String thumbnail_url) {
        _thumbnail_url = thumbnail_url;
    }

    public void setUrl(String url) {
        _url = url;
    }

    public void setDelete_url(String delete_url) {
        _delete_url = delete_url;
    }

    @Override
    public String toString() {
        return ReflectionToStringBuilder.toString(this, ToStringStyle.SHORT_PREFIX_STYLE);
    }
}

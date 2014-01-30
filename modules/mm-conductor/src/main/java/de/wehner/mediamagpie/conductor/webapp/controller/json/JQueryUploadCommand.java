package de.wehner.mediamagpie.conductor.webapp.controller.json;

import java.io.Serializable;

import org.apache.commons.lang.builder.ReflectionToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

/**
 * Used in combination with the @RequestMapping annotation to get a JSON object at final result.
 * 
 * @author ralfwehner
 * 
 */
public class JQueryUploadCommand implements Serializable {

    private static final long serialVersionUID = 1L;
    
    private String name;
    private Long size;
    private String url;
    private String thumbnailUrl;
    private String deleteUrl;
    private String deleteType;

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @param name
     *            the name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return the size
     */
    public Long getSize() {
        return size;
    }

    /**
     * @param size
     *            the size to set
     */
    public void setSize(Long size) {
        this.size = size;
    }

    /**
     * @return the url
     */
    public String getUrl() {
        return url;
    }

    /**
     * @param url
     *            the url to set
     */
    public void setUrl(String url) {
        this.url = url;
    }

    /**
     * @return the thumbnailUrl
     */
    public String getThumbnailUrl() {
        return thumbnailUrl;
    }

    /**
     * @param thumbnailUrl
     *            the thumbnailUrl to set
     */
    public void setThumbnailUrl(String thumbnailUrl) {
        this.thumbnailUrl = thumbnailUrl;
    }

    /**
     * @return the deleteUrl
     */
    public String getDeleteUrl() {
        return deleteUrl;
    }

    /**
     * @param deleteUrl
     *            the deleteUrl to set
     */
    public void setDeleteUrl(String deleteUrl) {
        this.deleteUrl = deleteUrl;
    }

    /**
     * @return the deleteType
     */
    public String getDeleteType() {
        return deleteType;
    }

    /**
     * @param deleteType
     *            the deleteType to set
     */
    public void setDeleteType(String deleteType) {
        this.deleteType = deleteType;
    }

    @Override
    public String toString() {
        return ReflectionToStringBuilder.toString(this, ToStringStyle.SHORT_PREFIX_STYLE);
    }
}

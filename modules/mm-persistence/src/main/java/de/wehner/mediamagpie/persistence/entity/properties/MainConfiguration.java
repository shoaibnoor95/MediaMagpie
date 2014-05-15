package de.wehner.mediamagpie.persistence.entity.properties;

import java.io.File;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.builder.ReflectionToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
import org.hibernate.validator.constraints.NotEmpty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.validation.Errors;

import de.wehner.mediamagpie.core.util.properties.PropertiesBacked;

@PropertiesBacked(prefix = "mainconfiguration")
public class MainConfiguration implements PropertyBackedConfiguration, FilesystemConfiguration {

    private static final Logger LOG = LoggerFactory.getLogger(MainConfiguration.class);

    /**
     * The path were all dynamically created thumbimages will be stored
     */
    private String _tempMediaPath;

    /**
     * The path were all converted video files will be stored
     */
    private String _convertedVideoPath;

    /**
     * The base path, were all user uploaded media files will be stored. The final user path will be
     * <code>&lt;baseUploadPath&gt;/&lt;userId&gt;/</code>.
     */
    private String _baseUploadPath;

    /**
     * Used to show thumbs of media
     */
    private Integer _defaultThumbSize;

    /**
     * The size of thumbs used in galleriffic's detail view
     */
    private Integer _defaultGalleryDetailThumbSize;

    /**
     * Ths size used for detail view were you can edit a media's title, description etc.
     */
    private Integer _defaultDetailThumbSize;

    /**
     * Number of medias that will be shown on media's search page at least
     */
    private Integer _hitsPerPage = 21;

    @NotEmpty
    public String getTempMediaPath() {
        return _tempMediaPath;
    }

    public void setTempMediaPath(String tempMediaPath) {
        _tempMediaPath = tempMediaPath;
    }

    @NotEmpty
    public String getConvertedVideoPath() {
        return _convertedVideoPath;
    }

    public void setConvertedVideoPath(String convertedVideoPath) {
        _convertedVideoPath = convertedVideoPath;
    }

    @NotEmpty
    public String getBaseUploadPath() {
        return _baseUploadPath;
    }

    public void setBaseUploadPath(String baseUploadPath) {
        _baseUploadPath = baseUploadPath;
    }

    @Max(value = 500)
    @Min(value = 10)
    public Integer getDefaultThumbSize() {
        return _defaultThumbSize;
    }

    public void setDefaultThumbSize(Integer defaultThumbSize) {
        _defaultThumbSize = defaultThumbSize;
    }

    @Max(value = 1000)
    @Min(value = 20)
    public Integer getDefaultGalleryDetailThumbSize() {
        return _defaultGalleryDetailThumbSize;
    }

    public void setDefaultGalleryDetailThumbSize(Integer defaultGalleryDetailThumbSize) {
        _defaultGalleryDetailThumbSize = defaultGalleryDetailThumbSize;
    }

    @Max(value = 2048)
    @Min(value = 200)
    public Integer getDefaultDetailThumbSize() {
        return _defaultDetailThumbSize;
    }

    public void setDefaultDetailThumbSize(Integer defaultDetailThumbSize) {
        _defaultDetailThumbSize = defaultDetailThumbSize;
    }

    @Max(value = 500)
    @Min(value = 1)
    public Integer getHitsPerPage() {
        return _hitsPerPage;
    }

    public void setHitsPerPage(Integer hitsPerPages) {
        _hitsPerPage = hitsPerPages;
    }

    @Override
    public void prepareDirectories(Errors e) {
        if (!createDirIfNecessary(getBaseUploadPath())) {
            e.rejectValue("baseUploadPath", "can.not.create.local.dir", new String[] { getBaseUploadPath() }, "Can not create directory.");
        }
        if (!createDirIfNecessary(getTempMediaPath())) {
            e.rejectValue("tempMediaPath", "can.not.create.local.dir", new String[] { getTempMediaPath() }, "Can not create directory.");
        }
        if (!createDirIfNecessary(getConvertedVideoPath())) {
            e.rejectValue("convertedVideoPath", "can.not.create.local.dir", new String[] { getConvertedVideoPath() }, "Can not create directory.");
        }
    }

    private boolean createDirIfNecessary(String dirName) {
        if (!StringUtils.isEmpty(dirName) && !new File(dirName).exists()) {
            LOG.info("Try creation directory '" + dirName + "'.");
            return new File(dirName).mkdirs();
        }
        return true;
    }

    @Override
    public String toString() {
        return ReflectionToStringBuilder.toString(this, ToStringStyle.SHORT_PREFIX_STYLE);
    }
}

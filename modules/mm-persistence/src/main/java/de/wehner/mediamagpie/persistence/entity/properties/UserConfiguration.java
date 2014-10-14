package de.wehner.mediamagpie.persistence.entity.properties;

import java.util.Arrays;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;

import org.apache.commons.lang.builder.ReflectionToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
import org.springframework.beans.propertyeditors.StringArrayPropertyEditor;

import de.wehner.mediamagpie.core.util.properties.PropertiesBacked;
import de.wehner.mediamagpie.core.util.properties.PropertyDef;
import de.wehner.mediamagpie.core.util.properties.PropertyTransient;

@PropertiesBacked(prefix = "user.configuration", initFromProperties = false)
public class UserConfiguration implements UserPropertyBackedConfiguration {

    @PropertyDef(editorClass = StringArrayPropertyEditor.class)
    private String[] _rootMediaPathes;

    private Integer _thumbImageSize;

    @Deprecated
    private Integer _thumbImageSizeTable;

    private Integer _detailImageSize;

    public UserConfiguration() {
    }

    public String[] getRootMediaPathes() {
        return _rootMediaPathes;
    }

    public void setRootMediaPathes(String[] rootMediaPathes) {
        _rootMediaPathes = rootMediaPathes;
    }

    @PropertyTransient
    public void setSingleRootMediaPath(String path) {
        _rootMediaPathes = new String[] { path };
    }

    @Max(value = 500)
    @Min(value = 10)
    public Integer getThumbImageSize() {
        return _thumbImageSize;
    }

    public void setThumbImageSize(Integer thumbImageSize) {
        _thumbImageSize = thumbImageSize;
    }

    @Max(value = 500)
    @Min(value = 10)
    @Deprecated
    public Integer getThumbImageSizeTable() {
        return _thumbImageSizeTable;
    }

    @Deprecated
    public void setThumbImageSizeTable(Integer thumbImageSizeTable) {
        _thumbImageSizeTable = thumbImageSizeTable;
    }

    @Max(value = 2048)
    @Min(value = 10)
    public Integer getDetailImageSize() {
        return _detailImageSize;
    }

    public void setDetailImageSize(Integer detailImageSize) {
        _detailImageSize = detailImageSize;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((_detailImageSize == null) ? 0 : _detailImageSize.hashCode());
        result = prime * result + Arrays.hashCode(_rootMediaPathes);
        result = prime * result + ((_thumbImageSize == null) ? 0 : _thumbImageSize.hashCode());
        result = prime * result + ((_thumbImageSizeTable == null) ? 0 : _thumbImageSizeTable.hashCode());
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
        UserConfiguration other = (UserConfiguration) obj;
        if (_detailImageSize == null) {
            if (other._detailImageSize != null)
                return false;
        } else if (!_detailImageSize.equals(other._detailImageSize))
            return false;
        if (!Arrays.equals(_rootMediaPathes, other._rootMediaPathes))
            return false;
        if (_thumbImageSize == null) {
            if (other._thumbImageSize != null)
                return false;
        } else if (!_thumbImageSize.equals(other._thumbImageSize))
            return false;
        if (_thumbImageSizeTable == null) {
            if (other._thumbImageSizeTable != null)
                return false;
        } else if (!_thumbImageSizeTable.equals(other._thumbImageSizeTable))
            return false;
        return true;
    }

    @Override
    public String toString() {
        return ReflectionToStringBuilder.toString(this, ToStringStyle.SHORT_PREFIX_STYLE);
    }

}

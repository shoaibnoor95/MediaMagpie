package de.wehner.mediamagpie.persistence.service;

import java.util.ArrayList;
import java.util.List;

import de.wehner.mediamagpie.persistence.entity.properties.MainConfiguration;
import de.wehner.mediamagpie.persistence.entity.properties.UserConfiguration;

/**
 * A facade to retrieve configuration information which is build from main and user's configuration. Use this class in combination with
 * {@linkplain ConfigurationProvider}.
 * 
 * @author ralfwehner
 * 
 */
public class ConfigurationFacade {

    private final MainConfiguration _mainConfiguration;
    private final UserConfiguration _userConfiguration;

    public ConfigurationFacade(MainConfiguration mainConfiguration, UserConfiguration userConfiguration) {
        super();
        _mainConfiguration = mainConfiguration;
        _userConfiguration = userConfiguration;
    }

    /**
     * @return The size mostly used on pages like the search media view.
     */
    public int getCommonThumbSize() {
        if (_userConfiguration != null && _userConfiguration.getThumbImageSize() != null) {
            return _userConfiguration.getThumbImageSize();
        }
        return _mainConfiguration.getDefaultThumbSize();
    }

    public int getDetailThumbSize() {
        if (_userConfiguration != null && _userConfiguration.getDetailImageSize() != null) {
            return _userConfiguration.getDetailImageSize();
        }
        return _mainConfiguration.getDefaultDetailThumbSize();
    }

    public int getGalleryDetailThumbSize() {
        return _mainConfiguration.getDefaultGalleryDetailThumbSize();
    }

    public List<Integer> getAllThumbSizes() {
        List<Integer> ret = new ArrayList<Integer>();
        ret.add(getCommonThumbSize());
        ret.add(getDetailThumbSize());
        ret.add(getGalleryDetailThumbSize());
        // used for public album view
        if (getCommonThumbSize() != _mainConfiguration.getDefaultThumbSize()) {
            ret.add(_mainConfiguration.getDefaultThumbSize());
        }
        return ret;
    }
}

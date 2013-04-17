package de.wehner.mediamagpie.conductor.webapp.controller.commands;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;

import de.wehner.mediamagpie.conductor.webapp.services.ImageService;
import de.wehner.mediamagpie.persistence.entity.Media;
import de.wehner.mediamagpie.persistence.entity.Priority;

/**
 * @author ralfwehner
 * @deprecated Does we really need this one anywere? Remove this class and use MediaThumbCommand instead which can be created in ImageService!
 */
public class MediaCommand extends Media {

    /**
     * Link to thumb image
     */
    private String _thumbImageLink;
    /**
     * Link to detail image url
     */
    private String _detailImageUrl;

    public MediaCommand() {
        super();
    }

    public MediaCommand(Media media) {
        super(media);
    }

    public MediaCommand(Media media, ImageService imageService, int thumbSize) {
        super(media);
        _thumbImageLink = imageService.getOrCreateImageUrl(media, thumbSize, false, Priority.NORMAL);
    }

    /**
     * @param thumbImageLink
     * @deprecated Better use new constructor
     */
    public void setThumbImageLink(String thumbImageLink) {
        _thumbImageLink = thumbImageLink;
    }

    public String getThumbImageLink() {
        return _thumbImageLink;
    }

    public String getMediaTitle() {
        try {
            String path = new URI(getUri()).getPath();
            return new File(path).getName();
        } catch (URISyntaxException e) {
        }
        return null;
    }

    public String getDetailImageUrl() {
        return _detailImageUrl;
    }

    public void setDetailImageUrl(String detailImageUrl) {
        _detailImageUrl = detailImageUrl;
    }
}

package de.wehner.mediamagpie.conductor.webapp.controller.commands;

import java.util.ArrayList;
import java.util.List;

import de.wehner.mediamagpie.common.persistence.entity.MediaTag;


public class TagCollection {

    private List<MediaTag> _tags = new ArrayList<MediaTag>();

    public TagCollection() {
    }

    public TagCollection(List<MediaTag> tags) {
        _tags = tags;
    }

    public void setTags(List<MediaTag> tags) {
        _tags = tags;
    }

    public List<MediaTag> getTags() {
        return _tags;
    }

    public void addTag(MediaTag tag) {
        _tags.add(tag);
    }
}

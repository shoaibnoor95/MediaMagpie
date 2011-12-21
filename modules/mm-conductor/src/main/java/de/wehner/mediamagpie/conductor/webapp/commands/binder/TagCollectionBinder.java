package de.wehner.mediamagpie.conductor.webapp.commands.binder;

import java.beans.PropertyEditorSupport;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import de.wehner.mediamagpie.common.persistence.entity.MediaTag;
import de.wehner.mediamagpie.common.util.StringUtil;
import de.wehner.mediamagpie.conductor.webapp.controller.commands.TagCollection;


// TODO rwe: makes no sense, because the id will be lost when reducing to simple string
@Deprecated
public class TagCollectionBinder extends PropertyEditorSupport {

    @Override
    public String getAsText() {
        TagCollection tagCollection = (TagCollection) getValue();
        return StringUtils.join(tagCollection.getTags(), ',');
    }

    @Override
    public void setAsText(String text) throws IllegalArgumentException {
        if (StringUtil.isEmpty(text)) {
            setValue(null);
        } else {
            String[] strings = StringUtils.split(text, ',');
            List<MediaTag> tags = new ArrayList<MediaTag>(strings.length);
            for (String string : strings) {
                tags.add(new MediaTag(string));
            }
            TagCollection tagCollection = new TagCollection();
            tagCollection.setTags(tags);
            setValue(tagCollection);
        }
    }
}

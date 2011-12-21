package de.wehner.mediamagpie.conductor.webapp.controller.json;

import org.apache.commons.lang.builder.ReflectionToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

public class TagAutocompleteCommand {

    private String _name;

    public TagAutocompleteCommand(String name) {
        _name = name;
    }

    public TagAutocompleteCommand() {
    }

    public String getName() {
        return _name;
    }

    public void setName(String name) {
        _name = name;
    }

    public String getLabel() {
        return _name;
    }

    public void setLabel(String name) {
        _name = name;
    }

    @Override
    public String toString() {
        return ReflectionToStringBuilder.toString(this, ToStringStyle.SHORT_PREFIX_STYLE);
    }
}

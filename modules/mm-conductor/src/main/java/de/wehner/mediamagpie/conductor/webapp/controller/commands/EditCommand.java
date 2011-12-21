package de.wehner.mediamagpie.conductor.webapp.controller.commands;

import org.apache.commons.lang.builder.ReflectionToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

/**
 * @author ralfwehner
 */
public class EditCommand {

    public static enum Action {
        DELETE, UNDO
    }

    protected Action _action;
    protected Long _id;

    public void setAction(Action action) {
        _action = action;
    }

    public Action getAction() {
        return _action;
    }

    public void setId(Long id) {
        _id = id;
    }

    public Long getId() {
        return _id;
    }

    @Override
    public String toString() {
        return ReflectionToStringBuilder.toString(this, ToStringStyle.SHORT_PREFIX_STYLE);
    }

}
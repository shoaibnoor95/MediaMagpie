package de.wehner.mediamagpie.conductor.webapp.commands.binder;

import java.beans.PropertyEditorSupport;

import org.apache.commons.lang.StringUtils;

public class EnumBinder<T extends Enum<?>> extends PropertyEditorSupport {

    @SuppressWarnings("rawtypes")
    private final Class<Enum> _enumClass;

    @SuppressWarnings({ "rawtypes", "unchecked" })
    public EnumBinder(Class enumClass) {
        super();
        _enumClass = enumClass;
    }

    @Override
    public String getAsText() {
        @SuppressWarnings("unchecked")
        T status = (T) getValue();
        return (status == null) ? "" : (status.name());
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    @Override
    public void setAsText(String text) throws IllegalArgumentException {
        if (StringUtils.isEmpty(text)) {
            setValue(null);
        } else {
            Enum status = Enum.valueOf(_enumClass, text);
            setValue(status);
        }
    }
}

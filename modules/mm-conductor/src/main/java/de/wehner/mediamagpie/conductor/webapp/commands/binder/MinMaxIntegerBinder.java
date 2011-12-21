package de.wehner.mediamagpie.conductor.webapp.commands.binder;

import java.beans.PropertyEditorSupport;

import org.apache.commons.lang.StringUtils;

import de.wehner.mediamagpie.common.util.MinMaxValue;
import de.wehner.mediamagpie.common.util.StringUtil;


public class MinMaxIntegerBinder extends PropertyEditorSupport {

    @Override
    public String getAsText() {
        @SuppressWarnings("unchecked")
        MinMaxValue<Integer> minMax = (MinMaxValue<Integer>) getValue();
        return (minMax == null) ? "" : (minMax.getMin() + "," + minMax.getMax());
    }

    @Override
    public void setAsText(String text) throws IllegalArgumentException {
        if (StringUtil.isEmpty(text)) {
            setValue(null);
        } else {
            String[] strings = StringUtils.split(text, ',');
            MinMaxValue<Integer> status = new MinMaxValue<Integer>(Integer.parseInt(strings[0]), Integer.parseInt(strings[1]));
            setValue(status);
        }
    }

}

package de.wehner.mediamagpie.common.util;

import org.apache.commons.lang.builder.ReflectionToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

public class MinMaxValue<T extends Number> {

    private T _min;
    private T _max;

    public MinMaxValue(T min, T max) {
        super();
        _min = min;
        _max = max;
    }

    public T getMin() {
        return _min;
    }

    public void setMin(T min) {
        _min = min;
    }

    public T getMax() {
        return _max;
    }

    public void setMax(T max) {
        _max = max;
    }

    @Override
    public String toString() {
        return ReflectionToStringBuilder.toString(this, ToStringStyle.SHORT_PREFIX_STYLE);
    }

}

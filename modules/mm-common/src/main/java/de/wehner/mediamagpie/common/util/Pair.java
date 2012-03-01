package de.wehner.mediamagpie.common.util;

import java.io.Serializable;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

public class Pair<M, N> implements Serializable {

    private static final long serialVersionUID = 1L;

    private M _first;
    private N _second;

    public Pair(M first, N second) {
        super();
        _first = first;
        _second = second;
    }

    public Pair() {

    }

    public M getFirst() {
        return _first;
    }

    public void setFirst(M first) {
        _first = first;
    }

    public N getSecond() {
        return _second;
    }

    public void setSecond(N second) {
        _second = second;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((_first == null) ? 0 : _first.hashCode());
        result = prime * result + ((_second == null) ? 0 : _second.hashCode());
        return result;
    }

    @SuppressWarnings("rawtypes")
    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Pair other = (Pair) obj;
        if (_first == null) {
            if (other._first != null)
                return false;
        } else if (!_first.equals(other._first))
            return false;
        if (_second == null) {
            if (other._second != null)
                return false;
        } else if (!_second.equals(other._second))
            return false;
        return true;
    }

    @Override
    public String toString() {
        return ReflectionToStringBuilder.toString(this, ToStringStyle.SHORT_PREFIX_STYLE);
    }
}

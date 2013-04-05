package de.wehner.mediamagpie.core.util;

public class Holder<T> {

    private T _value;

    public Holder() {
    }

    public Holder(T value) {
        _value = value;
    }

    public T get() {
        return _value;
    }

    public void set(T value) {
        _value = value;
    }
}

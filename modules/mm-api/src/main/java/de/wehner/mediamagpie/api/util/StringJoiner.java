package de.wehner.mediamagpie.api.util;

public class StringJoiner {

    private final StringBuilder _builder = new StringBuilder();

    private int _elementCount;

    private final String _separator;

    public StringJoiner(String separator) {
        _separator = separator;
    }

    public StringJoiner() {
        this(", ");
    }

    public void add(String element) {
        if (_elementCount > 0) {
            _builder.append(_separator);
        }
        _builder.append(element);
        _elementCount++;
    }

    @Override
    public String toString() {
        return _builder.toString();
    }

}

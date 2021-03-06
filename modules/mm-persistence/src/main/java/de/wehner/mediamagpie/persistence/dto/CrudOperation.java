package de.wehner.mediamagpie.persistence.dto;

public enum CrudOperation {
    LIST(true), CREATE(true), VIEW(true), EDIT(true), DELETE(false);

    private final boolean _hasView;

    private CrudOperation(boolean hasView) {
        _hasView = hasView;
    }

    public boolean hasView() {
        return _hasView;
    }

}
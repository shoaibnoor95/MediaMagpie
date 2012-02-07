package de.wehner.mediamagpie.common.fslayer;

public abstract class AbstractFile {

    protected final IFSLayer _fsLayer;

    public AbstractFile(IFSLayer fsLayer) {
        super();
        _fsLayer = fsLayer;
    }

}

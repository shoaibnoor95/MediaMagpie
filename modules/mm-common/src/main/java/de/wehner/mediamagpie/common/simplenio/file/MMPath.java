package de.wehner.mediamagpie.common.simplenio.file;


public interface MMPath extends Comparable<MMPath>, Iterable<MMPath> {

    /**
     * Returns the file system that created this object.
     * 
     * @return the file system that created this object
     */
    MMFileSystem getFileSystem();

}

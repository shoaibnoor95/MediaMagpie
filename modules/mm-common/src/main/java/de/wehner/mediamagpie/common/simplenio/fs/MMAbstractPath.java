package de.wehner.mediamagpie.common.simplenio.fs;

import java.io.File;
import java.util.Iterator;
import java.util.NoSuchElementException;

import de.wehner.mediamagpie.common.simplenio.file.MMPath;

public abstract class MMAbstractPath implements MMPath {
    
    public final boolean startsWith(String paramString) {
        return startsWith(getFileSystem().getPath(paramString, new String[0]).toString());
    }

    public final boolean endsWith(String paramString) {
        return endsWith(getFileSystem().getPath(paramString, new String[0]).toString());
    }

    public final MMPath resolve(String paramString) {
        return resolve(getFileSystem().getPath(paramString, new String[0]).toString());
    }

    public final MMPath resolveSibling(MMPath paramPath) {
        if (paramPath == null) {
            throw new NullPointerException();
        }
        MMPath localPath = getParent();
        return localPath == null ? paramPath : localPath.resolve(paramPath);
    }

    public final MMPath resolveSibling(String paramString) {
        return resolveSibling(getFileSystem().getPath(paramString, new String[0]));
    }

    public final Iterator<MMPath> iterator() {
        return new Iterator() {
            private int i = 0;

            public boolean hasNext() {
                return this.i < MMAbstractPath.this.getNameCount();
            }

            public MMPath next() {
                if (this.i < MMAbstractPath.this.getNameCount()) {
                    MMPath localPath = MMAbstractPath.this.getName(this.i);
                    this.i += 1;
                    return localPath;
                }
                throw new NoSuchElementException();
            }

            public void remove() {
                throw new UnsupportedOperationException();
            }
        };
    }

    public final File toFile() {
        return new File(toString());
    }

}
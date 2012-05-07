package de.wehner.mediamagpie.common.simplenio.fs;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import de.wehner.mediamagpie.common.simplenio.file.MMDirectoryStream;
import de.wehner.mediamagpie.common.simplenio.file.MMPath;

public class MMUnixDirectoryStream implements MMDirectoryStream<MMPath> {

    private final MMUnixPath dir;
    private final MMDirectoryStream.Filter<? super MMPath> filter;
    private final ReentrantReadWriteLock streamLock = new ReentrantReadWriteLock(true);
    private volatile boolean isClosed;
    private Iterator<MMPath> iterator;

    MMUnixDirectoryStream(MMUnixPath paramUnixPath, MMDirectoryStream.Filter<? super MMPath> paramFilter) {
        this.dir = paramUnixPath;
        this.filter = paramFilter;
    }

    protected final MMUnixPath directory() {
        return this.dir;
    }

    protected final Lock readLock() {
        return this.streamLock.readLock();
    }

    protected final Lock writeLock() {
        return this.streamLock.writeLock();
    }

    protected final boolean isOpen() {
        return !this.isClosed;
    }

    protected final boolean closeImpl() throws IOException {
        if (!this.isClosed) {
            this.isClosed = true;
            // try {
            // UnixNativeDispatcher.closedir(this.dp);
            // } catch (UnixException localUnixException) {
            // throw new IOException(localUnixException.errorString());
            // }
            return true;
        }
        return false;
    }

    public void close() throws IOException {
        writeLock().lock();
        try {
            closeImpl();
        } finally {
            writeLock().unlock();
        }
    }

    protected final Iterator<MMPath> iterator(MMDirectoryStream<MMPath> paramDirectoryStream) {
        if (this.isClosed) {
            throw new IllegalStateException("Directory stream is closed");
        }
        synchronized (this) {
            if (this.iterator != null) {
                throw new IllegalStateException("Iterator already obtained");
            }
            // this.iterator = new MMUnixDirectoryIterator(paramDirectoryStream);
            File path = dir.getPath();
            FileFilter fileFilter = new java.io.FileFilter() {

                @Override
                public boolean accept(File pathname) {
                    try {
                        return filter.accept(new MMUnixPath(dir.getFileSystem(), pathname.getPath()));
                    } catch (IOException e) {
                        return false;
                    }
                }
            };
            File[] resultFiles = path.listFiles(fileFilter);
            List<MMPath> result = new ArrayList<MMPath>();
            for (File file : resultFiles) {
                result.add(new MMUnixPath(dir.getFileSystem(), file.getPath()));
            }
            return result.iterator();
        }
    }

    public Iterator<MMPath> iterator() {
        return iterator(this);
    }

    public MMPath[] list() {
        List<MMPath> list = new ArrayList<MMPath>();
        Iterator<MMPath> iterator = iterator();
        while (iterator.hasNext()) {
            list.add(iterator.next());
        }
        return list.toArray(new MMPath[list.size()]);
    }

    // private class MMUnixDirectoryIterator implements Iterator<MMPath> {
    // private final MMDirectoryStream<MMPath> stream;
    // private boolean atEof = false;
    // private MMPath nextEntry;
    //
    // MMUnixDirectoryIterator(MMDirectoryStream<MMPath> paramDirectoryStream) {
    // this.stream = paramDirectoryStream;
    // }
    //
    // private boolean isSelfOrParent(byte[] paramArrayOfByte) {
    // return (paramArrayOfByte[0] == 46)
    // && ((paramArrayOfByte.length == 1) || ((paramArrayOfByte.length == 2) && (paramArrayOfByte[1] == 46)));
    // }
    //
    // // private MMPath readNextEntry() {
    // // assert (Thread.holdsLock(this));
    // // while (true) {
    // // byte[] arrayOfByte = null;
    // //
    // // MMUnixDirectoryStream.this.readLock().lock();
    // // try {
    // // if (MMUnixDirectoryStream.this.isOpen())
    // // arrayOfByte = MMUnixNativeDispatcher.readdir(MMUnixDirectoryStream.this.dp);
    // // } catch (MMUnixException localUnixException) {
    // // IOException localIOException1 = localUnixException.asIOException(MMUnixDirectoryStream.this.dir);
    // // throw new DirectoryIteratorException(localIOException1);
    // // } finally {
    // // MMUnixDirectoryStream.this.readLock().unlock();
    // // }
    // //
    // // if (arrayOfByte == null) {
    // // this.atEof = true;
    // // return null;
    // // }
    // //
    // // if (!isSelfOrParent(arrayOfByte)) {
    // // MMUnixPath localUnixPath = MMUnixDirectoryStream.this.dir.resolve(arrayOfByte);
    // // try {
    // // if ((MMUnixDirectoryStream.this.filter == null) || (MMUnixDirectoryStream.this.filter.accept(localUnixPath)))
    // // return localUnixPath;
    // // } catch (IOException localIOException2) {
    // // throw new DirectoryIteratorException(localIOException2);
    // // }
    // // }
    // // }
    // // }
    //
    // private MMPath readNextEntry() {
    // assert (Thread.holdsLock(this));
    // while (true) {
    // byte[] arrayOfByte = null;
    //
    // MMUnixDirectoryStream.this.readLock().lock();
    // try {
    // if (MMUnixDirectoryStream.this.isOpen()) {
    // // arrayOfByte = MMUnixNativeDispatcher.readdir(MMUnixDirectoryStream.this.dp);
    // }
    // } finally {
    // MMUnixDirectoryStream.this.readLock().unlock();
    // }
    //
    // if (arrayOfByte == null) {
    // this.atEof = true;
    // return null;
    // }
    //
    // if (!isSelfOrParent(arrayOfByte)) {
    // // MMUnixPath localUnixPath = MMUnixDirectoryStream.this.dir.resolve(arrayOfByte);
    // MMUnixPath localUnixPath = null;
    // try {
    // if ((MMUnixDirectoryStream.this.filter == null) || (MMUnixDirectoryStream.this.filter.accept(localUnixPath))) {
    // // return localUnixPath;
    // System.out.println("blah2");
    // }
    // } catch (IOException localIOException2) {
    // // throw new DirectoryIteratorException(localIOException2);
    // }
    // }
    // }
    // }
    //
    // public synchronized boolean hasNext() {
    // if ((this.nextEntry == null) && (!this.atEof)){
    // this.nextEntry = readNextEntry();}
    // return this.nextEntry != null;
    // }
    //
    // public synchronized MMPath next() {
    // MMPath localPath;
    // if ((this.nextEntry == null) && (!this.atEof)) {
    // localPath = readNextEntry();
    // } else {
    // localPath = this.nextEntry;
    // this.nextEntry = null;
    // }
    // if (localPath == null)
    // throw new NoSuchElementException();
    // return localPath;
    // }
    //
    // public void remove() {
    // throw new UnsupportedOperationException();
    // }
    // }
}
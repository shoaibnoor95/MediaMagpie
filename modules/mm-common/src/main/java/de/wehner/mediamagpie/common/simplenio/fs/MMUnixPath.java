package de.wehner.mediamagpie.common.simplenio.fs;

import java.io.File;
import java.lang.ref.SoftReference;
import java.net.URI;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.nio.charset.CharsetEncoder;
import java.nio.charset.CoderResult;
import java.nio.charset.CodingErrorAction;
import java.util.Arrays;

import de.wehner.mediamagpie.common.simplenio.file.MMInvalidPathException;
import de.wehner.mediamagpie.common.simplenio.file.MMPath;
import de.wehner.mediamagpie.common.simplenio.file.MMProviderMismatchException;

public class MMUnixPath extends MMAbstractPath {

    /**
     * TODO rwe: delete encoder because we don't need this
     */
    private static ThreadLocal<SoftReference<CharsetEncoder>> encoder;
    private final MMUnixFileSystem fs;
    // private final byte[] path;
    // private volatile String stringValue;
    // private int hash;
    // private volatile int[] offsets;
    private final File path;

    @Deprecated
    MMUnixPath(MMUnixFileSystem paramUnixFileSystem, byte[] paramArrayOfByte) {
        // this.fs = paramUnixFileSystem;
        // this.path = new File(new String(paramArrayOfByte));
        this(paramUnixFileSystem, new String(paramArrayOfByte));
    }

    MMUnixPath(MMUnixFileSystem paramUnixFileSystem, String paramString) {
        // this(paramUnixFileSystem, encode(normalizeAndCheck(paramString)));
        this.fs = paramUnixFileSystem;
        this.path = new File(paramString);

    }

    static String normalizeAndCheck(String paramString) {
        int i = paramString.length();
        int j = 0;
        for (int k = 0; k < i; k++) {
            char c = paramString.charAt(k);
            if ((c == '/') && (j == 47))
                return normalize(paramString, i, k - 1);
            checkNotNul(paramString, c);
            j = c;
        }
        if (j == 47)
            return normalize(paramString, i, i - 1);
        return paramString;
    }

    private static void checkNotNul(String paramString, char paramChar) {
        if (paramChar == 0)
            throw new MMInvalidPathException(paramString, "Nul character not allowed");
    }

    private static String normalize(String paramString, int paramInt1, int paramInt2) {
        if (paramInt1 == 0)
            return paramString;
        int i = paramInt1;
        while ((i > 0) && (paramString.charAt(i - 1) == '/'))
            i--;
        if (i == 0)
            return "/";
        StringBuilder localStringBuilder = new StringBuilder(paramString.length());
        if (paramInt2 > 0)
            localStringBuilder.append(paramString.substring(0, paramInt2));
        int j = 0;
        for (int k = paramInt2; k < i; k++) {
            char c = paramString.charAt(k);
            if ((c == '/') && (j == 47))
                continue;
            checkNotNul(paramString, c);
            localStringBuilder.append(c);
            j = c;
        }
        return localStringBuilder.toString();
    }

    @Deprecated
    private static byte[] encode(String paramString) {
        SoftReference localSoftReference = (SoftReference) encoder.get();
        CharsetEncoder localCharsetEncoder = localSoftReference != null ? (CharsetEncoder) localSoftReference.get() : null;
        if (localCharsetEncoder == null) {
            localCharsetEncoder = Charset.defaultCharset().newEncoder().onMalformedInput(CodingErrorAction.REPORT)
                    .onUnmappableCharacter(CodingErrorAction.REPORT);

            encoder.set(new SoftReference(localCharsetEncoder));
        }

        char[] arrayOfChar = paramString.toCharArray();

        byte[] arrayOfByte = new byte[(int) (arrayOfChar.length * localCharsetEncoder.maxBytesPerChar())];

        ByteBuffer localByteBuffer = ByteBuffer.wrap(arrayOfByte);
        CharBuffer localCharBuffer = CharBuffer.wrap(arrayOfChar);
        localCharsetEncoder.reset();
        CoderResult localCoderResult = localCharsetEncoder.encode(localCharBuffer, localByteBuffer, true);
        int i;
        if (!localCoderResult.isUnderflow()) {
            i = 1;
        } else {
            localCoderResult = localCharsetEncoder.flush(localByteBuffer);
            i = !localCoderResult.isUnderflow() ? 1 : 0;
        }
        if (i != 0) {
            throw new MMInvalidPathException(paramString, "Malformed input or input contains unmappable chacraters");
        }

        int j = localByteBuffer.position();
        if (j != arrayOfByte.length) {
            arrayOfByte = Arrays.copyOf(arrayOfByte, j);
        }
        return arrayOfByte;
    }

    // byte[] asByteArray() {
    // return this.path;
    // }
    //
    // byte[] getByteArrayForSysCalls() {
    // if (getFileSystem().needToResolveAgainstDefaultDirectory()) {
    // return resolve(getFileSystem().defaultDirectory(), this.path);
    // }
    // if (!isEmpty()) {
    // return this.path;
    // }
    //
    // byte[] arrayOfByte = { 46 };
    // return arrayOfByte;
    // }
    //
    String getPathForExecptionMessage() {
        return toString();
    }

    //
    // String getPathForPermissionCheck() {
    // if (getFileSystem().needToResolveAgainstDefaultDirectory()) {
    // return new String(getByteArrayForSysCalls());
    // }
    // return toString();
    // }
    //
    static MMUnixPath toUnixPath(MMPath paramPath) {
        if (paramPath == null)
            throw new NullPointerException();
        if (!(paramPath instanceof MMUnixPath))
            throw new MMProviderMismatchException();
        return (MMUnixPath) paramPath;
    }

    //
    // private void initOffsets() {
    // if (this.offsets == null) {
    // int i = 0;
    // int j = 0;
    // if (isEmpty()) {
    // i = 1;
    // } else
    // while (j < this.path.length) {
    // int k = this.path[(j++)];
    // if (k != 47) {
    // i++;
    // while ((j < this.path.length) && (this.path[j] != 47)) {
    // j++;
    // }
    // }
    // }
    //
    // int[] arrayOfInt = new int[i];
    // i = 0;
    // j = 0;
    // while (j < this.path.length) {
    // int m = this.path[j];
    // if (m == 47) {
    // j++;
    // } else {
    // arrayOfInt[(i++)] = (j++);
    // while ((j < this.path.length) && (this.path[j] != 47))
    // j++;
    // }
    // }
    // synchronized (this) {
    // if (this.offsets == null)
    // this.offsets = arrayOfInt;
    // }
    // }
    // }
    //
    // private boolean isEmpty() {
    // return this.path.length == 0;
    // }
    //
    // private MMUnixPath emptyPath() {
    // return new MMUnixPath(getFileSystem(), new byte[0]);
    // }
    //
    // public MMUnixPath getRoot() {
    // if ((this.path.length > 0) && (this.path[0] == 47)) {
    // return getFileSystem().rootDirectory();
    // }
    // return null;
    // }
    //
    @Override
    public MMUnixPath getFileName() {
        String name = path.getName();
        return new MMUnixPath(fs, name);
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((fs == null) ? 0 : fs.hashCode());
        result = prime * result + ((path == null) ? 0 : path.hashCode());
        return result;
    }

    public boolean equals(Object paramObject) {
        if ((paramObject != null) && ((paramObject instanceof MMUnixPath))) {
            return compareTo((MMPath) paramObject) == 0;
        }
        return false;
    }

    //
    // public String toString() {
    // if (this.stringValue == null)
    // this.stringValue = new String(this.path);
    // return this.stringValue;
    // }
    //
    // int openForAttributeAccess(boolean paramBoolean) throws IOException {
    // int i = 0;
    // if (!paramBoolean)
    // i |= 256;
    // try {
    // return UnixNativeDispatcher.open(this, i, 0);
    // } catch (UnixException localUnixException) {
    // if ((getFileSystem().isSolaris()) && (localUnixException.errno() == 22)) {
    // localUnixException.setError(62);
    // }
    // if (localUnixException.errno() == 62) {
    // throw new FileSystemException(getPathForExecptionMessage(), null, new StringBuilder().append(localUnixException.getMessage())
    // .append(" or unable to access attributes of symbolic link").toString());
    // }
    //
    // localUnixException.rethrowAsIOException(this);
    // }
    // return -1;
    // }
    //
    // void checkRead() {
    // SecurityManager localSecurityManager = System.getSecurityManager();
    // if (localSecurityManager != null)
    // localSecurityManager.checkRead(getPathForPermissionCheck());
    // }
    //
    // void checkWrite() {
    // SecurityManager localSecurityManager = System.getSecurityManager();
    // if (localSecurityManager != null)
    // localSecurityManager.checkWrite(getPathForPermissionCheck());
    // }
    //
    void checkDelete() {
        SecurityManager localSecurityManager = System.getSecurityManager();
        if (localSecurityManager != null) {
            // localSecurityManager.checkDelete(getPathForPermissionCheck());
            localSecurityManager.checkDelete(path.getAbsolutePath());
        }
    }

    //
    // public MMUnixPath toAbsolutePath() {
    // if (isAbsolute()) {
    // return this;
    // }
    //
    // SecurityManager localSecurityManager = System.getSecurityManager();
    // if (localSecurityManager != null) {
    // localSecurityManager.checkPropertyAccess("user.dir");
    // }
    // return new MMUnixPath(getFileSystem(), resolve(getFileSystem().defaultDirectory(), this.path));
    // }
    //
    // public Path toRealPath(LinkOption[] paramArrayOfLinkOption) throws IOException {
    // checkRead();
    //
    // MMUnixPath localUnixPath1 = toAbsolutePath();
    //
    // if (Util.followLinks(paramArrayOfLinkOption)) {
    // try {
    // byte[] arrayOfByte = UnixNativeDispatcher.realpath(localUnixPath1);
    // return new MMUnixPath(getFileSystem(), arrayOfByte);
    // } catch (UnixException localUnixException1) {
    // localUnixException1.rethrowAsIOException(this);
    // }
    //
    // }
    //
    // MMUnixPath localUnixPath2 = this.fs.rootDirectory();
    // for (int i = 0; i < localUnixPath1.getNameCount(); i++) {
    // MMUnixPath localUnixPath3 = localUnixPath1.getName(i);
    //
    // if ((localUnixPath3.asByteArray().length == 1) && (localUnixPath3.asByteArray()[0] == 46)) {
    // continue;
    // }
    // if ((localUnixPath3.asByteArray().length == 2) && (localUnixPath3.asByteArray()[0] == 46) && (localUnixPath3.asByteArray()[1] ==
    // 46))
    // {
    // UnixFileAttributes localUnixFileAttributes = null;
    // try {
    // localUnixFileAttributes = UnixFileAttributes.get(localUnixPath2, false);
    // } catch (UnixException localUnixException3) {
    // localUnixException3.rethrowAsIOException(localUnixPath2);
    // }
    // if (!localUnixFileAttributes.isSymbolicLink()) {
    // localUnixPath2 = localUnixPath2.getParent();
    // if (localUnixPath2 != null)
    // continue;
    // localUnixPath2 = this.fs.rootDirectory();
    // continue;
    // }
    //
    // }
    //
    // localUnixPath2 = localUnixPath2.resolve(localUnixPath3);
    // }
    //
    // try {
    // UnixFileAttributes.get(localUnixPath2, false);
    // } catch (UnixException localUnixException2) {
    // localUnixException2.rethrowAsIOException(localUnixPath2);
    // }
    // return localUnixPath2;
    // }
    //
    // public URI toUri() {
    // return UnixUriUtils.toUri(this);
    // }
    //
    // public WatchKey register(WatchService paramWatchService, WatchEvent.Kind<?>[] paramArrayOfKind, WatchEvent.Modifier[]
    // paramArrayOfModifier)
    // throws IOException {
    // if (paramWatchService == null)
    // throw new NullPointerException();
    // if (!(paramWatchService instanceof AbstractWatchService))
    // throw new MMProviderMismatchException();
    // checkRead();
    // return ((AbstractWatchService) paramWatchService).register(this, paramArrayOfKind, paramArrayOfModifier);
    // }

    static {
        encoder = new ThreadLocal();
    }

    @Override
    public MMUnixFileSystem getFileSystem() {
        return this.fs;
    }

    @Override
    public MMPath getParent() {
        return new MMUnixPath(getFileSystem(), path.getParentFile().getPath());
    }

    @Override
    public MMPath resolve(MMPath other) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public int getNameCount() {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public MMPath getName(int index) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public int compareTo(MMPath o) {
        if (o instanceof MMUnixPath) {
            return path.compareTo(((MMUnixPath) o).path);
        }
        throw new RuntimeException("internal error");
    }

    public File getPath() {
        return path;
    }

    @Override
    public URI toUri() {
        return path.toURI();
    }

    @Override
    public String toString() {
        return path.getPath();
    }

}
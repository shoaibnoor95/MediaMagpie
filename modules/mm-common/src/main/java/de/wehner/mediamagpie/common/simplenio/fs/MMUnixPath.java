package de.wehner.mediamagpie.common.simplenio.fs;

import java.io.File;
import java.lang.ref.SoftReference;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.nio.charset.CharsetEncoder;
import java.nio.charset.CoderResult;
import java.nio.charset.CodingErrorAction;
import java.util.Arrays;

import org.apache.commons.lang.builder.ReflectionToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

import de.wehner.mediamagpie.common.simplenio.file.MMInvalidPathException;
import de.wehner.mediamagpie.common.simplenio.file.MMPath;
import de.wehner.mediamagpie.common.simplenio.file.MMProviderMismatchException;

public class MMUnixPath extends MMAbstractPath {

    private static ThreadLocal<SoftReference<CharsetEncoder>> encoder;
    private final MMUnixFileSystem fs;
    // private final byte[] path;
    // private volatile String stringValue;
    // private int hash;
    // private volatile int[] offsets;
    private final File path;

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
    // public MMUnixPath getFileName() {
    // initOffsets();
    //
    // int i = this.offsets.length;
    //
    // if (i == 0) {
    // return null;
    // }
    //
    // if ((i == 1) && (this.path.length > 0) && (this.path[0] != 47)) {
    // return this;
    // }
    // int j = this.offsets[(i - 1)];
    // int k = this.path.length - j;
    // byte[] arrayOfByte = new byte[k];
    // System.arraycopy(this.path, j, arrayOfByte, 0, k);
    // return new MMUnixPath(getFileSystem(), arrayOfByte);
    // }
    //
    // public MMUnixPath getParent() {
    // initOffsets();
    //
    // int i = this.offsets.length;
    // if (i == 0) {
    // return null;
    // }
    // int j = this.offsets[(i - 1)] - 1;
    // if (j <= 0) {
    // return getRoot();
    // }
    // byte[] arrayOfByte = new byte[j];
    // System.arraycopy(this.path, 0, arrayOfByte, 0, j);
    // return new MMUnixPath(getFileSystem(), arrayOfByte);
    // }
    //
    // public int getNameCount() {
    // initOffsets();
    // return this.offsets.length;
    // }
    //
    // public MMUnixPath getName(int paramInt) {
    // initOffsets();
    // if (paramInt < 0)
    // throw new IllegalArgumentException();
    // if (paramInt >= this.offsets.length) {
    // throw new IllegalArgumentException();
    // }
    // int i = this.offsets[paramInt];
    // int j;
    // if (paramInt == this.offsets.length - 1)
    // j = this.path.length - i;
    // else {
    // j = this.offsets[(paramInt + 1)] - i - 1;
    // }
    //
    // byte[] arrayOfByte = new byte[j];
    // System.arraycopy(this.path, i, arrayOfByte, 0, j);
    // return new MMUnixPath(getFileSystem(), arrayOfByte);
    // }
    //
    // public MMUnixPath subpath(int paramInt1, int paramInt2) {
    // initOffsets();
    //
    // if (paramInt1 < 0)
    // throw new IllegalArgumentException();
    // if (paramInt1 >= this.offsets.length)
    // throw new IllegalArgumentException();
    // if (paramInt2 > this.offsets.length)
    // throw new IllegalArgumentException();
    // if (paramInt1 >= paramInt2) {
    // throw new IllegalArgumentException();
    // }
    //
    // int i = this.offsets[paramInt1];
    // int j;
    // if (paramInt2 == this.offsets.length)
    // j = this.path.length - i;
    // else {
    // j = this.offsets[paramInt2] - i - 1;
    // }
    //
    // byte[] arrayOfByte = new byte[j];
    // System.arraycopy(this.path, i, arrayOfByte, 0, j);
    // return new MMUnixPath(getFileSystem(), arrayOfByte);
    // }
    //
    // public boolean isAbsolute() {
    // return (this.path.length > 0) && (this.path[0] == 47);
    // }
    //
    // private static byte[] resolve(byte[] paramArrayOfByte1, byte[] paramArrayOfByte2) {
    // int i = paramArrayOfByte1.length;
    // int j = paramArrayOfByte2.length;
    // if (j == 0)
    // return paramArrayOfByte1;
    // if ((i == 0) || (paramArrayOfByte2[0] == 47))
    // return paramArrayOfByte2;
    // byte[] arrayOfByte;
    // if ((i == 1) && (paramArrayOfByte1[0] == 47)) {
    // arrayOfByte = new byte[j + 1];
    // arrayOfByte[0] = 47;
    // System.arraycopy(paramArrayOfByte2, 0, arrayOfByte, 1, j);
    // } else {
    // arrayOfByte = new byte[i + 1 + j];
    // System.arraycopy(paramArrayOfByte1, 0, arrayOfByte, 0, i);
    // arrayOfByte[paramArrayOfByte1.length] = 47;
    // System.arraycopy(paramArrayOfByte2, 0, arrayOfByte, i + 1, j);
    // }
    // return arrayOfByte;
    // }
    //
    // public MMUnixPath resolve(MMPath paramPath) {
    // byte[] arrayOfByte1 = toUnixPath(paramPath).path;
    // if ((arrayOfByte1.length > 0) && (arrayOfByte1[0] == 47))
    // return (MMUnixPath) paramPath;
    // byte[] arrayOfByte2 = resolve(this.path, arrayOfByte1);
    // return new MMUnixPath(getFileSystem(), arrayOfByte2);
    // }
    //
    // MMUnixPath resolve(byte[] paramArrayOfByte) {
    // return resolve(new MMUnixPath(getFileSystem(), paramArrayOfByte));
    // }
    //
    // public MMUnixPath relativize(MMPath paramPath) {
    // MMUnixPath localUnixPath = toUnixPath(paramPath);
    // if (localUnixPath.equals(this)) {
    // return emptyPath();
    // }
    //
    // if (isAbsolute() != localUnixPath.isAbsolute()) {
    // throw new IllegalArgumentException("'other' is different type of Path");
    // }
    //
    // if (isEmpty()) {
    // return localUnixPath;
    // }
    // int i = getNameCount();
    // int j = localUnixPath.getNameCount();
    //
    // int k = i > j ? j : i;
    // int m = 0;
    // while ((m < k) && (getName(m).equals(localUnixPath.getName(m)))) {
    // m++;
    // }
    //
    // int n = i - m;
    // if (m < j) {
    // MMUnixPath localObject = localUnixPath.subpath(m, j);
    // if (n == 0) {
    // return localObject;
    // }
    //
    // boolean bool = localUnixPath.isEmpty();
    //
    // int i1 = n * 3 + ((MMUnixPath) localObject).path.length;
    // if (bool) {
    // assert (((MMUnixPath) localObject).isEmpty());
    // i1--;
    // }
    // byte[] arrayOfByte = new byte[i1];
    // int i2 = 0;
    // while (n > 0) {
    // arrayOfByte[(i2++)] = 46;
    // arrayOfByte[(i2++)] = 46;
    // if (bool) {
    // if (n > 1)
    // arrayOfByte[(i2++)] = 47;
    // } else {
    // arrayOfByte[(i2++)] = 47;
    // }
    // n--;
    // }
    // System.arraycopy(((MMUnixPath) localObject).path, 0, arrayOfByte, i2, ((MMUnixPath) localObject).path.length);
    // return new MMUnixPath(getFileSystem(), arrayOfByte);
    // }
    //
    // byte[] localObject = new byte[n * 3 - 1];
    // boolean bool = false;
    // while (n > 0) {
    // localObject[(bool++)] = 46;
    // localObject[(bool++)] = 46;
    //
    // if (n > 1)
    // localObject[(bool++)] = 47;
    // n--;
    // }
    // return (MMUnixPath) new MMUnixPath(getFileSystem(), localObject);
    // }
    //
    // public Path normalize() {
    // int i = getNameCount();
    // if (i == 0) {
    // return this;
    // }
    // boolean[] arrayOfBoolean = new boolean[i];
    // int[] arrayOfInt = new int[i];
    // int j = i;
    // int k = 0;
    // boolean bool = isAbsolute();
    //
    // for (int m = 0; m < i; m++) {
    // n = this.offsets[m];
    //
    // if (m == this.offsets.length - 1)
    // i1 = this.path.length - n;
    // else {
    // i1 = this.offsets[(m + 1)] - n - 1;
    // }
    // arrayOfInt[m] = i1;
    //
    // if (this.path[n] == 46) {
    // if (i1 == 1) {
    // arrayOfBoolean[m] = true;
    // j--;
    // } else if (this.path[(n + 1)] == 46) {
    // k = 1;
    // }
    // }
    //
    // }
    //
    // if (k != 0) {
    // do {
    // m = j;
    // n = -1;
    // for (i1 = 0; i1 < i; i1++) {
    // if (arrayOfBoolean[i1] != 0) {
    // continue;
    // }
    // if (arrayOfInt[i1] != 2) {
    // n = i1;
    // } else {
    // i2 = this.offsets[i1];
    // if ((this.path[i2] != 46) || (this.path[(i2 + 1)] != 46)) {
    // n = i1;
    // } else if (n >= 0) {
    // arrayOfBoolean[n] = true;
    // arrayOfBoolean[i1] = true;
    // j -= 2;
    // n = -1;
    // } else if (bool) {
    // int i3 = 0;
    // for (int i4 = 0; i4 < i1; i4++) {
    // if (arrayOfBoolean[i4] == 0) {
    // i3 = 1;
    // break;
    // }
    // }
    // if (i3 != 0)
    // continue;
    // arrayOfBoolean[i1] = true;
    // j--;
    // }
    // }
    // }
    // } while (m > j);
    // }
    //
    // if (j == i) {
    // return this;
    // }
    //
    // if (j == 0) {
    // return bool ? getFileSystem().rootDirectory() : emptyPath();
    // }
    //
    // m = j - 1;
    // if (bool) {
    // m++;
    // }
    // for (int n = 0; n < i; n++) {
    // if (arrayOfBoolean[n] == 0)
    // m += arrayOfInt[n];
    // }
    // byte[] arrayOfByte = new byte[m];
    //
    // int i1 = 0;
    // if (bool)
    // arrayOfByte[(i1++)] = 47;
    // for (int i2 = 0; i2 < i; i2++) {
    // if (arrayOfBoolean[i2] == 0) {
    // System.arraycopy(this.path, this.offsets[i2], arrayOfByte, i1, arrayOfInt[i2]);
    // i1 += arrayOfInt[i2];
    // j--;
    // if (j > 0) {
    // arrayOfByte[(i1++)] = 47;
    // }
    // }
    // }
    // return new MMUnixPath(getFileSystem(), arrayOfByte);
    // }
    //
    // public boolean startsWith(Path paramPath) {
    // if (!(Objects.requireNonNull(paramPath) instanceof MMUnixPath))
    // return false;
    // MMUnixPath localUnixPath = (MMUnixPath) paramPath;
    //
    // if (localUnixPath.path.length > this.path.length) {
    // return false;
    // }
    // int i = getNameCount();
    // int j = localUnixPath.getNameCount();
    //
    // if ((j == 0) && (isAbsolute())) {
    // return !localUnixPath.isEmpty();
    // }
    //
    // if (j > i) {
    // return false;
    // }
    //
    // if ((j == i) && (this.path.length != localUnixPath.path.length)) {
    // return false;
    // }
    //
    // for (int k = 0; k < j; k++) {
    // Integer localInteger1 = Integer.valueOf(this.offsets[k]);
    // Integer localInteger2 = Integer.valueOf(localUnixPath.offsets[k]);
    // if (!localInteger1.equals(localInteger2)) {
    // return false;
    // }
    // }
    //
    // k = 0;
    // while (k < localUnixPath.path.length) {
    // if (this.path[k] != localUnixPath.path[k])
    // return false;
    // k++;
    // }
    //
    // return (k >= this.path.length) || (this.path[k] == 47);
    // }
    //
    // public boolean endsWith(MMPath paramPath) {
    // if (!(Objects.requireNonNull(paramPath) instanceof MMUnixPath))
    // return false;
    // MMUnixPath localUnixPath = (MMUnixPath) paramPath;
    //
    // int i = this.path.length;
    // int j = localUnixPath.path.length;
    //
    // if (j > i) {
    // return false;
    // }
    //
    // if ((i > 0) && (j == 0)) {
    // return false;
    // }
    //
    // if ((localUnixPath.isAbsolute()) && (!isAbsolute())) {
    // return false;
    // }
    // int k = getNameCount();
    // int m = localUnixPath.getNameCount();
    //
    // if (m > k) {
    // return false;
    // }
    //
    // if (m == k) {
    // if (k == 0)
    // return true;
    // n = i;
    // if ((isAbsolute()) && (!localUnixPath.isAbsolute()))
    // n--;
    // if (j != n) {
    // return false;
    // }
    // } else if (localUnixPath.isAbsolute()) {
    // return false;
    // }
    //
    // int n = this.offsets[(k - m)];
    // int i1 = localUnixPath.offsets[0];
    // if (j - i1 != i - n)
    // return false;
    // while (i1 < j) {
    // if (this.path[(n++)] != localUnixPath.path[(i1++)]) {
    // return false;
    // }
    // }
    // return true;
    // }
    //
    // public int compareTo(MMPath paramPath) {
    // int i = this.path.length;
    // int j = ((MMUnixPath) paramPath).path.length;
    //
    // int k = Math.min(i, j);
    // byte[] arrayOfByte1 = this.path;
    // byte[] arrayOfByte2 = ((MMUnixPath) paramPath).path;
    //
    // int m = 0;
    // while (m < k) {
    // int n = arrayOfByte1[m] & 0xFF;
    // int i1 = arrayOfByte2[m] & 0xFF;
    // if (n != i1) {
    // return n - i1;
    // }
    // m++;
    // }
    // return i - j;
    // }
    //
    // public boolean equals(Object paramObject) {
    // if ((paramObject != null) && ((paramObject instanceof MMUnixPath))) {
    // return compareTo((MMPath) paramObject) == 0;
    // }
    // return false;
    // }
    //
    // public int hashCode() {
    // int i = this.hash;
    // if (i == 0) {
    // for (int j = 0; j < this.path.length; j++) {
    // i = 31 * i + (this.path[j] & 0xFF);
    // }
    // this.hash = i;
    // }
    // return i;
    // }
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
    public String toString() {
        return ReflectionToStringBuilder.toString(this, ToStringStyle.SHORT_PREFIX_STYLE);
    }

}
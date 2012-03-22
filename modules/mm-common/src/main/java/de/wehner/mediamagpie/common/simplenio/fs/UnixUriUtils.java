package de.wehner.mediamagpie.common.simplenio.fs;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;

import de.wehner.mediamagpie.common.simplenio.file.MMPath;

public class UnixUriUtils {

    private static final long L_DIGIT;
    private static final long H_DIGIT = 0L;
    private static final long L_UPALPHA = 0L;
    private static final long H_UPALPHA;
    private static final long L_LOWALPHA = 0L;
    private static final long H_LOWALPHA;
    private static final long L_ALPHA = 0L;
    private static final long H_ALPHA;
    private static final long L_ALPHANUM;
    private static final long H_ALPHANUM;
    private static final long L_MARK;
    private static final long H_MARK;
    private static final long L_UNRESERVED;
    private static final long H_UNRESERVED;
    private static final long L_PCHAR;
    private static final long H_PCHAR;
    private static final long L_PATH;
    private static final long H_PATH;
    private static final char[] hexDigits;

    static MMPath fromUri(MMUnixFileSystem paramUnixFileSystem, URI paramURI) {
        if (!paramURI.isAbsolute())
            throw new IllegalArgumentException("URI is not absolute");
        if (paramURI.isOpaque())
            throw new IllegalArgumentException("URI is not hierarchical");
        String str1 = paramURI.getScheme();
        if ((str1 == null) || (!str1.equalsIgnoreCase("file")))
            throw new IllegalArgumentException("URI scheme is not \"file\"");
        if (paramURI.getAuthority() != null)
            throw new IllegalArgumentException("URI has an authority component");
        if (paramURI.getFragment() != null)
            throw new IllegalArgumentException("URI has a fragment component");
        if (paramURI.getQuery() != null) {
            throw new IllegalArgumentException("URI has a query component");
        }

        if (!paramURI.toString().startsWith("file:///")) {
            // TODO rwe: find solution
            return null;
            // return new File(paramURI).toPath();
        }

        String str2 = paramURI.getRawPath();
        int i = str2.length();
        if (i == 0) {
            throw new IllegalArgumentException("URI path component is empty");
        }

        if ((str2.endsWith("/")) && (i > 1))
            i--;
        byte[] arrayOfByte = new byte[i];
        int j = 0;
        int k = 0;
        while (k < i) {
            int m = str2.charAt(k++);
            int n;
            if (m == 37) {
                assert (k + 2 <= i);
                char c1 = str2.charAt(k++);
                char c2 = str2.charAt(k++);
                n = (byte) (decode(c1) << 4 | decode(c2));
                if (n == 0)
                    throw new IllegalArgumentException("Nul character not allowed");
            } else {
                assert (m < 128);
                n = (byte) m;
            }
            arrayOfByte[(j++)] = (byte) n;
        }
        if (j != arrayOfByte.length) {
            arrayOfByte = Arrays.copyOf(arrayOfByte, j);
        }
        return new MMUnixPath(paramUnixFileSystem, arrayOfByte);
    }

//    static URI toUri(MMUnixPath paramUnixPath) {
//        byte[] arrayOfByte = paramUnixPath.toAbsolutePath().asByteArray();
//        StringBuilder localStringBuilder = new StringBuilder("file:///");
//        assert (arrayOfByte[0] == 47);
//        for (int i = 1; i < arrayOfByte.length; i++) {
//            char c = (char) (arrayOfByte[i] & 0xFF);
//            if (match(c, L_PATH, H_PATH)) {
//                localStringBuilder.append(c);
//            } else {
//                localStringBuilder.append('%');
//                localStringBuilder.append(hexDigits[(c >> '\004' & 0xF)]);
//                localStringBuilder.append(hexDigits[(c & 0xF)]);
//            }
//
//        }
//
//        if (localStringBuilder.charAt(localStringBuilder.length() - 1) != '/')
//            try {
//                if (UnixFileAttributes.get(paramUnixPath, true).isDirectory())
//                    localStringBuilder.append('/');
//            } catch (UnixException localUnixException) {
//            }
//        try {
//            return new URI(localStringBuilder.toString());
//        } catch (URISyntaxException localURISyntaxException) {
//        }
//        throw new AssertionError(localURISyntaxException);
//    }

    private static long lowMask(String paramString) {
        int i = paramString.length();
        long l = 0L;
        for (int j = 0; j < i; j++) {
            int k = paramString.charAt(j);
            if (k < 64)
                l |= 1L << k;
        }
        return l;
    }

    private static long highMask(String paramString) {
        int i = paramString.length();
        long l = 0L;
        for (int j = 0; j < i; j++) {
            int k = paramString.charAt(j);
            if ((k >= 64) && (k < 128))
                l |= 1L << k - 64;
        }
        return l;
    }

    private static long lowMask(char paramChar1, char paramChar2) {
        long l = 0L;
        int i = Math.max(Math.min(paramChar1, 63), 0);
        int j = Math.max(Math.min(paramChar2, 63), 0);
        for (int k = i; k <= j; k++)
            l |= 1L << k;
        return l;
    }

    private static long highMask(char paramChar1, char paramChar2) {
        long l = 0L;
        int i = Math.max(Math.min(paramChar1, 127), 64) - 64;
        int j = Math.max(Math.min(paramChar2, 127), 64) - 64;
        for (int k = i; k <= j; k++)
            l |= 1L << k;
        return l;
    }

    private static boolean match(char paramChar, long paramLong1, long paramLong2) {
        if (paramChar < '@')
            return (1L << paramChar & paramLong1) != 0L;
        if (paramChar < '')
            return (1L << paramChar - '@' & paramLong2) != 0L;
        return false;
    }

    private static int decode(char paramChar) {
        if ((paramChar >= '0') && (paramChar <= '9'))
            return paramChar - '0';
        if ((paramChar >= 'a') && (paramChar <= 'f'))
            return paramChar - 'a' + 10;
        if ((paramChar >= 'A') && (paramChar <= 'F'))
            return paramChar - 'A' + 10;
        throw new AssertionError();
    }

    static {
        L_DIGIT = lowMask('0', '9');

        H_UPALPHA = highMask('A', 'Z');

        H_LOWALPHA = highMask('a', 'z');

        H_ALPHA = H_LOWALPHA | H_UPALPHA;

        L_ALPHANUM = L_DIGIT | 0L;
        H_ALPHANUM = 0L | H_ALPHA;

        L_MARK = lowMask("-_.!~*'()");
        H_MARK = highMask("-_.!~*'()");

        L_UNRESERVED = L_ALPHANUM | L_MARK;
        H_UNRESERVED = H_ALPHANUM | H_MARK;

        L_PCHAR = L_UNRESERVED | lowMask(":@&=+$,");

        H_PCHAR = H_UNRESERVED | highMask(":@&=+$,");

        L_PATH = L_PCHAR | lowMask(";/");
        H_PATH = H_PCHAR | highMask(";/");

        hexDigits = new char[] { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F' };
    }
}
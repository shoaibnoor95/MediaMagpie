package de.wehner.mediamagpie.core.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.apache.commons.codec.binary.Hex;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;

public class DigestUtil {

    private static final Logger LOG = Logger.getLogger(DigestUtil.class);

    public static String computeSha1AsHexString(File inputFile) {
        FileInputStream inputStream = null;
        try {
            inputStream = new FileInputStream(inputFile);
            return computeSha1AsHexString(inputStream);
        } catch (IOException e) {
            throw ExceptionUtil.convertToRuntimeException(e);
        } finally {
            IOUtils.closeQuietly(inputStream);
        }
    }

    public static String computeSha1AsHexString(String input) {
        try {
            InputStream inputStream = IOUtils.toInputStream(input, "ISO8859-15");
            return computeSha1AsHexString(inputStream);
        } catch (IOException e) {
            throw ExceptionUtil.convertToRuntimeException(e);
        }
    }

    public static String computeSha1AsHexString(InputStream inputStream) {
        try {
            return computeHashAsHexString(inputStream, MessageDigest.getInstance("SHA1"));
        } catch (NoSuchAlgorithmException e) {
            throw ExceptionUtil.convertToRuntimeException(e);
        }
    }

    public static String computeMd5AsHexString(String input) {
        try {
            InputStream inputStream = IOUtils.toInputStream(input, "ISO8859-15");
            return computeMd5AsHexString(inputStream);
        } catch (IOException e) {
            throw ExceptionUtil.convertToRuntimeException(e);
        }
    }

    @Deprecated
    public static String computeMd5AsHexString(InputStream inputStream) {
        try {
            return computeHashAsHexString(inputStream, MessageDigest.getInstance("MD5"));
        } catch (NoSuchAlgorithmException e) {
            throw ExceptionUtil.convertToRuntimeException(e);
        }
    }

    public static String computeHashAsHexString(InputStream fin, MessageDigest messageDigest) {
        try {
            byte[] buffer = new byte[1024];
            int read;
            do {
                read = fin.read(buffer);
                if (read > 0) {
                    messageDigest.update(buffer, 0, read);
                }
            } while (read != -1);
            byte[] digest = messageDigest.digest();
            if (digest == null) {
                return null;
            }
            return asHexString(digest);
        } catch (Exception e) {
            return null;
        } finally {
            IOUtils.closeQuietly(fin);
        }
    }

    @Deprecated
    public static byte[] buildDigest(File file, MessageDigest messageDigest) {
        InputStream inputStream = null;
        try {
            byte[] buffer = new byte[1024];
            int read;
            inputStream = new FileInputStream(file);
            do {
                read = inputStream.read(buffer);
                if (read > 0) {
                    messageDigest.update(buffer, 0, read);
                }
            } while (read != -1);
            byte[] digest = messageDigest.digest();
            if (digest == null) {
                return null;
            }
            byte[] dest = new byte[digest.length];
            System.arraycopy(digest, 0, dest, 0, digest.length);
            return dest;
        } catch (Exception e) {
            return null;
        } finally {
            IOUtils.closeQuietly(inputStream);
        }
    }

    public static String asHexString(byte[] digest) {
        return new String(Hex.encodeHex(digest));
    }

    @Deprecated
    public static void writeDigestAsHexIntoFile(File digestFile, MessageDigest md) throws IOException {
        String digestString = asHexString(md.digest());
        LOG.debug("Write hash value '" + digestString + "' into file '" + digestFile.getPath() + "'.");
        FileUtils.writeStringToFile(digestFile, digestString);
    }

    @Deprecated
    public static String readDigestFromHashFile(File hashFile) throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader(hashFile));
        String digest = reader.readLine();
        reader.close();
        return digest;

    }

    @Deprecated
    public static MessageDigest getMd5MessageDigest() {
        try {
            return MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            throw ExceptionUtil.convertToRuntimeException(e);
        }
    }

    @Deprecated
    public static MessageDigest getSha1MessageDigest() {
        try {
            return MessageDigest.getInstance("SHA1");
        } catch (NoSuchAlgorithmException e) {
            throw ExceptionUtil.convertToRuntimeException(e);
        }
    }
}

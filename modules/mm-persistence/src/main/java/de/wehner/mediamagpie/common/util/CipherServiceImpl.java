package de.wehner.mediamagpie.common.util;

import java.security.GeneralSecurityException;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;

import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.binary.Hex;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import de.wehner.mediamagpie.core.util.CipherService;
import de.wehner.mediamagpie.core.util.ExceptionUtil;


@Service
public class CipherServiceImpl implements CipherService {

    private static final Logger LOG = LoggerFactory.getLogger(CipherServiceImpl.class);

    /**
     * rwe: For a stronger encryption use the AES algorithm. See http://stackoverflow.com/questions/992019/java-256bit-aes-encryption
     */
    private static final String ALGORITHM = "DES";

    private Cipher _encryptCipher;
    private Cipher _decryptCipher;

    @Autowired
    public CipherServiceImpl(@Qualifier("cipher.key") String cipherKey) {
        while (cipherKey.length() < 8) {
            cipherKey += cipherKey;
        }
        try {
            SecretKeyFactory keyFactory = SecretKeyFactory.getInstance(ALGORITHM);
            SecretKey key = keyFactory.generateSecret(new DESKeySpec(cipherKey.getBytes()));
            _encryptCipher = Cipher.getInstance(ALGORITHM);
            _encryptCipher.init(Cipher.ENCRYPT_MODE, key);
            _decryptCipher = Cipher.getInstance(ALGORITHM);
            _decryptCipher.init(Cipher.DECRYPT_MODE, key);
        } catch (GeneralSecurityException e) {
            throw ExceptionUtil.convertToRuntimeException(e);
        }
    }

    public String decryptFromBase64(String text) {
        try {
            byte[] encryptedData = text.getBytes();
            if (!Base64.isBase64(encryptedData)) {
                LOG.warn("Skip decrypting of unencrypted data.");
                return text;
            }
            byte[] decodedEncryptedData = Base64.decodeBase64(encryptedData);
            if (decodedEncryptedData.length % 8 != 0) {
                LOG.warn("Skip decrypting of unencrypted data.");
                return text;
            }

            byte[] decryptedData = _decryptCipher.doFinal(decodedEncryptedData);
            return new String(decryptedData);
        } catch (Exception e) {
            throw ExceptionUtil.convertToRuntimeException(e);
        }
    }

    public String encryptToBase64(String text) {
        try {
            byte[] encryptedData = _encryptCipher.doFinal(text.getBytes());
            byte[] base64Encoded = Base64.encodeBase64(encryptedData);
            return new String(base64Encoded);
        } catch (Exception e) {
            throw ExceptionUtil.convertToRuntimeException(e);
        }
    }

    public String decryptFromHex(String text) {
        try {
            byte[] decodedEncryptedData = Hex.decodeHex(text.toCharArray());
            if (decodedEncryptedData.length % 8 != 0) {
                LOG.warn("Skip decrypting of unencrypted data.");
                return text;
            }

            byte[] decryptedData = _decryptCipher.doFinal(decodedEncryptedData);
            return new String(decryptedData);
        } catch (DecoderException e) {
            LOG.warn("Skip decrypting of unencrypted data. Text seems not to be hexadecimal decoded.");
            return text;
        } catch (Exception e) {
            throw ExceptionUtil.convertToRuntimeException(e);
        }
    }

    public String encryptToHex(String text) {
        try {
            byte[] encryptedData = _encryptCipher.doFinal(text.getBytes());
            char[] hexEncoded = Hex.encodeHex(encryptedData, false);
            return new String(hexEncoded);
        } catch (Exception e) {
            throw ExceptionUtil.convertToRuntimeException(e);
        }
    }
}

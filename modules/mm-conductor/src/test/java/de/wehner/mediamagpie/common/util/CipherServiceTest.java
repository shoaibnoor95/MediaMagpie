package de.wehner.mediamagpie.common.util;

import static org.junit.Assert.*;

import org.junit.Test;

import de.wehner.mediamagpie.common.util.CipherService;

public class CipherServiceTest {

    @Test
    public void testDecryptUnencrypedData() {
        CipherService cipherService = new CipherService("password");
        String plain = "hello";
        String decrypt = cipherService.decryptFromBase64("hello");
        assertEquals(plain, decrypt);
    }

    @Test
    public void testRoundTrip() {
        CipherService cipherService = new CipherService("password");
        String plain = "hello";
        String encrypt = cipherService.encryptToBase64(plain);
        assertFalse(plain.equals(encrypt));
        String decrypt = cipherService.decryptFromBase64(encrypt);
        assertEquals(plain, decrypt);
    }

    @Test
    public void testDifferentKeys() {
        CipherService cipherService1 = new CipherService("password1");
        CipherService cipherService2 = new CipherService("other");
        String plain = "hello";
        String encrypt1 = cipherService1.encryptToBase64(plain);
        String encrypt2 = cipherService2.encryptToBase64(plain);
        assertFalse(encrypt1.equals(encrypt2));
    }
}

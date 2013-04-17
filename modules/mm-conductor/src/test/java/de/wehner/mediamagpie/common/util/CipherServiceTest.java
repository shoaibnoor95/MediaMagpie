package de.wehner.mediamagpie.common.util;

import static org.junit.Assert.*;

import org.junit.Test;

import de.wehner.mediamagpie.persistence.util.CipherServiceImpl;



public class CipherServiceTest {

    @Test
    public void testDecryptUnencrypedData() {
        CipherServiceImpl cipherService = new CipherServiceImpl("password");
        String plain = "hello";
        String decrypt = cipherService.decryptFromBase64("hello");
        assertEquals(plain, decrypt);
    }

    @Test
    public void testRoundTrip() {
        CipherServiceImpl cipherService = new CipherServiceImpl("password");
        String plain = "hello";
        String encrypt = cipherService.encryptToBase64(plain);
        assertFalse(plain.equals(encrypt));
        String decrypt = cipherService.decryptFromBase64(encrypt);
        assertEquals(plain, decrypt);
    }

    @Test
    public void testDifferentKeys() {
        CipherServiceImpl cipherService1 = new CipherServiceImpl("password1");
        CipherServiceImpl cipherService2 = new CipherServiceImpl("other");
        String plain = "hello";
        String encrypt1 = cipherService1.encryptToBase64(plain);
        String encrypt2 = cipherService2.encryptToBase64(plain);
        assertFalse(encrypt1.equals(encrypt2));
    }
}

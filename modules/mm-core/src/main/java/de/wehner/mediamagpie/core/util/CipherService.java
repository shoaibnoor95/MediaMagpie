package de.wehner.mediamagpie.core.util;

public interface CipherService {

    Object decryptFromBase64(String value);

    String encryptToBase64(String propertyValue);

}

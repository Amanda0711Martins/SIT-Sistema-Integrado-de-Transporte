// CryptoUtil.java
package com.logistics.HumanResources.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Base64;

@Component
@Slf4j
public class CryptoUtil {

    @Value("${hr.encryption.key}")
    private String secretKey;

    /**
     * Encrypts a string using AES algorithm
     *
     * @param data the string to encrypt
     * @return the encrypted string encoded in Base64
     */
    public String encrypt(String data) {
        try {
            Key key = generateKey();
            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.ENCRYPT_MODE, key);
            byte[] encryptedBytes = cipher.doFinal(data.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(encryptedBytes);
        } catch (Exception e) {
            log.error("Error encrypting data", e);
            throw new RuntimeException("Error encrypting data", e);
        }
    }

    /**
     * Decrypts a Base64 encoded string using AES algorithm
     *
     * @param encryptedData the Base64 encoded encrypted string
     * @return the decrypted string
     */
    public String decrypt(String encryptedData) {
        try {
            Key key = generateKey();
            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.DECRYPT_MODE, key);
            byte[] decodedBytes = Base64.getDecoder().decode(encryptedData);
            byte[] decryptedBytes = cipher.doFinal(decodedBytes);
            return new String(decryptedBytes, StandardCharsets.UTF_8);
        } catch (Exception e) {
            log.error("Error decrypting data", e);
            throw new RuntimeException("Error decrypting data", e);
        }
    }

    private Key generateKey() {
        // Ensure the key is exactly 16, 24 or 32 bytes for AES-128, AES-192 or AES-256
        byte[] keyBytes = new byte[16];
        byte[] secretKeyBytes = secretKey.getBytes(StandardCharsets.UTF_8);

        // Copy the key bytes, padding or truncating as necessary
        int length = Math.min(secretKeyBytes.length, keyBytes.length);
        System.arraycopy(secretKeyBytes, 0, keyBytes, 0, length);

        return new SecretKeySpec(keyBytes, "AES");
    }
}
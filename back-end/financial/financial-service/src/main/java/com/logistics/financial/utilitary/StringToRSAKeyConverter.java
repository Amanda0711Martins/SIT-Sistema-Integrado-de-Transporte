package com.logistics.financial.utilitary;

import org.springframework.stereotype.Component;
import java.security.*;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.*;
import java.util.Base64;
import java.util.regex.Pattern;

@Component
public class StringToRSAKeyConverter {

    private static final Pattern PEM_PATTERN = Pattern.compile(
            "-----BEGIN (.*)-----|-----END (.*)-----|\\s");

    public RSAPublicKey convertPub(String publicKeyPem) {
        try {
            // Clean the PEM string
            String publicKeyContent = PEM_PATTERN.matcher(publicKeyPem).replaceAll("");

            // Use MIME decoder which is more lenient with formatting
            byte[] keyBytes = Base64.getMimeDecoder().decode(publicKeyContent);

            // Try both X509 and PKCS8 formats
            try {
                X509EncodedKeySpec keySpec = new X509EncodedKeySpec(keyBytes);
                KeyFactory keyFactory = KeyFactory.getInstance("RSA");
                return (RSAPublicKey) keyFactory.generatePublic(keySpec);
            } catch (InvalidKeySpecException e) {
                PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(keyBytes);
                KeyFactory keyFactory = KeyFactory.getInstance("RSA");
                return (RSAPublicKey) keyFactory.generatePublic(keySpec);
            }
        } catch (Exception e) {
            throw new IllegalArgumentException("Failed to convert public key string to RSAPublicKey", e);
        }
    }

    public RSAPrivateKey convertPriString(String privateKeyPem) {
        try {
            String privateKeyContent = PEM_PATTERN.matcher(privateKeyPem).replaceAll("");
            byte[] encoded = Base64.getMimeDecoder().decode(privateKeyContent);

            PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(encoded);
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            return (RSAPrivateKey) keyFactory.generatePrivate(keySpec);
        } catch (Exception e) {
            throw new IllegalArgumentException("Failed to convert private key string to RSAPrivateKey", e);
        }
    }
}
package com.sittransportadora;

import com.sittransportadora.utilitary.StringToRSAKeyConverter;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;

import java.security.interfaces.RSAPublicKey;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.util.AssertionErrors.fail;

@SpringBootTest
public class KeyValidationTest {

    @Value("${jwt.public.key}")
    private String publicKey;

    @Autowired
    private StringToRSAKeyConverter converter;

    @Test
    void testPublicKey() {
        try {
            RSAPublicKey key = converter.convertPub(publicKey);
            assertNotNull(key);
            System.out.println("Key successfully parsed:");
            System.out.println("Algorithm: " + key.getAlgorithm());
            System.out.println("Format: " + key.getFormat());
            System.out.println("Modulus: " + key.getModulus());
        } catch (Exception e) {
            System.err.println("Key parsing failed:");
            e.printStackTrace();
            fail("Key parsing failed");
        }
    }
}
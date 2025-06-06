package com.sittransportadora.config;

import com.sittransportadora.utilitary.StringToRSAKeyConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;

@Configuration
public class JwtKeyConfig {
    @Value("${jwt.public.key}")
    private String publicKey;

    @Value("${jwt.private.key}")
    private String privateKey;

    @Bean
    public RSAPublicKey rsaPublicKey(StringToRSAKeyConverter keyReader) throws Exception {
        return keyReader.convertPub(publicKey);
    }

    @Bean
    public RSAPrivateKey rsaPrivateKey(StringToRSAKeyConverter keyReader) throws Exception {
        return keyReader.convertPriString(privateKey);
    }


}

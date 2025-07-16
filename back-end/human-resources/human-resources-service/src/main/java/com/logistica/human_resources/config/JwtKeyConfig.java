package com.logistica.human_resources.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.logistica.human_resources.utilitary.*;

import java.security.interfaces.RSAPublicKey;

@Configuration
public class JwtKeyConfig {
    @Value("${jwt.public.key}")
    private String publicKey;

    @Bean
    public RSAPublicKey rsaPublicKey(StringToRSAKeyConverter keyReader) throws Exception {
        return keyReader.convertPub(publicKey);
    }
}

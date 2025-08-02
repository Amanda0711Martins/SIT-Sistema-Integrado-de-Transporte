package com.logistica.customer.utilitary;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.security.KeyFactory;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.*;
import java.util.Base64;

@Slf4j
@Component
public class StringToRSAKeyConverter {

    public RSAPublicKey convertPub(String publicKeyPem) {
        log.info("Iniciando conversão da chave pública...");
        log.debug("Conteúdo original da chave PEM: " + publicKeyPem); 
        if (publicKeyPem == null || publicKeyPem.isEmpty()) {
            log.error("A string da chave pública (PEM) está nula ou vazia.");
        }
        String publicKeyContent = publicKeyPem
                .replace("-----BEGIN PUBLIC KEY-----", "")
                .replace("-----END PUBLIC KEY-----", "")
                .replace("-----BEGIN RSA PUBLIC KEY-----", "") 
                .replace("-----END RSA PUBLIC KEY-----", "") 
                .replaceAll("\\s", ""); 

        log.info("Conversão feita da chave pública. Conteúdo final: " + publicKeyContent);
        try {

            byte[] keyBytes = Base64.getMimeDecoder().decode(publicKeyContent);

            X509EncodedKeySpec keySpec = new X509EncodedKeySpec(keyBytes);
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            RSAPublicKey publicKey = (RSAPublicKey) keyFactory.generatePublic(keySpec);

            log.info("Chave pública convertida com sucesso.");
            return publicKey;

        } catch (Exception e) {
            log.error("Erro ao converter chave pública. Verifique se o formato está correto (X.509 PEM).", e);
            throw new IllegalArgumentException("Failed to convert public key string to RSAPublicKey", e);
        }
    }

    public RSAPrivateKey convertPriString(String privateKeyPem) {
    try {
        log.info("Iniciando conversão da chave privada...");
        if (privateKeyPem == null || privateKeyPem.isEmpty()) {
            log.error("A string da chave privada (PEM) está nula ou vazia.");
            throw new IllegalArgumentException("Chave privada PEM não pode ser nula ou vazia.");
        }

        String privateKeyContent = privateKeyPem
                .replace("-----BEGIN PRIVATE KEY-----", "")  // CORRETO
                .replace("-----END PRIVATE KEY-----", "")    // CORRETO
                .replace("-----BEGIN RSA PRIVATE KEY-----", "")
                .replace("-----END RSA PRIVATE KEY-----", "")
                .replaceAll("\\s", "");

        log.info("Conversão feita da chave privada. Conteúdo final: " + privateKeyContent);

        byte[] encoded = Base64.getDecoder().decode(privateKeyContent);

        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(encoded);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        RSAPrivateKey privateKey = (RSAPrivateKey) keyFactory.generatePrivate(keySpec);

        log.info("Chave privada convertida com sucesso.");
        return privateKey;

    } catch (Exception e) {
        log.error("Erro ao converter chave privada", e);
        throw new IllegalArgumentException("Failed to convert private key string to RSAPrivateKey", e);
    }
}}

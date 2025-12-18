package org.wa.auth.service.service.impl;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.wa.auth.service.service.EncryptService;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.Base64;

@Service
public class EncryptServiceImpl implements EncryptService {
    @Value("${encrypt.cipher-algorithm}")
    private String cipherAlgorithm;

    @Value("${encrypt.secret-key-algorithm}")
    private String secretKeyAlgorithm;

    private final SecretKey secretKey;

    public EncryptServiceImpl(@Value("${encrypt.secret}") String secret) {
        byte[] key = Arrays.copyOf(secret.getBytes(StandardCharsets.UTF_8), 32);
        this.secretKey = new SecretKeySpec(key, secretKeyAlgorithm);
    }

    public String encrypt(String valueToEncrypt) {
        if (valueToEncrypt == null) return null;

        try {
            Cipher cipher = Cipher.getInstance(cipherAlgorithm);
            byte[] encryptVector = new byte[12];
            SecureRandom.getInstanceStrong().nextBytes(encryptVector);

            cipher.init(Cipher.ENCRYPT_MODE, secretKey, new GCMParameterSpec(128, encryptVector));
            byte[] encrypted = cipher.doFinal(valueToEncrypt.getBytes(StandardCharsets.UTF_8));

            return Base64.getEncoder().encodeToString(encryptVector) + ":" +
                    Base64.getEncoder().encodeToString(encrypted);

        } catch (Exception e) {
            throw new IllegalStateException("Encryption failed", e);
        }
    }
}

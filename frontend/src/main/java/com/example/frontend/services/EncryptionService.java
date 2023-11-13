package com.example.frontend.services;

import javax.crypto.*;
import java.security.*;
import java.util.Base64;

public class EncryptionService {
    /**
     * Generate a key pair for RSA encryption
     * @return The key pair
     */
    public KeyPair generateSecretKey() throws NoSuchAlgorithmException {
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
        keyPairGenerator.initialize(2048);
        return keyPairGenerator.generateKeyPair();
    }

    /**
     * Encrypt data with a public key using RSA encryption
     * @param data The data to encrypt
     * @param publicKey The public key
     * @return The encrypted data
     */
    public String encrypt(String data, PublicKey publicKey) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.ENCRYPT_MODE, publicKey);
        byte[] encryptedBytes = cipher.doFinal(data.getBytes());
        return Base64.getEncoder().encodeToString(encryptedBytes);

    }

    /**
     * Decrypt data with a private key using RSA encryption
     * @param data The data to decrypt
     * @param privateKey The private key
     * @return The decrypted data
     */
    public String decrypt(String data, PrivateKey privateKey) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.DECRYPT_MODE, privateKey);
        byte[] decryptedBytes = cipher.doFinal(Base64.getDecoder().decode(data));
        return new String(decryptedBytes);

    }
}

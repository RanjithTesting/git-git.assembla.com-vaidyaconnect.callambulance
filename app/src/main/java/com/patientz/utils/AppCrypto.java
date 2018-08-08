package com.patientz.utils;


import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SecureRandom;

import javax.crypto.Cipher;
import javax.crypto.Mac;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;

public class AppCrypto {

        private static final int SALT_LENGTH = 200;
        private static final int IV_LENGTH = 16;
        private static final int PBE_ITERATION_COUNT = 1000;
        private static final String RANDOM_ALGORITHM = "SHA1PRNG";
        private static final String PBE_ALGORITHM = "PBEWithSHA256And256BitAES-CBC-BC";
        private static final String CIPHER_ALGORITHM = "AES/CBC/PKCS5Padding";
        private static final String SECRET_KEY_ALGORITHM = "AES";
    	private static final String TAG = "AdvancedCrypto";

        public static String encrypt(SecretKey secret, String cleartext) throws Exception {
                try {

                        byte[] iv = generateIv();
                        String ivBase64 = EncodeFile.byteArrayToBase64(iv);
                        IvParameterSpec ivspec = new IvParameterSpec(iv);

                        Cipher encryptionCipher = Cipher.getInstance(CIPHER_ALGORITHM);
                        encryptionCipher.init(Cipher.ENCRYPT_MODE, secret, ivspec);
                        byte[] encryptedText = encryptionCipher.doFinal(cleartext.getBytes("UTF-8"));
                        String encryptedBase64 = EncodeFile.byteArrayToBase64(encryptedText);
                        return ivBase64 + encryptedBase64;

                } catch (Exception e) {
                        throw new Exception("Unable to encrypt", e);
                }
        }

        public static String decrypt(SecretKey secret, String encrypted) throws Exception {
                try {
                        Cipher decryptionCipher = Cipher.getInstance(CIPHER_ALGORITHM);
                        Log.d(TAG, "1");
                        String ivBase64 = encrypted.substring(0, 12 * 2);//TODO resolve exception in this line
                        Log.d("2", "2");
                        String encryptedBase64 = encrypted.substring(12 * 2);
                        Log.d("3", "3");
                        IvParameterSpec ivspec = new IvParameterSpec(EncodeFile.base64ToByteArray(ivBase64));
                        Log.d("4", "4");
                        decryptionCipher.init(Cipher.DECRYPT_MODE, secret, ivspec);
                        Log.d("5", "5");
                        byte[] decryptedText = decryptionCipher.doFinal(EncodeFile.base64ToByteArray(encryptedBase64));
                        String decrypted = new String(decryptedText, "UTF-8");
                        return decrypted;
                } catch (Exception e) {
                        throw new Exception("Unable to decrypt", e);
                }
        }

        public static SecretKey getSecretKey(String password, String salt) throws Exception {
        	SecretKey secret = null;
               try
               {
            	   PBEKeySpec pbeKeySpec = new PBEKeySpec(password.toCharArray(), EncodeFile.base64ToByteArray(salt), PBE_ITERATION_COUNT, 256);
                   SecretKeyFactory factory = SecretKeyFactory.getInstance(PBE_ALGORITHM);
                   SecretKey tmp = factory.generateSecret(pbeKeySpec);
                   secret = new SecretKeySpec(tmp.getEncoded(), SECRET_KEY_ALGORITHM);
               }
               catch (Exception e) {
				throw new Exception(e.getMessage(),e);// TODO: handle exception
			}
                        
                        return secret;
               
        }

        public static String getHash(String password, String salt) throws Exception {
                try {
                        String input = password + salt;
                        Mac mac = Mac.getInstance("HmacSHA1");
                        SecretKeySpec secret = new SecretKeySpec(input.getBytes("UTF-8"), mac.getAlgorithm());
                        mac.init(secret);
                        byte[] out = mac.doFinal(input.getBytes("UTF-8"));
                        return EncodeFile.byteArrayToBase64(out);
                } catch (Exception e) {
                        throw new Exception("Unable to get hash", e);
                }
        }

        public String generateSalt() throws Exception {
                try {
                        SecureRandom random = SecureRandom.getInstance(RANDOM_ALGORITHM);
                        byte[] salt = new byte[SALT_LENGTH];
                        random.nextBytes(salt);
                        String saltBase64 = EncodeFile.byteArrayToBase64(salt);
                        return saltBase64;
                } catch (Exception e) {
                        throw new Exception("Unable to generate salt", e);
                }
        }

        private static byte[] generateIv() throws NoSuchAlgorithmException, NoSuchProviderException {
                SecureRandom random = SecureRandom.getInstance(RANDOM_ALGORITHM);
                byte[] iv = new byte[IV_LENGTH];
                random.nextBytes(iv);
                return iv;
        }

}
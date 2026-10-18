import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.security.spec.KeySpec;
import java.util.Arrays;
import java.util.Base64;

public class CryptoService {
    private static final String ALGORITHM = "AES";
    private static final String TRANSFORMATION = "AES/GCM/NoPadding";
    private static final int GCM_NONCE_LENGTH = 12;
    private static final int GCM_TAG_LENGTH_BITS = 128;
    private static final int SALT_LENGTH = 16;
    private static final int PBKDF2_ITERATIONS = 65536;
    private static final int KEY_LENGTH_BITS = 256;

    private static final SecureRandom RANDOM = new SecureRandom();

    public static String encrypt(char[] masterPassword, String plaintext) {
        try {
            byte[] salt = new byte[SALT_LENGTH];
            byte[] nonce = new byte[GCM_NONCE_LENGTH];
            RANDOM.nextBytes(salt);
            RANDOM.nextBytes(nonce);

            SecretKey key = deriveKey(masterPassword, salt);

            Cipher cipher = Cipher.getInstance(TRANSFORMATION);
            GCMParameterSpec spec = new GCMParameterSpec(GCM_TAG_LENGTH_BITS, nonce);
            cipher.init(Cipher.ENCRYPT_MODE, key, spec);

            byte[] ciphertext = cipher.doFinal(plaintext.getBytes(StandardCharsets.UTF_8));

            byte[] combined = new byte[salt.length + nonce.length + ciphertext.length];
            System.arraycopy(salt, 0, combined, 0, salt.length);
            System.arraycopy(nonce, 0, combined, salt.length, nonce.length);
            System.arraycopy(ciphertext, 0, combined, salt.length + nonce.length, ciphertext.length);

            return Base64.getEncoder().encodeToString(combined);
        } catch (Exception e) {
            throw new CryptoException("Encryption failed", e);
        }
    }

    public static String decrypt(char[] masterPassword, String encryptedPayload) {
        try {
            byte[] combined = Base64.getDecoder().decode(encryptedPayload);

            if (combined.length < SALT_LENGTH + GCM_NONCE_LENGTH) {
                throw new CryptoException("Invalid encrypted payload");
            }

            byte[] salt = Arrays.copyOfRange(combined, 0, SALT_LENGTH);
            byte[] nonce = Arrays.copyOfRange(combined, SALT_LENGTH, SALT_LENGTH + GCM_NONCE_LENGTH);
            byte[] ciphertext = Arrays.copyOfRange(combined, SALT_LENGTH + GCM_NONCE_LENGTH, combined.length);

            SecretKey key = deriveKey(masterPassword, salt);

            Cipher cipher = Cipher.getInstance(TRANSFORMATION);
            GCMParameterSpec spec = new GCMParameterSpec(GCM_TAG_LENGTH_BITS, nonce);
            cipher.init(Cipher.DECRYPT_MODE, key, spec);

            byte[] plaintext = cipher.doFinal(ciphertext);
            return new String(plaintext, StandardCharsets.UTF_8);
        } catch (CryptoException e) {
            throw e;
        } catch (Exception e) {
            throw new CryptoException("Decryption failed", e);
        }
    }

    private static SecretKey deriveKey(char[] password, byte[] salt) throws Exception {
        SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
        KeySpec spec = new PBEKeySpec(password, salt, PBKDF2_ITERATIONS, KEY_LENGTH_BITS);
        return new SecretKeySpec(factory.generateSecret(spec).getEncoded(), ALGORITHM);
    }
}

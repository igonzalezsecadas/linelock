import javax.crypto.*;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.Base64;

public class Entry {
    public String service;
    public String user;
    private String password;

    public Entry(String service, String user) {
        this.service = service;
        this.user = user;
        this.password = "";
    }

    public String getDecryptedPassword() throws NoSuchPaddingException, NoSuchAlgorithmException, IllegalBlockSizeException, BadPaddingException, InvalidAlgorithmParameterException, InvalidKeyException, IOException {
        byte[] decoded = Base64.getDecoder().decode(this.password);

        byte[] iv = new byte[16];
        byte[] cipherText = new byte[decoded.length - 16];

        SecretKey key = getKeyFromPassword();

        System.arraycopy(decoded, 0, iv, 0, 16);
        System.arraycopy(decoded, 16, cipherText, 0, cipherText.length);

        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        cipher.init(Cipher.DECRYPT_MODE, key, new IvParameterSpec(iv));

        return new String(cipher.doFinal(cipherText));
    }

    public void setEncryptedPassword(String unsafePassword) throws NoSuchPaddingException, NoSuchAlgorithmException, IllegalBlockSizeException, BadPaddingException, InvalidAlgorithmParameterException, InvalidKeyException, IOException {
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");

        byte[] iv = new byte[16];
        SecureRandom random = new SecureRandom();
        random.nextBytes(iv);
        SecretKey key = getKeyFromPassword();

        IvParameterSpec ivSpec = new IvParameterSpec(iv);
        cipher.init(Cipher.ENCRYPT_MODE, key, ivSpec);

        byte[] encrypted = cipher.doFinal(unsafePassword.getBytes());

        // IV + ciphertext juntos
        byte[] encryptedIVAndText = new byte[iv.length + encrypted.length];
        System.arraycopy(iv, 0, encryptedIVAndText, 0, iv.length);
        System.arraycopy(encrypted, 0, encryptedIVAndText, iv.length, encrypted.length);

        this.password = Base64.getEncoder().encodeToString(encryptedIVAndText);
    }

    public static SecretKey getKeyFromPassword() throws IOException {
        byte[] key = Key.getMaster().getBytes();
        key = Arrays.copyOf(key, 32); // 256 bits
        return new SecretKeySpec(key, "AES");
    }

    public String toString() {
        try {
            return getDecryptedPassword();
        } catch (Exception e) {
            return e.getMessage();
        }
    }
}

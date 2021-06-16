import java.io.Console;
import java.util.Base64;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import java.security.Security;
import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import javax.crypto.KeyGenerator;
import javax.crypto.spec.IvParameterSpec;
import java.security.SecureRandom;
import java.security.KeyPairGenerator;
import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;

public class Assignment_2 {

	public static void main(String[] args) {

        /*  Finding/Verifying the provider is available
    	System.out.println("BC: \n");
        String name = "BC";
        if (Security.getProvider(name) == null) {
            System.out.println("not installed \n");
        } else {
            System.out.println("installed \n");
        }
        */

        // inputText will be the test string to carry through the following three tests
        Console cnsl = System.console();
        String inputText = cnsl.readLine("Please enter a test string: \n");
        System.out.println("You entered: \n" + inputText);

        // Proceeding with AES Tests
        System.out.println("Test string to be encrypted via AES: " + inputText + "\n");

        Cipher cipher;
        SecretKeySpec key;
        byte[] keyBytes = new byte[16];
        key = new SecretKeySpec(keyBytes, "AES");
        byte[] inputBytes = inputText.getBytes();

        try {
            // Encrypt
            cipher = Cipher.getInstance("Blowfish", "BC");
            cipher.init(Cipher.ENCRYPT_MODE, key);
            byte[] encrypted = cipher.doFinal(inputBytes);

            // Decrpyt
            cipher.init(Cipher.DECRYPT_MODE, key);
            byte[] decrypted = cipher.doFinal(encrypted);
            String result = new String(decrypted);

            // Print
            System.out.println(result);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

        // Proceeding with Blowfish
        System.out.println("Test string to be encrypted via Blowfish: " + inputText + "\n");
        key = new SecretKeySpec(keyBytes, "Blowfish");
        try {
            // Encrypt
            cipher = Cipher.getInstance("AES/ECB/PKCS5Padding", "BC");
            cipher.init(Cipher.ENCRYPT_MODE, key);
            byte[] encrypted = cipher.doFinal(inputBytes);

            // Decrpyt
            cipher.init(Cipher.DECRYPT_MODE, key);
            byte[] decrypted = cipher.doFinal(encrypted);
            String result = new String(decrypted);

            // Print
            System.out.println(result);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

        // Proceeding with RSA Enc/Dec/Sign
        System.out.println("Test string to be encrypted via RSA: " + inputText + "\n");
        try {
            // Key Setup for RSA
            KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");
            keyGen.initialize(1024);
            KeyPair pair = keyGen.generateKeyPair();
            PublicKey pubKey = pair.getPublic();
            PrivateKey privKey = pair.getPrivate();

            // Message Encryption
            cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding", "BC");
            cipher.init(Cipher.ENCRYPT_MODE, pubKey);
            byte[] encrypted = cipher.doFinal(inputBytes);

            // Message Decryption
            cipher.init(Cipher.DECRYPT_MODE, privKey);
            byte[] decrypted = cipher.doFinal(encrypted);
            String result = new String(decrypted);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

        System.out.println("Verifying RSA Signature: " + inputText + "\n");
        try {
            // Key Setup for RSA
            KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");
            keyGen.initialize(1024);
            KeyPair pair = keyGen.generateKeyPair();
            PublicKey pubKey = pair.getPublic();
            PrivateKey privKey = pair.getPrivate();

            // Signature Setup
            Signature signa = Signature.getInstance("SHA1withRSA");
            signa.initSign(privKey);
            signa.update(inputBytes);
            byte[] signaBytes = signa.sign();
            signa.initVerify(pubKey);
            signa.update(inputBytes);
            System.out.println(signa.verify(signaBytes));
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    
    }
}
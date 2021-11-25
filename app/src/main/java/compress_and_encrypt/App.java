/*
 * This Java source file was generated by the Gradle 'init' task.
 */
package compress_and_encrypt;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.Base64.Encoder;
import java.util.zip.GZIPOutputStream;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import net.lingala.zip4j.core.ZipFile;
import net.lingala.zip4j.exception.ZipException;
import net.lingala.zip4j.model.FileHeader;
import net.lingala.zip4j.model.ZipParameters;
import net.lingala.zip4j.util.Zip4jConstants;

public class App {

    public static void zipAes256(String filePath, String outZipPath, String password) throws IOException, ZipException {
        File srcFile = new File(filePath);
        ZipParameters zipParameters = new ZipParameters();
        zipParameters.setCompressionMethod(Zip4jConstants.COMP_DEFLATE);
        zipParameters.setCompressionLevel(Zip4jConstants.DEFLATE_LEVEL_NORMAL);
        zipParameters.setEncryptFiles(true);
        zipParameters.setEncryptionMethod(Zip4jConstants.ENC_METHOD_AES);
        zipParameters.setAesKeyStrength(Zip4jConstants.AES_STRENGTH_256);
        zipParameters.setPassword(password);
        ZipFile zipFile = new ZipFile(outZipPath);
        zipFile.addFile(srcFile, zipParameters);

        FileHeader fileHeader = (FileHeader) zipFile.getFileHeaders().get(0);
        InputStream is = zipFile.getInputStream(fileHeader);
        System.out.println(is);

        System.out.println("zip compress and AES256 encrypt success!");
    }

    public static SecretKeySpec getSecretKey(String password) {
        KeyGenerator kg = null;
        try {
            kg = KeyGenerator.getInstance("AES");
            SecureRandom random = SecureRandom.getInstance("SHA1PRNG");
            random.setSeed(password.getBytes());
            kg.init(256, random);
            SecretKey secretKey = kg.generateKey();
            return new SecretKeySpec(secretKey.getEncoded(), "AES");
        } catch(NoSuchAlgorithmException e) {
            System.err.println(e.getMessage());
        }
        return null;
    }

    public static String compressAndEncryptString(String input, String password) throws IOException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
        if(input == null || input.length() == 0) {
            return input;
        }

        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        GZIPOutputStream gzip = new GZIPOutputStream(bos);
        gzip.write(input.getBytes());
        Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
        gzip.toString();
        gzip.close();
        byte[] content = bos.toByteArray();
        cipher.init(Cipher.ENCRYPT_MODE, getSecretKey(password));
        byte[] result = cipher.doFinal(content);
        Encoder encoder = Base64.getEncoder();
        return encoder.encodeToString(result);
    }

    public static void main(String[] args) throws IOException, ZipException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
        StringBuilder sb = new StringBuilder();
        BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(new File("./test.txt"))));
        String line = null;
        while((line = br.readLine()) != null) {
            sb.append(line);
        }

        // System.out.println(sb.toString());
        System.out.println(compressAndEncryptString(sb.toString(), "abc123"));
    }

    
}

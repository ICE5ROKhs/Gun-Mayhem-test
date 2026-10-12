package DESencryption;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;
import java.security.SecureRandom;
import java.util.Base64;

public class DESencryption {

    /**
     * DES加密方法
     * @param data 需要加密的字符串
     * @param keyStr 8位长的密钥字符串
     * @return 加密后的Base64编码字符串
     */
    public static String encrypt(String data, String keyStr) {
        try {
            // 1. 创建一个DESKeySpec对象，用于保存密钥
            DESKeySpec desKeySpec = new DESKeySpec(keyStr.getBytes("UTF-8"));

            // 2. 创建一个SecretKeyFactory对象，用于生成密钥
            SecretKeyFactory secretKeyFactory = SecretKeyFactory.getInstance("DES");

            // 3. 从DESKeySpec对象生成一个SecretKey对象
            SecretKey secretKey = secretKeyFactory.generateSecret(desKeySpec);

            // 4. 创建一个Cipher对象，用于执行加密操作
            Cipher cipher = Cipher.getInstance("DES/ECB/PKCS5Padding");

            // 5. 初始化Cipher对象为加密模式
            cipher.init(Cipher.ENCRYPT_MODE, secretKey, SecureRandom.getInstance("SHA1PRNG"));

            // 6. 执行加密操作，将原始数据转换为字节数组
            byte[] encryptedBytes = cipher.doFinal(data.getBytes("UTF-8"));

            // 7. 使用Base64编码将字节数组转换为字符串，便于存储和传输
            return Base64.getEncoder().encodeToString(encryptedBytes);
        } catch (Exception e) {
            throw new RuntimeException("加密过程中发生错误", e);
        }
    }

    /**
     * DES解密方法
     * @param encryptedData 加密后的Base64编码字符串
     * @param keyStr 8位长的密钥字符串
     * @return 解密后的字符串
     */
    public static String decrypt(String encryptedData, String keyStr) {
        try {
            // 1. 创建一个DESKeySpec对象，用于保存密钥
            DESKeySpec desKeySpec = new DESKeySpec(keyStr.getBytes("UTF-8"));

            // 2. 创建一个SecretKeyFactory对象，用于生成密钥
            SecretKeyFactory secretKeyFactory = SecretKeyFactory.getInstance("DES");

            // 3. 从DESKeySpec对象生成一个SecretKey对象
            SecretKey secretKey = secretKeyFactory.generateSecret(desKeySpec);

            // 4. 创建一个Cipher对象，用于执行解密操作
            Cipher cipher = Cipher.getInstance("DES/ECB/PKCS5Padding");

            // 5. 初始化Cipher对象为解密模式
            cipher.init(Cipher.DECRYPT_MODE, secretKey, SecureRandom.getInstance("SHA1PRNG"));

            // 6. 使用Base64解码将字符串转换回字节数组
            byte[] encryptedBytes = Base64.getDecoder().decode(encryptedData);

            // 7. 执行解密操作，将字节数组转换为原始数据
            byte[] decryptedBytes = cipher.doFinal(encryptedBytes);

            // 8. 将字节数组转换为字符串
            return new String(decryptedBytes, "UTF-8");
        } catch (Exception e) {
            throw new RuntimeException("解密过程中发生错误", e);
        }
    }

}


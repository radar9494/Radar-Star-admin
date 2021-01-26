package com.liuqi.utils;

import org.apache.commons.lang.StringUtils;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.spec.SecretKeySpec;


/**
 * @author Administrator
 * AES工具
 */
public class AESUtil {

    private static final String defaultCharset = "UTF-8";
    private static final String KEY_AES = "AES";

    //算法
    public static final String ALGORITHMSTR = "AES/ECB/PKCS5Padding";


    /**
     * 解密
     *
     * @param data 待解密内容
     * @param key 解密密钥
     * @return
     */
    public static String decrypt(String data, String key) {
        byte[] encryptBytes = AESUtil.parseHexStr2Byte(data);

        try {
            KeyGenerator kgen = KeyGenerator.getInstance("AES");
            kgen.init(128);

            Cipher cipher = Cipher.getInstance(ALGORITHMSTR);
            cipher.init(Cipher.DECRYPT_MODE, new SecretKeySpec(key.getBytes(), "AES"));
            byte[] decryptBytes = cipher.doFinal(encryptBytes);
            return new String(decryptBytes);

        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 加解密
     */
    public static String encrypt(String content, String key) {
        try {
            if (StringUtils.isBlank(content) || StringUtils.isBlank(content)) {
                return null;
            }
            KeyGenerator kgen = KeyGenerator.getInstance("AES");
            kgen.init(128);
            Cipher cipher = Cipher.getInstance(ALGORITHMSTR);
            cipher.init(Cipher.ENCRYPT_MODE, new SecretKeySpec(key.getBytes(), "AES"));

            byte[] result = cipher.doFinal(content.getBytes("utf-8"));

            return parseByte2HexStr(result);
        } catch (Exception e) {
            System.out.println("AES 密文处理异常" + e);
        }
        return null;
    }
    /**
     * 将二进制转换成16进制
     *
     * @param buf
     * @return
     */
    public static String parseByte2HexStr(byte buf[]) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < buf.length; i++) {
            String hex = Integer.toHexString(buf[i] & 0xFF);
            if (hex.length() == 1) {
                hex = '0' + hex;
            }
            sb.append(hex.toUpperCase());
        }
        return sb.toString();
    }
    /**
     * 将16进制转换为二进制
     *
     * @param hexStr
     * @return
     */
    public static byte[] parseHexStr2Byte(String hexStr) {
        if (hexStr.length() < 1) {
            return null;
        }
        byte[] result = new byte[hexStr.length() / 2];
        for (int i = 0; i < hexStr.length() / 2; i++) {
            int high = Integer.parseInt(hexStr.substring(i * 2, i * 2 + 1), 16);
            int low = Integer.parseInt(hexStr.substring(i * 2 + 1, i * 2 + 2), 16);
            result[i] = (byte) (high * 16 + low);
        }
        return result;
    }
    public static void main(String[] args) throws Exception {
        String content = "{\"action\":\"getcashpick\"}";
//      System.out.println("加密前：" + content);
//      System.out.println("加密密钥和解密密钥：" + Constant.AES_KEY);
//      String encrypt = encrypt(content, Constant.AES_KEY);
//      System.out.println("加密后：" + encrypt);
//
//      String decrypt = decrypt(encrypt, Constant.AES_KEY);
//      System.out.println("解密后：" + decrypt);
//      String data = encrypt;
//		String result = "";
//		if (data != null && data.length() > 0) {
//			String decData = AESUtil.decrypt(data, KEY);
//			if (decData != null && decData.length() > 0) {
//				JSONObject jsonObject = JSONObject.fromObject(decData);
//				String action = jsonObject.getString("action");
//				System.out.println(action);
////				if ("dd".equals(action)) {// 提现
////					result = getCashPick(jsonObject.getString("pickUid"), jsonObject.getString("toAddress"), jsonObject.getString("ethValue"));
////				} else if (ACTION_GET_PICK.equals(action)) {
////					result = getPickRecord();
////				} else if (ACTION_TRANSACTION.equals(action)) {
////					result = pickHash(jsonObject.getString("toAddress"), jsonObject.getString("txHash"));
////				}
//			}
//		}
    }
}

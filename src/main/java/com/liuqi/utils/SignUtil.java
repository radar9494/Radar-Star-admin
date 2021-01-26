package com.liuqi.utils;

import cn.hutool.crypto.SecureUtil;
import cn.hutool.crypto.symmetric.DES;
import org.apache.commons.lang3.StringUtils;

/**
 * @author tanyan
 * @create 2020-07=20
 * @description
 */
public class SignUtil {
    private static final DES des= SecureUtil.des("".getBytes());

    /**
     * 加密
     * @param key
     * @return
     */
    public static String getEncrypt(String key){
        if(StringUtils.isNotEmpty(key)){
            return des.encryptBase64(key,"utf-8");
        }
        return "";
    }

    /**
     * 解密
     * @param encryptKey
     * @return
     */
    public static String getDecode(String encryptKey){
        String decode="";
        if(StringUtils.isNotEmpty(encryptKey)){
            try {
                decode= des.decryptStr(encryptKey);
            }catch (Exception e){

            }
        }
        return decode;
    }
}

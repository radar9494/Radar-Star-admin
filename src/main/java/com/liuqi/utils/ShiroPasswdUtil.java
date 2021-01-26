package com.liuqi.utils;

import com.liuqi.base.BaseConstant;
import org.apache.shiro.codec.Hex;
import org.apache.shiro.crypto.hash.SimpleHash;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * @author tanyan
 * @create 2019-11=29
 * @description
 */
public class ShiroPasswdUtil {


    public static String getUserPwd(String orginPwd){
        String slat=BaseConstant.TYPE_USER+BaseConstant.BASE_PROJECT;
        return ShiroPasswdUtil.getBasePwd(orginPwd,slat);
    }

    public static String getSysPwd(String orginPwd){
        String slat=BaseConstant.TYPE_SYS+BaseConstant.BASE_PROJECT;
        return ShiroPasswdUtil.getBasePwd(orginPwd,slat);
    }

    public static String getAdminPwd(String orginPwd){
        String slat=BaseConstant.TYPE_ADMIN+BaseConstant.BASE_PROJECT;
        return ShiroPasswdUtil.getBasePwd(orginPwd,slat);
    }

    public static String getBasePwd(String orginPwd,String slat){
        String hashAlgorithmName = "MD5";//加密方式
        int hashIterations = BaseConstant.PWD_COUNT;//加密次
        return new SimpleHash(hashAlgorithmName,orginPwd,slat,hashIterations).toString();
    }

    public static void main(String[] args) {
        String str="str";
//        String salt="user";

        System.out.println(getUserPwd(str));
//        try {
//            MessageDigest digest = MessageDigest.getInstance("MD5");
//            digest.update(salt.getBytes());
//            byte[] hashed = str.getBytes();
//            for(int i = 0; i < 67; i++) {
//                hashed = digest.digest(hashed);
//            }
//            System.out.println("-2--"+Hex.encodeToString(hashed));
//        } catch (NoSuchAlgorithmException e) {
//            e.printStackTrace();
//        }
    }
}

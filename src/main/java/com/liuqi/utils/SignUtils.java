package com.liuqi.utils;

import org.apache.commons.lang3.StringUtils;

import java.util.Map;
import java.util.TreeMap;

public class SignUtils {

    public static String signData(Map<String, Object> params,String key){
        TreeMap<String, String> tempMap = new TreeMap<String, String>();
        for (Map.Entry<String, Object> entry : params.entrySet()) {
            if (StringUtils.isNotBlank(entry.getValue().toString()) && !"sign".equalsIgnoreCase(entry.getKey())) {
                tempMap.put(entry.getKey(), entry.getValue().toString());
            }
        }
        StringBuffer buf = new StringBuffer();
        for (Map.Entry<String, String> entry : tempMap.entrySet()) {
            buf.append(entry.getKey()).append("=").append(entry.getValue()).append("&");
        }
        String signStr = buf.append("key="+key).toString();
        System.out.println("组装签名后数据:" + signStr);
        String signData = MD5Util.MD5Encode(signStr);
        System.out.println("签名-->" + signData);
        return signData;
    }

    public static boolean verifySignData(Map<String, Object> params,String key) {
        String sign = "";
        TreeMap<String, String> tempMap = new TreeMap<String, String>();
        for (Map.Entry<String, Object> entry : params.entrySet()) {
            if (StringUtils.isNotBlank(entry.getValue().toString())) {
                if (entry.getKey().equals("sign")) {
                    sign = entry.getValue().toString();
                } else {
                    tempMap.put(entry.getKey(), entry.getValue().toString());
                }
            }
        }
        StringBuffer buf = new StringBuffer();
        for (Map.Entry<String, String> entry : tempMap.entrySet()) {
            buf.append(entry.getKey()).append("=").append(entry.getValue()).append("&");
        }
        String signStr = buf.append("key="+key).toString();
        System.out.println("组装签名后数据:" + signStr);
        String signData = MD5Util.MD5Encode(signStr);
        System.out.println("验证签名-->" + signData);
        return signData.equalsIgnoreCase(sign);
    }
}

package com.liuqi.utils;

import org.apache.commons.lang3.StringUtils;

import java.util.Map;
import java.util.TreeMap;

public class APISignUtils {

    public static String signData(String timestamp,Map<String, Object> params,String key){
        TreeMap<String, String> tempMap = new TreeMap<String, String>();
        for (Map.Entry<String, Object> entry : params.entrySet()) {
            if (entry.getValue()!=null && StringUtils.isNotEmpty(entry.getValue().toString())) {
                tempMap.put(entry.getKey(), entry.getValue().toString());
            }
        }
        StringBuilder buf = new StringBuilder(timestamp);
        for (Map.Entry<String, String> entry : tempMap.entrySet()) {
            buf.append(entry.getKey()).append("=").append(entry.getValue());
        }
        String signStr = buf.append(key).toString();
        System.out.println("组装签名后数据:" + signStr);
        String signData = MD5Util.MD5Encode(signStr);
        System.out.println("签名-->" + signData);
        return signData;
    }

}

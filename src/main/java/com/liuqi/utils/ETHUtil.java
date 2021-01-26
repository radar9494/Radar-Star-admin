package com.liuqi.utils;

import java.math.BigDecimal;
import java.math.BigInteger;

public class ETHUtil {

    public static BigDecimal parseEth(String ethNum,int decimals) {
        BigDecimal bigDecimal = new BigDecimal(ethNum);
        BigDecimal result = bigDecimal.divide(new BigDecimal(Math.pow(10,decimals)));
        return result;
    }

    public static BigDecimal parseRpcEth(String ethNum,int decimals) {
        ethNum = ethNum.substring(2);
        BigInteger bigInteger = new BigInteger(ethNum, 16);
        BigDecimal bigDecimal = new BigDecimal(bigInteger);
        BigDecimal result = bigDecimal.divide(new BigDecimal(Math.pow(10,decimals)));
        return result;
    }


}

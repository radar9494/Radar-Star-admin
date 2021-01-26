package com.liuqi.third.price;

import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpUtil;
import com.alibaba.fastjson.JSONObject;
import com.liuqi.business.constant.KeyConstant;
import com.liuqi.exception.BusinessException;
import com.liuqi.redis.RedisRepository;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.concurrent.TimeUnit;

/**
 * 查询火币交易对价格
 */
@Component
@Slf4j
public class HBSearchPrice{
    private static final String URL="https://api.huobi.pro/market/detail?symbol=";
    private static final String MERGED_URL="https://api.huobi.pro/market/detail/merged?symbol=";


    @Autowired
    private RedisRepository redisRepository;

    /**
     * 获取单个价格
     * @param name  获取名称   比如：ethqc
     * @return
     */
    public BigDecimal getPriceHttp(String name){
        try {
            HttpRequest request= HttpUtil.createGet(URL+name);
            String result=request.timeout(2000).execute().body();
            if(StringUtils.isNotEmpty(result)){
                JSONObject obj = JSONObject.parseObject(result);
                if ("ok".equals(obj.get("status"))) {
                    return obj.getJSONObject("tick").getBigDecimal("close");
                }
            }
        }catch (Exception e){
            log.error("HB price  not fount :"+name);
        }
        return BigDecimal.ZERO;
    }

    public JSONObject getHttp(String name,String nameTwo){
        try {
            HttpRequest request= HttpUtil.createGet(MERGED_URL+getName(name,nameTwo));
            String result=request.timeout(2000).execute().body();
            if(StringUtils.isNotEmpty(result)){
                JSONObject obj = JSONObject.parseObject(result);
                if (obj.get("status").equals("ok")) {
                    return obj.getJSONObject("tick");
                }
            }
        }catch (Exception e){
            throw  new BusinessException("买一卖一获取失败");
        }
        return null;
    }

    private String getName(String name, String nameTwo) {
        String searchName = name+nameTwo;
        return searchName.toLowerCase();
    }
    /**
     * 接口获取价格  未获取到是获取缓存价格
     * @param searchName
     * @return
     */
    public PriceDto getPriceDto(String name, String nameTwo) {
        String searchName = getName(name, nameTwo);
        String key = KeyConstant.KEY_PRICE_HB + searchName;
        PriceDto priceDto = redisRepository.getModel(key);
        if (priceDto == null) {
            priceDto=new PriceDto(searchName,this.getPriceHttp(searchName));
            redisRepository.set(key,priceDto,1L, TimeUnit.SECONDS);
        }
        return priceDto;
    }

    /**
     * 获取单个价格
     * @param searchName  获取名称   比如：ethqc
     * @return
     */
    public BigDecimal getPrice(String name, String nameTwo){
        PriceDto priceDto=this.getPriceDto(name, nameTwo);
        return priceDto.getPrice();
    }



}

package com.liuqi.third.zb;

import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpUtil;
import com.alibaba.fastjson.JSONObject;
import com.liuqi.business.constant.KeyConstant;
import com.liuqi.redis.RedisRepository;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.concurrent.TimeUnit;

/**
 * 查询zb交易对价格
 */
@Component
public class SearchPrice {

    private static String URL = "http://api.zb.plus/data/v1/ticker";

    @Autowired
    private RedisRepository redisRepository;
    @Autowired
    private Environment env;

    /**
     * 获取单个价格
     * @param name  获取名称   比如：ethqc
     * @return
     */
    public SearchPriceDto getPriceInfo(String name) {
        String key = KeyConstant.KEY_ZB_PRICE + name;
        SearchPriceDto price = redisRepository.getModel(key);
        if (price == null) {

            if (!"prod".equals(env.getProperty("spring.profiles.active", String.class))) {
                price=new SearchPriceDto();
                price.setLast("7");
            }else{
                HttpRequest request = HttpUtil.createGet(SearchPrice.URL + "?market=" + name);
                String result = request.execute().body();
                if (StringUtils.isNotEmpty(result)) {
                    JSONObject obj = JSONObject.parseObject(result);
                    price = JSONObject.parseObject(obj.getString("ticker"), SearchPriceDto.class);
                    redisRepository.set(key, price, 2L, TimeUnit.SECONDS);
                }
            }
        }
        return price;
    }
    /**
     * 获取单个价格
     * @param name  获取名称   比如：ethqc
     * @return
     */
    public BigDecimal getPrice(String name) {
        if(StringUtils.isEmpty(name)){
            return BigDecimal.ZERO;
        }
        SearchPriceDto dto = this.getPriceInfo(name);
        return dto!=null?new BigDecimal(dto.getLast()):BigDecimal.ZERO;
    }


    /**
     * 获取usdt的人民币价格
     * @return
     */
    public BigDecimal getUsdtQcPrice() {
        if (!"prod".equals(env.getProperty("spring.profiles.active", String.class))) {
           return BigDecimal.valueOf(7);
        }else{
            SearchPriceDto dto = this.getPriceInfo("usdt_qc");
            return dto!=null?new BigDecimal(dto.getLast()):BigDecimal.ZERO;
        }
    }


}

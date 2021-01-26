package com.liuqi.base;

import com.alibaba.fastjson.JSONObject;
import com.liuqi.business.dto.RecordDto;
import com.liuqi.business.dto.SelectDto;
import com.liuqi.business.enums.KTypeEnum;
import com.liuqi.business.model.CurrencyTradeModelDto;
import com.liuqi.business.model.TradeRecordModelDto;
import com.liuqi.business.service.CurrencyTradeService;
import com.liuqi.business.service.KDataService;
import com.liuqi.business.service.TradeInfoCacheService;
import com.liuqi.redis.RedisRepository;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Date;
import java.util.List;
import java.util.Set;

public class RedisZsetTest extends BaseTest{

    @Autowired
    RedisRepository redisRepository;
    @Autowired
    private TradeInfoCacheService tradeInfoCacheService;
    @Autowired
    private KDataService kDataService;
    @Autowired
    private CurrencyTradeService currencyTradeService;
    @Test
    public void test01(){
        double base= System.currentTimeMillis();
        for(int i=0;i<10;i++) {
            String key = "ttsize";
            double score= System.currentTimeMillis();
            JSONObject obj=new JSONObject();
            obj.put("id",i);
            obj.put("name","ty"+i);
            obj.put("score", score);
            redisRepository.add(key, obj, score);
        }
        double base2= System.currentTimeMillis();
        System.out.println("--获取长度---" + redisRepository.getSize("ttsize"));
        Set set = redisRepository.getAll("ttsize");
        System.out.println("--获取所有---"+JSONObject.toJSONString(set));
        Set set3 = redisRepository.getByScore("ttsize", base, base2);
        System.out.println("--获取tt1 1.5---"+JSONObject.toJSONString(set3));
        System.out.println("--获取第一个---"+JSONObject.toJSONString(set3));
    }
    @Test
    public void test02(){
        for(int i=100;i<150;i++){
            TradeRecordModelDto dto=new TradeRecordModelDto();
            dto.setId(Long.valueOf(i+""));
            dto.setBuyUserId(1L);
            dto.setTradeId(5L);
            dto.setTradeType(i%2);
            dto.setCreateTime(new Date());
            tradeInfoCacheService.tradeRecordCache(dto);
        }

        System.out.println("--------------------");
        List<RecordDto> list = tradeInfoCacheService.tradeRecordList(5L);
        System.out.println("------------list:"+list);
        for(RecordDto dto:list){
            System.out.println("------------"+JSONObject.toJSONString(dto));
        }

        System.out.println("------------end");

    }


    @Test
    public void test03(){
        List<SelectDto> typeList= KTypeEnum.getList();
        List<CurrencyTradeModelDto> list= currencyTradeService.queryListByDto(new CurrencyTradeModelDto(),false);
        for(SelectDto type:typeList){
            for(CurrencyTradeModelDto trade:list){
                kDataService.initCache(Integer.valueOf(type.getKey().toString()),trade.getId());
            }
        }

    }
}

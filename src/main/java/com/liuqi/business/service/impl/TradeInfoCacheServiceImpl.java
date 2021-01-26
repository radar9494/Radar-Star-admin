package com.liuqi.business.service.impl;

import cn.hutool.core.util.RandomUtil;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.liuqi.business.constant.KeyConstant;
import com.liuqi.business.constant.KeyConstant;
import com.liuqi.business.dto.PriceNumsDto;
import com.liuqi.business.dto.RecordDto;
import com.liuqi.business.dto.TradeInfoDto;
import com.liuqi.business.enums.BuySellEnum;
import com.liuqi.business.enums.SwitchEnum;
import com.liuqi.business.model.CurrencyTradeModelDto;
import com.liuqi.business.model.TradeRecordModelDto;
import com.liuqi.business.model.TrusteeModel;
import com.liuqi.business.model.TrusteeModelDto;
import com.liuqi.business.service.*;
import com.liuqi.redis.RedisRepository;
import com.liuqi.utils.MathUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 交易相关数据
 */
@Component
public class TradeInfoCacheServiceImpl implements TradeInfoCacheService {
    @Autowired
    private RedisRepository redisRepository;
    @Autowired
    private TrusteeService trusteeService;
    @Autowired
    private TradeRecordService tradeRecordService;
    @Autowired
    private TradeService tradeService;
    @Autowired
    private CurrencyTradeService currencyTradeService;

    @Override
    public boolean canTrade(Long tradeId) {
        List<PriceNumsDto> buyList = this.buyList(tradeId);
        List<PriceNumsDto> sellList = this.sellList(tradeId);
        return buyList != null && buyList.size() > 0
                && sellList != null && sellList.size() > 0
                && buyList.get(0).getPrice().compareTo(sellList.get(0).getPrice()) >= 0;
    }

    /**
     * 发布修改缓存
     * @param tradeId
     * @param tradeType
     * @param price
     * @param incrementNum
     */
    @Override
    public void publishCache(Long tradeId,Integer tradeType, BigDecimal price, BigDecimal incrementNum,boolean isRobot) {
        if(BuySellEnum.BUY.getCode().equals(tradeType)){
            this.modifyBuyListCache(tradeId,price,incrementNum);
        }else{
            this.modifySellListCache(tradeId,price,incrementNum);
        }

        //机器人随机一个买猫
        if(isRobot){
            tradeType = RandomUtil.randomInt(10) % 2 == 0 ? BuySellEnum.BUY.getCode() : BuySellEnum.SELL.getCode();
        }
    }

    /**
     * 取消修改缓存
     * @param trusteeId
     */
    @Override
    public void cancelCache(Long trusteeId) {
        TrusteeModel trustee=trusteeService.getById(trusteeId);
        //0-剩余数量
        BigDecimal incrementNum=MathUtil.zeroSub(MathUtil.sub(trustee.getQuantity(),trustee.getTradeQuantity()));
        if(BuySellEnum.BUY.getCode().equals(trustee.getTradeType())){
            this.modifyBuyListCache(trustee.getTradeId(),trustee.getPrice(),incrementNum);
        }else{
            this.modifySellListCache(trustee.getTradeId(),trustee.getPrice(),incrementNum);
        }
    }

    /**
     * 交易修改缓存
     *
     * @param record
     */
    @Override
    public void tradeRecordCache(TradeRecordModelDto record) {
        //买卖缓存
        //0-交易数量
        BigDecimal incrementNum=MathUtil.zeroSub(record.getTradeQuantity());
        //买卖减去
        this.modifyBuyListCache(record.getTradeId(),record.getBuyPrice(),incrementNum);
        this.modifySellListCache(record.getTradeId(),record.getSellPrice(),incrementNum);

        //成交记录缓存
        String key = KeyConstant.KEY_TRADERECORD_LIST + record.getTradeId();
        RecordDto dto = new RecordDto();
        dto.setDate(record.getCreateTime());
        dto.setTradeType(record.getTradeType());
        dto.setPrice(record.getTradePrice());
        dto.setNum(record.getTradeQuantity());

        redisRepository.lLSet(key,dto);
        redisRepository.lRemove(key, 0L, Long.valueOf(KeyConstant.KEY_TRADE_LIST_NUM - 1));
    }


    /**
     * 定期同步缓存
     * @param tradeId
     */
    @Override
    public void syncInfo(Long tradeId) {
        //买单
        String key = KeyConstant.KEY_TRUSTEE_BUY_LIST + tradeId;
        this.getMapByDb(key, BuySellEnum.BUY.getCode(), tradeId, new HashMap<>());

        //卖单
        key = KeyConstant.KEY_TRUSTEE_SELL_LIST + tradeId;
        this.getMapByDb(key, BuySellEnum.SELL.getCode(), tradeId, new HashMap<>());

    }



    /********买**********************************************************************************************************/
    /**
     * 设置买列表
     * @param tradeId
     * @param price
     * @param incrementNum 添加数量
     */
    private void modifyBuyListCache(Long tradeId, BigDecimal price, BigDecimal incrementNum) {
        String key = KeyConstant.KEY_TRUSTEE_BUY_LIST + tradeId;
        Map<String, PriceNumsDto> map = this.buyMap(tradeId);
        PriceNumsDto priceNums = map.get(MathUtil.getString(price));
        //null判断
        priceNums = priceNums == null ? new PriceNumsDto(price, BigDecimal.ZERO) : priceNums;
        //增减
        BigDecimal newNum = MathUtil.add(priceNums.getNums(), incrementNum);
        priceNums.setNums(newNum);
        //小于等于0的去除掉
        if (newNum.compareTo(BigDecimal.ZERO) <= 0) {
            map.remove(MathUtil.getString(price));
        } else {
            //增或者减
            map.put(MathUtil.getString(price), priceNums);
        }
        redisRepository.set(key, JSONObject.toJSONString(map));
    }

    /**
     * 获取买列表
     *
     * @param tradeId
     * @return
     */
    private Map<String, PriceNumsDto> buyMap(Long tradeId) {
        Map<String, PriceNumsDto> map = new HashMap<>();
        String key = KeyConstant.KEY_TRUSTEE_BUY_LIST + tradeId;
        String value = redisRepository.getString(key);
        if (StringUtils.isNotEmpty(value)) {
            map = JSONObject.parseObject(value, new TypeReference<Map<String, PriceNumsDto>>() {
            });
        } else {
            this.getMapByDb(key, BuySellEnum.BUY.getCode(),tradeId, map);
        }
        return map;
    }

    @Override
    public List<PriceNumsDto> buyList(Long tradeId ) {
        List<PriceNumsDto> buyList = new ArrayList<>();
        Map<String, PriceNumsDto> map = this.buyMap(tradeId);
        if (!map.isEmpty()) {
            buyList = new ArrayList<>(map.values());
            //过滤大于0的
            buyList= buyList.stream().filter(t->t.getNums().compareTo(BigDecimal.ZERO)>0).collect(Collectors.toList());
            //排序 买单升序
            Collections.sort(buyList, new Comparator<PriceNumsDto>() {
                @Override
                public int compare(PriceNumsDto t1, PriceNumsDto t2) {
                    return t2.getPrice().compareTo(t1.getPrice());
                }
            });
            //大于20条  取20条
            if (buyList.size() > KeyConstant.KEY_TRADE_LIST_NUM) {
                buyList = buyList.subList(0, KeyConstant.KEY_TRADE_LIST_NUM - 1);
            }
        }
        return buyList;
    }
    /********卖**********************************************************************************************************/

    /**
     * 设置卖列表
     * @param tradeId
     * @param price
     * @param incrementNum
     */
    private void modifySellListCache(Long tradeId, BigDecimal price, BigDecimal incrementNum) {
        String key = KeyConstant.KEY_TRUSTEE_SELL_LIST + tradeId;
        Map<String, PriceNumsDto> map = this.sellMap(tradeId);
        PriceNumsDto priceNums = map.get(MathUtil.getString(price));
        //null判断
        priceNums = priceNums == null ? new PriceNumsDto(price, BigDecimal.ZERO) : priceNums;
        //增减
        BigDecimal newNum = MathUtil.add(priceNums.getNums(), incrementNum);
        priceNums.setNums(newNum);
        //小于等于0的去除掉
        if (newNum.compareTo(BigDecimal.ZERO) <= 0) {
            map.remove(MathUtil.getString(price));
        } else {
            //增或者减
            map.put(MathUtil.getString(price), priceNums);
        }
        redisRepository.set(key, JSONObject.toJSONString(map));
    }

    /**
     * 获取卖列表
     *
     * @param tradeId
     * @return
     */
    private Map<String, PriceNumsDto> sellMap(Long tradeId) {
        //倒序
        Map<String, PriceNumsDto> map = new HashMap<>();
        String key = KeyConstant.KEY_TRUSTEE_SELL_LIST + tradeId;
        String value = redisRepository.getString(key);
        if (StringUtils.isNotEmpty(value)) {
            map = JSONObject.parseObject(value, new TypeReference<Map<String, PriceNumsDto>>() {
            });
        } else {
            this.getMapByDb(key,BuySellEnum.SELL.getCode(),tradeId,map);
        }
        return map;
    }
    @Override
    public List<PriceNumsDto> sellList(Long tradeId) {
        List<PriceNumsDto> sellList = new ArrayList<>();
        Map<String, PriceNumsDto> map = this.sellMap(tradeId);
        if (!map.isEmpty()) {
            sellList = new ArrayList<>(map.values());
            //过滤大于0的
            sellList= sellList.stream().filter(t->t.getNums().compareTo(BigDecimal.ZERO)>0).collect(Collectors.toList());
            //排序 买单升序
            Collections.sort(sellList, new Comparator<PriceNumsDto>() {
                @Override
                public int compare(PriceNumsDto t1, PriceNumsDto t2) {
                    return t1.getPrice().compareTo(t2.getPrice());
                }
            });
            //大于20条  取20条
            if (sellList.size() > KeyConstant.KEY_TRADE_LIST_NUM) {
                sellList = sellList.subList(0, KeyConstant.KEY_TRADE_LIST_NUM - 1);
            }
        }
        return sellList;
    }

    private void getMapByDb(String key, Integer tradeType, Long tradeId, Map<String, PriceNumsDto> map) {
        List<TrusteeModelDto> buyList = trusteeService.findTrusteeOrderList(tradeType, tradeId,50);
        if (buyList != null && buyList.size() > 0) {
            for (TrusteeModelDto dto : buyList) {
                if(dto.getNums().compareTo(BigDecimal.ZERO)>0) {
                    map.put(MathUtil.getString(dto.getPrice()), new PriceNumsDto(dto.getPrice(), dto.getNums()));
                }
            }
        }
        redisRepository.set(key, JSONObject.toJSONString(map));
    }

    /********记录**********************************************************************************************************/


    /**
     * 获取交易记录
     *
     * @param tradeId
     * @return
     */
    @Override
    public List<RecordDto> tradeRecordList(Long tradeId) {
        List<RecordDto> recordList = redisRepository.lGet(KeyConstant.KEY_TRADERECORD_LIST + tradeId, 0L, Long.valueOf(KeyConstant.KEY_TRADE_LIST_NUM));
        if (recordList == null || recordList.size() == 0) {
            //数据库读取
            recordList = tradeRecordService.findRecordList(tradeId, KeyConstant.KEY_TRADE_LIST_NUM);
            if (recordList != null && recordList.size() > 0) {
                redisRepository.lLSet(KeyConstant.KEY_TRADERECORD_LIST + tradeId, recordList);
            }
        }
        return recordList;
    }

    /********交易信息**********************************************************************************************************/
    /**
     * 获取交易信息
     *
     * @param tradeId
     * @return
     */
    @Override
    public JSONObject getTradeInfo(Long tradeId) {
        //查询交易对信息
        CurrencyTradeModelDto tradeModel = currencyTradeService.getById(tradeId);
        JSONObject object = new JSONObject();
        //托管买卖信息
        List<PriceNumsDto> buyList = this.buyList(tradeId);
        List<PriceNumsDto> sellList = this.sellList(tradeId);
        //获取已交易成功信息
        List<RecordDto> recordList = this.tradeRecordList(tradeId);
        object.put("tradeId", tradeId);
        object.put("buyList", buyList);
        object.put("sellList", sellList);
        object.put("recordList", recordList);
        int buyCount=buyList!=null?buyList.size():0;
        int sellCount=sellList!=null?sellList.size():0;

        if(buyCount>0){
            //获取上一个价格
            if(SwitchEnum.isOn(tradeModel.getVirtualSwitch())) {
                buyList.stream().forEach(t -> t.setNums(MathUtil.add(t.getNums(), new BigDecimal(RandomUtil.randomDouble(0.1, 0.5)))));
                BigDecimal price = buyList.get(buyCount - 1).getPrice();
                //获取U价格小数位后一位
                BigDecimal base= MathUtil.getBaseByDigits(MathUtil.getDigits(price)+1,1);
                if (base.compareTo(BigDecimal.ZERO)>0 && buyCount < 10) {
                    for (int i = 0; i < 10 - buyCount; i++) {
                        price = MathUtil.sub(price, new BigDecimal(RandomUtil.randomDouble(base.doubleValue(), 5*base.doubleValue())));
                        if (price.compareTo(BigDecimal.ZERO) <= 0) {
                            break;
                        }
                        buyList.add(new PriceNumsDto(price, new BigDecimal(RandomUtil.randomDouble(0.1, 0.5))));
                    }
                }
            }
            Optional<PriceNumsDto> buy=buyList.stream().collect(Collectors.maxBy(Comparator.comparing(PriceNumsDto::getNums)));
            BigDecimal buyMax=buy.isPresent()?buy.get().getNums():BigDecimal.ZERO;
            object.put("buyMax",buyMax);
            if(buyMax.compareTo(BigDecimal.ZERO)>0) {
                buyList.stream().forEach(t -> t.setRatio(MathUtil.div(t.getNums(), buyMax)));
            }
        }else{
            buyList=new ArrayList<>();
        }
        if(sellCount>0){
            if(SwitchEnum.isOn(tradeModel.getVirtualSwitch())) {
                //获取上一个价格
                sellList.stream().forEach(t -> t.setNums(MathUtil.add(t.getNums(), new BigDecimal(RandomUtil.randomDouble(0.1, 0.5)))));
                BigDecimal price = sellList.get(sellCount - 1).getPrice();
                //获取U价格小数位后一位
                BigDecimal base= MathUtil.getBaseByDigits(MathUtil.getDigits(price)+1,1);
                if (base.compareTo(BigDecimal.ZERO)>0 && sellCount < 10) {
                    for (int i = 0; i < 10 - sellCount; i++) {
                        price = MathUtil.add(price, new BigDecimal(RandomUtil.randomDouble(base.doubleValue(), 5*base.doubleValue())));
                        sellList.add(new PriceNumsDto(price, new BigDecimal(RandomUtil.randomDouble(0.1, 0.5))));
                    }
                }
            }
            Collections.reverse(sellList);
            Optional<PriceNumsDto> sell=sellList.stream().collect(Collectors.maxBy(Comparator.comparing(PriceNumsDto::getNums)));
            BigDecimal sellMax=sell.isPresent()?sell.get().getNums():BigDecimal.ZERO;
            object.put("sellMax",sellMax);
            if(sellMax.compareTo(BigDecimal.ZERO)>0) {
                sellList.stream().forEach(t -> t.setRatio(MathUtil.div(t.getNums(), sellMax)));
            }
        }else{
            buyList=new ArrayList<>();
        }
        TradeInfoDto tradeInfo = tradeService.getByCurrencyAndTradeType(tradeModel);
        object.put("tradeInfo", tradeInfo);
        return object;
    }


    /**
     * 深度图
     *
     * @param tradeId
     * @return
     */
    @Override
    public JSONObject getTradeDepthInfo(Long tradeId,Integer gear) {
        JSONObject object = new JSONObject();
        List<PriceNumsDto> buyList = this.buyList(tradeId);
        List<PriceNumsDto> sellList = this.sellList(tradeId);
        object.put("tradeId", tradeId);
        Map<String, PriceNumsDto> map = new HashMap<>();
        CurrencyTradeModelDto currencyTradeModelDto=currencyTradeService.getById(tradeId);
        int digitsP = currencyTradeModelDto.getDigitsP();
        if(gear!=null){
            for(PriceNumsDto dto:buyList){
                BigDecimal price = dto.getPrice().setScale(digitsP - gear + 1, RoundingMode.DOWN);
                dto.setPrice(price);
               if(map.get(price.toPlainString())!=null){
                   PriceNumsDto priceNumsDto = map.get(price.toPlainString());
                   priceNumsDto.setNums(priceNumsDto.getNums().add(dto.getNums()));
               }else{
                   map.put(price.toString(),dto);
               }
            }
            buyList=map.values().stream().collect(Collectors.toList());

            for(PriceNumsDto dto:sellList){
                BigDecimal price = dto.getPrice().setScale(digitsP - gear + 1, RoundingMode.DOWN);
                dto.setPrice(price);
                if(map.get(price.toPlainString())!=null){
                    PriceNumsDto priceNumsDto = map.get(price.toPlainString());
                    priceNumsDto.setNums(priceNumsDto.getNums().add(dto.getNums()));
                }else{
                    map.put(price.toString(),dto);
                }
            }
            sellList=map.values().stream().collect(Collectors.toList());
        }


        object.put("buyList", buyList);
        object.put("sellList", sellList);
        return object;
    }


}

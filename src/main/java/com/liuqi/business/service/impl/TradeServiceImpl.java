package com.liuqi.business.service.impl;


import com.alibaba.fastjson.JSONObject;
import com.liuqi.business.constant.KeyConstant;
import com.liuqi.business.constant.ConfigConstant;
import com.liuqi.business.dto.TradeInfoDto;
import com.liuqi.business.enums.*;
import com.liuqi.business.model.*;
import com.liuqi.business.service.*;
import com.liuqi.exception.TradeException;
import com.liuqi.redis.RedisRepository;
import com.liuqi.third.zb.SearchPrice;
import com.liuqi.utils.DateTimeUtils;
import com.liuqi.utils.MathUtil;
import io.shardingsphere.api.HintManager;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * 主要逻辑（分开时各自的处理）
 * ETH/ENCY
 * 买单：
 * create:(冻结ENCY,获取ETH[更新用户ENCY钱包 可用-，冻结+])
 * trade:(【1获取的ETH=ETH-ETH交易手续费
 * 2生成交易订单明细表：记录卖单买单信息
 * 3生成手续费表】
 * 4更新用户ETH钱包[可用+]
 * 5更新用户ENCY钱包[冻结-]
 * 6卖单对应操作
 * )
 * cancel:(返回未交易的ENCY)
 * 卖单：
 * create:(冻结ETH,获取ENCY)
 * trade:(【1获取ETH交易手续费
 * 2生成交易订单明细表：记录卖单买单信息
 * 3生成手续费表】
 * 4更新用户ENCY钱包[可用+]
 * 5更新用户ETH钱包[冻结-]
 * 6卖单对应操作
 * )
 * cancel:(返回未交易的ETH)
 * ---------------------------------------------------------------------------------------
 */
@Service
@Transactional(readOnly = true)
public class TradeServiceImpl implements TradeService {


    @Autowired
    private TrusteeService trusteeService;
    @Autowired
    private TradeRecordService tradeRecordService;
    @Autowired
    private ServiceChargeDetailService serviceChargeDetailService;
    @Autowired
    private UserWalletService userWalletService;
    @Autowired
    private UserWalletLogService userWalletLogService;
    @Autowired
    private RedisRepository redisRepository;
    @Autowired
    private CurrencyService currencyService;
    @Autowired
    private ConfigService configService;
    @Autowired
    private CurrencyTradeService currencyTradeService;
    @Autowired
    private TradeInfoCacheService tradeInfoCacheService;
    @Autowired
    private SearchPrice searchPrice;
    /*******撮合交易处理逻辑*****************************************************************************************************************/

    /**
     * 撮合处理
     *
     * @param tradeType
     * @return
     */
    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public TradeRecordModelDto doTrade(CurrencyTradeModelDto trade,Long buyId,Long sellId, Integer tradeType) {
        TradeRecordModelDto record =null;
        TrusteeModelDto buyOrder = trusteeService.getById(buyId);
        TrusteeModelDto sellOrder = trusteeService.getById(sellId);

        //判断两个单子是否能交易
        //买1和卖1都有  买价格大于卖价格即可以撮合交易
        if (buyOrder != null && sellOrder != null
                && TrusteeStatusEnum.WAIT.getCode().equals(buyOrder.getStatus())
                && TrusteeStatusEnum.WAIT.getCode().equals(sellOrder.getStatus())
                && buyOrder.getPrice().compareTo(sellOrder.getPrice()) >= 0) {
            //买是否白名单
            boolean buyWhite = YesNoEnum.YES.getCode().equals(buyOrder.getWhite());
            boolean buyRobot = YesNoEnum.YES.getCode().equals(buyOrder.getRobot());
            //卖是否白名单
            boolean sellWhite = YesNoEnum.YES.getCode().equals(sellOrder.getWhite());
            boolean sellRobot = YesNoEnum.YES.getCode().equals(sellOrder.getRobot());

            BigDecimal buySurplusQuantity = buyOrder.getQuantity().subtract(buyOrder.getTradeQuantity());
            BigDecimal sellSurplusQuantity = sellOrder.getQuantity().subtract(sellOrder.getTradeQuantity());
            //获取交易数量  小的那个为交易数量
            BigDecimal tradeQuantity = buySurplusQuantity.compareTo(sellSurplusQuantity) >= 0 ? sellSurplusQuantity : buySurplusQuantity;

            //交易数量大于0
            if (tradeQuantity.compareTo(BigDecimal.ZERO) > 0) {
                record = this.doTrustee(trade, buyOrder, sellOrder, tradeQuantity, tradeType, buyWhite, sellWhite, buyRobot, sellRobot);
                try {
                    //判断卖单交易是否完成
                    sellOrder.setTradeQuantity(MathUtil.add(sellOrder.getTradeQuantity(), tradeQuantity));
                    sellOrder.setRemark("");
                    //发布数量-交易数量
                    if (YesNoEnum.YES.getCode().equals(sellOrder.getRobot())
                            || sellOrder.getQuantity().compareTo(sellOrder.getTradeQuantity()) <= 0) {//完全交易
                        sellOrder.setStatus(TrusteeStatusEnum.SUCCESS.getCode());
                    }
                    //修改卖单信息
                    trusteeService.update(sellOrder);
                } catch (Exception e) {
                    throw new TradeException(e.getMessage(), sellOrder.getId());
                }
                try {
                    //判断买单是否交易完毕
                    buyOrder.setTradeQuantity(MathUtil.add(buyOrder.getTradeQuantity(), tradeQuantity));
                    buyOrder.setRemark("");
                    if (YesNoEnum.YES.getCode().equals(buyOrder.getRobot())
                            || buyOrder.getQuantity().compareTo(buyOrder.getTradeQuantity()) <= 0) {//完全交易
                        buyOrder.setStatus(TrusteeStatusEnum.SUCCESS.getCode());
                    }
                    trusteeService.update(buyOrder);
                } catch (Exception e) {
                    throw new TradeException(e.getMessage(), buyOrder.getId());
                }
            }
        }else{
            tradeInfoCacheService.syncInfo(trade.getId());
        }
        return record;
    }



    /**
     * 买单取消处理
     * eg: ETH/ency
     * 发布买单  价格1000  买2个
     * 冻结ency-->ency钱包操作：可用-（2*1000），冻结+（2*1000）
     * 取消操作时--》eth钱包操作：可用+未交易的数量*单价，冻结的-未交易的数量*单价
     *
     * @param trade
     * @param trusteeId
     * @return
     */
    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void doCancelBuyTrade(CurrencyTradeModelDto trade, Long trusteeId) {
        TrusteeModelDto trusteeOrder=trusteeService.getById(trusteeId);
        if ( !TrusteeStatusEnum.WAIT.getCode().equals(trusteeOrder.getStatus()) && !TrusteeStatusEnum.ERROR.getCode().equals(trusteeOrder.getStatus())) {
            return;
        }
        //非机器人单子   取消操作资产
        if (!YesNoEnum.YES.getCode().equals(trusteeOrder.getRobot())){
            //更新用户交易币种 解冻数量=（发布数量-交易数量）*单价
            BigDecimal surplus = MathUtil.mul(MathUtil.sub(trusteeOrder.getQuantity(), trusteeOrder.getTradeQuantity()), trusteeOrder.getPrice());
            if(surplus.compareTo(BigDecimal.ZERO)>0) {
                //更新用户交易币种 可用数量=可用数量+ 发布数量-已交易数量
                BigDecimal changeUsing = surplus;
                BigDecimal changeFreeze = MathUtil.zeroSub(surplus);
                UserWalletModel wallet = userWalletService.modifyWallet(trusteeOrder.getUserId(), trade.getCurrencyId(), changeUsing, changeFreeze);
                userWalletLogService.addLog(trusteeOrder.getUserId(), trade.getCurrencyId(), surplus, WalletLogTypeEnum.TRADE_BUY.getCode(), trusteeOrder.getId() , "交易买取消返还", wallet);
            }
        }
        //直接状态改为取消
        trusteeOrder.setStatus(TrusteeStatusEnum.CANCEL.getCode());
        trusteeService.update(trusteeOrder);
    }

    /**
     * 卖单取消处理
     * eg: ETH/ency
     * 发布卖单  卖ETH  2个  价格1000
     * 冻结ETH-->eth钱包操作：可用-2，冻结+2
     * 取消操作时--》eth钱包操作：可用+未交易的数量，冻结的-未交易的数量
     *
     * @param trade
     * @param trusteeId
     * @return
     */
    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void doCancelSellTrade(CurrencyTradeModelDto trade,Long trusteeId ) {
        TrusteeModelDto trusteeOrder=trusteeService.getById(trusteeId);
        if ( !TrusteeStatusEnum.WAIT.getCode().equals(trusteeOrder.getStatus()) && !TrusteeStatusEnum.ERROR.getCode().equals(trusteeOrder.getStatus())) {
            return;
        }
        //非机器人
        if (!YesNoEnum.YES.getCode().equals(trusteeOrder.getRobot())) {
            //更新用户交易币种 解冻数量=发布数量-交易数量
            BigDecimal surplus = MathUtil.sub(trusteeOrder.getQuantity(), trusteeOrder.getTradeQuantity());
            if(surplus.compareTo(BigDecimal.ZERO)>0) {
                BigDecimal changeUsing = surplus;
                BigDecimal changeFreeze = MathUtil.zeroSub(surplus);
                UserWalletModel wallet = userWalletService.modifyWallet(trusteeOrder.getUserId(), trade.getTradeCurrencyId(), changeUsing, changeFreeze);
                userWalletLogService.addLog(trusteeOrder.getUserId(), trade.getTradeCurrencyId(), surplus, WalletLogTypeEnum.TRADE_SELL.getCode(),  trusteeOrder.getId() , "交易卖取消返还", wallet);
            }
        }
        //直接状态改为取消
        trusteeOrder.setStatus(TrusteeStatusEnum.CANCEL.getCode());
        trusteeService.update(trusteeOrder);
    }

    /**
     * 交易记录
     * @param trade
     * @param buyOrder
     * @param sellOrder
     * @param tradeQuantity
     * @param tradeType
     * @param buyWhite
     * @param sellWhite
     * @param buyRobot
     * @param sellRobot
     */
    private TradeRecordModelDto doTrustee(CurrencyTradeModelDto trade, TrusteeModelDto buyOrder, TrusteeModelDto sellOrder, BigDecimal tradeQuantity, Integer tradeType, boolean buyWhite, boolean sellWhite,boolean buyRobot, boolean sellRobot) {
        //生成订单明细表====>价格按照卖单价格
        //手续费   数量*税率
        BigDecimal buyRateMoney = tradeQuantity.multiply(buyOrder.getRate()).setScale(8, BigDecimal.ROUND_DOWN);
        if (buyWhite) {//白名单 手续费0
            buyRateMoney = BigDecimal.ZERO;
        }
        //卖单手续费   交易数量*单价*利率
        BigDecimal sellRateMoney = tradeQuantity.multiply(sellOrder.getPrice()).multiply(sellOrder.getRate()).setScale(8, BigDecimal.ROUND_DOWN);
        if (sellWhite) {//白名单 手续费0
            sellRateMoney = BigDecimal.ZERO;
        }

        BigDecimal tradePrice = buyOrder.getPrice();
        //获取时间早的那个
        if (sellOrder.getCreateTime().compareTo(buyOrder.getCreateTime())<0
                ||(sellOrder.getCreateTime().compareTo(buyOrder.getCreateTime())==0&& sellOrder.getId()<buyOrder.getId())) {
            tradePrice = sellOrder.getPrice();
        }

        //是否机器人单子  买卖都是机器人
        boolean robot=buyRobot&&sellRobot;
        TradeRecordModelDto record = TradeRecordModelDto.buildTradeRecordModel(trade.getId(), buyOrder, buyRateMoney, sellOrder, sellRateMoney, tradeQuantity, tradePrice, buyOrder.getPrice(), sellOrder.getPrice(), tradeType,robot);
        tradeRecordService.insert(record);

        if (!buyRobot && !buyWhite && buyRateMoney.compareTo(BigDecimal.ZERO)>0) {//非机器人 非白名单 生成手续费记录
            //生成手续费表 卖单手续费未交易币种手续费
            ServiceChargeDetailModelDto detail_buy = ServiceChargeDetailModelDto.buildServiceChargeDetailModel(record.getId(), buyRateMoney, trade.getTradeCurrencyId(), ChargeDetailTypeEnum.TRADE.getCode());
            serviceChargeDetailService.insert(detail_buy);
        }
        if (!sellRobot && !sellWhite && sellRateMoney.compareTo(BigDecimal.ZERO)>0) {//非机器人 非白名单 生成手续费记录
            //卖单手续费为 币种手续费
            ServiceChargeDetailModelDto detail_sell = ServiceChargeDetailModelDto.buildServiceChargeDetailModel(record.getId(), sellRateMoney, trade.getCurrencyId(), ChargeDetailTypeEnum.TRADE.getCode());
            serviceChargeDetailService.insert(detail_sell);
        }
        return record;
    }

    @Override
    public void doTradeInfo(TradeRecordModelDto record) {
        CurrencyTradeModelDto trade=currencyTradeService.getById(record.getTradeId());
        String key=KeyConstant.KEY_TRADEINFO_ID+DateTimeUtils.currentDate("MMdd")+"_"+trade.getId();
        TradeInfoDto dto=this.getByCurrencyAndTradeType(trade);
        //缓存查询的数据  添加当前增量数量
        if(dto!=null && dto.getSearchType()==1){
            BigDecimal curPrice=record.getTradePrice();
            dto.setCurrentPrice(curPrice);//修改成交价
            dto.setTradeMaxPrice(dto.getTradeMaxPrice().compareTo(curPrice)>0?dto.getTradeMaxPrice():curPrice);//最大价格
            //最小价格大雨0并且最小价格小于当前价格 则最小价格为当前价格  否则最小价格为当前价格
            dto.setTradeMinPrice((dto.getTradeMinPrice().compareTo(BigDecimal.ZERO)>0 && dto.getTradeMinPrice().compareTo(curPrice)<0)
                                        ?dto.getTradeMinPrice():curPrice);//最小价格
            dto.setTradeMoney(MathUtil.add(dto.getTradeMoney(),MathUtil.mul(record.getTradeQuantity(),record.getTradePrice())));//成交金额
            dto.setTradeNums(MathUtil.add(dto.getTradeNums(),record.getTradeQuantity()));//成交量
            dto.setUsings(SwitchEnum.ON.getCode().equals(trade.getTradeSwitch()));
            BigDecimal rise = this.calcRise(dto.getCurrentPrice(), dto.getOpen());
            dto.setRecordTime(DateTimeUtils.currentDateTime());//记录时间
            dto.setRise(rise);
            if(dto.getBaseId()==null){
                dto.setBaseId(Long.valueOf(configService.queryValueByName(ConfigConstant.CONFIGNAME_BASE)));
                dto.setBasePriceName(currencyService.getNameById(dto.getBaseId()));
            }
            //基础币种价格
            dto.setBasePrice(this.getPriceByCurrencyId(dto.getCurrencyId()));

            //usdt价格
            dto.setUsdtPirce(searchPrice.getUsdtQcPrice());
            redisRepository.set(key, JSONObject.toJSONString(dto));
        }
    }

    /**
     * 查询交易对 交易信息
     *
     * @param trade
     * @return
     */
    @Override
    public TradeInfoDto getByCurrencyAndTradeType(CurrencyTradeModelDto trade) {
        TradeInfoDto dto =null;
        if (trade == null) {
            return new TradeInfoDto();
        }
        String key= KeyConstant.KEY_TRADEINFO_ID+DateTimeUtils.currentDate("MMdd")+"_"+trade.getId();
        String value = redisRepository.getString(key);
        if (StringUtils.isNotEmpty(value)) {
            dto = JSONObject.parseObject(value, TradeInfoDto.class);
            dto.setSearchType(1);//缓存查询
            dto.setUsings(SwitchEnum.ON.getCode().equals(trade.getTradeSwitch()));
        }else{
            dto = tradeRecordService.getTodayTrade(trade.getId());
            dto.setSearchType(0);//数据库查询
            dto.setArea(trade.getArea());
            //当前的成交价格
            BigDecimal currentTradePrice = tradeRecordService.getCurrentTradePrice(trade.getId());
            dto.setCurrentPrice(currentTradePrice);
            dto.setTradeId(trade.getId());
            dto.setCurrencyId(trade.getCurrencyId());
            dto.setCurrencyName(trade.getCurrencyName());
            dto.setTradeCurrencyId(trade.getTradeCurrencyId());
            dto.setTradeCurrencyName(trade.getTradeCurrencyName());
            //两个开关都是开表示开启
            dto.setUsings(SwitchEnum.ON.getCode().equals(trade.getTradeSwitch()));
            //今日开盘的价格
            BigDecimal openPrice = tradeRecordService.getDayOpenPrice(trade.getId());
            //计算涨跌幅
            BigDecimal rise = this.calcRise(dto.getCurrentPrice(), openPrice);
            dto.setOpen(openPrice);//开盘价
            dto.setRecordTime(DateTimeUtils.currentDateTime());//记录时间
            dto.setRise(rise);
            //基础币种的名称
            dto.setBaseId(Long.valueOf(configService.queryValueByName(ConfigConstant.CONFIGNAME_BASE)));
            dto.setBasePriceName(currencyService.getNameById(dto.getBaseId()));
            dto.setBasePrice(this.getPriceByCurrencyId(dto.getCurrencyId()));

            //usdt价格
            dto.setUsdtPirce(searchPrice.getUsdtQcPrice());
            //缓存一天
            redisRepository.set(key, JSONObject.toJSONString(dto), 25L, TimeUnit.HOURS);
        }
        return dto;
    }

    @Override
    public BigDecimal getOpenPrice(Long tradeId) {
        BigDecimal price=BigDecimal.ZERO;
        CurrencyTradeModelDto trade=currencyTradeService.getById(tradeId);
        if(trade!=null) {
            TradeInfoDto dto=this.getByCurrencyAndTradeType(trade);
            if(dto!=null && dto.getTradeId()>0){
                price=dto.getOpen();
            }
        }
        return price;
    }

    @Override
    public BigDecimal getPriceByTradeId(Long tradeId) {
        BigDecimal price=BigDecimal.ZERO;
        CurrencyTradeModelDto trade=currencyTradeService.getById(tradeId);
        if(trade!=null) {
            TradeInfoDto dto=this.getByCurrencyAndTradeType(trade);
            if(dto!=null && dto.getTradeId()>0){
                price=dto.getCurrentPrice();
            }
        }
        return price;
    }


    @Override
    public BigDecimal getPriceByCurrencyId(Long currencyId) {
        BigDecimal price=BigDecimal.ONE;
        Long baseId=Long.valueOf(configService.queryValueByName(ConfigConstant.CONFIGNAME_BASE));
        if(!currencyId.equals(baseId)){
            //是否有基础币种交易对
            CurrencyTradeModelDto trade=currencyTradeService.getByCurrencyId(baseId,currencyId);
            if(trade!=null) {
                price = getPriceByTradeId(trade.getId());
            } else {
                //获取其他交易对   currencyId/xxx
                List<Long> list = currencyTradeService.getTradeIdListByTradeCurrencyId(currencyId);
                if (list != null && list.size() > 0) {
                    for (Long id : list) {
                        trade = currencyTradeService.getById(id);
                        price = this.getPriceByTradeId(trade.getId());
                        BigDecimal tempPric = this.getPriceByCurrencyId(trade.getCurrencyId());
                        price = MathUtil.mul(price, tempPric);
                        if (price.compareTo(BigDecimal.ZERO) > 0) {
                            break;
                        }
                    }
                } else {
                    price = BigDecimal.ZERO;
                }
            }
        }
        return price;
    }

    @Override
    public Map<String, String> getAllPrice() {
        Map<String, String> allPrice=new HashMap<>();
        String value = redisRepository.getString(KeyConstant.KEY_ALL_PRICE);
        if(StringUtils.isNotEmpty(value)){
            allPrice=JSONObject.parseObject(value,Map.class);
        }else{
            List<CurrencyModelDto> list=currencyService.getAll();
            for(CurrencyModelDto currency:list){
                allPrice.put(currency.getId()+"",this.getPriceByCurrencyId(currency.getId()).toString());
            }
            redisRepository.set(KeyConstant.KEY_ALL_PRICE, JSONObject.toJSONString(allPrice), 10L, TimeUnit.SECONDS);
        }
        return allPrice;
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void tradeBuyWallet(Long recordId) {
        HintManager hintManager = HintManager.getInstance();
        hintManager.setMasterRouteOnly();
        TradeRecordModelDto record = tradeRecordService.getById(recordId);
        //买
        if (!WalletDoEnum.SUCCESS.getCode().equals(record.getBuyWalletStatus())) {
            CurrencyTradeModelDto trade = currencyTradeService.getById(record.getTradeId());
            TrusteeModel buy = trusteeService.getById(record.getBuyTrusteeId());
            if(buy!=null && !YesNoEnum.YES.getCode().equals(buy.getRobot())) {
                userWalletService.doBuyWallet(record.getBuyUserId(), trade.getCurrencyId(), trade.getTradeCurrencyId(), record.getTradeQuantity(), record.getBuyPrice(), record.getBuyCharge(), YesNoEnum.YES.getCode().equals(buy.getWhite()), recordId);
            }
            record.setBuyWalletStatus(WalletDoEnum.SUCCESS.getCode());

            tradeRecordService.update(record);
        }
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void tradeSellWallet(Long recordId) {
        HintManager hintManager = HintManager.getInstance();
        hintManager.setMasterRouteOnly();
        TradeRecordModelDto record = tradeRecordService.getById(recordId);
        //卖
        if (!WalletDoEnum.SUCCESS.getCode().equals(record.getSellWalletStatus())) {
            CurrencyTradeModelDto trade = currencyTradeService.getById(record.getTradeId());
            TrusteeModel sell = trusteeService.getById(record.getSellTrusteeId());
            if(sell!=null &&!YesNoEnum.YES.getCode().equals(sell.getRobot())) {
                userWalletService.doSellWallet(record.getSellUserId(), trade.getCurrencyId(), trade.getTradeCurrencyId(), record.getTradeQuantity(), record.getSellPrice(), record.getSellCharge(), YesNoEnum.YES.getCode().equals(sell.getWhite()), recordId);
            }
            record.setSellWalletStatus(WalletDoEnum.SUCCESS.getCode());

            tradeRecordService.update(record);
        }
    }


    /**
     * 计算涨跌幅
     *
     * @param newPrice
     * @param lastedPrice
     * @return
     */
    private BigDecimal calcRise(BigDecimal newPrice, BigDecimal lastedPrice) {
        if (lastedPrice.compareTo(BigDecimal.ZERO) <= 0) {
            return BigDecimal.ZERO;
        } else {
            //（当前价-开盘价）/开盘价 *100
            return MathUtil.mul(MathUtil.div(MathUtil.sub(newPrice, lastedPrice), lastedPrice, 4), new BigDecimal("100"));
        }
    }
}

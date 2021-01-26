package com.liuqi.business.trade;

import cn.hutool.log.Log;
import cn.hutool.log.dialect.log4j2.Log4j2LogFactory;
import com.alibaba.fastjson.JSONObject;
import com.liuqi.business.constant.KeyConstant;
import com.liuqi.business.constant.KeyConstant;
import com.liuqi.business.constant.LockConstant;
import com.liuqi.business.enums.BuySellEnum;
import com.liuqi.business.enums.SwitchEnum;
import com.liuqi.business.enums.TrusteeStatusEnum;
import com.liuqi.business.enums.WalletDoEnum;
import com.liuqi.business.model.CurrencyTradeModelDto;
import com.liuqi.business.model.TradeRecordModelDto;
import com.liuqi.business.model.TrusteeModel;
import com.liuqi.business.model.TrusteeModelDto;
import com.liuqi.business.service.*;
import com.liuqi.exception.BusinessException;
import com.liuqi.exception.TradeException;
import com.liuqi.mq.ChargeProducer;
import com.liuqi.mq.CurKDataProducer;
import com.liuqi.mq.ReleaseProducer;
import com.liuqi.mq.TradeWalletProducer;
import com.liuqi.mq.dto.ChargeDto;
import com.liuqi.mq.dto.KDataDto;
import com.liuqi.mq.dto.ReleaseDto;
import com.liuqi.mq.dto.TradeWalletDto;
import com.liuqi.redis.RedisRepository;
import com.liuqi.redis.lock.RedissonLockUtil;
import com.liuqi.utils.DateTimeUtils;
import io.shardingsphere.api.HintManager;
import org.apache.commons.lang3.StringUtils;
import org.redisson.api.RLock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * 交易处理总方法
 * 取消
 * 卖单
 * 买单
 */
@Component
public class TradeRequest {
    private static Log log = Log4j2LogFactory.get("trade");

    @Autowired
    private TrusteeService trusteeService;
    @Autowired
    private TradeService tradeService;
    @Autowired
    private CurrencyTradeService currencyTradeService;
    @Autowired
    private TradeWalletProducer tradeWalletProducer;
    @Autowired
    private RedisRepository redisRepository ;
    @Autowired
    private TradeInfoCacheService tradeInfoCacheService;
    @Autowired
    private CurKDataProducer curKDataProducer;
    @Autowired
    private ReleaseProducer releaseProducer;
    @Autowired
    private LockConfigService lockConfigService;
    @Autowired
    private ChargeProducer chargeProducer;
    public void cancel(Long orderId) {
        //强制使用主库
        HintManager hintManager = HintManager.getInstance();
        hintManager.setMasterRouteOnly();
        if (orderId != null && orderId > 0) {
            TrusteeModelDto trusteeOrder = trusteeService.getById(orderId);
            if (trusteeOrder == null ) {
                //log.info("mq处理 订单异常"+trusteeOrder.getId());
                return;
            }
            if ( !TrusteeStatusEnum.WAIT.getCode().equals(trusteeOrder.getStatus())) {
                //log.info("mq处理 订单已处理"+trusteeOrder.getId());
                return;
            }

            //交易对信息 获取当前订单交易对信息 是哪个币种和哪个币种交易
            CurrencyTradeModelDto trade = currencyTradeService.getById(trusteeOrder.getTradeId());
            if (BuySellEnum.BUY.getCode().equals(trusteeOrder.getTradeType())) {
                //log.info("mq处理订单处理买单取消-----》");
                tradeService.doCancelBuyTrade(trade, trusteeOrder.getId());
            } else {
                //log.info("mq处理订单处理卖单取消-----》");
                tradeService.doCancelSellTrade(trade, trusteeOrder.getId());
            }
        }
    }


    /**
     * 撮合处理
     *
     * @param tradeId
     */
    public void request(Long tradeId) {
        //强制使用主库
        HintManager hintManager = HintManager.getInstance();
        hintManager.setMasterRouteOnly();
        //只允许一个在跑  如果
        String key = KeyConstant.KEY_TRADE_LASTTIME + tradeId;
        String runTime=redisRepository.getString(key);
        if(StringUtils.isNotEmpty(runTime)){
            return;
        }

        boolean runSwitch=true;
        while(runSwitch) {
            //判断开关
            String tradeSwitchStr = redisRepository.getString(KeyConstant.KEY_TRADE_SWITCH + tradeId);
            runSwitch =SwitchEnum.isOn(tradeSwitchStr);
            /**  */
            if(!runSwitch){
                continue;
            }

            try {
                //修改撮合时间   覆盖缓存30秒
                redisRepository.set(key, DateTimeUtils.currentDateTime(), 30L, TimeUnit.SECONDS);
                //交易对信息 获取当前订单交易对信息 是哪个币种和哪个币种交易
                CurrencyTradeModelDto trade = currencyTradeService.getById(tradeId);
                //如果交易对未启用  休眠5秒再去判断
                if (CurrencyTradeModelDto.STATUS_STOP == trade.getStatus()) {
                    try {
                        Thread.sleep(5000L);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    continue;
                }
                //判断是否有撮合  -- tradeId对应的对象里面买单卖单都有
                boolean hasTrade = tradeInfoCacheService.canTrade(tradeId);
                if (hasTrade) {
                    //撮合
                    this.doOrder(trade);
                    //推送交易数据
                    //tradeTopic.sendMessage(tradeId);
                }else{
                    try {
                        Thread.sleep(100L);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    private void doOrder(CurrencyTradeModelDto trade) {
        //错误类型
        String key=KeyConstant.KEY_TRADE_ERROR_TIME+ DateTimeUtils.currentDate("MMdd")+trade.getId();
        String value=redisRepository.getString(key);
        Map<Long,Integer> errorTimes=new HashMap<Long,Integer>();
        if(StringUtils.isNotEmpty(value)){
            errorTimes= JSONObject.parseObject(value,Map.class);
        }else{
            redisRepository.set(key,JSONObject.toJSONString(errorTimes),1L, TimeUnit.DAYS);
        }
        boolean isEnd = false;
        //状态为待交易时
        while (!isEnd) {
            RLock lock1 = null;
            RLock lock2 = null;
            try {
                HintManager hintManager = HintManager.getInstance();
                hintManager.setMasterRouteOnly();
                //查询买一和卖一数据
                TrusteeModelDto buyOrder = trusteeService.findFirstBuy(trade.getId());
                TrusteeModelDto sellOrder = trusteeService.findFirstSell(trade.getId());
                if (buyOrder != null && sellOrder != null) {
                    //交易类型   买时间>=卖的时间  则为买  否则为卖
                    Integer tradeType = buyOrder.getCreateTime().compareTo(sellOrder.getCreateTime()) >= 0 ? BuySellEnum.BUY.getCode() : BuySellEnum.SELL.getCode();
                    //获取2个锁   防止死锁   每次都获取id小的那个
                    Long id1 = buyOrder.getId();
                    Long id2 = sellOrder.getId();
                    if (buyOrder.getId() > sellOrder.getId()) {
                        id1 = sellOrder.getId();
                        id2 = buyOrder.getId();
                    }
                    //获取锁
                    lock1 = RedissonLockUtil.lock(LockConstant.LOCK_TRUSTEE_ID + id1);
                    lock2 = RedissonLockUtil.lock(LockConstant.LOCK_TRUSTEE_ID + id2);
                    if (buyOrder.getPrice().compareTo(sellOrder.getPrice()) >= 0) {
                        TradeRecordModelDto record = tradeService.doTrade(trade, buyOrder.getId(), sellOrder.getId(), tradeType);
                        if (record != null && record.getId() > 0) {
                            //设置交易对信息  放在缓存中
                            tradeService.doTradeInfo(record);
                            //缓存数据
                            tradeInfoCacheService.tradeRecordCache(record);
                            //异步 处理钱
                            tradeWalletProducer.sendMessage(new TradeWalletDto(record.getId(),
                                    WalletDoEnum.SUCCESS.getCode().equals(record.getBuyWalletStatus()),
                                    WalletDoEnum.SUCCESS.getCode().equals(record.getSellWalletStatus())));
                            //实时K线
                            curKDataProducer.curKData(new KDataDto(record.getTradeId(), record.getTradePrice(), record.getTradeQuantity()));

                            //上级手续费奖励
                            chargeProducer.chargeMessage(new ChargeDto(record.getId()));

                            //买卖释放
                            if(lockConfigService.isLock(record.getTradeId())){
                                releaseProducer.sendBuyMessage(new ReleaseDto(record.getId(), record.getTradeId()));
                                releaseProducer.sendSellMessage(new ReleaseDto(record.getId(), record.getTradeId()));
                            }
                        }
                    }
                }else{
                    //没有数据进入    说明缓存异常  清理缓存
                    tradeInfoCacheService.syncInfo(trade.getId());
                }
                isEnd = true;
                //订单异常的  达到一定次数时修改为异常单
            }catch (TradeException e){
                log.error(e.getMessage());
                e.printStackTrace();
                Long errId=e.getTradeId();
                //获取订单错误次数  错误次数大于3次的改为异常
                if(errId!=null && errId>0) {
                    Integer times = errorTimes.get(errId);
                    times = times == null ? 0 : times;
                    errorTimes.put(errId , times+1);
                    redisRepository.set(key,JSONObject.toJSONString(errorTimes));
                    if (times > 3) {
                        //查询订单
                        TrusteeModel trusteeOrder = trusteeService.getById(errId);
                        trusteeOrder.setRemark(e.getMessage());
                        trusteeOrder.setStatus(TrusteeStatusEnum.ERROR.getCode());
                        trusteeService.update(trusteeOrder);

                        //清除缓存数据
                        tradeInfoCacheService.cancelCache(trusteeOrder.getId());
                        isEnd = true;
                    }
                }
            }catch (Exception e){
                log.error(e.getMessage());
                throw new BusinessException(e.getMessage());
            } finally {
                RedissonLockUtil.unlock(lock1);
                RedissonLockUtil.unlock(lock2);
            }
        }
    }
}

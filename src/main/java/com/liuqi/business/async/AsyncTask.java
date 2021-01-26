package com.liuqi.business.async;

import com.liuqi.business.constant.KeyConstant;
import com.liuqi.business.constant.LockConstant;
import com.liuqi.business.dto.chain.ExtractSearchDto;
import com.liuqi.business.enums.ExtractMoneyEnum;
import com.liuqi.business.enums.ExtractSearchStatusEnum;
import com.liuqi.business.model.CurrencyModel;
import com.liuqi.business.model.ExtractModel;
import com.liuqi.business.service.*;
import com.liuqi.business.trade.TradeRequest;
import com.liuqi.mq.TradeTopic;
import com.liuqi.redis.RedisRepository;
import com.liuqi.redis.lock.RedissonLockUtil;
import com.liuqi.utils.DateTimeUtils;
import com.liuqi.utils.IpUtils;
import org.apache.commons.lang3.StringUtils;
import org.redisson.api.RLock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 注意事项：
 如下方式会使@Async失效
 一、异步方法使用static修饰
 二、异步类没有使用@Component注解（或其他注解）导致spring无法扫描到异步类
 三、异步方法不能与调用异步方法的方法在同一个类中
 四、类中需要使用@Autowired或@Resource等注解自动注入，不能自己手动new对象
 五、如果使用SpringBoot框架必须在启动类中增加@EnableAsync注解
 六、在Async 方法上标注@Transactional是没用的。 在Async 方法调用的方法上标注@Transactional 有效。
 */
@Component("asyncTask")
@Async
public class AsyncTask {
    @Autowired
    private TradeInfoCacheService tradeInfoCacheService;
    @Autowired
    private SuperNodeSendService superNodeSendService;
    @Autowired
    private TradeTopic tradeTopic;
    @Autowired
    private AutoExtractService autoExtractService;
    @Autowired
    private ExtractService extractService;
    @Autowired
    private CtcOrderService ctcOrderService;
    @Autowired
    private TradeRequest tradeRequest;
    @Autowired
    private LockReleaseService lockReleaseService;
    @Autowired
    private AutoRechargeService autoRechargeService;
    @Autowired
    private RedisRepository redisRepository;
    @Autowired
    private UserAdminLoginService userAdminLoginService;
    /**
     * 同步缓存数据
     *
     * @param tradeId
     */
    public void syncInfoTask(Long tradeId) {
        tradeInfoCacheService.syncInfo(tradeId);
    }

    public void extractTask(List<String> queryIds, String thridCurrency) {
        List<ExtractSearchDto> queryList = autoExtractService.queryExtractInfo(queryIds, thridCurrency);
        if (queryList != null && queryList.size() > 0) {
            for (ExtractSearchDto dto : queryList) {
                ExtractModel model = extractService.getById(Long.valueOf(dto.getOrderNo()));
                if (model != null) {
                    RLock rLock=null;
                    String key= LockConstant.LOCK_EXTRACT_ORDER+model.getId();
                    try {
                        rLock=RedissonLockUtil.lock(key);
                        if(ExtractSearchStatusEnum.SUCCESS.getCode().equals(dto.getStatus())) {
                            extractService.doSuccess(model, dto);
                        }else if(ExtractSearchStatusEnum.CANCEL.getCode().equals(dto.getStatus())){
                            extractService.doWait(model, dto);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        model.setRemark(e.getMessage());
                        model.setStatus(ExtractMoneyEnum.APPLY_ERROR.getCode());
                        extractService.update(model);
                    }finally {
                        RedissonLockUtil.unlock(rLock);
                    }
                }
            }
        }
    }

    /**
     * 超级节点分红
     * @param id
     */
    public void superNodeRealse(Long id) {
        String key = LockConstant.LOCK_SUPER_RELEASE + id;
        RLock lock = null;
        try {
            //获取订单锁
            lock=RedissonLockUtil.lock(key);
            superNodeSendService.recordRelease(id);
        }catch (Exception e) {
            e.printStackTrace();
        } finally {
            RedissonLockUtil.unlock(lock);
        }
    }

    public void pushInfoTask(Long tradeId) {
        tradeTopic.sendTradeMessage(tradeId);
    }

    /**
     * ctc接单
     *
     * @param orderId
     */
    public void ctcMatch(Long orderId) {
        String key = LockConstant.LOCK_CTC_ORDER + orderId;
        RLock lock = null;
        try {
            lock = RedissonLockUtil.lock(key);
            ctcOrderService.matchStore(orderId);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            RedissonLockUtil.unlock(lock);
        }
    }

    /**
     * 取消订单
     * @param orderId
     */
    public void tradeCancel(Long orderId, Long userId,Long tradeId) {
        String userKey = LockConstant.LOCK_WALLET_USER + userId+"_"+tradeId;
        String key = LockConstant.LOCK_TRUSTEE_ID + orderId;
        RLock userLock = null;
        RLock lock = null;
        try {
            //获取用户取消锁  同一用户大批量取消操作钱包出现死锁
            userLock = RedissonLockUtil.lock(userKey);
            //获取订单锁
            lock=RedissonLockUtil.lock(key);
            tradeRequest.cancel(orderId);
            //取消操作缓存修改
            tradeInfoCacheService.cancelCache(orderId);
        }catch (Exception e) {
            e.printStackTrace();
        } finally {
            RedissonLockUtil.unlock(userLock);
            RedissonLockUtil.unlock(lock);
        }
    }

    /**
     * 超级节点分红
     * @param id
     */

    /**
     * 锁仓释放
     *
     * @param recordId
     */
    public void receiveLockRelease(Long recordId,Integer tradeType) {
        //释放id
        Long releaseId=lockReleaseService.createRelease(recordId,tradeType);
        //执行一次发放
        if(releaseId!=null && releaseId>0){
            String key = LockConstant.LOCK_RELEASE_SEND + releaseId;
            RLock lock = null;
            try {
                lock=RedissonLockUtil.lock(key);
                lockReleaseService.recordRelease(releaseId);
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                RedissonLockUtil.unlock(lock);
            }
        }
    }

    public void searchRecharge(CurrencyModel currency, Integer protocol, String thirdCurrency,Integer confirm) {
        String key = KeyConstant.KEY_RECHARGE + currency.getId() + thirdCurrency;
        if (!redisRepository.hasKey(key)) {
            redisRepository.set(key, DateTimeUtils.currentDateTime() + "", 10L, TimeUnit.MINUTES);
            try {
                autoRechargeService.check(currency, protocol,thirdCurrency,confirm);
            } catch (Exception e) {
                e.printStackTrace();
            }
            redisRepository.del(key);
        }
    }

    public void addAdminLoginLog(HttpServletRequest request, String loginName, String remark){
        if(StringUtils.isEmpty(remark)){
            remark="-";
        }
        String ip= IpUtils.getIpAddr(request);
        String city=IpUtils.getCity(ip);
        userAdminLoginService.addLog(loginName,ip,city,remark);
    }
}

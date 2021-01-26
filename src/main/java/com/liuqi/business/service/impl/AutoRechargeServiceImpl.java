package com.liuqi.business.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpUtil;
import cn.hutool.log.Log;
import cn.hutool.log.dialect.log4j2.Log4j2LogFactory;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.liuqi.business.constant.KeyConstant;
import com.liuqi.business.constant.KeyConstant;
import com.liuqi.business.dto.chain.ThirdCollectDto;
import com.liuqi.business.dto.chain.TxInfo;
import com.liuqi.business.enums.ProtocolEnum;
import com.liuqi.business.enums.RechargeSendTypeEnum;
import com.liuqi.business.enums.SwitchEnum;
import com.liuqi.business.model.*;
import com.liuqi.business.service.*;
import com.liuqi.exception.BusinessException;
import com.liuqi.redis.RedisRepository;
import com.liuqi.response.ReturnResponse;
import com.liuqi.utils.SignUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class AutoRechargeServiceImpl implements AutoRechargeService {
    private static Log log= Log4j2LogFactory.get("auto");
    @Autowired
    private UserRechargeAddressService userRechargeAddressService;
    @Autowired
    private CurrencyService currencyService;
    @Autowired
    private RechargeService rechargeService;
    @Autowired
    private ReConfigService reConfigService;
    @Autowired
    private RedisRepository redisRepository;
    @Autowired
    private CurrencyConfigService currencyConfigService;
    @Autowired
    private MessageService messageService;
    @Override
    @Transactional
    public void check(CurrencyModel currency,Integer protocol, String thirdCurrency,Integer confirm) {
        Long currencyId = currency.getId();
        //log.info("查询到账-->" + thirdCurrency);
        CurrencyConfigModelDto config = currencyConfigService.getByCurrencyId(currencyId);
        //未开放充值  不做查询
        if (!SwitchEnum.isOn(config.getRechargeSwitch())) {
            log.info("查询到账-->" + thirdCurrency + "未开放，不查询");
            return;
        }
        //最小充值数量
        BigDecimal minRecharge = config.getRechargeMinQuantity();
        //EOS 处理
        String fromAddress = "";
        if (ProtocolEnum.EOS.getCode().equals(currency.getProtocol())
                ||ProtocolEnum.XRP.getCode().equals(currency.getProtocol())) {
            fromAddress = config.getRechargeAddress();
            if (StringUtils.isEmpty(fromAddress)) {
                log.info("查询到账-->" + thirdCurrency + ",充值地址未配置：");
                return;
            }
        }
        if (StringUtils.isEmpty(currency.getThirdCurrency())) {
            log.info("查询到账-->" + thirdCurrency + "未配置接口参数");
            return;
        }
        //查询最新的区块
        Long curBlock = this.block(currency,thirdCurrency);
        //实际能查询的最高区块 =查询最新区块减去确认区块的数量
        curBlock = curBlock - confirm;
        //获取最新的区块信息
        Long searchBlock = redisRepository.getLong(KeyConstant.KEY_BLOCK +thirdCurrency);
        //没有查询过的 直接查询当前前5个区块信息
        searchBlock = searchBlock > 0 ? searchBlock : curBlock - 5;
        if (searchBlock <= 0) {
            log.info("查询到账-->" + thirdCurrency + "区块异常：" + searchBlock);
            return;
        }
        Long error = 0L;
        Long endBlock=0L;
        //控制每次只查询300次  以免任务一直在跑
        int maxCount=300;
        while (searchBlock < curBlock) {
            try {
                if(maxCount<=0){
                    break;
                }
                //是否有停止标识
                if(redisRepository.hasKey(KeyConstant.KEY_RECHARGE_SEARCH_STOP+currency.getId())){
                    break;
                }
                if (ProtocolEnum.EOS.getCode().equals(currency.getProtocol())
                        ||ProtocolEnum.XRP.getCode().equals(currency.getProtocol())) {
                    endBlock=curBlock;
                }
                this.recharge(currency,protocol,thirdCurrency, searchBlock,endBlock, minRecharge, fromAddress);
                if (ProtocolEnum.EOS.getCode().equals(currency.getProtocol())
                        ||ProtocolEnum.XRP.getCode().equals(currency.getProtocol())) {
                    redisRepository.set(KeyConstant.KEY_BLOCK + thirdCurrency, endBlock);
                    searchBlock = endBlock;
                }else{
                    redisRepository.set(KeyConstant.KEY_BLOCK + thirdCurrency, searchBlock);
                    searchBlock = searchBlock + 1;
                }
                maxCount--;
            } catch (Exception e) {
                e.printStackTrace();
                error = redisRepository.incrOne(KeyConstant.KEY_BLOCK_ERROR +thirdCurrency);
                if (error > 3) {
                    messageService.insertMessage(0L, "区块查询异常" + thirdCurrency + "，跳过" + searchBlock);
                    searchBlock = searchBlock + 1;
                }
            }
        }
    }

    @Override
    @Transactional
    public void checkBlock(Long currencyId,Integer protocol, String thirdCurrency, Long block) {
        CurrencyModel currency = currencyService.getById(currencyId);
        //log.info("查询到账-->" + thirdCurrency );
        CurrencyConfigModel config = currencyConfigService.getByCurrencyId(currencyId);
        //未开放充值  不做查询
        if (!SwitchEnum.isOn(config.getRechargeSwitch())) {
            log.info("查询到账-->" + thirdCurrency  + "未开放，不查询");
            return;
        }
        //EOS 处理
        String fromAddress = "";
        if (ProtocolEnum.EOS.getCode().equals(currency.getProtocol())
                ||ProtocolEnum.XRP.getCode().equals(currency.getProtocol())) {
            fromAddress = config.getRechargeAddress();
            if (StringUtils.isEmpty(fromAddress)) {
                log.info("查询到账-->" + thirdCurrency  + ",充值地址未配置：");
                return;
            }
        }
        if (StringUtils.isEmpty(currency.getThirdCurrency())) {
            log.info("查询到账-->" + thirdCurrency + "未配置接口参数");
            return;
        }
        //最小充值数量
        BigDecimal minRecharge = config.getRechargeMinQuantity();
        //获取最新的区块信息
        Long curSearchBlock = redisRepository.getLong(KeyConstant.KEY_BLOCK + thirdCurrency);
        if (block <= 0 || block > curSearchBlock) {
            log.info("查询到账-->" + thirdCurrency  + "区块异常：" + block);
            return;
        }
        this.recharge(currency,protocol,thirdCurrency, block,block+1L, minRecharge, fromAddress);
    }

    /**
     * 充值封装
     *
     * @param currency
     * @param block
     * @param minRecharge
     */
    private void recharge(CurrencyModel currency,Integer protocol,String thirdCurrency, Long block,Long endBlock, BigDecimal minRecharge, String fromAddress) {
        log.info("查询到账-->" + thirdCurrency  + "区块高度：" + block);
        List<TxInfo> list = this.getByBlock(thirdCurrency, block,endBlock, fromAddress);
        if (list != null && list.size() > 0) {
            for (TxInfo tx : list) {
                //查询地址是否是本地地址
                Long userId = userRechargeAddressService.findBindingUserIdByAddress(tx.getToAddress(), protocol);
                if (userId != null && userId > 0
                        && tx.getQuantity().compareTo(minRecharge) >= 0 && !rechargeService.existRemark(currency.getId(), userId, tx.getHash())) {
                    //4完成充值到账动作
                    rechargeService.autoRecharge(userId, currency.getId(), tx.getQuantity(), tx.getToAddress(), tx.getHash(), tx.getTime(), protocol);
                }
            }
        }
    }


    @Override
    public Long block(CurrencyModel currency, String thirdCurrency) {
        Long block = 0L;
        ReConfigModelDto config = reConfigService.getConfig();
        String url = config.getUrl() + "/api/recharge/searchCurBlock";
        HttpRequest request = HttpUtil.createGet(url);
        Map<String, Object> params = new HashMap<>();
        params.put("storeNo", config.getStoreNo());
        params.put("currency", thirdCurrency);
        String sign = SignUtils.signData(params, config.getKey());
        params.put("sign", sign);
        String result = request.form(params).execute().body();
        if (StringUtils.isNotEmpty(result)) {
            System.out.println("back-->"+(result.length()>50?result.substring(0,50):result));
            ReturnResponse re = JSONObject.parseObject(result, ReturnResponse.class);
            if (re.getCode() == ReturnResponse.RETURN_OK) {
                block = Long.valueOf(re.getObj().toString());
            }else{
                System.out.println("error"+re.getMsg());
            }
        }
        return block;
    }

    @Override
    public List<TxInfo> getByBlock(String thirdCurrency, Long block,Long endBlock, String fromAddress) {
        List<TxInfo> list = null;
        ReConfigModelDto config = reConfigService.getConfig();
        String url = config.getUrl() + "/api/recharge/searchByBlock";
        HttpRequest request = HttpUtil.createGet(url);
        Map<String, Object> params = new HashMap<>();
        params.put("storeNo", config.getStoreNo());
        params.put("block", block);
        params.put("endBlock", endBlock);
        params.put("currency", thirdCurrency);
        params.put("fromAddress", fromAddress);
        String sign = SignUtils.signData(params, config.getKey());
        params.put("sign", sign);
        String result = request.form(params).execute().body();
        if (StringUtils.isNotEmpty(result)) {
            System.out.println("back-->"+(result.length()>50?result.substring(0,50):result));
            ReturnResponse re = JSONObject.parseObject(result, ReturnResponse.class);
            if (re.getCode() == ReturnResponse.RETURN_OK) {
                list = JSONArray.parseArray(re.getObj().toString(), TxInfo.class);
            }else{
                System.out.println("error"+re.getMsg());
            }
        }
        return list;
    }

    @Override
    @Transactional
    public void send(RechargeModel rechargeModel) {
        log.info("推送充值请求" + rechargeModel.getId());
        CurrencyModel currency = currencyService.getById(rechargeModel.getCurrencyId());
        String thirdCurrency = currencyService.getThirdCurrency(currency,rechargeModel.getProtocol());
        if (StringUtils.isNotEmpty(thirdCurrency) && rechargeModel.getHash().length() > 10) {
            ReConfigModelDto config = reConfigService.getConfig();
            String url = config.getUrl() + "/api/collect/push";
            HttpRequest request = HttpUtil.createGet(url);
            ThirdCollectDto collect = new ThirdCollectDto();
            collect.setStoreNo(config.getStoreNo());
            collect.setNo(rechargeModel.getId() + "");
            collect.setQuantity(rechargeModel.getQuantity());
            collect.setCurrency(thirdCurrency);
            collect.setAddress(rechargeModel.getAddress());
            collect.setMemo("");
            collect.setSign("");
            //私钥
            Map<String, Object> params = BeanUtil.beanToMap(collect);
            String sign = SignUtils.signData(params, config.getKey());
            params.put("sign", sign);
            log.info("推送充值请求" + rechargeModel.getId() + ",--参数-->" + params);
            String result = request.form(params).execute().body();
            log.info("推送充值请求" + rechargeModel.getId() + ",--返回-->" + result);
            if (StringUtils.isNotEmpty(result)) {
                System.out.println("back-->"+(result.length()>50?result.substring(0,50):result));
                ReturnResponse re = JSONObject.parseObject(result, ReturnResponse.class);
                if (re.getCode() == ReturnResponse.RETURN_OK) {
                    rechargeModel.setSendType(RechargeSendTypeEnum.SENDED.getCode());
                    rechargeService.update(rechargeModel);
                    return;
                }else{
                    System.out.println("error"+re.getMsg());
                }
                throw new BusinessException("推送充值异常" + re.getMsg());
            } else {
                throw new BusinessException("推送充值异常");
            }
        }
    }
}

package com.liuqi.business.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.google.common.base.Strings;
import com.liuqi.business.constant.LockConstant;
import com.liuqi.business.dto.PriceNumsDto;
import com.liuqi.business.dto.TradeInfoDto;
import com.liuqi.business.dto.api.AllWalletDto;
import com.liuqi.business.dto.api.TradeQuotationDto;
import com.liuqi.business.dto.api.request.*;
import com.liuqi.business.dto.api.response.OrderRecordRespDto;
import com.liuqi.business.dto.api.response.OrderRespDto;
import com.liuqi.business.dto.api.response.RecordRespDto;
import com.liuqi.business.dto.api.response.WalletRespDto;
import com.liuqi.business.enums.BuySellEnum;
import com.liuqi.business.enums.YesNoEnum;
import com.liuqi.business.enums.api.ApiResultEnum;
import com.liuqi.business.model.*;
import com.liuqi.business.service.*;
import com.liuqi.exception.BusinessException;
import com.liuqi.redis.lock.RedissonLockUtil;
import com.liuqi.response.APIResult;
import com.liuqi.response.DataResult;
import com.liuqi.utils.MathUtil;
import org.redisson.api.RLock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.util.List;

/**
 * description: OpenApiServiceImpl <br>
 * date: 2020/5/20 15:53 <br>
 * author: chenX <br>
 * version: 1.0 <br>
 */
@Service
public class OpenApiServiceImpl implements OpenApiService {

    private CurrencyTradeService currencyTradeService;
    private TradeRecordService tradeRecordService;
    private TradeInfoCacheService tradeInfoCacheService;
    private TradeService tradeService;
    private TrusteeService trusteeService;
    private UserWalletService userWalletService;
    private CurrencyService currencyService;
    private UserWalletLogService userWalletLogService;
    private UserService userService;
    private UserApiKeyService userApiKeyService;
    @Autowired
    public OpenApiServiceImpl(CurrencyTradeService currencyTradeService, TradeRecordService tradeRecordService,
                              TradeInfoCacheService tradeInfoCacheService,TradeService tradeService,
                              TrusteeService trusteeService, UserWalletService userWalletService,
                              CurrencyService currencyService,UserWalletLogService userWalletLogService,
                              UserApiKeyService userApiKeyService,
                              UserService userService) {
        this.currencyTradeService = currencyTradeService;
        this.tradeRecordService = tradeRecordService;
        this.tradeInfoCacheService = tradeInfoCacheService;
        this.tradeService = tradeService;
        this.trusteeService = trusteeService;
        this.userWalletService = userWalletService;
        this.currencyService = currencyService;
        this.userWalletLogService = userWalletLogService;
        this.userService = userService;
        this.userApiKeyService=userApiKeyService;
    }


    @Override
    public APIResult symbols() {
        return APIResult.success(currencyTradeService.queryListByDto(null,false));
    }

    @Override
    public APIResult trade(String symbol) {
        CurrencyTradeModelDto currencyTradeModelDto = checkTrade(symbol);
        return APIResult.success(tradeRecordService.findRecordList(currencyTradeModelDto.getId(), 20));
    }


    @Override
    public APIResult depth(String symbol,Integer gear) {
        CurrencyTradeModelDto currencyTradeModelDto = checkTrade(symbol);

        return APIResult.success(tradeInfoCacheService.getTradeDepthInfo(currencyTradeModelDto.getId(),gear));
    }





    @Override
    public APIResult quotation(String symbol) {
        CurrencyTradeModelDto currencyTradeModelDto = checkTrade(symbol);
        TradeInfoDto tradeInfoDto = tradeService.getByCurrencyAndTradeType(currencyTradeModelDto);
        return APIResult.success(TradeQuotationDto.builder()
                .amount(MathUtil.format(tradeInfoDto.getTradeNums()))
                .currentPrice(MathUtil.format(tradeInfoDto.getCurrentPrice()))
                .openPrice(MathUtil.format(tradeInfoDto.getOpen()))
                .tradeMaxPrice(MathUtil.format(tradeInfoDto.getTradeMaxPrice()))
                .tradeMinPrice(MathUtil.format(tradeInfoDto.getTradeMinPrice())).build());
    }

    private CurrencyTradeModelDto checkTrade(String symbol){
        Assert.isTrue(!Strings.isNullOrEmpty(symbol), "symbol is null");
        String[] split = symbol.split("-");
        CurrencyTradeModelDto currencyTradeModelDto = currencyTradeService.getByCurrencyName(split[1], split[0]);
        Assert.notNull(currencyTradeModelDto, "trade does not exist");
        return  currencyTradeModelDto;
    }

    private Long getCurrencyId(String currencyName){
        CurrencyModelDto currency=currencyService.getByName(currencyName);
        return currency!=null?currency.getId():-1L;
    }


    @Override
    public APIResult<?> wallet(WalletDto wallet) {
        if(StringUtils.isEmpty(wallet.getCurrency())) {
            List<UserWalletModelDto> list=userWalletService.getByUserId(wallet.getUserId(),null);
            return APIResult.success(WalletRespDto.transferList(list));
        }else{
            Long currencyId = getCurrencyId(wallet.getCurrency());
            UserWalletModelDto userWallet = userWalletService.getByUserAndCurrencyId(wallet.getUserId(), currencyId);
            WalletRespDto respDto = null;
            if (userWallet != null) {
                respDto=WalletRespDto.transfer(wallet.getCurrency(),userWallet);
            }
            return APIResult.success(respDto);
        }
    }


    @Override
    public APIResult<?> record(RecordDto record) {
        Long currencyId=this.getCurrencyId(record.getCurrency());
        if(currencyId<=0){
            return APIResult.fail(ApiResultEnum.ERROR_CURRENCY,null);
        }
        if(record.getPageSize()>20){
            record.setPageSize(20);
        }
        UserWalletLogModelDto search=new UserWalletLogModelDto();
        search.setUserId(record.getUserId());
        search.setCurrencyId(currencyId);
        search.setStartCreateTime(record.getStartTime());
        search.setEndCreateTime(record.getEndTime());
        search.setType(record.getType());
        DataResult<UserWalletLogModelDto> page=userWalletLogService.queryPageByDto(search,record.getPageNum(),record.getPageSize());
        List<RecordRespDto> list=RecordRespDto.transfer(record.getCurrency(),page.getData());
        return APIResult.success(new DataResult<>(page.getCount(),list));
    }


    @Override
    public APIResult<?> cancel(OrderDto order) {
        trusteeService.cancel(order.getOrderId(), order.getUserId(), true);
        return APIResult.success();
    }

    @Override
    public APIResult<?> queryOrder(OrderDto order) {
        TrusteeModelDto queryOrder=trusteeService.getById(order.getOrderId());
        if(queryOrder==null){
            return APIResult.fail(ApiResultEnum.ERROR_ORDER_NOT_EXITS,null);
        }
        if(!queryOrder.getUserId().equals(order.getUserId())){
            return APIResult.fail(ApiResultEnum.ERROR_PERMISSION,null);
        }
        CurrencyTradeModelDto trade=currencyTradeService.getById(queryOrder.getTradeId());
        String symbol=trade.getTradeCurrencyName()+"-"+trade.getCurrencyName();
        return APIResult.success(OrderRespDto.transfer(symbol,queryOrder));
    }

    @Override
    public APIResult<?> queryOrderList(OrderListDto orderList) {
        if(orderList.getPageSize()>20){
            orderList.setPageSize(20);
        }
        CurrencyTradeModelDto trade = checkTrade(orderList.getSymbol());
        TrusteeModelDto search=new TrusteeModelDto();
        search.setUserId(orderList.getUserId());
        search.setTradeId(trade.getId());
        search.setStatus(orderList.getStatus());
        search.setStartCreateTime(orderList.getStartTime());
        search.setEndCreateTime(orderList.getEndTime());
        DataResult<TrusteeModelDto> page=trusteeService.queryPageByDto(search,orderList.getPageNum(),orderList.getPageSize());
        List<OrderRespDto> list=OrderRespDto.transferList(orderList.getSymbol(),page.getData());
        return APIResult.success(new DataResult<>(page.getCount(),list));
    }



    @Override
    public APIResult<?> allWallet(WalletDto wallet, HttpServletRequest request) {
        Assert.isTrue(!Strings.isNullOrEmpty(wallet.getCurrency()), "currency is null");
        CurrencyModelDto currency = currencyService.getByName(wallet.getCurrency());
        Assert.notNull(currency,"currency does not exist");
        String apiKey=request.getHeader("apiKey");
        UserApiKeyModelDto userApi=userApiKeyService.getByApiKey(apiKey);
        if(!userApi.getCurrencyId().equals(currency.getId())){
             throw new BusinessException("currency error");
        }
        List<AllWalletDto> list=userWalletService.allWallet(currency.getId());
        return APIResult.success(list);
    }

    @Override
    public APIResult<?> queryOrderRecordList(OrderRecordDto orderRecord) {
        if(orderRecord.getPageSize()>20){
            orderRecord.setPageSize(20);
        }
        CurrencyTradeModelDto trade = checkTrade(orderRecord.getSymbol());
        TradeRecordModelDto search=new TradeRecordModelDto();
        if(BuySellEnum.SELL.getCode().equals(orderRecord.getTradeType())){
            search.setSellUserId(orderRecord.getUserId());
        }else{
            search.setBuyUserId(orderRecord.getUserId());
        }
        search.setTradeId(trade.getId());
        search.setStartCreateTime(orderRecord.getStartTime());
        search.setEndCreateTime(orderRecord.getEndTime());
        DataResult<TradeRecordModelDto> page=tradeRecordService.queryPageByDto(search,orderRecord.getPageNum(),orderRecord.getPageSize());
        List<OrderRecordRespDto> list=OrderRecordRespDto.transferList(orderRecord.getSymbol(),orderRecord.getUserId(),page.getData());
        return APIResult.success(new DataResult<>(page.getCount(),list));
    }

    @Override
    public APIResult<?> publish(OrderPublishDto order) {
        CurrencyTradeModelDto trade = checkTrade(order.getSymbol());

        TrusteeModelDto trusteeModel=new TrusteeModelDto();
        trusteeModel.setTradeId(trade.getId());
        trusteeModel.setQuantity(order.getQuantity());
        trusteeModel.setPrice(order.getPrice());
        //传入的类型强制为0和1
        trusteeModel.setTradeType(order.getTradeType() >= 1 ? 1 : 0);
        if (order.getTransactionType()==1){
            if (trusteeModel.getTradeType().equals(BuySellEnum.BUY.getCode())){
                TrusteeModelDto firstSell = trusteeService.findFirstSell(trusteeModel.getTradeId());
                Assert.notNull(firstSell,"价格获取失败");
                trusteeModel.setPrice(firstSell.getPrice());
            }else {
                TrusteeModelDto firstBuy = trusteeService.findFirstBuy(trusteeModel.getTradeId());
                Assert.notNull(firstBuy,"价格获取失败");
                trusteeModel.setPrice(firstBuy.getPrice());
            }
        }
        if (trusteeModel.getPrice().compareTo(BigDecimal.ZERO) <= 0) {
            return APIResult.fail(ApiResultEnum.ERROR_PRICE_NOT_EXITS,"价格小于0");
        }
        UserModelDto user = userService.getById(order.getUserId());
        String lockKey = LockConstant.LOCK_TRADE_USER + order.getUserId();
        RLock lock = null;
        try {
            trusteeModel.setWhite(user.getWhiteIf());
            trusteeModel.setRobot(YesNoEnum.NO.getCode());//前台发布的设置为非机器人
            trusteeModel.setUserId(order.getUserId());
            trusteeModel.setPriority(1);//设置用户交易优先级
            //检查是否能交易  所有的检查放到该方法中
            trusteeService.checkPublish(trusteeModel);
            //获取锁
            lock = RedissonLockUtil.lock(lockKey);
            trusteeService.publishTrade(trusteeModel);
            //发布缓存修改
            tradeInfoCacheService.publishCache(trusteeModel.getTradeId(), trusteeModel.getTradeType(), trusteeModel.getPrice(), trusteeModel.getQuantity(), YesNoEnum.YES.getCode().equals(trusteeModel.getRobot()));
            return APIResult.success(trusteeModel.getId());
        } finally {
            RedissonLockUtil.unlock(lock);
        }
    }
}

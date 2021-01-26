package com.liuqi.business.service.impl;


import com.liuqi.base.BaseMapper;
import com.liuqi.base.BaseServiceImpl;
import com.liuqi.business.dto.RecordDto;
import com.liuqi.business.dto.TradeInfoDto;
import com.liuqi.business.enums.TableIdNameEnum;
import com.liuqi.business.mapper.TradeRecordMapper;
import com.liuqi.business.model.*;
import com.liuqi.business.service.*;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
public class TradeRecordServiceImpl extends BaseServiceImpl<TradeRecordModel, TradeRecordModelDto> implements TradeRecordService {

    @Autowired
    private TradeRecordMapper tradeRecordMapper;
    @Autowired
    private UserService userService;
    @Autowired
    private CurrencyTradeService currencyTradeService;
    @Autowired
    private TradeRecordUserService tradeRecordUserService;
    @Autowired
    private TableIdService tableIdService;
    @Override
    public BaseMapper<TradeRecordModel, TradeRecordModelDto> getBaseMapper() {
        return this.tradeRecordMapper;
    }

    @Override
    @Transactional
    public void insert(TradeRecordModel tradeRecordModel) {
        //设置id
        tradeRecordModel.setId(tableIdService.getNextId(TableIdNameEnum.TRADE_RECORD));
        super.insert(tradeRecordModel);
    }
    @Override
    public void afterAddOperate(TradeRecordModel tradeRecordModel) {
        super.afterAddOperate(tradeRecordModel);
        tradeRecordUserService.insertUserRecord(tradeRecordModel);
    }

    /**
     * 查询开盘价  昨天的价格为0时查询今天第一笔交易价格
     * @param tradeId
     * @return
     */
    @Override
    public BigDecimal getDayOpenPrice(Long tradeId) {
        Date startDate = DateTime.now().withTimeAtStartOfDay().toDate();
        //今天的前一笔价格
        BigDecimal openPrice = tradeRecordMapper.selectDayOpenPrice(startDate, tradeId);
        openPrice = Optional.ofNullable(openPrice).orElse(BigDecimal.ZERO);
        if (openPrice.compareTo(BigDecimal.ZERO) == 0) {
            //查询今天第一笔交易
            BigDecimal firstPrice = tradeRecordMapper.selectDayFirstPrice(startDate, tradeId);
            openPrice = Optional.ofNullable(firstPrice).orElse(BigDecimal.ZERO);
        }
        return openPrice;
    }
    /**
     * 获取关盘价格  没有值返回0
     * @param startTime
     * @return
     */
    @Override
    public BigDecimal getOpenPriceByDate(Date startTime, Long tradeId) {
        return Optional.ofNullable(tradeRecordMapper.findOpenPriceByDate(startTime, tradeId)).orElse(BigDecimal.ZERO);
    }
    /**
     * 获取关盘价格  没有值返回0
     * @param endtime
     * @return
     */
    @Override
    public BigDecimal getClosePriceByDate(Date endtime, Long tradeId) {
        return Optional.ofNullable(tradeRecordMapper.findClosePriceByDate(endtime, tradeId)).orElse(BigDecimal.ZERO);
    }

    /**
     * 查询交易对今天信息
     *
     * @param tradeId
     * @return
     */
    @Override
    public TradeInfoDto getTodayTrade(Long tradeId) {
        return tradeRecordMapper.findTodayTrade(tradeId);
    }

    /**
     * 查询交易对当前价格
     *
     * @param tradeId
     * @return
     */
    @Override
    public BigDecimal getCurrentTradePrice(Long tradeId) {
        return Optional.ofNullable(tradeRecordMapper.selectCurrentTradePrice(tradeId)).orElse(BigDecimal.ZERO);
    }

    /**
     * 获取时间内的最大价格，最小价格，成交数量
     *
     * @param startDate
     * @param endDate
     * @return
     */
    @Override
    public KDataModelDto getTradeDataByDate(Date startDate, Date endDate, Long tradeId) {
        return tradeRecordMapper.findTradeDataByDate(startDate, endDate, tradeId);
    }
    /**
     * 查询最新多少条交易对交易
     * @param tradeId
     * @param num
     * @return
     */
    @Override
    public List<TradeRecordModelDto> findTradeRecordList(Long tradeId, int num) {
        return tradeRecordMapper.findTradeRecordList(tradeId,num);
    }

    @Override
    public List<RecordDto> findRecordList(Long tradeId, int num) {
        return tradeRecordMapper.findRecordList(tradeId,num);
    }


    @Override
    protected void doMode(TradeRecordModelDto dto) {
        super.doMode(dto);
        dto.setBuyUserName(userService.getNameById(dto.getBuyUserId()));
        dto.setSellUserName(userService.getNameById(dto.getSellUserId()));

        CurrencyTradeModelDto trade=currencyTradeService.getById(dto.getTradeId());
        dto.setCurrencyName(trade!=null?trade.getCurrencyName():"");
        dto.setTradeCurrencyName(trade!=null?trade.getTradeCurrencyName():"");

        trade=null;
    }
}

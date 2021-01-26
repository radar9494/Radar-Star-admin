package com.liuqi.business.service.impl;

import com.liuqi.base.BaseMapper;
import com.liuqi.base.BaseServiceImpl;
import com.liuqi.business.enums.BuySellEnum;
import com.liuqi.business.enums.TableIdNameEnum;
import com.liuqi.business.mapper.TradeRecordUserMapper;
import com.liuqi.business.model.TradeRecordModel;
import com.liuqi.business.model.TradeRecordUserModel;
import com.liuqi.business.model.TradeRecordUserModelDto;
import com.liuqi.business.service.CurrencyTradeService;
import com.liuqi.business.service.TableIdService;
import com.liuqi.business.service.TradeRecordUserService;
import com.liuqi.business.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class TradeRecordUserServiceImpl extends BaseServiceImpl<TradeRecordUserModel, TradeRecordUserModelDto> implements TradeRecordUserService {

    @Autowired
    private TradeRecordUserMapper tradeRecordUserMapper;
    @Autowired
    private UserService userService;
    @Autowired
    private CurrencyTradeService currencyTradeService;
    @Autowired
    private TableIdService tableIdService;

    @Override
    public BaseMapper<TradeRecordUserModel, TradeRecordUserModelDto> getBaseMapper() {
        return this.tradeRecordUserMapper;
    }
    @Override
    @Transactional
    public void insert(TradeRecordUserModel tradeRecordUserModel) {
        //设置id
        tradeRecordUserModel.setId(tableIdService.getNextId(TableIdNameEnum.TRADE_RECORD_USER));
        super.insert(tradeRecordUserModel);
    }


    @Override
    protected void doMode(TradeRecordUserModelDto dto) {
        super.doMode(dto);
        dto.setUserName(userService.getNameById(dto.getUserId()));
        dto.setTradeName(currencyTradeService.getNameById(dto.getTradeId()));
    }

    /**
     * 查询用户已完成的交易数据  买/卖
     *
     * @param userId
     * @param tradeId
     * @return
     */
    @Override
    public List<TradeRecordUserModelDto> findUserRecord(Long userId, Long tradeId, boolean limit, Integer count) {
        TradeRecordUserModelDto search = new TradeRecordUserModelDto();
        search.setUserId(userId);
        if (tradeId != null && tradeId > 0) {
            search.setTradeId(tradeId);
        }
        if (limit && count > 0) {
            search.setLimit(limit);
            search.setCount(count);
        }
        return this.queryListByDto(search, true);
    }

    @Override
    @Transactional
    public void insertUserRecord(TradeRecordModel tradeRecordModel) {
        //买单
        TradeRecordUserModel buy = new TradeRecordUserModel();
        buy.setTradeId(tradeRecordModel.getTradeId());
        buy.setTradeQuantity(tradeRecordModel.getTradeQuantity());
        buy.setTradePrice(tradeRecordModel.getTradePrice());
        buy.setRobot(tradeRecordModel.getRobot());

        buy.setUserId(tradeRecordModel.getBuyUserId());
        buy.setTrusteeId(tradeRecordModel.getBuyTrusteeId());
        buy.setCharge(tradeRecordModel.getBuyCharge());
        buy.setPrice(tradeRecordModel.getBuyPrice());
        buy.setTradeType(BuySellEnum.BUY.getCode());
        this.insert(buy);

        //卖
        TradeRecordUserModel sell = new TradeRecordUserModel();
        sell.setTradeId(tradeRecordModel.getTradeId());
        sell.setTradeQuantity(tradeRecordModel.getTradeQuantity());
        sell.setTradePrice(tradeRecordModel.getTradePrice());
        sell.setRobot(tradeRecordModel.getRobot());

        sell.setUserId(tradeRecordModel.getSellUserId());
        sell.setTrusteeId(tradeRecordModel.getSellTrusteeId());
        sell.setCharge(tradeRecordModel.getSellCharge());
        sell.setPrice(tradeRecordModel.getSellPrice());
        sell.setTradeType(BuySellEnum.SELL.getCode());
        this.insert(sell);
    }
}

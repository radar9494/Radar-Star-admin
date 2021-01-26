package com.liuqi.business.service.impl;


import com.liuqi.business.enums.RaiseConfigEnum;
import com.liuqi.business.enums.WalletLogTypeEnum;
import com.liuqi.business.model.*;
import com.liuqi.business.service.*;
import com.liuqi.exception.BusinessException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.liuqi.base.BaseMapper;
import com.liuqi.base.BaseServiceImpl;


import com.liuqi.business.mapper.RaiseRecordMapper;

import java.math.BigDecimal;
import java.util.List;

@Service
@Transactional(readOnly = true)
public class RaiseRecordServiceImpl extends BaseServiceImpl<RaiseRecordModel, RaiseRecordModelDto> implements RaiseRecordService {


    @Autowired
    private UserWalletService userWalletService;
    @Autowired
    private UserWalletLogService userWalletLogService;
    @Autowired
    private RaiseConfigService raiseConfigService;
    @Autowired
    private CurrencyService currencyService;
    @Autowired
    private UserService userService;

    @Override
    protected void doMode(RaiseRecordModelDto dto) {
        super.doMode(dto);
        dto.setUserName(userService.getNameById(dto.getUserId()));
        dto.setCurrencyName(currencyService.getNameById(dto.getCurrencyId()));
        dto.setExchangeCurrencyName(currencyService.getNameById(dto.getExchangeCurrencyId()));
    }

    @Transactional
    @Override
    public void buy(Long userId, BigDecimal quantity, Long configId, Integer type) {
        if (quantity.compareTo(BigDecimal.ZERO) <= 0) {
            throw new BusinessException("数量不正确!");
        }
        RaiseConfigModelDto config = raiseConfigService.getById(configId);
        Long currencyId = null;
        BigDecimal price = BigDecimal.ZERO;
        if(config.getStatus().equals(RaiseConfigEnum.WAIT.getCode())){
            throw new BusinessException("募集未开始!");
        }
        if(config.getBuyTotal().add(quantity).compareTo(config.getIssuance())>0){
            throw new BusinessException("剩余数量不足!");
        }
        if(config.getStatus().equals(RaiseConfigEnum.END.getCode())){
            throw new BusinessException("募集已结束!");
        }
        config.setBuyTotal(config.getBuyTotal().add(quantity));
        raiseConfigService.update(config);
        if (type == 0) {//RDB
            price = config.getRdbPrice();
            currencyId = currencyService.getRdtId();
        } else {//USDT
            price = config.getUsdtPrice();
            currencyId = currencyService.getUsdtId();
        }
        BigDecimal money = quantity.multiply(price);
        UserWalletModelDto walelt = userWalletService.getByUserAndCurrencyId(userId, currencyId);
        if (walelt.getUsing().compareTo(money) < 0) {
            throw new BusinessException("余额不足!");
        }
        walelt.setUsing(walelt.getUsing().subtract(money));
        userWalletService.update(walelt);
        RaiseRecordModel model = new RaiseRecordModel();
        model.setConfigId(configId);
        model.setUserId(userId);
        model.setQuantity(quantity);
        model.setMoney(money);
        model.setPrice(price);
        model.setExchangeCurrencyId(config.getCurrencyId());
        this.insert(model);
        userWalletLogService.addLog(userId, currencyId, money.negate(), WalletLogTypeEnum.RAISE.getCode(),model.getId(),"募集",walelt );
    }

    @Override
    public List<RaiseRecordModel> getByConfigId(Long id) {
        return raiseRecordMapper.getByConfigId(id);
    }

    @Autowired
    private RaiseRecordMapper raiseRecordMapper;


    @Override
    public BaseMapper<RaiseRecordModel, RaiseRecordModelDto> getBaseMapper() {
        return this.raiseRecordMapper;
    }

}

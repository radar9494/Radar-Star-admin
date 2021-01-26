package com.liuqi.business.service.impl;


import cn.hutool.core.collection.CollectionUtil;
import com.liuqi.business.enums.WalletLogTypeEnum;
import com.liuqi.business.model.RaiseRecordModel;
import com.liuqi.business.model.UserWalletModelDto;
import com.liuqi.business.service.*;
import com.liuqi.response.ReturnResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.liuqi.base.BaseMapper;
import com.liuqi.base.BaseServiceImpl;
import com.liuqi.business.model.RaiseConfigModel;
import com.liuqi.business.model.RaiseConfigModelDto;


import com.liuqi.business.mapper.RaiseConfigMapper;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class RaiseConfigServiceImpl extends BaseServiceImpl<RaiseConfigModel, RaiseConfigModelDto> implements RaiseConfigService {

    @Autowired
    private RaiseConfigMapper raiseConfigMapper;
    @Autowired
    private RaiseRecordService raiseRecordService;
    @Autowired
    private UserWalletLogService userWalletLogService;
    @Autowired
    private UserWalletService userWalletService;
    @Autowired
    private CurrencyService currencyService;


    @Transactional
    @Override
    public ReturnResponse start(Long id) {
        RaiseConfigModel model = raiseConfigMapper.getById(id);
        if (model.getStatus() != 0) {
            ReturnResponse.backFail("已经开始!");
        }
        model.setStatus(1);
        this.update(model);
        return ReturnResponse.backSuccess();
    }

    @Transactional
    @Override
    public ReturnResponse end(Long id) {
        RaiseConfigModel model = raiseConfigMapper.getById(id);
        if (model.getStatus() != 1) {
            ReturnResponse.backFail("已经结束!");
        }
        model.setStatus(2);
        this.update(model);
        List<RaiseRecordModel> list = raiseRecordService.getByConfigId(id);
        if (CollectionUtil.isNotEmpty(list)) {
            for (RaiseRecordModel item : list) {
                UserWalletModelDto wallet = userWalletService.getByUserAndCurrencyId(item.getUserId(), item.getExchangeCurrencyId());
                wallet.setUsing(wallet.getUsing().add(item.getQuantity()));
                userWalletService.update(wallet);
                userWalletLogService.addLog(item.getUserId(),item.getExchangeCurrencyId(),item.getQuantity(), WalletLogTypeEnum.RAISE.getCode(),model.getId(),"募集获得",wallet);
            }
        }
        return ReturnResponse.backSuccess();
    }

    @Override
    protected void doMode(RaiseConfigModelDto dto) {
        super.doMode(dto);
        dto.setCurrencyName(currencyService.getNameById(dto.getCurrencyId()));
    }

    @Override
    public BaseMapper<RaiseConfigModel, RaiseConfigModelDto> getBaseMapper() {
        return this.raiseConfigMapper;
    }

}

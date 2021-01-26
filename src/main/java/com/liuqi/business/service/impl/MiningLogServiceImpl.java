package com.liuqi.business.service.impl;


import com.liuqi.business.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.liuqi.base.BaseMapper;
import com.liuqi.base.BaseServiceImpl;
import com.liuqi.business.model.MiningLogModel;
import com.liuqi.business.model.MiningLogModelDto;


import com.liuqi.business.service.MiningLogService;
import com.liuqi.business.mapper.MiningLogMapper;

import java.math.BigDecimal;

@Service
@Transactional(readOnly = true)
public class MiningLogServiceImpl extends BaseServiceImpl<MiningLogModel, MiningLogModelDto> implements MiningLogService {

    @Autowired
    private MiningLogMapper miningLogMapper;
    @Autowired
    private UserService userService;

    @Override
    public BaseMapper<MiningLogModel, MiningLogModelDto> getBaseMapper() {
        return this.miningLogMapper;
    }

    @Transactional
    public MiningLogModel addLog(long userId, BigDecimal num, long currencyId, String currencyName, byte type) {
        MiningLogModel miningLogModel = new MiningLogModel();
        miningLogModel.setUserId(userId);
        miningLogModel.setCurrencyId(currencyId);
        miningLogModel.setCurrencyName(currencyName);
        miningLogModel.setType(type);
        miningLogModel.setNum(num);
        miningLogModel.setState((byte) 0);
        insert(miningLogModel);
        return miningLogModel;
    }

    @Override
    protected void doMode(MiningLogModelDto dto) {
        dto.setUserName(userService.getNameById(dto.getUserId()));
    }
}

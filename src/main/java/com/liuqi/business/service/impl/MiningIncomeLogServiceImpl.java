package com.liuqi.business.service.impl;


import cn.hutool.core.collection.CollectionUtil;
import com.liuqi.base.BaseMapper;
import com.liuqi.base.BaseServiceImpl;
import com.liuqi.business.dto.CoordinateDto;
import com.liuqi.business.mapper.MiningIncomeLogMapper;
import com.liuqi.business.model.MiningIncomeLogModel;
import com.liuqi.business.model.MiningIncomeLogModelDto;
import com.liuqi.business.service.MiningIncomeLogService;
import com.liuqi.business.service.UserService;
import com.liuqi.utils.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDate;
import java.util.Date;
import java.util.List;

@Service
public class MiningIncomeLogServiceImpl extends BaseServiceImpl<MiningIncomeLogModel, MiningIncomeLogModelDto> implements MiningIncomeLogService {

    @Autowired
    private MiningIncomeLogMapper miningIncomeLogMapper;

    @Autowired
    private UserService userService;

    @Override
    protected void doMode(MiningIncomeLogModelDto dto) {
        dto.setUserName(userService.getNameById(dto.getUserId()));
    }

    @Override
    public BigDecimal getTotal(long userId) {
        return miningIncomeLogMapper.getTotal(userId);
    }

    @Override
    public BigDecimal getTotalByType(long userId, int i) {
        return miningIncomeLogMapper.getTotalByType(userId,i);
    }

    @Override
    public BigDecimal getTotalByType(long userId, int i, Long currencyId) {
        return miningIncomeLogMapper.getTotalByTypeByCurrencyId(userId,i,currencyId);
    }

    @Override
    public BaseMapper<MiningIncomeLogModel, MiningIncomeLogModelDto> getBaseMapper() {
        return this.miningIncomeLogMapper;
    }

    @Override
    public BigDecimal yesteartDayTotal(long userId, Long currencyId) {
        return miningIncomeLogMapper.yesteartDayTotal(userId,currencyId, DateUtil.getDay(new Date()));
    }

    public MiningIncomeLogModel addLog(long userId, BigDecimal num, long currencyId, String currencyName, byte type) {
        MiningIncomeLogModel miningLogModel = new MiningIncomeLogModel();
        miningLogModel.setUserId(userId);
        miningLogModel.setCurrencyId(currencyId);
        miningLogModel.setCurrencyName(currencyName);
        miningLogModel.setType(type);
        miningLogModel.setNum(num);
        miningLogModel.setDate(LocalDate.now());
        insert(miningLogModel);
        return miningLogModel;
    }

    public List<CoordinateDto> sevenDay(long userId, byte type) {
        return miningIncomeLogMapper.findByUserId(userId, type);
    }
    @Override
    public      List<MiningIncomeLogModel> findSumByUserId(long userId,Integer type) {
        List<MiningIncomeLogModel> list=null;
        if(type==0){
            list= miningIncomeLogMapper.findSumByUserId(userId, null);
        }else{
            list= miningIncomeLogMapper.findSumByUserId(userId, LocalDate.now());
        }
        return list;
    }





}

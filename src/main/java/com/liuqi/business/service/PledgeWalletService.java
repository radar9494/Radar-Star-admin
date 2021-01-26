package com.liuqi.business.service;

import com.liuqi.base.BaseService;
import com.liuqi.business.model.PledgeWalletModel;
import com.liuqi.business.model.PledgeWalletModelDto;

import java.math.BigDecimal;

public interface PledgeWalletService extends BaseService<PledgeWalletModel,PledgeWalletModelDto>{


    PledgeWalletModel modifyWalletUsing(Long userId, BigDecimal quantity);

    PledgeWalletModelDto getByUserId(Long userId);

    void addPledge(Long userId, BigDecimal quantity);

    PledgeWalletModel getTotal(Integer status);
}

package com.liuqi.business.service;

import com.liuqi.base.BaseService;
import com.liuqi.business.model.PledgeWalletLogModel;
import com.liuqi.business.model.PledgeWalletLogModelDto;

import java.math.BigDecimal;

public interface PledgeWalletLogService extends BaseService<PledgeWalletLogModel,PledgeWalletLogModelDto>{


    void addLog(Long userId, BigDecimal quantity, Integer code, BigDecimal using);
}

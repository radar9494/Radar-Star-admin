package com.liuqi.business.service;

import com.liuqi.base.BaseService;
import com.liuqi.business.model.MiningWalletLogModel;
import com.liuqi.business.model.MiningWalletLogModelDto;
import com.liuqi.business.model.MiningWalletModel;

import java.math.BigDecimal;

public interface MiningWalletLogService extends BaseService<MiningWalletLogModel,MiningWalletLogModelDto>{

    void addLog(Long userId, Long currencyId, BigDecimal money, Integer type, String remarks, MiningWalletModel wallet);

}

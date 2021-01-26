package com.liuqi.business.service;

import com.liuqi.base.BaseService;
import com.liuqi.business.model.OtcWalletLogModel;
import com.liuqi.business.model.OtcWalletLogModelDto;
import com.liuqi.business.model.OtcWalletModel;

import java.math.BigDecimal;

public interface OtcWalletLogService extends BaseService<OtcWalletLogModel,OtcWalletLogModelDto>{


    void addLog(Long buyUserId, Long currencyId, BigDecimal buyChangeUsing, Integer code, Long id, String remark, OtcWalletModel buyWallet);
}

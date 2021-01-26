package com.liuqi.business.service;

import com.liuqi.base.BaseService;
import com.liuqi.business.model.WalletStaticModel;
import com.liuqi.business.model.WalletStaticModelDto;

import java.math.BigDecimal;

public interface WalletStaticService extends BaseService<WalletStaticModel,WalletStaticModelDto>{


    void walletStat(int i);


}

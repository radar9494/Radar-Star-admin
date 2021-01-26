package com.liuqi.business.service;

import com.liuqi.base.BaseService;
import com.liuqi.business.model.MiningWalletModel;
import com.liuqi.business.model.MiningWalletModelDto;
import com.liuqi.business.model.OtcWalletModel;
import com.liuqi.business.model.WalletStaticModel;

import java.math.BigDecimal;
import java.util.List;

public interface MiningWalletService extends BaseService<MiningWalletModel,MiningWalletModelDto>{

    MiningWalletModel modified(long userId, long currencyId, BigDecimal num,BigDecimal freeze);

    MiningWalletModel findByUserIdAndCurrencyId(long userId, long currencyId );

    List getByUserId(Long userId, String currencyName);

    void adminUpdate(MiningWalletModel model, Long userId);

    List<WalletStaticModel> groupByCurrencyId();

    void insertMiningWallet(Long userId);

    List<MiningWalletModel> getFreeze();

    MiningWalletModel getTotal(Long id,Integer status);
}

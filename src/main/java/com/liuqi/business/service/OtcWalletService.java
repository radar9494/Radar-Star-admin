package com.liuqi.business.service;

import com.liuqi.base.BaseService;
import com.liuqi.business.model.OtcWalletModel;
import com.liuqi.business.model.OtcWalletModelDto;
import com.liuqi.business.model.UserWalletModel;
import com.liuqi.business.model.WalletStaticModel;

import java.math.BigDecimal;
import java.util.List;

public interface OtcWalletService extends BaseService<OtcWalletModel,OtcWalletModelDto>{


    OtcWalletModel getByUserAndCurrencyId(Long userId, Long currencyId);

    OtcWalletModelDto addWallet(Long userId, Long currencyId);

    OtcWalletModel modifyWallet(Long sellUserId, Long currencyId, BigDecimal sellChangeUsing, BigDecimal sellChangeFreeze);

    List getByUserId(Long userId, String currencyName);

    void adminUpdate(OtcWalletModel model, Long userId);

    List<WalletStaticModel> groupByCurrencyId();

    void insertOtcWallet(Long userId);

    OtcWalletModel getTotal(Long id,Integer type);
}

package com.liuqi.business.service.impl;


import com.liuqi.business.model.UserWalletModel;
import com.liuqi.business.service.*;
import org.apache.poi.ss.formula.functions.T;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.liuqi.base.BaseMapper;
import com.liuqi.base.BaseServiceImpl;
import com.liuqi.business.model.WalletStaticModel;
import com.liuqi.business.model.WalletStaticModelDto;


import com.liuqi.business.mapper.WalletStaticMapper;

import java.math.BigDecimal;
import java.util.List;

@Service
@Transactional(readOnly = true)
public class WalletStaticServiceImpl extends BaseServiceImpl<WalletStaticModel, WalletStaticModelDto> implements WalletStaticService {




    @Transactional
    @Override
    public void walletStat(int type) {
        List<WalletStaticModel> list = null;
        if (type == 0) {
            list = userWalletService.groupByCurrencyId();
        } else if (type == 1) {
            list = otcWalletService.groupByCurrencyId();
        } else {
            list = miningWalletService.groupByCurrencyId();
        }
        for (WalletStaticModel item : list) {
            if (type == 2) {
                item.setTotal(miningUserTotalHandleService.getQuantity(item.getCurrencyId()));
            }
            item.setType(type);
            this.insert(item);
        }

    }

    @Autowired
    private WalletStaticMapper walletStaticMapper;

    @Autowired
    private UserWalletService userWalletService;
    @Autowired
    private OtcWalletService otcWalletService;

    @Autowired
    private MiningWalletService miningWalletService;
    @Autowired
    private MiningUserTotalHandleService miningUserTotalHandleService;


    @Override
    public BaseMapper<WalletStaticModel, WalletStaticModelDto> getBaseMapper() {
        return this.walletStaticMapper;
    }

}

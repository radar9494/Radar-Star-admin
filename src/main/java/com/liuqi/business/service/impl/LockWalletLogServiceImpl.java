package com.liuqi.business.service.impl;


import com.liuqi.base.BaseMapper;
import com.liuqi.base.BaseServiceImpl;
import com.liuqi.business.enums.TableIdNameEnum;
import com.liuqi.business.mapper.LockWalletLogMapper;
import com.liuqi.business.model.LockWalletLogModel;
import com.liuqi.business.model.LockWalletLogModelDto;
import com.liuqi.business.model.LockWalletModel;
import com.liuqi.business.service.CurrencyService;
import com.liuqi.business.service.LockWalletLogService;
import com.liuqi.business.service.TableIdService;
import com.liuqi.business.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
@Transactional(readOnly = true)
public class LockWalletLogServiceImpl extends BaseServiceImpl<LockWalletLogModel, LockWalletLogModelDto> implements LockWalletLogService {

    @Autowired
    private LockWalletLogMapper lockWalletLogMapper;
    @Autowired
    private UserService userService;
    @Autowired
    private CurrencyService currencyService;
    @Autowired
    private TableIdService tableIdService;

    @Override
    public BaseMapper<LockWalletLogModel, LockWalletLogModelDto> getBaseMapper() {
        return this.lockWalletLogMapper;
    }

    @Override
    public void insert(LockWalletLogModel lockWalletLogModel) {
        //设置id
        lockWalletLogModel.setId(tableIdService.getNextId(TableIdNameEnum.LOCK_WALLET_LOG));
        super.insert(lockWalletLogModel);
    }

    @Override
    @Transactional
    public void addLog(Long userId, Long currencyId, BigDecimal money, Integer type,Long orderId, String remarks, LockWalletModel wallet) {
        LockWalletLogModel log = new LockWalletLogModel();
        log.setCurrencyId(currencyId);
        log.setMoney(money);
        log.setUserId(userId);
        log.setType(type);
        log.setRemark(remarks);
        log.setOrderId(orderId);
        log.setBalance(wallet.getLocking());
        log.setSnapshot("锁仓：" + wallet.getLocking() + ",冻结：" + wallet.getFreeze());
        this.insert(log);
    }


    @Override
    protected void doMode(LockWalletLogModelDto dto) {
        super.doMode(dto);
        dto.setUserName(userService.getNameById(dto.getUserId()));
        dto.setRealName(userService.getRealNameById(dto.getUserId()));
        dto.setCurrencyName(currencyService.getNameById(dto.getCurrencyId()));
    }
}

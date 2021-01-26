package com.liuqi.business.service;

import com.github.pagehelper.PageInfo;
import com.liuqi.base.BaseService;
import com.liuqi.business.dto.WalletLogDto;
import com.liuqi.business.model.UserWalletLogModel;
import com.liuqi.business.model.UserWalletLogModelDto;
import com.liuqi.business.model.UserWalletModel;

import java.math.BigDecimal;

public interface UserWalletLogService extends BaseService<UserWalletLogModel,UserWalletLogModelDto>{


    /**
     ** 添加日志
     * @param userId
     * @param currencyId
     * @param money  操作金额
     * @param type   UserWalletLogModel 常量1交易  2转账  3充值  4提现
     * @param remarks   备注
     */
    void addLog(Long userId, Long currencyId, BigDecimal money ,Integer type,Long orderId,String remarks,UserWalletModel model);

    PageInfo<WalletLogDto> getTotalLog(Long userId, Integer pageNum, Integer pageSize);
}

package com.liuqi.business.service;

import com.liuqi.base.BaseService;
import com.liuqi.business.model.LockWalletModel;
import com.liuqi.business.model.LockWalletModelDto;

import java.math.BigDecimal;

public interface LockWalletService extends BaseService<LockWalletModel, LockWalletModelDto> {

    /**
     * 查询用户钱包信息
     *
     * @param userId
     * @param currencyId
     * @return
     */
    LockWalletModelDto getByUserAndCurrencyId(Long userId, Long currencyId);

    /**
     * 初始化用户钱包
     *
     * @param userId
     */
    void insertUserWallet(Long userId);

    /**
     * 添加钱包
     *
     * @param userId
     * @param currencyId
     */
    LockWalletModelDto addWallet(Long userId, Long currencyId);

    /**
     * 初始化用户钱包
     *
     * @param userId
     */
    void adminUpdate(LockWalletModelDto model, Long userId);

    /**
     * 修改资产 返回修改后的数据
     *
     * @param userId
     * @param currencyId
     * @param changeUsing  修改的可用值
     * @param changeFreeze 修改的冻结值
     */
    LockWalletModelDto modifyWallet(Long userId, Long currencyId, BigDecimal changeUsing, BigDecimal changeFreeze);

    /**
     * 修改资产 返回修改后的数据
     *
     * @param userId
     * @param currencyId
     * @param changeUsing 修改的可用值
     */
    LockWalletModelDto modifyWalletLocking(Long userId, Long currencyId, BigDecimal changeUsing);

    /**
     * 修改资产 返回修改后的数据
     *
     * @param userId
     * @param currencyId
     * @param changeFreeze 修改的冻结值
     */
    LockWalletModelDto modifyWalletFreeze(Long userId, Long currencyId, BigDecimal changeFreeze);

}

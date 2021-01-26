package com.liuqi.business.service;

import com.liuqi.base.BaseService;
import com.liuqi.business.dto.api.AllWalletDto;
import com.liuqi.business.model.UserWalletModel;
import com.liuqi.business.model.UserWalletModelDto;
import com.liuqi.business.model.WalletStaticModel;
import org.apache.ibatis.annotations.Select;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public interface UserWalletService extends BaseService<UserWalletModel,UserWalletModelDto>{
    /**
     * 查询用户钱包信息
     * @param userId
     * @param currencyId
     * @return
     */
    UserWalletModelDto getByUserAndCurrencyId(Long userId,Long currencyId);


    /**
     * 查询用户钱包 按钱包模糊查询
     * @param userId
     * @param currencyName
     * @return
     */
    List<UserWalletModelDto> getByUserId(Long userId, String currencyName);

    /**
     * 初始化用户钱包
     * @param userId
     */
    void  insertUserWallet(Long userId);

    /**
     * 添加钱包
     * @param userId
     * @param currencyId
     */
    UserWalletModelDto addWallet(Long userId, Long currencyId);

    /**
     * 初始化用户钱包
     * @param userId
     */
    void  adminUpdate(UserWalletModelDto model,Long userId);

    /**
     * 交易买钱包修改
     * @param userId
     * @param currencyId
     * @param tradeCurrencyId
     * @param buyTotal
     * @param price
     * @param rateMoney
     * @param buyWhite
     * @param recordId
     */
    void doBuyWallet(Long userId, Long currencyId, Long tradeCurrencyId, BigDecimal buyTotal, BigDecimal price, BigDecimal rateMoney, boolean buyWhite, Long recordId);
    /**
     * 操作卖家钱包
     *
     * @param userId
     * @param currencyId
     * @param tradeCurrencyId
     * @param tradeQuantity   交易数量
     * @param price           价格
     * @param rateMoney       手续费
     */
    void doSellWallet(Long userId, Long currencyId, Long tradeCurrencyId, BigDecimal tradeQuantity, BigDecimal price, BigDecimal rateMoney, boolean sellWhite, Long recordId);

    /**
     * 修改资产 返回修改后的数据
     * @param userId
     * @param currencyId
     * @param changeUsing   修改的可用值
     * @param changeFreeze  修改的冻结值
     */
    UserWalletModelDto modifyWallet(Long userId,Long currencyId,BigDecimal changeUsing,BigDecimal changeFreeze);

    /**
     * 修改资产 返回修改后的数据
     * @param userId
     * @param currencyId
     * @param changeUsing   修改的可用值
     */
    UserWalletModelDto modifyWalletUsing(Long userId,Long currencyId,BigDecimal changeUsing);

    /**
     * 修改资产 返回修改后的数据
     * @param userId
     * @param currencyId
     * @param changeFreeze  修改的冻结值
     */
    UserWalletModelDto modifyWalletFreeze(Long userId,Long currencyId,BigDecimal changeFreeze);

    void updateOff(Long userId, Long currencyId, Integer off);

    List<WalletStaticModel> groupByCurrencyId();

    UserWalletModel getTotal(Long id,Integer status);



    List<AllWalletDto> allWallet(Long currencyId);
}

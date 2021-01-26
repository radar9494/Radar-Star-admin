package com.liuqi.business.mapper;

import com.liuqi.base.BaseMapper;
import com.liuqi.business.model.LockWalletModel;
import com.liuqi.business.model.LockWalletModelDto;
import org.apache.ibatis.annotations.Param;

import java.math.BigDecimal;


public interface LockWalletMapper extends BaseMapper<LockWalletModel, LockWalletModelDto> {

    LockWalletModelDto getByUserAndCurrencyId(@Param("userId") Long userId, @Param("currencyId") Long currencyId);

    int modifyWallet(@Param("userId") Long userId, @Param("currencyId") Long currencyId, @Param("changeLocking") BigDecimal changeLocking, @Param("changeFreeze") BigDecimal changeFreeze);

    int modifyWalletLocking(@Param("userId") Long userId, @Param("currencyId") Long currencyId, @Param("changeLocking") BigDecimal changeLocking);

    int modifyWalletFreeze(@Param("userId") Long userId, @Param("currencyId") Long currencyId,@Param("changeFreeze") BigDecimal changeFreeze);

}

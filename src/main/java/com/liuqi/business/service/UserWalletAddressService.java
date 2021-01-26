package com.liuqi.business.service;

import com.liuqi.base.BaseService;
import com.liuqi.business.model.UserWalletAddressModel;
import com.liuqi.business.model.UserWalletAddressModelDto;

public interface UserWalletAddressService extends BaseService<UserWalletAddressModel,UserWalletAddressModelDto>{

    /**
     * 添加提币地址
     * @param remark
     * @param currencyId
     * @param address
     * @param memo
     * @param userId
     * @return
     */
    void addAddress(String remark,Long currencyId,String address,String memo,Long userId);

    /**
     * 删除提币地址
     * @param id
     * @return
     */
    boolean delete(Long id);
}

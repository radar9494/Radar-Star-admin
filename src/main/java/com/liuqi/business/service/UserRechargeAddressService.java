package com.liuqi.business.service;

import com.liuqi.base.BaseService;
import com.liuqi.business.model.UserRechargeAddressModel;
import com.liuqi.business.model.UserRechargeAddressModelDto;

public interface UserRechargeAddressService extends BaseService<UserRechargeAddressModel, UserRechargeAddressModelDto>{

    /**
     * 根据用户id，协议获取地址
     * @param userId
     * @param protocol
     * @return
     */
    String getRechargeAddress(Long userId,Integer protocol);
    /**
     * 初始化
     * @param userId
     * @param protocol
     * @return
             */
    String initRechargeAddressLock(Long userId,Integer protocol);
    /**
     * 初始化
     * @param userId
     * @param protocol
     * @return
     */
    String initRechargeAddress(Long userId,Integer protocol);

    /**
     *根据地址获取绑定的用户
     * @param address
     * @param protocol
     * @return
     */
    Long findBindingUserIdByAddress(String address,Integer protocol);
}

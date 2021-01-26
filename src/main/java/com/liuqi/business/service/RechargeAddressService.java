package com.liuqi.business.service;


import com.liuqi.business.dto.RechargeAddressDto;

public interface RechargeAddressService {

    /**
     *根据类型获取一个可用地址
     * @param type 1ETH  2BTC
     * @return
     */
     RechargeAddressDto getAddress(int type);
}

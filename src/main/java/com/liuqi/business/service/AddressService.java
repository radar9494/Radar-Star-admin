package com.liuqi.business.service;


import com.liuqi.business.model.AddressModel;

public interface AddressService {
    /**
     * 获取一个未使用的地址
     * @return
     */
    AddressModel getNoUserAddress(String tableName);

}

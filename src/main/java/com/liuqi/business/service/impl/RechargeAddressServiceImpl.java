package com.liuqi.business.service.impl;

import com.liuqi.business.dto.RechargeAddressDto;
import com.liuqi.business.enums.ProtocolEnum;
import com.liuqi.business.model.AddressModel;
import com.liuqi.business.service.AddressService;
import com.liuqi.business.service.RechargeAddressService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class RechargeAddressServiceImpl implements RechargeAddressService {

    @Autowired
    private AddressService addressService;
    /**
     *根据类型获取一个可用地址
     * @param type 1ETH  2BTC
     * @return
     */
    @Override
    @Transactional
    public RechargeAddressDto getAddress(int type) {
        RechargeAddressDto dto=null;
        boolean hasCode = ProtocolEnum.hasCode(type);
        if (hasCode) {
            dto = new RechargeAddressDto();
            String tableName = "t_address_" + ProtocolEnum.getName(type).toLowerCase();
            AddressModel address = addressService.getNoUserAddress(tableName);
            if (address != null) {
                dto.setPath(address.getPath());
                dto.setAddress(address.getAddress());
            }
        }
        return dto;
    }
}

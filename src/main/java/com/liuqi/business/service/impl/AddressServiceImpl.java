package com.liuqi.business.service.impl;


import com.liuqi.business.mapper.AddressMapper;
import com.liuqi.business.model.AddressModel;
import com.liuqi.business.service.AddressService;
import com.liuqi.exception.BusinessException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AddressServiceImpl implements AddressService {

	@Autowired
	private AddressMapper addressMapper;
	/**
	 * 获取一个未使用的地址
	 * @return
	 */
	@Override
	@Transactional
	public AddressModel getNoUserAddress(String tableName) {
		//获取未使用地址
		AddressModel address = addressMapper.getNoUserAddress(tableName);
		if(address!=null){
			//修改为使用
			addressMapper.updateUsing(address.getId(), tableName);
			return address;
		}else{
			throw  new BusinessException("无可用钱包地址");
		}
	}

}

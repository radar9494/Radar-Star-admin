package com.liuqi.business.service.impl;


import com.liuqi.base.BaseMapper;
import com.liuqi.base.BaseServiceImpl;
import com.liuqi.business.constant.LockConstant;
import com.liuqi.business.dto.RechargeAddressDto;
import com.liuqi.business.mapper.UserRechargeAddressMapper;
import com.liuqi.business.model.UserRechargeAddressModel;
import com.liuqi.business.model.UserRechargeAddressModelDto;
import com.liuqi.business.service.RechargeAddressService;
import com.liuqi.business.service.UserRechargeAddressService;
import com.liuqi.business.service.UserService;
import com.liuqi.redis.lock.RedissonLockUtil;
import org.apache.commons.lang3.StringUtils;
import org.redisson.api.RLock;
import org.springframework.aop.framework.AopContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class UserRechargeAddressServiceImpl extends BaseServiceImpl<UserRechargeAddressModel, UserRechargeAddressModelDto> implements UserRechargeAddressService {

	@Autowired
	private UserRechargeAddressMapper userRechargeAddressMapper;
	@Autowired
	private RechargeAddressService rechargeAddressService;
	@Autowired
	private UserService userService;
	@Override
	public BaseMapper<UserRechargeAddressModel,UserRechargeAddressModelDto> getBaseMapper() {
		return this.userRechargeAddressMapper;
	}
	@Override
	public String getRechargeAddress(Long userId, Integer protocol) {
		UserRechargeAddressModel model = userRechargeAddressMapper.getRechargeAddress(userId, protocol);
		if (model != null) {
			return model.getAddress();
		}
		return "";
	}

	/**
	 * 无事物  统一获取  加锁
	 * @param userId
	 * @param protocol
	 * @return
	 */
	@Override
	@Transactional(propagation = Propagation.NOT_SUPPORTED)
	public String initRechargeAddressLock(Long userId, Integer protocol) {
		String address="";
		String key= LockConstant.LOCK_EXTRACT_ADDRESS+userId+"_"+protocol;
		RLock lock=null;
		try {
			lock= RedissonLockUtil.lock(key);
			address=this.getRechargeAddress(userId, protocol);
			if(StringUtils.isEmpty(address)) {
				address = ((UserRechargeAddressService) AopContext.currentProxy()).initRechargeAddress(userId, protocol);
			}
		}catch (Exception e){
			e.printStackTrace();
		}finally {
			RedissonLockUtil.unlock(lock);
		}
		return address;
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public String initRechargeAddress(Long userId, Integer protocol) {
		RechargeAddressDto dto = rechargeAddressService.getAddress(protocol);
		UserRechargeAddressModel model = new UserRechargeAddressModel();
		model.setAddress(dto.getAddress());
		model.setPath(dto.getPath());
		model.setProtocol(protocol);
		model.setUserId(userId);
		this.insert(model);
		return model.getAddress();
	}


	@Override
	public Long findBindingUserIdByAddress(String address,Integer protocol) {
		return userRechargeAddressMapper.findBindingUserIdByAddress(address,protocol);
	}

	@Override
	protected void doMode(UserRechargeAddressModelDto dto) {
		super.doMode(dto);
		dto.setUserName(userService.getNameById(dto.getUserId()));
		dto.setRealName(userService.getRealNameById(dto.getUserId()));
	}
}

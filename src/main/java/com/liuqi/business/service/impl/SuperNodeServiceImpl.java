package com.liuqi.business.service.impl;

import com.liuqi.base.BaseMapper;
import com.liuqi.base.BaseServiceImpl;
import com.liuqi.business.enums.WalletLogTypeEnum;
import com.liuqi.business.mapper.SuperNodeMapper;
import com.liuqi.business.model.SuperNodeConfigModel;
import com.liuqi.business.model.SuperNodeModel;
import com.liuqi.business.model.SuperNodeModelDto;
import com.liuqi.business.model.UserWalletModel;
import com.liuqi.business.service.*;
import com.liuqi.exception.BusinessException;
import com.liuqi.redis.NumRepository;
import com.liuqi.utils.MathUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
@Transactional(readOnly = true)
public class SuperNodeServiceImpl extends BaseServiceImpl<SuperNodeModel,SuperNodeModelDto> implements SuperNodeService{

	@Autowired
	private SuperNodeMapper superNodeMapper;
	@Autowired
	private CurrencyService currencyService;
	@Autowired
	private UserService userService;
	@Autowired
	private UserWalletService userWalletService;
	@Autowired
	private UserWalletLogService userWalletLogService;
	@Autowired
	private NumRepository numRepository;
	@Override
	public BaseMapper<SuperNodeModel,SuperNodeModelDto> getBaseMapper() {
		return this.superNodeMapper;
	}

	@Override
	protected void doMode(SuperNodeModelDto dto) {
		super.doMode(dto);
		dto.setUserName(userService.getNameById(dto.getUserId()));
		dto.setRealName(userService.getRealNameById(dto.getUserId()));
		dto.setRecommendUserName(userService.getNameById(dto.getRecommendUserId()));
		dto.setRecommendRealName(userService.getRealNameById(dto.getRecommendUserId()));
		dto.setCurrencyName(currencyService.getNameById(dto.getCurrencyId()));
	}

	@Override
	public SuperNodeModelDto getByUserId(Long userId) {
		return superNodeMapper.getByUserId(userId);
	}

	@Override
	public int getTotalCount() {
		return superNodeMapper.getTotalCount();
	}

	@Override
	@Transactional
	public void joinSuperNode(SuperNodeConfigModel config, Long userId,Long recommendUserId,  boolean subWallet) {
		SuperNodeModel node = this.getByUserId(userId);
		if (node != null) {
			throw new BusinessException("已加入超级节点");
		}
		int count=this.getTotalCount();
		if (count>config.getCount()) {
			throw new BusinessException("节点已满");
		}
		node = new SuperNodeModel();
		node.setUserId(userId);
		node.setCurrencyId(config.getJoinCurrencyId());
		if(subWallet){
			node.setQuantity(config.getJoinQuantity());
		}else{
			node.setQuantity(BigDecimal.ZERO);
		}
		node.setNum(numRepository.getSuperNode());
		node.setRecommendUserId(recommendUserId);
		this.insert(node);

		//扣减钱包
		if (subWallet) {
			BigDecimal changeUsing = MathUtil.zeroSub(node.getQuantity());
			BigDecimal changeFreeze = BigDecimal.ZERO;
			UserWalletModel wallet = userWalletService.modifyWallet(node.getUserId(), node.getCurrencyId(), changeUsing, changeFreeze);
			userWalletLogService.addLog(node.getUserId(), node.getCurrencyId(), changeUsing, WalletLogTypeEnum.SUPERNODE.getCode(), node.getId() , "参加超级节点扣除", wallet);
		}
	}
}

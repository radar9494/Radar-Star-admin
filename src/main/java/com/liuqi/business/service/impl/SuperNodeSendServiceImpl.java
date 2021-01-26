package com.liuqi.business.service.impl;

import com.liuqi.base.BaseMapper;
import com.liuqi.base.BaseServiceImpl;
import com.liuqi.business.async.AsyncTask;
import com.liuqi.business.enums.WalletDoEnum;
import com.liuqi.business.enums.WalletLogTypeEnum;
import com.liuqi.business.mapper.SuperNodeSendMapper;
import com.liuqi.business.model.*;
import com.liuqi.business.service.*;
import com.liuqi.utils.MathUtil;
import org.springframework.aop.framework.AopContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@Service
@Transactional(readOnly = true)
public class SuperNodeSendServiceImpl extends BaseServiceImpl<SuperNodeSendModel,SuperNodeSendModelDto> implements SuperNodeSendService{

	@Autowired
	private SuperNodeSendMapper superNodeSendMapper;
	@Autowired
	private CurrencyService currencyService;
	@Autowired
	private UserService userService;
	@Autowired
	private SuperNodeService superNodeService;
	@Autowired
	private SuperNodeChargeService superNodeChargeService;
	@Autowired
	private UserWalletService userWalletService;
	@Autowired
	private UserWalletLogService userWalletLogService;
	@Autowired
	@Lazy
	private AsyncTask asyncTask;
	@Override
	public BaseMapper<SuperNodeSendModel,SuperNodeSendModelDto> getBaseMapper() {
		return this.superNodeSendMapper;
	}

	@Override
	protected void doMode(SuperNodeSendModelDto dto) {
		super.doMode(dto);
		dto.setCurrencyName(currencyService.getNameById(dto.getCurrencyId()));
		dto.setUserName(userService.getNameById(dto.getUserId()));
		dto.setRealName(userService.getRealNameById(dto.getUserId()));
	}

	@Override
	public void createChargeOrder(Date date) {
		SuperNodeChargeModel charge=superNodeChargeService.getByDate(date);
		if(charge!=null){
			int count=superNodeService.getTotalCount();
			SuperNodeModelDto search=new SuperNodeModelDto();
			List<SuperNodeModelDto> list=superNodeService.queryListByDto(search,false);
			if(list!=null){
				BigDecimal quantity= MathUtil.div(charge.getQuantity(),new BigDecimal(count));
				for (SuperNodeModelDto node:list){
					((SuperNodeSendService)AopContext.currentProxy()).createOrder(node.getUserId(),node.getCurrencyId(),quantity,date);
				}
			}
		}
	}

	@Override
	public SuperNodeSendModelDto getByUserIdAndDate(Long userId, Date date) {
		return superNodeSendMapper.getByUserIdAndDate(userId, date);
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public void createOrder(Long userId,Long currencyId,BigDecimal quantity,Date date){
		SuperNodeSendModel send = this.getByUserIdAndDate(userId, date);
		if (send == null) {
			send = new SuperNodeSendModel();
			send.setSendDate(date);
			send.setUserId(userId);
			send.setCurrencyId(currencyId);
			send.setQuantity(quantity);
			send.setStatus(WalletDoEnum.NOT.getCode());
			this.insert(send);
		}
	}


	@Override
	public void realse() {
		SuperNodeSendModelDto search = new SuperNodeSendModelDto();
		search.setStatus(WalletDoEnum.NOT.getCode());
		List<SuperNodeSendModelDto> list = this.queryListByDto(search, false);
		if (list != null) {
			int count=0;
			for (SuperNodeSendModelDto dto : list) {
				count++;
				asyncTask.superNodeRealse(dto.getId());
				//线程池超出
				try {
					if(count%200==0){
						Thread.sleep(1000);
					}
				} catch (InterruptedException e) {
					e.printStackTrace();
				}

			}
		}
	}

	@Override
	@Transactional
	public void recordRelease(Long id) {
		SuperNodeSendModel send = this.getById(id);
		if (send != null && !WalletDoEnum.SUCCESS.getCode().equals(send.getStatus())) {

			BigDecimal changeUsing = send.getQuantity();
			BigDecimal changeFreeze = BigDecimal.ZERO;
			UserWalletModel wallet = userWalletService.modifyWallet(send.getUserId(), send.getCurrencyId(), changeUsing, changeFreeze);
			userWalletLogService.addLog(send.getUserId(), send.getCurrencyId(), changeUsing, WalletLogTypeEnum.SUPERNODE.getCode(),  send.getId()  ,"参加超级分红", wallet);

			send.setStatus(WalletDoEnum.SUCCESS.getCode());
			this.update(send);
		}
	}
}

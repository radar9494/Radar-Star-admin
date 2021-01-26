package com.liuqi.business.service.impl;

import cn.hutool.core.date.DateField;
import cn.hutool.core.date.DateTime;
import com.liuqi.base.BaseMapper;
import com.liuqi.base.BaseServiceImpl;
import com.liuqi.business.constant.KeyConstant;
import com.liuqi.business.constant.ConfigConstant;
import com.liuqi.business.enums.*;
import com.liuqi.business.mapper.CtcOrderMapper;
import com.liuqi.business.model.*;
import com.liuqi.business.service.*;
import com.liuqi.exception.BusinessException;
import com.liuqi.jobtask.CtcAutoCancelJob;
import com.liuqi.redis.NumRepository;
import com.liuqi.utils.MathUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

@Service
@Transactional(readOnly = true)
public class CtcOrderServiceImpl extends BaseServiceImpl<CtcOrderModel,CtcOrderModelDto> implements CtcOrderService{

	@Autowired
	private CtcOrderMapper ctcOrderMapper;
	@Autowired
	private NumRepository numRepository;
	@Autowired
	private CurrencyService currencyService;
	@Autowired
	private UserService userService;
	@Autowired
	private UserWalletService userWalletService;
	@Autowired
	private UserWalletLogService userWalletLogService;
	@Autowired
	private TaskService taskService;
	@Autowired
	private ConfigService configService;
	@Autowired
	private UserStoreService userStoreService;
	@Autowired
	private UserPayService userPayService;
	@Autowired
	private CtcOrderLogService ctcOrderLogService;
	@Override
	public BaseMapper<CtcOrderModel,CtcOrderModelDto> getBaseMapper() {
		return this.ctcOrderMapper;
	}

	@Override
	protected void doMode(CtcOrderModelDto dto) {
		super.doMode(dto);
		dto.setCurrencyName(currencyService.getNameById(dto.getCurrencyId()));
		dto.setUserName(userService.getNameById(dto.getUserId()));
		dto.setRealName(userService.getRealNameById(dto.getUserId()));
		dto.setStoreName(userService.getNameById(dto.getStoreId()));
	}


	@Override
	@Transactional
	public Long createOrder(Long userId, CtcConfigModelDto config, BigDecimal price, BigDecimal quantity, Integer tradeType,String opeName) {
		Integer time = Integer.valueOf(configService.queryValueByName(ConfigConstant.CONFIGNAME_CTC_CANCEL));

		CtcOrderModel order = new CtcOrderModel();
		order.setUserId(userId);
		order.setTradeType(tradeType);
		order.setCurrencyId(config.getCurrencyId());
		order.setStoreId(0L);
		order.setPrice(price);
		order.setQuantity(quantity);
		order.setMoney(MathUtil.mul(order.getPrice(), order.getQuantity()).setScale(2,BigDecimal.ROUND_DOWN));
		order.setStatus(CtcOrderStatusEnum.WAIT.getCode());
		order.setMemo(numRepository.getCtcTradeCode());
		if (time > 0) {
			order.setAutoEndTime(DateTime.now().offset(DateField.MINUTE, time));
		}


		//卖单 冻结币
		if (BuySellEnum.SELL.getCode().equals(order.getTradeType())) {
			UserPayModelDto pay=userPayService.getByUserId(order.getUserId(), UserPayPayTypeEnum.YHK.getCode());
			if(pay!=null && UserPayStatusEnum.USING.getCode().equals(pay.getStatus())){
				order.setName(pay.getName());
				order.setBankNo(pay.getNo());
				order.setBankAddress(pay.getBankAddress());
				order.setBankName(pay.getBankName());
			}else{
				throw new BusinessException("请先配置收款银行卡");
			}
			this.insert(order);

			BigDecimal changeUsing = MathUtil.zeroSub(order.getQuantity());
			BigDecimal changeFreeze = order.getQuantity();
			UserWalletModel wallet=userWalletService.modifyWallet(order.getUserId(), order.getCurrencyId(), changeUsing, changeFreeze);
			userWalletLogService.addLog(order.getUserId(), order.getCurrencyId(), changeUsing, WalletLogTypeEnum.CTC.getCode(),  order.getId() , "下单扣除" , wallet);
		}else{
			this.insert(order);
		}

		ctcOrderLogService.addLog(order.getId(),opeName,"下单");

		//添加定时任务  多少分钟
		this.addTask(order, time);
		return order.getId();
	}

	@Override
	@Transactional
	public void matchStore(Long orderId) {
		CtcOrderModel order = this.getById(orderId);
		if (order.getStatus().equals(CtcOrderStatusEnum.WAIT.getCode())) {
			//匹配商户
			UserStoreModelDto store = userStoreService.getNextMatchStore(order.getCurrencyId(), order.getQuantity(), order.getTradeType());
			if (store != null) {

				order.setStoreId(store.getUserId());
				//如果是买单  商户接单后需要冻结
				if (BuySellEnum.BUY.getCode().equals(order.getTradeType())) {
					order.setName(store.getName());
					order.setBankNo(store.getBankNo());
					order.setBankAddress(store.getBankAddress());
					order.setBankName(store.getBankName());


					BigDecimal changeUsing = MathUtil.zeroSub(order.getQuantity());
					BigDecimal changeFreeze = order.getQuantity();
					UserWalletModel wallet=userWalletService.modifyWallet(order.getStoreId(), order.getCurrencyId(), changeUsing, changeFreeze);
					userWalletLogService.addLog(order.getStoreId(), order.getCurrencyId(), changeUsing, WalletLogTypeEnum.CTC.getCode(),  order.getId() , "接单扣除" , wallet);
				}
				order.setStatus(CtcOrderStatusEnum.RUNING.getCode());
				this.update(order);

				ctcOrderLogService.addLog(order.getId(),"系统匹配","接单");
			}
		}
	}

	@Override
	@Transactional
	public void confirm(Long id, String remark,String opeName) {
		CtcOrderModel order = this.getById(id);
		if (order.getStatus().equals(CtcOrderStatusEnum.RUNING.getCode())) {
			//修改订单未完成
			order.setStatus(CtcOrderStatusEnum.END.getCode());
			order.setRemark(remark);
			this.update(order);

			//买单   扣除商户,用户添加
			if (BuySellEnum.BUY.getCode().equals(order.getTradeType())) {
				BigDecimal changeUsing = BigDecimal.ZERO;
				BigDecimal changeFreeze = MathUtil.zeroSub(order.getQuantity());
				userWalletService.modifyWallet(order.getStoreId(), order.getCurrencyId(), changeUsing, changeFreeze);

				//用户添加
				BigDecimal changeUsing1 = order.getQuantity();
				BigDecimal changeFreeze1 = BigDecimal.ZERO;
				UserWalletModel wallet1 = userWalletService.modifyWallet(order.getUserId(), order.getCurrencyId(), changeUsing1, changeFreeze1);
				userWalletLogService.addLog(order.getUserId(), order.getCurrencyId(), changeUsing1, WalletLogTypeEnum.CTC.getCode(),  order.getId() , "购买", wallet1);

			} else { //卖  用户扣除  商家添加
				BigDecimal changeUsing = BigDecimal.ZERO;
				BigDecimal changeFreeze = MathUtil.zeroSub(order.getQuantity());
				userWalletService.modifyWallet(order.getUserId(), order.getCurrencyId(), changeUsing, changeFreeze);

				//商家添加
				BigDecimal changeUsing1 = order.getQuantity();
				BigDecimal changeFreeze1 = BigDecimal.ZERO;
				UserWalletModel wallet1 = userWalletService.modifyWallet(order.getStoreId(), order.getCurrencyId(), changeUsing1, changeFreeze1);
				userWalletLogService.addLog(order.getStoreId(), order.getCurrencyId(), changeUsing1, WalletLogTypeEnum.CTC.getCode(),  order.getId() , "购买", wallet1);
			}

			ctcOrderLogService.addLog(order.getId(),opeName,"处理订单");
		} else {
			throw new BusinessException("订单状态不为处理中");
		}
	}

	@Override
	@Transactional
	public void cancel(Long orderId,String remark, String opeName) {
		CtcOrderModel order = this.getById(orderId);
		if (order.getStatus().equals(CtcOrderStatusEnum.WAIT.getCode())
				|| order.getStatus().equals(CtcOrderStatusEnum.RUNING.getCode())) {
			order.setRemark(remark);
			//修改记录为完成
			this.cancel(order, "CTC取消返还");

			ctcOrderLogService.addLog(order.getId(),opeName,"取消订单");
		} else {
			throw new BusinessException("订单状态不为处理中");
		}
	}

	@Override
	@Transactional
	public void autoCancel(Long orderId) {
		CtcOrderModel order = this.getById(orderId);
		if (order.getStatus().equals(CtcOrderStatusEnum.WAIT.getCode())
				|| order.getStatus().equals(CtcOrderStatusEnum.RUNING.getCode())) {
			//修改记录为完成
			this.cancel(order, "CTC超时自动取消取消返还");

			ctcOrderLogService.addLog(order.getId(),"系统","超时自动取消订单");
		} else {
			throw new BusinessException("订单状态不为处理中");
		}
	}

	private void cancel(CtcOrderModel order, String remark) {
		//修改记录为完成
		order.setStatus(CtcOrderStatusEnum.CANCEL.getCode());
		this.update(order);

		BigDecimal changeUsing = order.getQuantity();
		BigDecimal changeFreeze = MathUtil.zeroSub(order.getQuantity());
		//买单   商户解冻
		if (BuySellEnum.BUY.getCode().equals(order.getTradeType())) {
			if (order.getStoreId() != null && order.getStoreId() > 0) {
				UserWalletModel wallet = userWalletService.modifyWallet(order.getStoreId(), order.getCurrencyId(), changeUsing, changeFreeze);
				userWalletLogService.addLog(order.getStoreId(), order.getCurrencyId(), changeUsing, WalletLogTypeEnum.CTC.getCode(),  order.getId() , remark, wallet);
			}
		} else {//卖单  用户解冻
			UserWalletModel wallet = userWalletService.modifyWallet(order.getUserId(), order.getCurrencyId(), changeUsing, changeFreeze);
			userWalletLogService.addLog(order.getUserId(), order.getCurrencyId(), changeUsing, WalletLogTypeEnum.CTC.getCode(),  order.getId() , remark, wallet);
		}
	}

	/**
	 * 添加自动取消定时任务
	 *
	 * @param order
	 */
	private void addTask(CtcOrderModel order, Integer time) {
		//用户卖单bu自动取消
		if (BuySellEnum.SELL.getCode().equals(order.getTradeType())) {
			return;
		}
		if (time > 0) {
			String str = order.getId() + "";
			Map<String, Object> params = new HashMap<String, Object>();
			params.put("id", order.getId());
			taskService.addDelayedJob(CtcAutoCancelJob.class.getName(), "ctccancel-" + str, "ctc定时取消" + str, time, params);
		}
	}

}

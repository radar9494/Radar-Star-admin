package com.liuqi.business.service.impl;


import com.liuqi.base.BaseMapper;
import com.liuqi.base.BaseServiceImpl;
import com.liuqi.business.constant.KeyConstant;
import com.liuqi.business.enums.*;
import com.liuqi.business.mapper.OtcOrderMapper;
import com.liuqi.business.model.*;
import com.liuqi.business.service.*;
import com.liuqi.exception.BusinessException;
import com.liuqi.redis.RedisRepository;
import com.liuqi.utils.MathUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class OtcOrderServiceImpl extends BaseServiceImpl<OtcOrderModel,OtcOrderModelDto> implements OtcOrderService{

	@Autowired
	private OtcOrderMapper otcOrderMapper;
	@Autowired
	private OtcOrderRecordService otcOrderRecordService;
	@Autowired
	private CurrencyService currencyService;
	@Autowired
	private UserService userService;
	@Autowired
	private OtcWalletService otcWalletService;
	@Autowired
	private OtcWalletLogService otcWalletLogService;
	@Autowired
	private UserPayService userPayService;
	@Autowired
	private OtcStoreService otcStoreService;
	@Autowired
	private RedisRepository redisRepository;
	@Override
	public BaseMapper<OtcOrderModel,OtcOrderModelDto> getBaseMapper() {
		return this.otcOrderMapper;
	}


	@Override
	protected void doMode(OtcOrderModelDto dto) {
		super.doMode(dto);
		dto.setCurrencyName(currencyService.getNameById(dto.getCurrencyId()));
		dto.setUserName(userService.getNameById(dto.getUserId()));
		dto.setOtcName(userService.getOtcNameById(dto.getUserId()));
		dto.setRealName(userService.getRealNameById(dto.getUserId()));
		OtcStoreModel store=otcStoreService.getByUserId(dto.getUserId(),dto.getCurrencyId());
		if(store!=null){
			dto.setTotal(store.getTotal());
			dto.setSuccess(store.getSuccess());
		}
	}

	/**
	 * 发布
	 *
	 * @param order
	 */
	@Override
	@Transactional
	public void publish(OtcOrderModel order) {
		//输入的数量和价格取八位
		order.setQuantity(order.getQuantity().setScale(8, BigDecimal.ROUND_DOWN));
		order.setPrice(order.getPrice().setScale(8, BigDecimal.ROUND_DOWN));
		order.setStatus(OtcOrderStatusEnum.WAIT.getCode());
		order.setTradeQuantity(BigDecimal.ZERO);
		order.setCancel(OtcOrderCancelEnum.NORMAL.getCode());

		this.insert(order);
		//判断卖
		if (order.getType().equals(BuySellEnum.SELL.getCode())) {
			List<UserPayModelDto> payList=userPayService.getByUserId(order.getUserId());
			if(payList!=null){
				payList=payList.stream().filter(t-> UserPayStatusEnum.USING.getCode().equals(t.getStatus())).collect(Collectors.toList());
			}
			if(payList==null||payList.size()==0){
				throw new BusinessException("未配置收款信息");
			}			BigDecimal quantity=order.getQuantity();
			//可用- 冻结+
			BigDecimal changeUsing= MathUtil.zeroSub(quantity);
			BigDecimal changeFreeze=quantity;
			OtcWalletModel wallet = otcWalletService.modifyWallet(order.getUserId(), order.getCurrencyId(), changeUsing, changeFreeze);
			otcWalletLogService.addLog(order.getUserId(), order.getCurrencyId(), changeUsing, WalletLogTypeEnum.OTC.getCode(), order.getId() , "OTC卖扣除",  wallet);

		}

	}

	/**
	 * 交易
	 *
	 * @param userId
	 * @param orderId
	 * @param quantity
	 * @return
	 */
	@Override
	@Transactional
	public Long trade(Long userId, Long orderId, BigDecimal quantity) {
		quantity = quantity.setScale(8, BigDecimal.ROUND_DOWN);
		OtcOrderModelDto order = this.getById(orderId);
		if (order == null) {
			throw new BusinessException("订单异常");
		}
		if (order.getStatus().equals(OtcOrderStatusEnum.END.getCode())) {
			throw new BusinessException("订单已结束");
		}
		if (order.getCancel().equals(OtcOrderCancelEnum.CANCEL.getCode())) {
			throw new BusinessException("订单已下架");
		}

		if (order.getResidue().compareTo(quantity) < 0) {
			throw new BusinessException("订单数量不足，剩余数量" + order.getResidue());
		}

		//判断自己是否还有未付款的买单
		int count = otcOrderRecordService.getMyBuyNoPay(userId);
		if (count > 0) {
			throw new BusinessException("请先支付未支付订单");
		}

		//如果otc是买单   冻结卖家的币
		if (order.getType().equals(BuySellEnum.BUY.getCode())) {

			//判断是否有收款信息
			List<UserPayModelDto> payList=userPayService.getByUserId(userId);
			if(payList!=null){
				payList=payList.stream().filter(t-> UserPayStatusEnum.USING.getCode().equals(t.getStatus())).collect(Collectors.toList());
			}
			if(payList==null||payList.size()==0){
				throw new BusinessException("未配置收款信息");
			}
			//可用- 冻结+
			BigDecimal changeUsing= MathUtil.zeroSub(quantity);
			BigDecimal changeFreeze=quantity;
			OtcWalletModel wallet = otcWalletService.modifyWallet(userId, order.getCurrencyId(), changeUsing, changeFreeze);
			otcWalletLogService.addLog(userId, order.getCurrencyId(), changeUsing, WalletLogTypeEnum.OTC.getCode(), order.getId() , "OTC卖扣除",  wallet);
		}else{
            String cancelKey= KeyConstant.KEY_OTC_CANCEL+userId;
            Long aLong = redisRepository.getLong(cancelKey);
            if(aLong>=3){
                throw new BusinessException("当日累计取消交易3笔，当日不可再购买");
            }
        }


		//判断订单是否完结
		order.setTradeQuantity(MathUtil.add(order.getTradeQuantity(), quantity));
		if (order.getResidue().compareTo(BigDecimal.ZERO) == 0) {
			order.setStatus(OtcOrderStatusEnum.END.getCode());
		}
		this.update(order);

		//生成订单
		Long recordId = otcOrderRecordService.createRecord(userId,order,quantity);
		return recordId;
	}

	/**
	 * 取消
	 *
	 * @param orderId
	 * @param userId    当前用户
	 * @param checkUser 是否检查用户=userId
	 */
	@Override
	@Transactional
	public void cancel(Long orderId, Long userId, boolean checkUser) {
		OtcOrderModelDto order = this.getById(orderId);
		if (order == null){
			throw new BusinessException("订单异常");
		}
		if(checkUser && !userId.equals(order.getUserId())) {
			throw new BusinessException("非订单发布用户不允许删除");
		}
		if(order.getStatus().equals(OtcOrderStatusEnum.END.getCode())) {
			throw new BusinessException("订单已结束");
		}

		boolean canCancel = otcOrderRecordService.canCancel(orderId);
		//查询出待支付或待收款的单子  申诉的单子
		if (!canCancel) {
			throw new BusinessException("请先处理完未完成的子订单，在取消订单");
		}

		//卖单
		if(order.getType().equals(BuySellEnum.SELL.getCode())) {
			//获取已交易的数量
			BigDecimal successQuantity=otcOrderRecordService.getSuccessQuantity(orderId);
			if(order.getTradeQuantity().compareTo(successQuantity)!=0){
				order.setTradeQuantity(successQuantity);
			}
			//可用+ 冻结-
			BigDecimal quantity=order.getResidue();
			BigDecimal changeUsing= quantity;
			BigDecimal changeFreeze=MathUtil.zeroSub(quantity);
			OtcWalletModel wallet = otcWalletService.modifyWallet(order.getUserId(),order.getCurrencyId(), changeUsing,changeFreeze);
			otcWalletLogService.addLog(order.getUserId(), order.getCurrencyId(), changeUsing, WalletLogTypeEnum.OTC.getCode(), order.getId() , "OTC取消返还",  wallet);
		}

		order.setStatus(OtcOrderStatusEnum.END.getCode());
		this.update(order);
	}

	/**
	 * 上下架修改
	 * @param orderId
	 * @param cancelStatus
	 */
	@Override
	@Transactional
	public void cancelStatus(Long orderId,Integer cancelStatus) {
		OtcOrderModelDto order = this.getById(orderId);
		if (order == null){
			throw new BusinessException("订单异常");
		}
		if(order.getStatus().equals(OtcOrderStatusEnum.END.getCode())) {
			throw new BusinessException("订单已结束");
		}
		otcOrderMapper.updateCancelStatus(orderId,cancelStatus);
	}
}

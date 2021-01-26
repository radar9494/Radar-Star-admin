package com.liuqi.business.service.impl;

import com.liuqi.base.BaseMapper;
import com.liuqi.base.BaseServiceImpl;
import com.liuqi.business.constant.KeyConstant;
import com.liuqi.business.dto.UserTotalDto;
import com.liuqi.business.enums.*;
import com.liuqi.business.mapper.OtcOrderRecordMapper;
import com.liuqi.business.model.*;
import com.liuqi.business.service.*;
import com.liuqi.exception.BusinessException;
import com.liuqi.jobtask.OtcAutoCancelJob;
import com.liuqi.redis.NumRepository;
import com.liuqi.redis.RedisRepository;
import com.liuqi.utils.MathUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class OtcOrderRecordServiceImpl extends BaseServiceImpl<OtcOrderRecordModel,OtcOrderRecordModelDto> implements OtcOrderRecordService{

	@Autowired
	private OtcOrderRecordMapper otcOrderRecordMapper;
	@Autowired
	private CurrencyService currencyService;
	@Autowired
	private UserService userService;
	@Autowired
	private NumRepository numRepository;
	@Autowired
	private OtcOrderRecordLogService otcOrderRecordLogService;
	@Autowired
	private OtcOrderService otcOrderService;
	@Autowired
	private OtcWalletService otcWalletService;
	@Autowired
	private OtcWalletLogService otcWalletLogService;
	@Autowired
	private UserAdminService userAdminService;
	@Autowired
	private OtcConfigService otcConfigService;
	@Autowired
	private TaskService taskService;
	@Autowired
	private OtcStoreService otcStoreService;
	@Autowired
	private RedisRepository redisRepository;
	@Override
	public BaseMapper<OtcOrderRecordModel,OtcOrderRecordModelDto> getBaseMapper() {
		return this.otcOrderRecordMapper;
	}

	@Override
	protected void doMode(OtcOrderRecordModelDto dto) {
		super.doMode(dto);
		dto.setCurrencyName(currencyService.getNameById(dto.getCurrencyId()));
		dto.setBuyUserName(userService.getNameById(dto.getBuyUserId()));
		dto.setSellUserName(userService.getNameById(dto.getSellUserId()));
		dto.setAppealUserName(userService.getNameById(dto.getAppealUser()));
		dto.setBuyOtcName(userService.getOtcNameById(dto.getBuyUserId()));
		dto.setSellOtcName(userService.getOtcNameById(dto.getSellUserId()));
		dto.setAppealOtcName(userService.getOtcNameById(dto.getAppealUser()));
	}

	@Override
	@Transactional
	public Long createRecord(Long userId, OtcOrderModelDto order, BigDecimal quantity) {
		OtcOrderRecordModel record = new OtcOrderRecordModel();
		record.setOrderId(order.getId());
		record.setCurrencyId(order.getCurrencyId());
		//买单
		if (order.getType().equals(BuySellEnum.BUY.getCode())) {
			record.setBuyUserId(order.getUserId());
			record.setSellUserId(userId);
		} else {
			record.setBuyUserId(userId);
			record.setSellUserId(order.getUserId());
		}
		record.setType(order.getType());
		record.setPrice(order.getPrice());
		record.setQuantity(quantity);
		record.setOrderNo(createNo(userId));
		record.setMoney(MathUtil.mul(order.getPrice(), quantity));
		record.setStatus(OtcOrderRecordStatusEnum.WAIT.getCode());
		record.setSuccessStatus(OtcOrderRecordSuccessStatusEnum.TRADEING.getCode());
		record.setMemo(numRepository.getOtcTradeCode());
		record.setAppealType(0);
		record.setAppealUser(0L);
		record.setAppealContent("");
		this.insert(record);

		//日志
		String opeName = userService.getNameById(userId);
		otcOrderRecordLogService.addLog(record.getId(), opeName, "发起交易");

		//添加定时任务
		this.addTask(record);

		return record.getId();
	}

	private String createNo(Long userId){
		return  userId+System.currentTimeMillis()+"";

	}



	@Override
	public int getMyBuyNoPay(Long userId) {
		return otcOrderRecordMapper.getMyBuyNoPay(userId, OtcOrderRecordStatusEnum.WAITPAY.getCode());
	}

	/**
	 * 判断主单是否能取消  判断子单中  状态为WAIT，WAITPAY，WAITGATHERING，APPEAL的数量
	 *
	 * @param orderId
	 * @return
	 */
	@Override
	public boolean canCancel(Long orderId) {
		List<Integer> statusList = new ArrayList<>();
		statusList.add(OtcOrderRecordStatusEnum.WAIT.getCode());
		statusList.add(OtcOrderRecordStatusEnum.WAITPAY.getCode());
		statusList.add(OtcOrderRecordStatusEnum.WAITGATHERING.getCode());
		statusList.add(OtcOrderRecordStatusEnum.APPEAL.getCode());
		return otcOrderRecordMapper.canCancel(orderId, statusList) == 0;
	}

	/**
	 * 接单
	 *
	 * @param userId
	 * @param recordId
	 */
	@Override
	@Transactional
	public void take(Long userId, Long recordId) {
		OtcOrderRecordModel record = this.getById(recordId);
		if (record == null) {
			throw new BusinessException("订单异常");
		}
		if (!record.getStatus().equals(OtcOrderRecordStatusEnum.WAIT.getCode())) {
			throw new BusinessException("订单不为待接单状态");
		}
		OtcOrderModel order = otcOrderService.getById(record.getOrderId());
		if (!userId.equals(order.getUserId())) {
			throw new BusinessException("非发布用户,禁止接单");
		}
		record.setStatus(OtcOrderRecordStatusEnum.WAITPAY.getCode());
		this.update(record);

		String opeName = userService.getNameById(userId);
		otcOrderRecordLogService.addLog(record.getId(), opeName, "接单");

		//添加定时任务
		this.addTask(record);

		//添加数据
		otcStoreService.addTotal(order.getUserId(),order.getCurrencyId());
	}

	/**
	 * 付款  买放操作
	 *
	 * @param userId
	 * @param recordId
	 */
	@Override
	@Transactional
	public void pay(Long userId, Long recordId,Integer payType) {
		OtcOrderRecordModel record = this.getById(recordId);
		if (record == null) {
			throw new BusinessException("订单异常");
		}
		if (!record.getStatus().equals(OtcOrderRecordStatusEnum.WAITPAY.getCode())) {
			throw new BusinessException("订单不为待支付状态");
		}
		if (!record.getBuyUserId().equals(userId)) {
			throw new BusinessException("非买方用户,禁止操作付款");
		}
		record.setStatus(OtcOrderRecordStatusEnum.WAITGATHERING.getCode());
		record.setPayType(payType);
		this.update(record);

		String opeName = userService.getNameById(userId);
		otcOrderRecordLogService.addLog(record.getId(), opeName, "付款");
	}

	/**
	 * 收款
	 *
	 * @param userId
	 * @param recordId
	 */
	@Override
	@Transactional
	public void gathering(Long userId, Long recordId) {
		OtcOrderRecordModel record = this.getById(recordId);
		if (!record.getStatus().equals(OtcOrderRecordStatusEnum.WAITGATHERING.getCode())) {
			throw new BusinessException("订单不为待收款状态");
		}
		if (!record.getSellUserId().equals(userId)) {
			throw new BusinessException("非卖方用户,禁止操作收款");
		}

		//处理收款
		String opeName = userService.getNameById(userId);
		this.gathering(record, opeName, "用户操作收款");

		record.setStatus(OtcOrderRecordStatusEnum.COMPLETE.getCode());
		record.setSuccessStatus(OtcOrderRecordSuccessStatusEnum.SUCCESS.getCode());
		this.update(record);
	}

	/**
	 * 收款操作
	 *
	 * @param record
	 * @param opeName
	 */
	private void gathering(OtcOrderRecordModel record, String opeName, String remark) {
		//扣除卖家冻结币
		BigDecimal sellChangeUsing= BigDecimal.ZERO;
		BigDecimal sellChangeFreeze=MathUtil.zeroSub(record.getQuantity());
		OtcWalletModel sellWallet =otcWalletService.modifyWallet(record.getSellUserId(), record.getCurrencyId(),sellChangeUsing,sellChangeFreeze);

		//买家获取币种
		BigDecimal buyChangeUsing= record.getQuantity();
		BigDecimal buyChangeFreeze=BigDecimal.ZERO;
		OtcWalletModel buyWallet =otcWalletService.modifyWallet(record.getBuyUserId(), record.getCurrencyId(),buyChangeUsing,buyChangeFreeze);
		otcWalletLogService.addLog(record.getBuyUserId(), record.getCurrencyId(), buyChangeUsing, WalletLogTypeEnum.OTC.getCode(), record.getId() ,"OTC交易买获取",  buyWallet);

		otcOrderRecordLogService.addLog(record.getId(), opeName, remark);

		//添加数据
		OtcOrderModel order=otcOrderService.getById(record.getOrderId());
		otcStoreService.addSuccess(order.getUserId(),order.getCurrencyId());
	}


	/**
	 * 取消
	 * 接单：两方都可取消
	 * 待支付：买方可取消
	 * 待收款：买方可取消
	 *
	 * @param userId
	 * @param recordId
	 */
	@Override
	@Transactional
	public void cancel(Long userId, Long recordId) {
		OtcOrderRecordModel record = this.getById(recordId);
		//检查状态  并且状态不为接单，待支付，待收款
		if (!record.getStatus().equals(OtcOrderRecordStatusEnum.WAIT.getCode())
				&& !record.getStatus().equals(OtcOrderRecordStatusEnum.WAITPAY.getCode())
				&& !record.getStatus().equals(OtcOrderRecordStatusEnum.WAITGATHERING.getCode())) {
			throw new BusinessException("订单不允许取消");
		}

		if (!record.getStatus().equals(OtcOrderRecordStatusEnum.WAIT.getCode())
				&& !record.getSellUserId().equals(userId)) {
			throw new BusinessException("非买方用户,禁止操作取消");
		}

		//处理敲
		String opeName = userService.getNameById(userId);
		this.cancel(record, opeName, "用户取消订单",userId);
		record.setSuccessStatus(OtcOrderRecordSuccessStatusEnum.CANCEL.getCode());
		record.setStatus(OtcOrderRecordStatusEnum.CANCEL.getCode());
		this.update(record);
	}

	private void cancel(OtcOrderRecordModel record, String opeName, String remark,Long userId) {

		//返还主单交易量
		OtcOrderModel order = otcOrderService.getById(record.getOrderId());
		String cancelKey= KeyConstant.KEY_OTC_CANCEL+userId;

		order.setTradeQuantity(MathUtil.sub(order.getTradeQuantity(), record.getQuantity()));
		if (order.getStatus().equals(OtcOrderStatusEnum.END.getCode())) {
			order.setStatus(OtcOrderStatusEnum.WAIT.getCode());
		}
		otcOrderService.update(order);

		//如果otc为买单   返回卖家冻结币
		if (record.getType().equals(BuySellEnum.BUY.getCode())) {
			//可用+ 冻结-
			BigDecimal quantity=record.getQuantity();
			BigDecimal changeUsing= quantity;
			BigDecimal changeFreeze=MathUtil.zeroSub(quantity);
			OtcWalletModel wallet =otcWalletService.modifyWallet(record.getSellUserId(), record.getCurrencyId(),changeUsing,changeFreeze);
			otcWalletLogService.addLog(record.getSellUserId(), record.getCurrencyId(), changeUsing, WalletLogTypeEnum.OTC.getCode(), record.getId() , "OTC交易取消返还",  wallet);
		}
		otcOrderRecordLogService.addLog(record.getId(), opeName, remark);
		redisRepository.incrOne(cancelKey);
	}


	@Override
	@Transactional
	public void autoCancel(Long recordId, Integer status) {
		OtcOrderRecordModel record = this.getById(recordId);
		if (record == null) {
			throw new BusinessException("订单异常");
		}
		//未超时 不处理
		if (!record.getStatus().equals(status)) {
			return;
		}
		Long userId=record.getSellUserId();
		if(record.getType().equals(BuySellEnum.SELL.getCode())){
			userId=record.getBuyUserId();
		}


		this.cancel(record, "系统", "超时自动取消",userId);
		record.setSuccessStatus(OtcOrderRecordSuccessStatusEnum.CANCEL.getCode());
		record.setStatus(OtcOrderRecordStatusEnum.CANCEL.getCode());
		this.update(record);
	}

	/**
	 * 申诉
	 * 待收款，买家没有付款： 卖家申诉。
	 * 待收款，卖家不确认： 买家申诉。
	 *
	 * @param userId
	 * @param recordId
	 * @param content
	 */
	@Override
	@Transactional
	public void appeal(Long userId, Long recordId, String content) {
		OtcOrderRecordModel record = this.getById(recordId);
		record.setAppealUser(userId);
		//保存申诉方向  卖家=操作用户
		if (record.getSellUserId().equals(userId)) {
			record.setAppealType(BuySellEnum.SELL.getCode());
		} else {
			record.setAppealType(BuySellEnum.BUY.getCode());
		}
		record.setAppealContent(content);

		record.setStatus(OtcOrderRecordStatusEnum.APPEAL.getCode());
		this.update(record);

		String opeName = userService.getNameById(userId);
		otcOrderRecordLogService.addLog(record.getId(), opeName, "提起申诉:" + content);
	}

	/**
	 * 申诉成功
	 *
	 * @param userId
	 * @param recordId
	 */
	@Override
	@Transactional
	public void appealSuccess(Long userId, Long recordId, String remark) {
		OtcOrderRecordModel record = this.getById(recordId);
		if (!record.getStatus().equals(OtcOrderRecordStatusEnum.APPEAL.getCode())) {
			throw new BusinessException("订单不为申诉状态");
		}

		record.setStatus(OtcOrderRecordStatusEnum.APPEALSUCCESS.getCode());


		String opeName = userAdminService.getNameById(userId);
		otcOrderRecordLogService.addLog(record.getId(), opeName, "申诉成功:" + remark);

		//买方申诉：付款后卖家不确认收款  成功：完成订单
		if (record.getAppealType().equals(BuySellEnum.BUY.getCode())) {
			//收款
			this.gathering(record, opeName,"申诉成功：收款");
			record.setSuccessStatus(OtcOrderRecordSuccessStatusEnum.SUCCESS.getCode());
		} else {//卖方申诉成功   卖家没有付款：取消订单
			//取消订单
			this.cancel(record, opeName,"申诉成功：取消订单",record.getBuyUserId());
			record.setSuccessStatus(OtcOrderRecordSuccessStatusEnum.CANCEL.getCode());
		}
		this.update(record);
	}

	/**
	 * 申诉失败
	 *
	 * @param userId
	 * @param recordId
	 */
	@Override
	@Transactional
	public void appealFail(Long userId, Long recordId, String remark) {
		OtcOrderRecordModel record = this.getById(recordId);
		if (!record.getStatus().equals(OtcOrderRecordStatusEnum.APPEAL.getCode())) {
			throw new BusinessException("订单不为申诉状态");
		}

		record.setStatus(OtcOrderRecordStatusEnum.APPEALFAIL.getCode());
		this.update(record);

		String opeName = userAdminService.getNameById(userId);
		otcOrderRecordLogService.addLog(record.getId(), opeName, "申诉失败:" + remark);

		//买方申诉：付款后卖家不确认收款  失败：取消订单
		if (record.getAppealType().equals(BuySellEnum.BUY.getCode())) {
			//收款
			this.cancel(record, opeName,"申诉失败：取消订单",record.getSellUserId());
			record.setSuccessStatus(OtcOrderRecordSuccessStatusEnum.CANCEL.getCode());
		} else {//卖方申诉失败   买方已付款，修改订单未付款
			//完成订单
			this.gathering(record, opeName, "申诉失败：收款");
			record.setSuccessStatus(OtcOrderRecordSuccessStatusEnum.SUCCESS.getCode());
		}
		this.update(record);
	}

	/**
	 * 添加自动取消定时任务
	 * @param record
	 */
	private void addTask(OtcOrderRecordModel record){
		int status=record.getStatus();
		OtcConfigModel config=otcConfigService.getByCurrencyId(record.getCurrencyId());
		int time=0;
		if(OtcOrderRecordStatusEnum.WAIT.getCode().equals(status)){
			time=config.getWaitTime();
		}else if(OtcOrderRecordStatusEnum.WAITPAY.getCode().equals(status)){
			time=config.getPayTime();
		}
		if(time>0) {
			String str = record.getId() + "-" + status;
			Map<String, Object> params = new HashMap<String, Object>();
			params.put("id", record.getId());
			params.put("status", status);
			taskService.addDelayedJob(OtcAutoCancelJob.class.getName(), "otccancel-" + str, "otc定时取消" + str, time, params);
		}
	}


	@Override
	public Map<Long, BigDecimal> stat(List<Long> userIdList, Integer type) {
		Long usdtId=currencyService.getUsdtId();
		List<UserTotalDto> list=null;
		List<Integer> statusList=new ArrayList<>();
		statusList.add(OtcOrderRecordSuccessStatusEnum.TRADEING.getCode());
		statusList.add(OtcOrderRecordSuccessStatusEnum.SUCCESS.getCode());
		if(BuySellEnum.BUY.getCode().equals(type)) {
			list = otcOrderRecordMapper.statBuy(userIdList,statusList, usdtId);
		}else{
			list = otcOrderRecordMapper.statSell(userIdList,statusList, usdtId);
		}
		return list.stream().collect(Collectors.toMap(t->t.getUserId(), t->t.getQuantity()));
	}

	@Override
	public BigDecimal getSuccessQuantity(Long orderId) {
		BigDecimal quantity= otcOrderRecordMapper.getSuccessQuantity(orderId,OtcOrderRecordSuccessStatusEnum.SUCCESS.getCode());
		return quantity==null?BigDecimal.ZERO:quantity;
	}

	@Override
	public OtcOrderRecordModel getMyWaitPay(Long userId) {
		return otcOrderRecordMapper.getMyWaitPay(userId);
	}
}

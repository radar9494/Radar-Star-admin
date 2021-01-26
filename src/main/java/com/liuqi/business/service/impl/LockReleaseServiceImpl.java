package com.liuqi.business.service.impl;


import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.liuqi.base.BaseMapper;
import com.liuqi.base.BaseServiceImpl;
import com.liuqi.business.constant.KeyConstant;
import com.liuqi.business.dto.ReleaseConfigDto;
import com.liuqi.business.enums.*;
import com.liuqi.business.mapper.LockReleaseMapper;
import com.liuqi.business.model.*;
import com.liuqi.business.service.*;
import com.liuqi.redis.RedisRepository;
import com.liuqi.utils.DateUtil;
import com.liuqi.utils.MathUtil;
import io.shardingsphere.api.HintManager;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Date;
import java.util.concurrent.TimeUnit;

@Service
@Transactional(readOnly = true)
public class LockReleaseServiceImpl extends BaseServiceImpl<LockReleaseModel,LockReleaseModelDto> implements LockReleaseService{

	@Autowired
	private LockReleaseMapper lockReleaseMapper;
	@Autowired
	private UserWalletService userWalletService;
	@Autowired
	private UserWalletLogService userWalletLogService;
	@Autowired
	private LockConfigService lockConfigService;
	@Autowired
	private TradeRecordService tradeRecordService;
	@Autowired
	private LockWalletService lockWalletService;
	@Autowired
	private MessageService messageService;
	@Autowired
	private RedisRepository redisRepository;
	@Autowired
	private UserService userService;
	@Autowired
	private CurrencyService currencyService;
	@Autowired
	private LockWalletLogService lockWalletLogService;
	@Override
	public BaseMapper<LockReleaseModel,LockReleaseModelDto> getBaseMapper() {
		return this.lockReleaseMapper;
	}

	@Override
	@Transactional
	public Long createRelease(Long recordId, Integer tradeType) {
		Long releaseId=-1L;
		HintManager hintManager = HintManager.getInstance();
		hintManager.setMasterRouteOnly();
		TradeRecordModel record = tradeRecordService.getById(recordId);
		if (record != null) {
			Date date = new Date();
			LockConfigModel config = lockConfigService.getByTradeId(record.getTradeId());
			//1判断开关以及是否是释放时间
			if (config != null && lockConfigService.canRelease(config, tradeType, date)) {
				String dateStr = DateUtil.formatDate(date, "MMdd");
				String orderId = record.getBuyTrusteeId() + "";
				Long userId = record.getBuyUserId();
				if (BuySellEnum.SELL.getCode().equals(tradeType)) {
					orderId = record.getSellTrusteeId() + "";
					userId = record.getSellUserId();
				}
				//2无锁仓币  返回
				LockWalletModel lock = lockWalletService.getByUserAndCurrencyId(userId, config.getCurrencyId());
				if (lock.getLocking().compareTo(BigDecimal.ZERO) <= 0) {
					messageService.insertMessageError(userId, MessageTypeEnum.LOCK_ZORE.getCode(), MessageTypeEnum.LOCK_ZORE.getName());
					return -1L;
				}
				//3获取配置信息
				ReleaseConfigDto rConfig = this.getReleaseConfig(config,userId,record.getTradeId(),dateStr, tradeType);

				//4获取用户最大释放数   当用户释放数量>系统最大释放数量时，最大释放=系统最大值
				//用户最大释放
				BigDecimal userMax=MathUtil.mul(lock.getLocking(),MathUtil.divPercent(rConfig.getDayRate()));
				BigDecimal sysMax=MathUtil.mul(rConfig.getDayMax(),MathUtil.divPercent(rConfig.getDayRate()));
				if(sysMax.compareTo(BigDecimal.ZERO)>0 && userMax.compareTo(sysMax)>0){
					userMax=sysMax;
				}

				//5获取今日释放信息
				String trusteeIds = redisRepository.getString(rConfig.getTrusteeIdsKey());
				JSONArray todayTrusteeIds= StringUtils.isNotEmpty(trusteeIds)? JSONArray.parseArray(trusteeIds):new JSONArray();
				//配置了释放次数 0表示不限制
				if(rConfig.getTimes()>0 &&!todayTrusteeIds.contains(orderId) && todayTrusteeIds.size()>=rConfig.getTimes()){
					int type=BuySellEnum.BUY.getCode().equals(tradeType)?MessageTypeEnum.LOCK_MAX_TIMES_BUY.getCode():MessageTypeEnum.LOCK_MAX_TIMES_SELL.getCode();
					messageService.insertMessageError(userId, type, BuySellEnum.getName(tradeType)+"已达释放次数"+rConfig.getTimes());
					return -1L;
				}

				//6获取已释放数量
				BigDecimal alreadyQuantity = redisRepository.getBigDecimal(rConfig.getAlreadyTotalKey());
				if(alreadyQuantity.compareTo(userMax)>=0){
					int type=BuySellEnum.BUY.getCode().equals(tradeType)?MessageTypeEnum.LOCK_MAX_TIMES_BUY.getCode():MessageTypeEnum.LOCK_MAX_TIMES_SELL.getCode();
					messageService.insertMessageError(userId, type, BuySellEnum.getName(tradeType)+"已达释放数量"+rConfig.getDayMax());
					return -1L;
				}

				//7本次释放数量 = 成交数量*单次释放百分比
				BigDecimal releaseQuantity = MathUtil.mul(record.getTradeQuantity(), MathUtil.divPercent(rConfig.getTimesRate()));

				//8计算本次释放 =本次+已释放>最大值    则释放=最大值-已释放
				if(MathUtil.add(alreadyQuantity,releaseQuantity).compareTo(userMax)>0){
					releaseQuantity=MathUtil.sub(userMax,alreadyQuantity);
				}

				//9生成记录，扣除锁仓
				releaseId=this.releaseRecord(userId, config.getCurrencyId(), tradeType, recordId, releaseQuantity, date, orderId);

				//10修改记录信息
				if(!todayTrusteeIds.contains(orderId)){
					todayTrusteeIds.add(orderId);
					redisRepository.set(rConfig.getTrusteeIdsKey(), JSONObject.toJSONString(todayTrusteeIds), 1L, TimeUnit.DAYS);
				}
				redisRepository.set(rConfig.getAlreadyTotalKey(), MathUtil.add(alreadyQuantity, releaseQuantity).toString(), 1L, TimeUnit.DAYS);
			}
		}

		return releaseId;
	}

	private ReleaseConfigDto getReleaseConfig(LockConfigModel config, Long userId,Long tradeId,String dateStr, Integer tradeType) {
		ReleaseConfigDto dto = new ReleaseConfigDto();
		if (BuySellEnum.BUY.getCode().equals(tradeType)) {
			dto.setTimes(config.getBuyTimes());
			dto.setTimesRate(config.getBuyTimesRate());
			dto.setDayRate(config.getBuyDayRate());
			dto.setDayMax(config.getBuyDayMax());
			dto.setAlreadyTotalKey(KeyConstant.KEY_LOCK_RELEASE_BUY + dateStr+userId+"_"+tradeId);
			dto.setTrusteeIdsKey(KeyConstant.KEY_LOCK_RELEASE_TRUESEE_BUY + dateStr+userId+"_"+tradeId);
		} else {
			dto.setTimes(config.getSellTimes());
			dto.setTimesRate(config.getSellTimesRate());
			dto.setDayRate(config.getSellDayRate());
			dto.setDayMax(config.getSellDayMax());
			dto.setAlreadyTotalKey(KeyConstant.KEY_LOCK_RELEASE_SELL + dateStr+userId+"_"+tradeId);
			dto.setTrusteeIdsKey(KeyConstant.KEY_LOCK_RELEASE_TRUESEE_SELL + dateStr+userId+"_"+tradeId);
		}
		return dto;
	}

	/**
	 * 释放记录以及扣除锁仓币
	 *
	 * @param userId
	 * @param currencyId
	 * @param tradeType
	 * @param recordId
	 * @param quantity
	 * @param date
	 * @param remark
	 */
	private Long releaseRecord(Long userId, Long currencyId, Integer tradeType, Long recordId, BigDecimal quantity,  Date date, String remark) {
		LockReleaseModel release = new LockReleaseModel();
		release.setRemark(remark);
		release.setSendDate(date);
		release.setTradeType(tradeType);
		release.setUserId(userId);
		release.setCurrencyId(currencyId);
		release.setQuantity(quantity);
		release.setStatus(WalletDoEnum.NOT.getCode());
		release.setOrderId(recordId);
		release.setCreateTime(date);

		//扣除锁仓数量
		BigDecimal changeUsing = MathUtil.zeroSub(release.getQuantity());
		BigDecimal changeFreeze = BigDecimal.ZERO;
		LockWalletModelDto lcok=lockWalletService.modifyWallet(release.getUserId(), release.getCurrencyId(), changeUsing, changeFreeze);
		lockWalletLogService.addLog(release.getUserId(), release.getCurrencyId(), changeUsing, LockWalletLogTypeEnum.SYS.getCode(), release.getId(),"锁仓释放", lcok);

		release.setSnapLock(lcok.getLocking());
		this.insert(release);

		return release.getId();
	}


	@Override
	@Transactional
	public void recordRelease(Long id) {
		LockReleaseModel release = this.getById(id);
		if (release != null && !WalletDoEnum.SUCCESS.getCode().equals(release.getStatus())) {
			BigDecimal changeUsing = release.getQuantity();
			BigDecimal changeFreeze = BigDecimal.ZERO;
			UserWalletModel wallet = userWalletService.modifyWallet(release.getUserId(), release.getCurrencyId(), changeUsing, changeFreeze);
			userWalletLogService.addLog(release.getUserId(), release.getCurrencyId(), changeUsing, WalletLogTypeEnum.RELEASE.getCode(), release.getId() , "锁仓释放", wallet);

			release.setStatus(WalletDoEnum.SUCCESS.getCode());
			this.update(release);
		}
	}


	@Override
	public LockReleaseModelDto getByDate(Long userId, Long recordId, Integer tradeType, Date date) {
		return lockReleaseMapper.getByDate(userId,recordId,tradeType,date);
	}

	@Override
	public BigDecimal getTodayQuantityByDate(Long userId, Long currencyId, Integer tradeType) {
		Date date=new Date();
		BigDecimal total=lockReleaseMapper.getTodayQuantityByDate(userId,currencyId,tradeType,date);
		return total!=null?total:BigDecimal.ZERO;
	}

	@Override
	protected void doMode(LockReleaseModelDto dto) {
		super.doMode(dto);
		dto.setUserName(userService.getNameById(dto.getUserId()));
		dto.setRealName(userService.getRealNameById(dto.getUserId()));
		dto.setCurrencyName(currencyService.getNameById(dto.getCurrencyId()));
	}
}

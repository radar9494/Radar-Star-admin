package com.liuqi.business.service.impl;


import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import cn.hutool.log.Log;
import cn.hutool.log.dialect.log4j2.Log4j2LogFactory;
import com.liuqi.base.BaseMapper;
import com.liuqi.base.BaseServiceImpl;
import com.liuqi.business.constant.KeyConstant;
import com.liuqi.business.constant.KeyConstant;
import com.liuqi.business.dto.CurrencyCountDto;
import com.liuqi.business.dto.chain.ExtractSearchDto;
import com.liuqi.business.enums.*;
import com.liuqi.business.mapper.ExtractMapper;
import com.liuqi.business.model.*;
import com.liuqi.business.service.*;
import com.liuqi.exception.BusinessException;
import com.liuqi.redis.RedisRepository;
import com.liuqi.utils.DateTimeUtils;
import com.liuqi.utils.MathUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
@Transactional(readOnly = true)
public class ExtractServiceImpl extends BaseServiceImpl<ExtractModel,ExtractModelDto> implements ExtractService{
	private static Log log = Log4j2LogFactory.get("extract");
	@Autowired
	private ExtractMapper extractMapper;
	@Autowired
	private UserWalletService userWalletService;
	@Autowired
	private UserWalletLogService userWalletLogService;
	@Autowired
	private AutoExtractService autoExtractService;
	@Autowired
	private RedisRepository redisRepository;
	@Autowired
	private UserService userService;
	@Autowired
	private CurrencyService currencyService;
	@Autowired
	private UserAdminService userAdminService;
	@Autowired
	private UserRechargeAddressService userRechargeAddressService;
	@Autowired
	private RechargeService rechargeService;
	@Autowired
	private AuthCodeService authCodeService;
	@Autowired
	private LockWalletService lockWalletService;
	@Autowired
	private LockWalletLogService lockWalletLogService;
	@Autowired
	private ConfigService configService;
	@Override
	public BaseMapper<ExtractModel,ExtractModelDto> getBaseMapper() {
		return this.extractMapper;
	}

	@Transactional
	@Override
	public void extractApply(ExtractModel extractModel,CurrencyConfigModel config) {
		boolean using=WalletTypeEnum.USING.getCode().equals(config.getWalletType());
		CurrencyModel currency=currencyService.getById(extractModel.getCurrencyId());
		extractModel.setProtocol(currency.getProtocol());
		extractModel.setWalletType(config.getWalletType());
		//提币如果是usdt  并且是ERC的地址
		Long usdtId=currencyService.getUsdtId();
		if(usdtId.equals(extractModel.getCurrencyId()) && (extractModel.getAddress().startsWith("0x")||extractModel.getAddress().startsWith("0X"))){
			extractModel.setProtocol(ProtocolEnum.ETH.getCode());
		}
		boolean checkDayMax= SwitchEnum.isOn(config.getExtractMaxDaySwitch());

		extractModel.setStatus(ExtractMoneyEnum.APPLY_ING.getCode());
		Long userId = extractModel.getUserId();

		//检查每天最大提币数量
		String quantityKey = KeyConstant.KEY_EXTRACT_QUANTITY + DateTimeUtils.currentDate("yyyy-MM-dd") + ":" + userId + ":" + extractModel.getCurrencyId();
		BigDecimal quantity = BigDecimal.ZERO;
		if(checkDayMax) {
			String quantityStr = redisRepository.getString(quantityKey);
			if (StringUtils.isNotEmpty(quantityStr)) {
				quantity = new BigDecimal(quantityStr);
			}
			quantity = MathUtil.add(quantity, extractModel.getQuantity());
			if (config.getExtractMaxDay().compareTo(BigDecimal.ZERO)>0 && quantity.compareTo(config.getExtractMaxDay()) > 0) {
				throw new BusinessException("超出每天最大提取数量!最大提取数量为:" + config.getExtractMaxDay());
			}
		}

		//计算手续费
		BigDecimal poundage = MathUtil.mul(config.getExtractRate(),BigDecimal.valueOf(0.01));
		//百分比
		if (YesNoEnum.YES.getCode().equals(config.getPercentage())) {
			poundage = MathUtil.mul(extractModel.getQuantity(),poundage);
		} else {
			poundage = config.getExtractRate();
		}
		//手续费
		extractModel.setRate(poundage);
		extractModel.setRateCurrencyId(config.getRateCurrencyId());
		UserWalletModel rateWallet = userWalletService.getByUserAndCurrencyId(extractModel.getUserId(), config.getRateCurrencyId());
		if (rateWallet.getUsing().compareTo(poundage) < 0) {
			throw new BusinessException("手续费不足!");
		}
		rateWallet.setUsing(rateWallet.getUsing().subtract(poundage));
		userWalletService.update(rateWallet);
		userWalletLogService.addLog(extractModel.getUserId(), extractModel.getCurrencyId(), poundage.negate(), WalletLogTypeEnum.EXTRACT_RATE.getCode(), extractModel.getId(),"提币:"+extractModel.getRealQuantity(),rateWallet);



		//钱包操作
		BigDecimal canUsing=BigDecimal.ZERO;
		if(using) {
			UserWalletModel walletTemp = userWalletService.getByUserAndCurrencyId(extractModel.getUserId(), extractModel.getCurrencyId());
			canUsing=walletTemp.getUsing();
			Long rdtId=currencyService.getRdtId();
			if(extractModel.getCurrencyId().equals(rdtId)){
				if(walletTemp.getGatewaySwitch()==0){
					throw new BusinessException("网关未开启!");
				}
			}
			if (canUsing.compareTo(extractModel.getQuantity()) < 0) {
				throw new BusinessException("可用的币不足,提币失败");
			}
			extractModel.setFreezeQuantity(extractModel.getQuantity());
			extractModel.setRealQuantity(extractModel.getQuantity().setScale(4,BigDecimal.ROUND_DOWN));

		}else{
			LockWalletModel walletTemp = lockWalletService.getByUserAndCurrencyId(extractModel.getUserId(), extractModel.getCurrencyId());
			canUsing=walletTemp.getLocking();
			if (canUsing.compareTo(extractModel.getQuantity()) < 0) {
				throw new BusinessException("可用的币不足,提币失败");
			}
		}
		//设置到账数量和冻结数量
		//手续费+提现数量>可用数量
//		if (MathUtil.add(extractModel.getQuantity(),poundage).compareTo(canUsing) > 0) {
//			//扣除提现   冻结=提现数量   到账=提现数量-手续费
//			extractModel.setFreezeQuantity(extractModel.getQuantity());
//			extractModel.setRealQuantity(MathUtil.sub(extractModel.getQuantity(), poundage));
//		} else {
//			//扣除可用数量  冻结=提现+手续费  到账=提现数量
//			extractModel.setFreezeQuantity(MathUtil.add(extractModel.getQuantity(), poundage));
//			extractModel.setRealQuantity(extractModel.getQuantity());
//		}

		//默认值
		extractModel.setType(InnerOuterEnum.OUTER.getCode());
		extractModel.setReceiveUserId(0L);

		//判断内部外部
		String add=extractModel.getAddress();
		if(ProtocolEnum.EOS.getCode().equals(currency.getProtocol())||ProtocolEnum.XRP.getCode().equals(currency.getProtocol())){
			add=extractModel.getMemo();
		}
		extractModel.setHash("");
		Long receiveUserId=userRechargeAddressService.findBindingUserIdByAddress(add,currency.getProtocol());
		if(receiveUserId!=null && receiveUserId>0){
			extractModel.setType(InnerOuterEnum.INNER.getCode());
			extractModel.setReceiveUserId(receiveUserId);
			extractModel.setHash("0x"+ UUID.randomUUID());
		}



		extractModel.setRate(poundage);

		this.insert(extractModel);







		BigDecimal changeUsing = MathUtil.zeroSub(extractModel.getFreezeQuantity());
		BigDecimal changeFreeze = extractModel.getFreezeQuantity();
		if(using) {
			UserWalletModel wallet = userWalletService.modifyWallet(extractModel.getUserId(), extractModel.getCurrencyId(), changeUsing, changeFreeze);
			userWalletLogService.addLog(extractModel.getUserId(), extractModel.getCurrencyId(), changeUsing, WalletLogTypeEnum.EXTRACT.getCode(), extractModel.getId(),"提币:"+extractModel.getRealQuantity(),wallet);

		}else{
			LockWalletModel wallet = lockWalletService.modifyWallet(extractModel.getUserId(), extractModel.getCurrencyId(), changeUsing, changeFreeze);
			lockWalletLogService.addLog(extractModel.getUserId(), extractModel.getCurrencyId(), changeUsing, LockWalletLogTypeEnum.EXTRACT.getCode(), extractModel.getId(),"提币:"+extractModel.getRealQuantity(),wallet);
		}

		//数量保存redis
		if(checkDayMax) {
			redisRepository.set(quantityKey, quantity + "", 1L, TimeUnit.DAYS);
		}

	   if(extractModel.getType().equals(InnerOuterEnum.INNER.getCode())){
		   String s = configService.queryValueByName("extract.auto.quantity");
           if(extractModel.getQuantity().compareTo(new BigDecimal(s))<=0){
			   this.confirmOrder(extractModel.getId(),"","",1L);
		   }
	   }
	}
	@Transactional
	@Override
	public void refuseOrder(Long orderId, String reason, Long adminUserId) {
		ExtractModel extractModel = extractMapper.getById(orderId);
		String quantityKey = KeyConstant.KEY_EXTRACT_QUANTITY + DateTimeUtils.currentDate("yyyy-MM-dd") + ":" + extractModel.getUserId() + ":" + extractModel.getCurrencyId();
		if (ExtractMoneyEnum.APPLY_ING.getCode().equals(extractModel.getStatus())) {
			extractModel.setReason(reason);
			extractModel.setDealDate(new Date());
			extractModel.setStatus(ExtractMoneyEnum.APPLY_FAIL.getCode());
			extractModel.setDealAdminId(adminUserId);
			if (this.update(extractModel)) {
				String content = "提币订单:" + orderId + ",提币数量:" + extractModel.getQuantity();
				loggerService.insert(LoggerModelDto.TYPE_REFUSE, content,"拒绝提币", adminUserId);

				BigDecimal changeUsing = extractModel.getFreezeQuantity();
				BigDecimal changeFreeze = MathUtil.zeroSub(extractModel.getFreezeQuantity());
				//可用提现
				if(WalletTypeEnum.USING.getCode().equals(extractModel.getWalletType())) {
					UserWalletModel wallet = userWalletService.modifyWallet(extractModel.getUserId(), extractModel.getCurrencyId(), changeUsing, changeFreeze);
					userWalletLogService.addLog(extractModel.getUserId(), extractModel.getCurrencyId(), extractModel.getFreezeQuantity(), WalletLogTypeEnum.EXTRACT.getCode(), extractModel.getId(), "提币拒绝返还:" + extractModel.getRealQuantity(), wallet);


					UserWalletModel rateWallet = userWalletService.getByUserAndCurrencyId(extractModel.getUserId(), extractModel.getRateCurrencyId());
					rateWallet.setUsing(rateWallet.getUsing().add(extractModel.getRate()));
					userWalletService.update(rateWallet);
					userWalletLogService.addLog(extractModel.getUserId(), extractModel.getRateCurrencyId(), extractModel.getRate(), WalletLogTypeEnum.EXTRACT_RATE.getCode(), extractModel.getId(), "提币拒绝返还:" + extractModel.getRate(), rateWallet);
				}else{
					LockWalletModel wallet = lockWalletService.modifyWallet(extractModel.getUserId(), extractModel.getCurrencyId(), changeUsing, changeFreeze);
					lockWalletLogService.addLog(extractModel.getUserId(), extractModel.getCurrencyId(), extractModel.getFreezeQuantity(), LockWalletLogTypeEnum.EXTRACT.getCode(), extractModel.getId(), "提币拒绝返还:" + extractModel.getRealQuantity(), wallet);
				}

				//判断是否是今天的提现单子
				if(extractModel.getCreateTime().compareTo(DateUtil.beginOfDay(new Date()))>=0) {
					//每天最大提币修改
					BigDecimal quantity = BigDecimal.ZERO;
					String quantityStr = redisRepository.getString(quantityKey);
					if (StringUtils.isNotEmpty(quantityStr)) {
						quantity = new BigDecimal(quantityStr);
						quantity = quantity.subtract(extractModel.getQuantity());
						if (quantity.compareTo(BigDecimal.ZERO) >= 0) {
							redisRepository.set(quantityKey, quantity + "", 1L, TimeUnit.DAYS);
						}
					}
				}
			}
		}
	}

	@Transactional
	@Override
	public void confirmOrder(Long orderId, String reason, String hash, Long adminUserId) {
		ExtractModel extractRecord = this.getById(orderId);
		if (ExtractMoneyEnum.APPLY_ING.getCode().equals(extractRecord.getStatus())) {
			extractRecord.setHash(hash);
			extractRecord.setReason(reason);
			extractRecord.setDealDate(new Date());
			extractRecord.setStatus(ExtractMoneyEnum.APPLY_SUCCESS.getCode());
			extractRecord.setDealAdminId(adminUserId);
			log.info("更新订单后" + extractRecord);
			this.update(extractRecord);


			BigDecimal changeFreeze = MathUtil.zeroSub(extractRecord.getFreezeQuantity());
			//可用提现
			if(WalletTypeEnum.USING.getCode().equals(extractRecord.getWalletType())) {
				userWalletService.modifyWalletFreeze(extractRecord.getUserId(), extractRecord.getCurrencyId(), changeFreeze);
			}else{
				lockWalletService.modifyWalletFreeze(extractRecord.getUserId(), extractRecord.getCurrencyId(), changeFreeze);
			}
			//内部转账
			if(extractRecord.getType().equals(InnerOuterEnum.INNER.getCode())) {
				//接收用户  可用添加
				Long receiveId = extractRecord.getReceiveUserId();
				//内部充值信息
				rechargeService.innerRecharge(receiveId, extractRecord.getCurrencyId(), extractRecord.getRealQuantity(), extractRecord.getAddress(), extractRecord.getId() + "", new Date(),extractRecord.getProtocol());
			}

			String content = "同意：提币订单号为:" + orderId;
			loggerService.insert(LoggerModelDto.TYPE_UPDATE, content, "提币", adminUserId);

			//短信提醒
			this.sendSms(extractRecord);
		}
	}





	/**
	 * 查询接口数据
	 */
	@Override
	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public void doSuccess(ExtractModel model, ExtractSearchDto dto) {
		if (StringUtils.isNotEmpty(dto.getHash())) {
			//判断是否异常数据
			if (dto.getToAddress().equals(model.getAddress()) && (dto.getQuantity().compareTo(model.getRealQuantity()) == 0)) {
				//金额处理
				this.doIt(model, dto.getUpdateTime(), dto.getHash());
			} else {
				throw new BusinessException("数量：" + dto.getQuantity() + ",查询地址为：" + dto.getToAddress());
			}
		}
	}

	private void doIt(ExtractModel extractRecord,Date dealDate,String hash){
		if (ExtractMoneyEnum.APPLY_DOING.getCode().equals(extractRecord.getStatus())) {
			extractRecord.setDealDate(dealDate);
			extractRecord.setHash(hash);
			extractRecord.setRemark("");
			extractRecord.setStatus(ExtractMoneyEnum.APPLY_SUCCESS.getCode());
			this.update(extractRecord);

			BigDecimal changeFreeze=MathUtil.zeroSub(extractRecord.getFreezeQuantity());
			if(WalletTypeEnum.USING.getCode().equals(extractRecord.getWalletType())) {
				userWalletService.modifyWalletFreeze(extractRecord.getUserId(), extractRecord.getCurrencyId(), changeFreeze);
			}else{
				lockWalletService.modifyWalletFreeze(extractRecord.getUserId(), extractRecord.getCurrencyId(), changeFreeze);
			}

			//短信提醒
			this.sendSms(extractRecord);
		}
	}

	@Transactional
	@Override
	public void autoExtract(Long orderId, Long adminUserId) {
		ExtractModel extractRecord = this.getById(orderId);
		if (ExtractMoneyEnum.APPLY_ING.getCode().equals(extractRecord.getStatus())|| ExtractMoneyEnum.APPLY_DOING.getCode().equals(extractRecord.getStatus())) {
			boolean doIt=autoExtractService.autoExtract(extractRecord,extractRecord.getRealQuantity());
			if(doIt){
				extractRecord.setStatus(ExtractMoneyEnum.APPLY_DOING.getCode());
				extractRecord.setDealAdminId(adminUserId);
				this.update(extractRecord);
				String content = "发送到提币系统：提币订单号为:" + orderId;
				loggerService.insert(LoggerModelDto.TYPE_UPDATE, content, "提取", adminUserId);
			}
		}

	}

	/**
	 * 按照日期查询各币种笔数和数量
	 *
	 * @param date
	 * @return
	 */
	@Override
	public List<CurrencyCountDto> queryCountByDate(Date date,Long currencyId){
		return extractMapper.queryCountByDate(date,currencyId,ExtractMoneyEnum.APPLY_SUCCESS.getCode());
	}

	@Override
	@Transactional
	public void doWait(ExtractModel model, ExtractSearchDto dto) {
		if (ExtractMoneyEnum.APPLY_DOING.getCode().equals(model.getStatus())) {
			model.setDealDate(new Date());
			model.setHash("");
			model.setRemark("提现打回");
			model.setStatus(ExtractMoneyEnum.APPLY_ING.getCode());
			this.update(model);
		}
	}

	@Override
	public BigDecimal getTotal(ExtractModelDto rechargeModelDto) {
		return extractMapper.getTotal(rechargeModelDto);
	}

	@Override
	protected void doMode(ExtractModelDto dto) {
		super.doMode(dto);
		dto.setUserName(userService.getNameById(dto.getUserId()));
		dto.setRealName(userService.getRealNameById(dto.getUserId()));
		dto.setCurrencyName(currencyService.getNameById(dto.getCurrencyId()));
		dto.setRateCurrencyName(currencyService.getNameById(dto.getRateCurrencyId()));
		//接收用户
		if(dto.getReceiveUserId()!=null && dto.getReceiveUserId()>0) {
			dto.setReceiveUserName(userService.getNameById(dto.getReceiveUserId()));
		}
		if(dto.getDealAdminId()!=null && dto.getDealAdminId()>0) {
			dto.setDealAdminName(userAdminService.getNameById(dto.getDealAdminId()));
		}
	}

	private void sendSms(ExtractModel extract){
		String currencyName=currencyService.getNameById(extract.getCurrencyId());
		String sms=String.format("尊敬的用户您好！您于%s提取%s已经汇出，提取数量为%s。", DateTime.of(extract.getCreateTime()).toString("yyyy-MM-dd HH:mm:ss"),currencyName,extract.getQuantity().setScale(4,BigDecimal.ROUND_DOWN));
		authCodeService.sendRechargeExtractSms(extract.getUserId(),sms,"提现");
	}
}

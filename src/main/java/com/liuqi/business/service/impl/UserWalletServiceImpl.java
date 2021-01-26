package com.liuqi.business.service.impl;


import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpUtil;
import com.liuqi.anno.user.CurUserId;
import com.liuqi.base.BaseMapper;
import com.liuqi.base.BaseServiceImpl;
import com.liuqi.business.dto.api.AllWalletDto;
import com.liuqi.business.enums.UsingEnum;
import com.liuqi.business.enums.WalletLogTypeEnum;
import com.liuqi.business.mapper.UserWalletMapper;
import com.liuqi.business.model.*;
import com.liuqi.business.service.*;
import com.liuqi.exception.BusinessException;
import com.liuqi.redis.lock.RedissonLockUtil;
import com.liuqi.utils.MathUtil;
import com.liuqi.utils.SignUtils;
import org.apache.commons.lang3.StringUtils;
import org.redisson.api.RLock;
import org.springframework.aop.framework.AopContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Transactional(readOnly = true)
public class UserWalletServiceImpl extends BaseServiceImpl<UserWalletModel,UserWalletModelDto> implements UserWalletService{

	@Autowired
	private UserWalletMapper userWalletMapper;

	@Override
	public BaseMapper<UserWalletModel,UserWalletModelDto> getBaseMapper() {
		return this.userWalletMapper;
	}
	@Autowired
	private CurrencyService currencyService;
	@Autowired
	private UserWalletUpdateLogService userWalletUpdateLogService;
	@Autowired
	private UserService userService;
	@Autowired
	private UserWalletLogService userWalletLogService;
	@Autowired
	private ReConfigService reConfigService;
	@Autowired
	private UserRechargeAddressService userRechargeAddressService;
	@Autowired
	private ConfigService configService;
	/**
	 * 修改
	 * @param t
	 */
	@Override
	@Transactional
	public boolean update(UserWalletModel t){
		cleanAllCache();
		cleanCacheByModel(t);
		//检查是否满足
		check(t);
		t.setUpdateTime(new Date());
		boolean status= this.getBaseMapper().update(t)>0;
		if(status){
			this.afterUpdateOperate(t);
			return status;
		}else{
			throw new BusinessException(t.getClass().getName()+"-id:"+t.getId()+"更新失败");
		}
	}


	/**
	 * 查询用户钱包信息
	 * @param userId
	 * @param currencyId
	 * @return
	 */
	@Override
	public UserWalletModelDto getByUserAndCurrencyId(Long userId, Long currencyId) {
		UserWalletModelDto wallet= userWalletMapper.getByUserAndCurrencyId(userId,currencyId);
		//插入时判断  如果没有钱包新建一个
		if(wallet==null){
			wallet= ((UserWalletService)AopContext.currentProxy()).addWallet(userId, currencyId);
		}
		return wallet;
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public UserWalletModelDto addWallet(Long userId, Long currencyId) {
		Long rdtId=currencyService.getRdtId();
		if(userId!=null && currencyId!=null && userId>0 && currencyId>0){
			UserWalletModelDto wallet = new UserWalletModelDto();
			wallet.setUserId(userId);
			wallet.setFreeze(BigDecimal.ZERO);
			wallet.setUsing(BigDecimal.ZERO);
			wallet.setCurrencyId(currencyId);
			wallet.setGatewaySwitch(0);
			if(currencyId.equals(rdtId)){
				wallet.setGatewaySwitch(0);
			}else{
				wallet.setGatewaySwitch(1);
			}
			this.insert(wallet);
			return wallet;
		}
		return null;
	}
	@Override
	public List<UserWalletModelDto> getByUserId(Long userId,String currencyName) {
		UserWalletModelDto search=new UserWalletModelDto();
		search.setUserId(userId);
		List<Long> currencyList=null;
		currencyList=currencyService.getLikeByName(currencyName, UsingEnum.USING.getCode());
		if(currencyList==null || currencyList.size()==0){
			currencyList.add(-1L);
		}
		search.setCurrencyList(currencyList);
		return this.queryListByDto(search,true);
	}

	@Transactional
	@Override
	public void insertUserWallet(Long userId) {
		//初始化用户钱包
		List<CurrencyModelDto> curList=currencyService.getUsing();
		for(CurrencyModel c:curList){
			//获取钱包  没有时创建
			this.getByUserAndCurrencyId(userId,c.getId());
		}
	}

	@Override
	@Transactional
	public void adminUpdate(UserWalletModelDto wallet, Long opeId) {
		//获取原对象
		UserWalletModelDto model = this.getById(wallet.getId());
		BigDecimal oldUsing = model.getUsing();
		BigDecimal oldFreeze  = model.getFreeze();
		BigDecimal modifyUsing=wallet.getUsing();
		BigDecimal modifyFreeze=wallet.getFreeze();
		BigDecimal newUsing = MathUtil.add(oldUsing,modifyUsing);
		BigDecimal newFreeze = MathUtil.add(oldFreeze,modifyFreeze);
		model.setUsing(newUsing);
		model.setFreeze(newFreeze);
		model.setUpdateTime(new Date());
		//更新数据
		this.modifyWallet(model.getUserId(),model.getCurrencyId(),modifyUsing,modifyFreeze);
		userWalletUpdateLogService.insert(oldUsing,modifyUsing,newUsing,oldFreeze,modifyFreeze,newFreeze,opeId,model.getUserId(),model.getCurrencyId(),wallet.getRemark(),0);
		userWalletLogService.addLog(model.getUserId(), model.getCurrencyId(), modifyUsing, WalletLogTypeEnum.SYS.getCode(), 0L,"系统修改", wallet);

	}
	private void check(UserWalletModel wallet){
		if(wallet.getUsing().compareTo(BigDecimal.ZERO)<0){
			throw new BusinessException("u:"+wallet.getUserId()+",c:"+wallet.getCurrencyId()+"，可用小于0->"+wallet.getUsing());
		}
		if(wallet.getFreeze().compareTo(BigDecimal.ZERO)<0){
			throw new BusinessException("u:"+wallet.getUserId()+",c:"+wallet.getCurrencyId()+"，冻结小于0->"+wallet.getFreeze());
		}
	}

	@Override
	public List<AllWalletDto> allWallet(Long currencyId) {
		return userWalletMapper.allWallet(currencyId);
	}

	@Override
	protected void doMode(UserWalletModelDto dto) {
		super.doMode(dto);
		dto.setUserName(userService.getNameById(dto.getUserId()));
		dto.setRealName(userService.getRealNameById(dto.getUserId()));
		dto.setCurrencyName(currencyService.getNameById(dto.getCurrencyId()));
		dto.setPosition(currencyService.getPositionById(dto.getCurrencyId()));
	}


	/**
	 * 操作买家钱包
	 *
	 * @param userId
	 * @param currencyId
	 * @param tradeCurrencyId
	 * @param buyTotal        总数
	 * @param price           价格
	 * @param rateMoney        手续费
	 */
	@Override
	@Transactional
	public void doBuyWallet(Long userId, Long currencyId, Long tradeCurrencyId, BigDecimal buyTotal, BigDecimal price, BigDecimal rateMoney, boolean buyWhite, Long recordId) {
		//可用=可用+总数量
		BigDecimal changeUsing=buyTotal;
		BigDecimal changeFreeze=BigDecimal.ZERO;
		UserWalletModel buyTradeWallet =this.modifyWallet(userId,tradeCurrencyId,changeUsing,changeFreeze);
				//交易
		userWalletLogService.addLog(userId, tradeCurrencyId, changeUsing, WalletLogTypeEnum.TRADE_BUY.getCode(),  + recordId , "交易获取", buyTradeWallet);
		//非白名单的扣除手续费
		if (!buyWhite) {
			//手续费
			BigDecimal changeUsing1=MathUtil.zeroSub(rateMoney);
			BigDecimal changeFreeze1=BigDecimal.ZERO;
			buyTradeWallet =this.modifyWallet(userId,tradeCurrencyId,changeUsing1,changeFreeze1);
			//手续费记录
			userWalletLogService.addLog(userId, tradeCurrencyId,changeUsing1, WalletLogTypeEnum.TRADE_BUY.getCode(), recordId , "交易手续费",  buyTradeWallet);
		}


		BigDecimal changeUsing2=BigDecimal.ZERO;
		//冻结币种=  -总交易数*价格
		BigDecimal changeFreeze2=MathUtil.zeroSub(MathUtil.mul(buyTotal, price));
		this.modifyWallet(userId, currencyId,changeUsing2,changeFreeze2);
	}

	/**
	 * 操作卖家钱包
	 *
	 * @param userId
	 * @param currencyId
	 * @param tradeCurrencyId
	 * @param tradeQuantity   交易数量
	 * @param price           价格
	 * @param rateMoney       手续费
	 */
	@Override
	@Transactional
	public void doSellWallet(Long userId, Long currencyId, Long tradeCurrencyId, BigDecimal tradeQuantity, BigDecimal price, BigDecimal rateMoney, boolean sellWhite, Long recordId) {
		BigDecimal changeUsing=BigDecimal.ZERO;
		//交易币种钱包  冻结=冻结-交易的数量
		BigDecimal changeFreeze=MathUtil.zeroSub(tradeQuantity);
		UserWalletModel sellTradeWallet =this.modifyWallet(userId, tradeCurrencyId,changeUsing,changeFreeze);


		//可用=可用+交易数量
		BigDecimal changeUsing1=MathUtil.mul(tradeQuantity, price);
		//交易币种钱包  冻结=冻结-交易的数量
		BigDecimal changeFreeze1=BigDecimal.ZERO;
		UserWalletModel sellWallet =this.modifyWallet(userId, currencyId,changeUsing1,changeFreeze1);
		//记录日志
		userWalletLogService.addLog(userId, currencyId, changeUsing1, WalletLogTypeEnum.TRADE_SELL.getCode(),  recordId , "交易获取",  sellWallet);
		//非白名单  扣减手续费
		if (!sellWhite) {
			//可用=可用-卖出手续费
			BigDecimal changeUsing2=BigDecimal.ZERO.subtract(rateMoney);
			//交易币种钱包  冻结=冻结-交易的数量
			BigDecimal changeFreeze2=BigDecimal.ZERO;
			sellWallet =this.modifyWallet(userId, currencyId,changeUsing2,changeFreeze2);
			userWalletLogService.addLog(userId, currencyId, changeUsing2, WalletLogTypeEnum.TRADE_SELL.getCode(), recordId , "交易手续费", sellWallet);
		}
	}

	@Override
	@Transactional
	public UserWalletModelDto modifyWallet(Long userId, Long currencyId, BigDecimal changeUsing, BigDecimal changeFreeze) {
		if (userId != null && currencyId != null && userId > 0 && currencyId > 0) {
			UserWalletModelDto dto = this.getByUserAndCurrencyId(userId, currencyId);
			boolean status = userWalletMapper.modifyWallet(userId, currencyId, changeUsing, changeFreeze) > 0;
			if (status) {
				dto = this.getByUserAndCurrencyId(userId, currencyId);
			} else {
				System.out.println("u:" + userId + ",c:" + currencyId);
				throw new BusinessException("余额不足");
			}
			return dto;
		}
		throw new BusinessException("参数异常u:" + userId + ",c:" + currencyId);
	}
	@Override
	@Transactional
	public UserWalletModelDto modifyWalletUsing(Long userId, Long currencyId, BigDecimal changeUsing) {
		if (userId != null && currencyId != null && userId > 0 && currencyId > 0) {
			UserWalletModelDto dto = this.getByUserAndCurrencyId(userId, currencyId);
			boolean status = userWalletMapper.modifyWalletUsing(userId, currencyId, changeUsing) > 0;
			if (status) {
				dto = this.getByUserAndCurrencyId(userId, currencyId);
			} else {
				System.out.println("u:" + userId + ",c:" + currencyId);
				throw new BusinessException("可用余额不足");
			}
			return dto;
		}
		throw new BusinessException("参数异常u:" + userId + ",c:" + currencyId);
	}

	private String getAddress(Long userId,Integer protocol){
		//协议2的地址
		String address = userRechargeAddressService.getRechargeAddress(userId, protocol);
		if (StringUtils.isEmpty(address)) {
			address = userRechargeAddressService.initRechargeAddressLock(userId,protocol);
		}
		return address;
	}


	@Override
	public List<WalletStaticModel> groupByCurrencyId() {
		return userWalletMapper.groupByCurrencyId();
	}

	@Override
	public UserWalletModel getTotal(Long id,Integer status) {
		return userWalletMapper.getTotal(id,status);
	}

	@Transactional
	@Override
	public void updateOff(Long userId, Long currencyId, Integer off) {
		UserWalletModelDto wallet = this.getByUserAndCurrencyId(userId, currencyId);
		if(off==0){
           	throw  new BusinessException("无法关闭网关");
		}else{
			Long rdb=currencyService.getRdtId();
			CurrencyModel currencyModel=currencyService.getById(currencyId);
			ReConfigModelDto config = reConfigService.getConfig();
			String address = this.getAddress(userId,currencyModel.getProtocol());
			if(currencyId.intValue()==rdb.intValue()){
				Long rdbId=currencyService.getRdbId();
				//扣除手续费
				UserWalletModelDto rdbWallet = this.getByUserAndCurrencyId(userId, rdbId);
                 BigDecimal free=new BigDecimal(configService.queryValueByName("rdt.network.free"));
                 if(rdbWallet.getUsing().compareTo(free)<0){
                 	throw new BusinessException("RDB不足！");
				 }
				rdbWallet.setUsing(rdbWallet.getUsing().subtract(free));
				this.update(rdbWallet);
				userWalletLogService.addLog(userId,rdbId,free.negate(),WalletLogTypeEnum.NET_WORK_FREE.getCode(),
					null	,WalletLogTypeEnum.NET_WORK_FREE.getName(),rdbWallet);
				String url = config.getUrl() + "/api/geteway/add";
				HttpRequest request = HttpUtil.createGet(url);
				Map<String, Object> params = new HashMap<>();
				params.put("storeNo", config.getStoreNo());
				params.put("address", address);
				String sign = SignUtils.signData(params, config.getKey());
				params.put("sign", sign);
				String result = request.form(params).execute().body();
		        System.out.println(result);


			}
		}
		wallet.setGatewaySwitch(off);
		this.update(wallet);
	}

	@Override
	@Transactional
	public UserWalletModelDto modifyWalletFreeze(Long userId, Long currencyId, BigDecimal changeFreeze) {
		if (userId != null && currencyId != null && userId > 0 && currencyId > 0) {
			UserWalletModelDto dto = this.getByUserAndCurrencyId(userId, currencyId);
			boolean status = userWalletMapper.modifyWalletFreeze(userId, currencyId, changeFreeze) > 0;
			if (status) {
				dto = this.getByUserAndCurrencyId(userId, currencyId);
			} else {
				System.out.println("u:" + userId + ",c:" + currencyId);
				throw new BusinessException("冻结余额不足");
			}
			return dto;
		}
		throw new BusinessException("参数异常u:" + userId + ",c:" + currencyId);
	}
}

package com.liuqi.business.service.impl;


import cn.hutool.log.Log;
import cn.hutool.log.dialect.log4j2.Log4j2LogFactory;
import com.liuqi.base.BaseMapper;
import com.liuqi.base.BaseServiceImpl;
import com.liuqi.business.async.AsyncTask;
import com.liuqi.business.enums.*;
import com.liuqi.business.mapper.TrusteeMapper;
import com.liuqi.business.model.*;
import com.liuqi.business.service.*;
import com.liuqi.exception.BusinessException;
import com.liuqi.utils.MathUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@Transactional(readOnly = true)
public class TrusteeServiceImpl extends BaseServiceImpl<TrusteeModel,TrusteeModelDto> implements TrusteeService {

	private static Log log= Log4j2LogFactory.get("trade");

	@Autowired
	private TrusteeMapper trusteeMapper;
	@Override
	public BaseMapper<TrusteeModel,TrusteeModelDto> getBaseMapper() {
		return this.trusteeMapper;
	}
	@Autowired
	private UserWalletService userWalletService;
	@Autowired
	private CurrencyTradeService currencyTradeService;
	@Autowired
	private UserService userService;
	@Autowired
	private UserWalletLogService userWalletLogService;
	@Autowired
	private TradeService tradeService;
	@Autowired
	private TableIdService tableIdService;
	@Autowired
	@Lazy
	private AsyncTask asyncTask;
	@Override
	public void insert(TrusteeModel trusteeModel) {
		//设置id
		trusteeModel.setId(tableIdService.getNextId(TableIdNameEnum.TRUSTEE));
		super.insert(trusteeModel);
	}

	@Override
	public void checkPublish(TrusteeModelDto trusteeModel) {
		//获取交易对
		CurrencyTradeModelDto trade=currencyTradeService.getById(trusteeModel.getTradeId());
		if(trade==null){
			throw new BusinessException("交易对异常");
		}
		if(trade.getStatus().equals(CurrencyTradeModelDto.STATUS_STOP) || !SwitchEnum.isOn(trade.getTradeSwitch())){//停用
			throw new BusinessException("交易对暂停交易");
		}
		//保留位小数  多余的直接去除
		trusteeModel.setQuantity(trusteeModel.getQuantity().setScale(trade.getDigitsQ(),BigDecimal.ROUND_DOWN));
		trusteeModel.setPrice(trusteeModel.getPrice().setScale(trade.getDigitsP(),BigDecimal.ROUND_DOWN));

		//判断金额  价格是否正常
		if(trusteeModel.getQuantity().compareTo(BigDecimal.ZERO)<=0 || trusteeModel.getPrice().compareTo(BigDecimal.ZERO)<=0){
			throw new BusinessException("交易金额和价格必须大于0");
		}
		//非机器人 价格判断
		if( YesNoEnum.NO.getCode().equals(trusteeModel.getRobot()) && SwitchEnum.isOn(trade.getPriceSwitch())){
			if (trade.getMinPirce()!=null && trade.getMinPirce().compareTo(BigDecimal.ZERO)>0 && trade.getMinPirce().compareTo(trusteeModel.getPrice()) >0) {
				throw new BusinessException("最小价格:"+trade.getMinPirce().setScale(4, BigDecimal.ROUND_HALF_UP).toString());
			}
			if (trade.getMaxPirce()!=null && trade.getMaxPirce().compareTo(BigDecimal.ZERO)>0 && trade.getMaxPirce().compareTo(trusteeModel.getPrice()) <0) {
				throw new BusinessException("最大价格:"+trade.getMaxPirce().setScale(4, BigDecimal.ROUND_HALF_UP).toString());
			}
		}
		//非机器人 判断数量
		if(YesNoEnum.NO.getCode().equals(trusteeModel.getRobot()) && SwitchEnum.isOn(trade.getQuantitySwitch())){
			if (trade.getMinQuantity()!=null && trade.getMinQuantity().compareTo(BigDecimal.ZERO)>0 && trade.getMinQuantity().compareTo(trusteeModel.getQuantity()) >0) {
				throw new BusinessException("最小数量:"+trade.getMinQuantity().setScale(4, BigDecimal.ROUND_HALF_UP).toString());
			}
			if (trade.getMaxQuantity()!=null && trade.getMaxQuantity().compareTo(BigDecimal.ZERO)>0 && trade.getMaxQuantity().compareTo(trusteeModel.getQuantity()) <0) {
				throw new BusinessException("最大数量:"+trade.getMaxQuantity().setScale(4, BigDecimal.ROUND_HALF_UP).toString());
			}
		}
		//判断涨跌幅限制
		if(SwitchEnum.isOn(trade.getLimitSwitch()) && trade.getLimitRate().compareTo(BigDecimal.ZERO)>0) {
			BigDecimal openPrice = tradeService.getOpenPrice(trade.getId());
			//价格大于0 并且 价格在幅度之内
			if (openPrice.compareTo(BigDecimal.ZERO) > 0) {
				//涨跌幅度 当前价格*（配置/100）
				BigDecimal rateMoney = MathUtil.mul(openPrice, MathUtil.div(trade.getLimitRate(),new BigDecimal("100")));
				BigDecimal maxMoney = MathUtil.add(openPrice,rateMoney);//最大价格  当前价格+涨跌幅
				BigDecimal minMoney = MathUtil.sub(openPrice,rateMoney);//最大价格  当前价格-涨跌幅
				//发布的价格小于最大价格 或者 发布价大于最小价格
				if (trusteeModel.getPrice().compareTo(maxMoney) > 0 || minMoney.compareTo(trusteeModel.getPrice()) > 0) {
					throw new BusinessException("涨跌幅限制，最低：" + minMoney.setScale(4, BigDecimal.ROUND_HALF_UP).toString() + ",最高：" + maxMoney.setScale(4, BigDecimal.ROUND_HALF_UP).toString());
				}
			}
		}
	}

	/**
	 * 发布交易
	 * @param trusteeOrder
	 */
	@Override
	@Transactional
	public void publishTrade(TrusteeModelDto trusteeOrder) {
		log.info("发布交易开始"+trusteeOrder);
		UserModel user=userService.getById(trusteeOrder.getUserId());
		if(user!=null&& user.getWhiteIf()!=null) {
			trusteeOrder.setWhite(user.getWhiteIf());
		}
		CurrencyTradeModelDto trade=currencyTradeService.getById(trusteeOrder.getTradeId());

		trusteeOrder.setTradeQuantity(BigDecimal.ZERO);//交易数量
		trusteeOrder.setStatus(TrusteeStatusEnum.WAIT.getCode());//状态  未交易

		//设置手续费
		if(BuySellEnum.BUY.getCode().equals(trusteeOrder.getTradeType())){
			trusteeOrder.setRate(MathUtil.div(trade.getBuyRate(),new BigDecimal("100")));
		}else{
			trusteeOrder.setRate(MathUtil.div(trade.getSellRate(),new BigDecimal("100")));
		}

		//机器人  直接处理
		if(YesNoEnum.YES.getCode().equals(trusteeOrder.getRobot())){
			this.insert(trusteeOrder);
		}else {//非机器人  余额扣减
			log.info("发布交易处理");
			//默认买参数
			Long currencyId=trade.getCurrencyId();
			BigDecimal tradeQuantity = MathUtil.mul(trusteeOrder.getPrice(), trusteeOrder.getQuantity());
			int walletLogType= WalletLogTypeEnum.TRADE_BUY.getCode();


			log.info("发布交易处理--》"+ BuySellEnum.getName(trusteeOrder.getTradeType()));
			//卖  不同的处理结果
			if (BuySellEnum.SELL.getCode().equals(trusteeOrder.getTradeType())) {
				currencyId=trade.getTradeCurrencyId();
				tradeQuantity = trusteeOrder.getQuantity();
				walletLogType= WalletLogTypeEnum.TRADE_SELL.getCode();
			}
			//处理金额  可用- 冻结+
			BigDecimal changeUsing= MathUtil.zeroSub(tradeQuantity);
			BigDecimal changeFreeze=tradeQuantity;
			UserWalletModel wallet=userWalletService.modifyWallet(trusteeOrder.getUserId(), currencyId,changeUsing,changeFreeze);
			this.insert(trusteeOrder);
			userWalletLogService.addLog(trusteeOrder.getUserId(), currencyId,changeUsing, walletLogType, trusteeOrder.getId() , "交易扣除",  wallet);
			log.info("发布交易处理--》，结束");
		}
	}

	/**
	 * 用户未成功订单
	 * @param userId
	 * @param tradeId
	 * @return
	 */
	@Override
	public List<TrusteeModelDto> findUserNoSuccess(Long userId, Long tradeId, boolean limit, Integer count) {

		TrusteeModelDto search=new TrusteeModelDto();
		search.setStatus(TrusteeStatusEnum.WAIT.getCode());
		search.setUserId(userId);
		if(tradeId!=null && tradeId>0){
			search.setTradeId(tradeId);
		}
		if(limit && count>0){
			search.setLimitCount(limit);
			search.setCount(count);
		}
		return this.queryListByDto(search,true);
	}


	/**
	 * 取消交易
	 * @param trusteeId
	 */
	@Override
	@Transactional
	public void cancel(Long trusteeId, Long userId, boolean checkUser) {
		TrusteeModel model=this.getById(trusteeId);
		if(checkUser && !model.getUserId().equals(userId)){
			throw new BusinessException("非用户订单，不允许取消");
		}
		asyncTask.tradeCancel(trusteeId,model.getUserId(),model.getTradeId());
		//controller传入的log
		this.addLogger();
	}

	/**
	 * 查询卖出最低价格
	 * @param tradeId 交易对id
	 * @return
	 */
	@Override
	public BigDecimal findTrusteeSellMinPrice(Long tradeId) {
		return trusteeMapper.findTrusteeSellMinPrice(tradeId);
	}
	/**
	 * 查询买入最高价格
	 * @param tradeId 交易对id
	 * @return
	 */
	@Override
	public BigDecimal findTrusteeBuyMaxPrice(Long tradeId) {
		return trusteeMapper.findTrusteeBuyMaxPrice(tradeId);
	}


	/**
	 * 取消机器人单子
	 * @param tradeId
	 * @param curPrice
	 */
	@Override
	public void cancelRobot(Long tradeId,Integer tradeType, BigDecimal curPrice) {
		//查询交易对中  机器人发布的单子
		List<Long> list=trusteeMapper.queryRobotPrice(tradeId,tradeType,curPrice);
		if(list!=null && list.size()>0){
			int count=0;
			for(Long canTradeId:list){
				count++;
				this.cancel(canTradeId, 0L, false);
				try {
					if(count%300==0) {
						Thread.sleep(1000L);
					}
				}catch (Exception e){
					e.printStackTrace();
				}
			}
		}
	}

	/**
	 * 卖1
	 * @param currencyTradeId
	 * @return
	 */
	@Override
	public TrusteeModelDto findFirstSell(Long currencyTradeId) {
		return trusteeMapper.findFirstSell(currencyTradeId);
	}

	/**
	 * 买1
	 * @param currencyTradeId
	 * @return
	 */
	@Override
	public TrusteeModelDto findFirstBuy(Long currencyTradeId) {
		return trusteeMapper.findFirstBuy(currencyTradeId);
	}

	/**
	 * 查询托管订单信息
	 * @param tradeType
	 * @param tradeId
	 * @return
	 */
	@Override
	public List<TrusteeModelDto> findTrusteeOrderList(Integer tradeType, Long tradeId, int num) {
		return trusteeMapper.findTrusteeOrderList(tradeType, tradeId,num);
	}



	@Override
	protected void doMode(TrusteeModelDto dto) {
		super.doMode(dto);
		dto.setUserName(userService.getNameById(dto.getUserId()));
		dto.setRealName(userService.getRealNameById(dto.getUserId()));
		CurrencyTradeModelDto trade=currencyTradeService.getById(dto.getTradeId());
		dto.setCurrencyName(trade!=null?trade.getCurrencyName():"");
		dto.setTradeCurrencyName(trade!=null?trade.getTradeCurrencyName():"");
		dto.setAreaName(trade!=null?trade.getAreaName():"");
		trade=null;
	}


    @Override
    public void cancelAllRobot(Long tradeId, Integer tradeType) {
		//查询交易对中  机器人发布的单子
		List<Long> list=trusteeMapper.queryRobot(tradeId,tradeType);
		if(list!=null && list.size()>0){
			for(Long canTradeId:list){
				this.cancel(canTradeId, 0L, false);
			}
		}
    }

    @Override
	@Transactional
    public void deleteRobotCancel() {
		trusteeMapper.deleteRobotCancel(TrusteeStatusEnum.CANCEL.getCode(), YesNoEnum.YES.getCode());
    }

	@Override
	@Transactional
	public void errorModify() {
		trusteeMapper.updateStatus(TrusteeStatusEnum.ERROR.getCode(),TrusteeStatusEnum.WAIT.getCode());
	}
}

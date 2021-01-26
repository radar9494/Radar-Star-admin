package com.liuqi.business.service.impl;

import cn.hutool.core.date.DateField;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import com.alibaba.fastjson.JSONObject;
import com.liuqi.base.BaseMapper;
import com.liuqi.base.BaseServiceImpl;
import com.liuqi.business.constant.KeyConstant;
import com.liuqi.business.dto.KDto;
import com.liuqi.business.dto.SelectDto;
import com.liuqi.business.enums.KTypeEnum;
import com.liuqi.business.enums.TableIdNameEnum;
import com.liuqi.business.mapper.KDataMapper;
import com.liuqi.business.model.CurrencyTradeModelDto;
import com.liuqi.business.model.KDataModel;
import com.liuqi.business.model.KDataModelDto;
import com.liuqi.business.service.*;
import com.liuqi.redis.RedisRepository;
import com.liuqi.utils.MathUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

@Service("kDataServiceImpl")
@Transactional(readOnly = true)
public class KDataServiceImpl extends BaseServiceImpl<KDataModel,KDataModelDto> implements KDataService{

	@Autowired
	private KDataMapper kDataMapper;
	@Autowired
	private TradeRecordService tradeRecordService;
	@Autowired
	private CurrencyTradeService currencyTradeService;
	@Autowired
	private TradeService tradeService;
	@Autowired
	private TableIdService tableIdService;
	@Autowired
	private RedisRepository redisRepository;
	@Autowired
	private CurrencyAreaService currencyAreaService;
	@Override
	public BaseMapper<KDataModel,KDataModelDto> getBaseMapper() {
		return this.kDataMapper;
	}
	@Override
	@Transactional
	public void insert(KDataModel t) {
		t.setId(tableIdService.getNextId(TableIdNameEnum.KDATA));
		if(t.getCreateTime()==null){
			Date date=DateTime.of(new Date()).setField(DateField.SECOND,0).setField(DateField.MILLISECOND,0);
			t.setCreateTime(date);
		}
		if(t.getUpdateTime()==null){
			t.setUpdateTime(new Date());
		}
		if(t.getVersion()==null){
			t.setVersion(0);
		}
		this.getBaseMapper().insert(t);
	}
	@Override
	@Transactional
	public void storeKChartData(CurrencyTradeModelDto trade, Integer type, Date startTime, Date endTime) {
		//判断时间是否生成K线
		KDataModelDto data= this.getKByDate(type,trade.getId(),startTime);
		if(data!=null){
			return;
		}
		KDataModelDto last = this.getLastByDb(type, trade.getId());
		//获取时间内的最大价格，最小价格，成交数量
		data = tradeRecordService.getTradeDataByDate(startTime, endTime, trade.getId());
		//没有数据时  不生成K线
		if (data == null || data.getMaxPrice().compareTo(BigDecimal.ZERO)<=0) {
			return;
		}
		//获取开盘价格  上一根线的收盘价 应该去时间升序的第一个价格
		BigDecimal openPrice = last != null ? last.getClosePrice() : tradeRecordService.getOpenPriceByDate(startTime, trade.getId());
		//获取收盘价
		BigDecimal closePrice = tradeRecordService.getClosePriceByDate(endTime, trade.getId());
		data.setOpenPrice(openPrice);
		data.setClosePrice(closePrice);
		data.setType(type);
		data.setTradeId(trade.getId());
		data.setTime(startTime);
		if (data.getMaxPrice().compareTo(BigDecimal.ZERO) == 0) {
			data.setMaxPrice(openPrice);
		}
		if (data.getMinPrice().compareTo(BigDecimal.ZERO) == 0) {
			data.setMinPrice(openPrice);
		}
		this.insert(data);
		//加入缓存
		this.addCache(data.getType(), data.getTradeId(), data);
	}

	@Override
	public KDataModelDto getLastByDb(Integer type, Long tradeId) {
		return kDataMapper.getLastData(type,tradeId);
	}

	@Override
	public KDataModelDto getKByDate(Integer type, Long tradeId, Date date) {
		return kDataMapper.getKByDate(type,tradeId,date);
	}

	@Override
	public List<KDto> queryDataByType(Integer type, Long tradeId, Date startDate, Date endDate) {
		return kDataMapper.queryDataByType(type,tradeId,startDate,endDate);
	}

	@Override
	public void initCache(Integer type, Long tradeId) {
		String key = KeyConstant.KEY_K  + tradeId + "_" + type;
		redisRepository.del(key);
		org.joda.time.DateTime endTime = org.joda.time.DateTime.now();
		org.joda.time.DateTime startTime = org.joda.time.DateTime.now().plusMonths(-1);
		List<KDto> list = kDataMapper.queryDataByType(type, tradeId, startTime.toDate(), endTime.toDate());
		while((list != null && list.size() > 0)) {
			Set set = null;
			for (KDto dto : list) {
				set = redisRepository.getByScore(key, dto.getDate().getTime(), dto.getDate().getTime());
				if (set == null || set.size() == 0) {
					redisRepository.add(key, dto, dto.getDate().getTime());
				}
			}
			endTime = startTime;
			startTime = startTime.plusMonths(-1);
			list = kDataMapper.queryDataByType(type, tradeId, startTime.toDate(), endTime.toDate());
		}
	}

	@Override
	public void addCache(Integer type, Long tradeId, KDataModel kDataModel) {
		KDto dto = new KDto();
		dto.setClose(kDataModel.getClosePrice());
		dto.setOpen(kDataModel.getOpenPrice());
		dto.setHigh(kDataModel.getMaxPrice().compareTo(BigDecimal.ZERO)>0?kDataModel.getMaxPrice():dto.getOpen());
		dto.setLow(kDataModel.getMinPrice().compareTo(BigDecimal.ZERO)>0?kDataModel.getMinPrice():dto.getOpen());
		dto.setVolume(kDataModel.getNums());
		dto.setDate(kDataModel.getTime());
		dto.setTime(kDataModel.getTime().getTime());
		String key =KeyConstant.KEY_K  + tradeId + "_" + type;
		Set set = redisRepository.getByScore(key, dto.getTime(), dto.getTime());
		if (set == null || set.size() == 0) {
			redisRepository.add(key, dto, dto.getTime());
		}
	}

	@Override
	public List<KDto> queryCacheDataByType(Integer type, Long tradeId, Date startTime, Date endTime) {
		String key = KeyConstant.KEY_K  + tradeId + "_" + type;
		Set<KDto> set = redisRepository.getByScore(key, startTime.getTime(), endTime.getTime());
		return new ArrayList<>(set);
	}
	@Override
	public KDto getLast(Integer type, Long tradeId) {
		String key = KeyConstant.KEY_K + tradeId + "_" + type;
		Set<KDto> set = redisRepository.getLast(key);
		return set != null && set.size() > 0 ? new ArrayList<KDto>(set).get(0) : null;
	}


	@Override
	public void addCacheCurK(Long tradeId, BigDecimal price, BigDecimal quantity) {
		List<SelectDto> list = KTypeEnum.getList();
		String key = "";
		Integer type = 0;
		for (SelectDto typeObj : list) {
			type = Integer.valueOf(typeObj.getKey().toString());
			KDto last = getLast(type, tradeId);
			if(last!=null) {
				key = KeyConstant.KEY_CUR_K + tradeId + "_" + type + "_" + last.getTime();
				KDto dto = getCacheCurK(type, tradeId);
				if (dto != null) {
					//赋值
					dto.setClose(price);
					dto.setHigh(dto.getHigh().compareTo(price) > 0 ? dto.getHigh() : price);
					dto.setLow(dto.getLow().compareTo(BigDecimal.ZERO) > 0 && dto.getLow().compareTo(price) < 0 ? dto.getLow() : price);
					dto.setVolume(MathUtil.add(dto.getVolume(), quantity));
					//缓存
					redisRepository.set(key, dto, 65L, TimeUnit.SECONDS);
				}
			}
		}
	}

	@Override
	public KDto getCacheCurK(Integer type, Long tradeId) {
		//获取最后一条数据
		KDto last = getLast(type, tradeId);
		KDto cur=null;
		if (last != null) {
			//根据时间获取当前K线时间
			Date time = this.getTime(type);
			String key = KeyConstant.KEY_CUR_K + tradeId + "_" + type + "_" + last.getTime();
			cur = redisRepository.getModel(key);
			if (cur == null) {
				//查询数据库 上一条到现在的
				KDataModelDto data = tradeRecordService.getTradeDataByDate(last.getDate(), new Date(), tradeId);
				cur = new KDto();
				cur.setOpen(last.getClose());
				cur.setClose(tradeService.getPriceByTradeId(tradeId));
				cur.setHigh(data.getMaxPrice().compareTo(BigDecimal.ZERO)>0?data.getMaxPrice():cur.getOpen());
				cur.setLow(data.getMinPrice().compareTo(BigDecimal.ZERO)>0?data.getMinPrice():cur.getOpen());
				cur.setVolume(data.getNums());
				cur.setDate(time);
				cur.setTime(time.getTime());
				redisRepository.set(key, cur, 65L, TimeUnit.SECONDS);
			}
		}
		return cur;
	}

	private Date getTime(Integer type) {
		//去除秒数后就是当前一分钟的时间
		DateTime date=DateTime.now().setField(DateField.SECOND,0).setField(DateField.MILLISECOND,0);
		//一分钟K   去当前时间
		if (KTypeEnum.FIVEM.getCode().equals(type)) {
			int surplus=date.minute()%5;
			date=date.offset(DateField.MINUTE,-surplus);
		} else if (KTypeEnum.FIFTEENM.getCode().equals(type)) {
			int surplus=date.minute()%15;
			date=date.offset(DateField.MINUTE,-surplus);
		} else if (KTypeEnum.THIRTYM.getCode().equals(type)) {
			int surplus=date.minute()%30;
			date=date.offset(DateField.MINUTE,-surplus);
		} else if (KTypeEnum.ONEH.getCode().equals(type)) {
			//分钟和小时都为0  表示这一个小时
			date=date.setField(DateField.MINUTE,0);
		} else if (KTypeEnum.ONED.getCode().equals(type)) {
			//今天开始时间
			date= DateUtil.beginOfDay(date);
		} else if (KTypeEnum.ONEW.getCode().equals(type)) {
			//这周开始时间
			date=DateUtil.beginOfWeek(date);
		}
		return date;
	}
}

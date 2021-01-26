package com.liuqi.business.service.impl;


import com.liuqi.base.BaseMapper;
import com.liuqi.base.BaseServiceImpl;
import com.liuqi.business.constant.KeyConstant;
import com.liuqi.business.enums.UsingEnum;
import com.liuqi.business.mapper.CurrencyAreaMapper;
import com.liuqi.business.model.CurrencyAreaModel;
import com.liuqi.business.model.CurrencyAreaModelDto;
import com.liuqi.business.service.CurrencyAreaService;
import com.liuqi.redis.RedisRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class CurrencyAreaServiceImpl extends BaseServiceImpl<CurrencyAreaModel, CurrencyAreaModelDto> implements CurrencyAreaService {

	@Autowired
	private CurrencyAreaMapper currencyAreaMapper;
	@Autowired
	private RedisRepository redisRepository;

	@Override
	public BaseMapper<CurrencyAreaModel, CurrencyAreaModelDto> getBaseMapper() {
		return this.currencyAreaMapper;
	}

	@Override
	public String getNameById(Long id) {
		String name="";
		CurrencyAreaModel area=this.getById(id);
		name=area!=null?area.getName():"";
		area=null;
		return name;
	}

	/**
	 * 查询所有币种（不分页）
	 * @return
	 */
	@Override
	public List<CurrencyAreaModelDto> findAllArea() {
		//有缓存则查询缓存  增删改时清除缓存
		List<CurrencyAreaModelDto> list = redisRepository.getModel(KeyConstant.KEY_CURRENCY_AREA);
		if (list == null) {
			CurrencyAreaModelDto search=new CurrencyAreaModelDto();
			search.setSortName("position");
			search.setSortType("asc");
			list = currencyAreaMapper.queryList(search);
			if(list!=null && list.size()>0){
				redisRepository.set(KeyConstant.KEY_CURRENCY_AREA, list, 1L, TimeUnit.DAYS);
			}
		}
		return list;
	}
	/**
	 * 查询所有启用币种（不分页）
	 * @return
	 */
	@Override
	public List<CurrencyAreaModelDto> findAllCanUseArea() {
		List<CurrencyAreaModelDto> areaList = this.findAllArea();
		//过滤出可用的
		return areaList!=null?areaList.stream().filter( model->UsingEnum.USING.getCode().equals(model.getStatus())).collect(Collectors.toList()):new ArrayList<>();
	}
	@Override
	public void cleanAllCache() {
		redisRepository.del(KeyConstant.KEY_CURRENCY_AREA);
	}
}

package com.liuqi.business.service.impl;


import com.liuqi.base.BaseMapper;
import com.liuqi.base.BaseServiceImpl;
import com.liuqi.business.constant.KeyConstant;
import com.liuqi.business.enums.ShowEnum;
import com.liuqi.business.enums.SlideTypeEnum;
import com.liuqi.business.mapper.SlideMapper;
import com.liuqi.business.model.SlideModel;
import com.liuqi.business.model.SlideModelDto;
import com.liuqi.business.service.SlideService;
import com.liuqi.redis.RedisRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
@Transactional(readOnly = true)
public class SlideServiceImpl extends BaseServiceImpl<SlideModel, SlideModelDto> implements SlideService {

	@Autowired
	private SlideMapper slideMapper;
	@Autowired
	private RedisRepository redisRepository;


	@Override
	public BaseMapper<SlideModel, SlideModelDto> getBaseMapper() {
		return this.slideMapper;
	}


	@Override
	@Transactional
	public void slideAdd(SlideModel slideModel) {
		cleanCacheByModel(slideModel);
		slideModel.setCreateTime(new Date());
		slideModel.setUpdateTime(new Date());
		slideMapper.insert(slideModel);
	}

	@Override
	@Transactional
	public boolean slideDelete(Long id) {
		SlideModel slideModel=this.getById(id);
		cleanCacheByModel(slideModel);
		slideMapper.removeById(id);
		return true;
	}
	@Override
	public List<SlideModelDto> getCanShow(Integer type) {
		SlideModelDto search=new SlideModelDto();
		search.setStatus(ShowEnum.SHOW.getCode());
		search.setType(type);
		search.setSortName("position");
		search.setSortType("asc");
		return slideMapper.queryList(search);
	}
	@Override
	public List<SlideModelDto> findSlide(Integer type) {
		String key=KeyConstant.KEY_SLIDE+type;
		List<SlideModelDto> list = redisRepository.getModel(key);
		if (list == null) {
			list = this.getCanShow(type);
			if(list!=null && list.size()>0){
				redisRepository.set(key, list, 2L, TimeUnit.DAYS);
			}else{
				//缓存10秒钟
				redisRepository.set(key, new ArrayList<>(), 10L, TimeUnit.SECONDS);
			}
		}
		return list;
	}

	@Override
	public void cleanCacheByModel(SlideModel slideModel) {
		redisRepository.del(KeyConstant.KEY_SLIDE + slideModel.getType());
	}

	@Override
	public void cleanAllCache() {
		redisRepository.del(KeyConstant.KEY_SLIDE + SlideTypeEnum.PC.getName());
		redisRepository.del(KeyConstant.KEY_SLIDE + SlideTypeEnum.APP.getName());

	}
}

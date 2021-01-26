package com.liuqi.business.service.impl;


import com.liuqi.base.BaseMapper;
import com.liuqi.base.BaseServiceImpl;
import com.liuqi.business.constant.KeyConstant;
import com.liuqi.business.constant.KeyConstant;
import com.liuqi.business.mapper.FinancingIntroduceMapper;
import com.liuqi.business.model.FinancingIntroduceModel;
import com.liuqi.business.model.FinancingIntroduceModelDto;
import com.liuqi.business.service.FinancingIntroduceService;
import com.liuqi.redis.RedisRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.concurrent.TimeUnit;

@Service
@Transactional(readOnly = true)
public class FinancingIntroduceServiceImpl extends BaseServiceImpl<FinancingIntroduceModel,FinancingIntroduceModelDto> implements FinancingIntroduceService{

	@Autowired
	private FinancingIntroduceMapper financingIntroduceMapper;

	@Autowired
	private RedisRepository redisRepository;

	@Override
	public BaseMapper<FinancingIntroduceModel,FinancingIntroduceModelDto> getBaseMapper() {
		return this.financingIntroduceMapper;
	}

    @Override
	@Transactional
    public void init(Long configId) {
		FinancingIntroduceModel model=new FinancingIntroduceModel();
		model.setConfigId(configId);
		this.insert(model);
    }

	@Override
	public FinancingIntroduceModelDto getByConfigId(Long configId) {
		String key= KeyConstant.KEY_FINANCING_INF+configId;
		FinancingIntroduceModelDto dto = redisRepository.getModel(key);
		if (dto == null) {
			dto=financingIntroduceMapper.getByConfigId(configId);
			redisRepository.set(key, dto, 1L, TimeUnit.DAYS);
		}
		return dto;
	}

	@Override
	public void cleanCacheByModel(FinancingIntroduceModel financingIntroduceModel) {
		String key= KeyConstant.KEY_FINANCING_INF+financingIntroduceModel.getConfigId();
		redisRepository.del(key);
	}
}

package com.liuqi.business.service.impl;


import com.liuqi.business.model.UserLevelModel;
import com.liuqi.business.model.UserModelDto;
import com.liuqi.exception.BusinessException;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.liuqi.base.BaseMapper;
import com.liuqi.base.BaseServiceImpl;
import com.liuqi.business.model.TimestampLevelModel;
import com.liuqi.business.model.TimestampLevelModelDto;


import com.liuqi.business.service.TimestampLevelService;
import com.liuqi.business.mapper.TimestampLevelMapper;

@Service
@Transactional(readOnly = true)
public class TimestampLevelServiceImpl extends BaseServiceImpl<TimestampLevelModel,TimestampLevelModelDto> implements TimestampLevelService{

	@Autowired
	private TimestampLevelMapper timestampLevelMapper;

	@Transactional
	@Override
	public void initParentLevel(UserModelDto item, Long userId) {
		TimestampLevelModel level=this.getByUserId(userId);
		level.setParentId(userId);
		TimestampLevelModel parent=this.getByUserId(userId);
		int changeTreeLevel=parent.getTreeLevel()-level.getTreeLevel()+1;
		String baseTreeInfo=parent.getTreeInfo()+level.getUserId()+",";
	     this.update(level);
		timestampLevelMapper.changeLevel( level.getTreeInfo(), changeTreeLevel, baseTreeInfo);
	}

	@Override
	public TimestampLevelModel getByUserId(Long userId) {
		return timestampLevelMapper.getByUserId(userId);
	}

	@Transactional
	@Override
	public void initSubLevel(UserModelDto item, Long userId) {
		TimestampLevelModel subLevel = this.getByUserId(item.getId());
		subLevel.setParentId(userId);
		subLevel.setTreeLevel(subLevel.getTreeLevel() + 1);
		subLevel.setTreeInfo(","+userId+""+subLevel.getTreeInfo());
		this.update(subLevel);
		TimestampLevelModel level=new TimestampLevelModel();
		level.setUserId(userId);
		level.setParentId(0L);
		level.setTreeLevel(1);
		level.setTreeInfo(","+item.getId()+",");
		this.insert(level);
	}

	@Override
	public BaseMapper<TimestampLevelModel,TimestampLevelModelDto> getBaseMapper() {
		return this.timestampLevelMapper;
	}

}

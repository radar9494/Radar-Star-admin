package com.liuqi.business.service.impl;


import com.github.pagehelper.PageHelper;
import com.liuqi.base.BaseMapper;
import com.liuqi.base.BaseServiceImpl;
import com.liuqi.business.constant.KeyConstant;
import com.liuqi.business.enums.ShowEnum;
import com.liuqi.business.mapper.ContentMapper;
import com.liuqi.business.model.ContentModel;
import com.liuqi.business.model.ContentModelDto;
import com.liuqi.business.service.ContentService;
import com.liuqi.redis.RedisRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
@Transactional(readOnly = true)
public class ContentServiceImpl extends BaseServiceImpl<ContentModel, ContentModelDto> implements ContentService {

	@Autowired
	private ContentMapper contentMapper;
	@Autowired
	private RedisRepository redisRepository;

	@Override
	public BaseMapper<ContentModel, ContentModelDto> getBaseMapper() {
		return this.contentMapper;
	}

	/**
	 * 获取最新的缓存数据
	 * @param pageSize
	 * @return
	 */
	@Override
	public List<ContentModelDto> getNewContent(Integer pageSize) {
		List<ContentModelDto> list = redisRepository.getModel(KeyConstant.KEY_CONTENT_INFO);
		if (list == null) {
			PageHelper.startPage(1, pageSize);
			ContentModelDto search = new ContentModelDto();
			search.setStatus(ShowEnum.SHOW.getCode());
			list = contentMapper.queryList(search);
			if (list != null && list.size() > 0) {
				//缓存2天
				redisRepository.set(KeyConstant.KEY_CONTENT_INFO, list, 2L, TimeUnit.DAYS);
			}
		}
		return list;
	}

	@Override
	public void cleanAllCache() {
		redisRepository.del(KeyConstant.KEY_CONTENT_INFO);
	}
}

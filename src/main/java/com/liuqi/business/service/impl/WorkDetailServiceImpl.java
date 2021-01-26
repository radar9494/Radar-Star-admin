package com.liuqi.business.service.impl;

import com.liuqi.base.BaseConstant;
import com.liuqi.base.BaseMapper;
import com.liuqi.base.BaseServiceImpl;
import com.liuqi.business.mapper.WorkDetailMapper;
import com.liuqi.business.model.WorkDetailModel;
import com.liuqi.business.model.WorkDetailModelDto;
import com.liuqi.business.service.WorkDetailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Transactional(readOnly = true)
public class WorkDetailServiceImpl extends BaseServiceImpl<WorkDetailModel,WorkDetailModelDto> implements WorkDetailService {

	@Autowired
	private WorkDetailMapper workDetailMapper;
	

	@Override
	public BaseMapper<WorkDetailModel,WorkDetailModelDto> getBaseMapper() {
		return this.workDetailMapper;
	}
	@Override
	@Transactional
	public void saveDetail(Long workId, String content, String file1, String file2 , String file3 ,Integer type) {
		WorkDetailModel detail=new WorkDetailModel();
		detail.setWorkId(workId);
		detail.setType(type);
		detail.setContent(content);
		detail.setPic1(file1);
		detail.setPic2(file2);
		detail.setPic3(file3);
		this.insert(detail);
	}

    @Override
    public List<WorkDetailModelDto> getByWork(Long id) {
		WorkDetailModelDto search=new WorkDetailModelDto();
		search.setWorkId(id);
        return this.queryListByDto(search,true);
    }
}

package com.liuqi.business.service;

import com.liuqi.base.BaseService;
import com.liuqi.business.model.ListingApplyModel;
import com.liuqi.business.model.ListingApplyModelDto;
import com.liuqi.response.ReturnResponse;

public interface ListingApplyService extends BaseService<ListingApplyModel,ListingApplyModelDto>{


    ReturnResponse updateStatus(Long id, Integer status, Long adminId);
}

package com.liuqi.business.service.impl;


import com.liuqi.base.BaseMapper;
import com.liuqi.base.BaseServiceImpl;
import com.liuqi.business.mapper.CtcOrderLogMapper;
import com.liuqi.business.model.CtcOrderLogModel;
import com.liuqi.business.model.CtcOrderLogModelDto;
import com.liuqi.business.service.CtcOrderLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class CtcOrderLogServiceImpl extends BaseServiceImpl<CtcOrderLogModel, CtcOrderLogModelDto> implements CtcOrderLogService {

    @Autowired
    private CtcOrderLogMapper ctcOrderLogMapper;


    @Override
    public BaseMapper<CtcOrderLogModel, CtcOrderLogModelDto> getBaseMapper() {
        return this.ctcOrderLogMapper;
    }

    @Override
    public void addLog(Long orderId, String opeName, String remark) {
        CtcOrderLogModel log = new CtcOrderLogModel();
        log.setOrderId(orderId);
        log.setName(opeName);
        log.setRemark(remark);
        this.insert(log);
    }

    @Override
    public List<CtcOrderLogModelDto> getByOrderId(Long orderId) {
        CtcOrderLogModelDto search = new CtcOrderLogModelDto();
        search.setOrderId(orderId);
        return this.queryListByDto(search, false);
    }
}

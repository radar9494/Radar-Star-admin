package com.liuqi.business.service;

import com.liuqi.base.BaseService;
import com.liuqi.business.model.OtcOrderRecordLogModel;
import com.liuqi.business.model.OtcOrderRecordLogModelDto;

import java.util.List;

public interface OtcOrderRecordLogService extends BaseService<OtcOrderRecordLogModel,OtcOrderRecordLogModelDto>{

    /**
     * 添加日志
     * @param recordId
     * @param opeName
     * @param remark
     */
    void addLog(Long recordId, String opeName, String remark);

    /**
     * 获取日志
     * @param recordId
     * @return
     */
    List<OtcOrderRecordLogModelDto> getByRecordId(Long recordId);
}

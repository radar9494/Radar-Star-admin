package com.liuqi.business.service;

import com.liuqi.base.BaseService;
import com.liuqi.business.dto.TransferTotalDto;
import com.liuqi.business.model.ApiTransferConfigModel;
import com.liuqi.business.model.ApiTransferModel;
import com.liuqi.business.model.ApiTransferModelDto;
import com.liuqi.external.api.dto.TransferDto;

import javax.validation.Valid;
import java.util.Date;

public interface ApiTransferService extends BaseService<ApiTransferModel,ApiTransferModelDto>{

    /**
     * 转入
     * @param userId
     * @param currencyId
     * @param transferDto
     * @param config
     * @return
     */
    Long publish(Long userId, Long currencyId, TransferDto transferDto, ApiTransferConfigModel config);

    ApiTransferModel getByNameAndNum(String name,String num);

    TransferTotalDto getTodayByUser(String name, Long userId, Long currencyId);

    TransferTotalDto getByUser(String name, Long userId, Long currencyId, Date startDate, Date endDate);

    /**
     * 审核通过
     * @param id
     */
    void agree(Long id,String remark);

    /**
     * 拒绝
     * @param id
     */
    void refuse(Long id,String remark);
}

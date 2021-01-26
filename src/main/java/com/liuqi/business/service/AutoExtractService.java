package com.liuqi.business.service;


import com.liuqi.business.dto.chain.ExtractSearchDto;
import com.liuqi.business.model.ExtractModel;

import java.math.BigDecimal;
import java.util.List;

public interface AutoExtractService {
    /**
     * 提现
     * @param extractModel
     * @param quantity
     * @return
     */
    boolean autoExtract(ExtractModel extractModel, BigDecimal quantity);

    /**
     * 查询到账情况
     */
    List<ExtractSearchDto> queryExtractInfo(List<String> ids, String thirdCurrency);

    /**
     * 接口查询所有需要查询的数据
     */
    void queryInfo();
}

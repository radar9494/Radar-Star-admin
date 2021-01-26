package com.liuqi.business.service;


import com.liuqi.business.dto.TradeInfoDto;

import java.util.List;

public interface IndexService {

    /**
     * 查询常用交易对
     * @return
     */
    List<TradeInfoDto> indexCommTrade();
    /**
     * 查询区域交易对
     * @param areaId
     * @return
     */
    List<TradeInfoDto>  getByAreaId(Long areaId);
}

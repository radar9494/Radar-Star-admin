package com.liuqi.business.service;

import com.liuqi.base.BaseService;
import com.liuqi.business.model.CurrencyAreaModel;
import com.liuqi.business.model.CurrencyAreaModelDto;

import java.util.List;

public interface CurrencyAreaService extends BaseService<CurrencyAreaModel, CurrencyAreaModelDto> {

    /**
     * 获取名称
     * @param id
     * @return
     */
    String getNameById(Long id);
    /**
     * 查询所有币种 缓存（不分页）
     * @return
     */
    List<CurrencyAreaModelDto> findAllArea();
    /**
     * 查询所有启用币种 缓存（不分页）
     * @return
     */
    List<CurrencyAreaModelDto> findAllCanUseArea();

}

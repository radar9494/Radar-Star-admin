package com.liuqi.business.service;

import com.liuqi.base.BaseService;
import com.liuqi.business.dto.RechargeSearchDto;
import com.liuqi.business.model.CurrencyModel;
import com.liuqi.business.model.CurrencyModelDto;

import java.util.List;

public interface CurrencyService extends BaseService<CurrencyModel, CurrencyModelDto> {
    /**
     * 获取排序
     * @return
     */
    Integer getPositionById(Long id);
    /**
     * 获取名称 无返回空字符串
     * @return
     */
    String getNameById(Long id);
    /**
     * 根据名称获取
     * @param name
     * @return
     */
    CurrencyModelDto getByName(String name);

    /**
     * 查询所有币种
     * @return
     */
    List<CurrencyModelDto> getAll();

    /**
     * 查询启用的
     * @return
     */
    List<CurrencyModelDto> getUsing();

    List<Long> getLikeByName(String currencyName,Integer status);

    /**
     * 获取usdt币种id
     * @return
     */
    Long getUsdtId();
    /**
     * 获取基础币种ID
     * @return
     */
    Long getBaseId();
    /**
     * 获取平台币id
     * @return
     */
    Long getPTId();

    /**
     * 查询所有币种
     * @return
     */
    List<RechargeSearchDto> getRecharge();

    /**
     * 根据协议获取接口的币种名称
     * @param currencyId
     * @param protocol
     * @return
     */
    String getThirdCurrency(Long currencyId,Integer protocol);
    /**
     * 根据协议获取接口的币种名称
     * @param currency
     * @param protocol
     * @return
     */
    String getThirdCurrency(CurrencyModel currency,Integer protocol);

    Long getRdbId();

    Long getRdtId();
}

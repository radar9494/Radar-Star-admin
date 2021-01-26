package com.liuqi.business.service;

import com.liuqi.base.BaseService;
import com.liuqi.business.model.ApiTransferConfigModel;
import com.liuqi.business.model.ApiTransferConfigModelDto;

import java.util.Date;

public interface ApiTransferConfigService extends BaseService<ApiTransferConfigModel,ApiTransferConfigModelDto>{

    /**
     * 是否能转入
     *      开关  包含币种  时间
     * @param name
     * @param currencyId
     * @param date
     * @return
     */
    boolean canTransfer(String name, Long currencyId, Date date);
    /**
     * 是否能转入
     *      开关  包含币种  时间
     * @param config
     * @param currencyId
     * @param date
     * @return
     */
    boolean canTransfer(ApiTransferConfigModel config, Long currencyId, Date date);

    ApiTransferConfigModel getByName(String name);

}

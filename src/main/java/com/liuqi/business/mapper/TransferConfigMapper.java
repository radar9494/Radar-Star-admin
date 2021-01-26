package com.liuqi.business.mapper;

import com.liuqi.base.BaseMapper;
import com.liuqi.business.model.TransferConfigModel;
import com.liuqi.business.model.TransferConfigModelDto;
import org.apache.ibatis.annotations.Select;


public interface TransferConfigMapper extends BaseMapper<TransferConfigModel,TransferConfigModelDto>{

    @Select("select * from \n" +
            " \n" +
            "\tt_transfer_config \n" +
            "WHERE\n" +
            "\tcurrency_id =#{currencyId}")
    TransferConfigModelDto getByCurrencyId(Long currencyId);
}

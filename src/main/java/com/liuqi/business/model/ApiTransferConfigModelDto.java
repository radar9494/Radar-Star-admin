package com.liuqi.business.model;

import com.liuqi.business.enums.ApiTransferTypeEnum;
import com.liuqi.business.enums.SwitchEnum;
import lombok.Data;

@Data
public class ApiTransferConfigModelDto extends ApiTransferConfigModel{

    private String onOffStr;

    private String typeStr;

    private String autoConfirmStr;

    public String getOnOffStr() {
        return SwitchEnum.getName(super.getOnOff());
    }

    public String getTypeStr() {
        return ApiTransferTypeEnum.getName(super.getType());
    }

    public String getAutoConfirmStr() {
        return SwitchEnum.getName(super.getAutoConfirm());
    }

    private String currencyNames;


}

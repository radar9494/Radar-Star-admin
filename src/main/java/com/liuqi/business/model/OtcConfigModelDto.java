package com.liuqi.business.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.liuqi.business.enums.SwitchEnum;
import lombok.Data;

@Data
public class OtcConfigModelDto extends OtcConfigModel {

    private String buySwitchStr;

    private String sellSwitchStr;

    public String getBuySwitchStr() {
        return SwitchEnum.getName(super.getBuySwitch());
    }

    public String getSellSwitchStr() {
        return SwitchEnum.getName(super.getSellSwitch());
    }

    private String currencyName;

    @JsonIgnore
    private String sortName="position";
    @JsonIgnore
    private String sortType="desc";
}

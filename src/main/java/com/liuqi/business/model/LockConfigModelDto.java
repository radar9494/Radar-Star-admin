package com.liuqi.business.model;

import com.liuqi.business.enums.SwitchEnum;
import lombok.Data;

@Data
public class LockConfigModelDto extends LockConfigModel{

    private String buySwitchStr;
    private String sellSwitchStr;

    public String getBuySwitchStr() {
        return SwitchEnum.getName(super.getBuySwitch());
    }

    public String getSellSwitchStr() {
        return SwitchEnum.getName(super.getSellSwitch());
    }

    private String currencyName;
    private String tradeName;

}

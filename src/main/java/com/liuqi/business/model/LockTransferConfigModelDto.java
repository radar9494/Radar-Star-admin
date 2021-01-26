package com.liuqi.business.model;

import com.liuqi.business.enums.SwitchEnum;
import lombok.Data;

@Data
public class LockTransferConfigModelDto extends LockTransferConfigModel {


    private String inputSwitchStr;
    private String transferSwitchStr;

    public String getInputSwitchStr() {
        return SwitchEnum.getName(super.getInputSwitch());
    }

    public String getTransferSwitchStr() {
        return SwitchEnum.getName(super.getTransferSwitch());
    }

    private String currencyName;

}

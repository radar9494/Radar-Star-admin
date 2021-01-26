package com.liuqi.business.model;

import com.liuqi.business.enums.SwitchEnum;
import lombok.Data;

@Data
public class SmsConfigModelDto extends SmsConfigModel {


    private String onoffStr;

    public String getOnoffStr() {
        return SwitchEnum.getName(super.getOnoff());
    }

}

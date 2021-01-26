package com.liuqi.business.model;

import com.liuqi.business.enums.SwitchEnum;
import lombok.Data;

@Data
public class SuperNodeConfigModelDto extends SuperNodeConfigModel{

    private String releaseOnoffStr;

    private String joinOnoffStr;

    public String getReleaseOnoffStr() {
        return SwitchEnum.getName(super.getReleaseOnoff());
    }

    public String getJoinOnoffStr() {
        return SwitchEnum.getName(super.getJoinOnoff());
    }

    private String releaseCurrencyName;
    private String joinCurrencyName;

}

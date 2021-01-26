package com.liuqi.business.model;

import com.liuqi.business.enums.OtcStoreTypeEnum;
import lombok.Data;

@Data
public class OtcStoreModelDto extends OtcStoreModel {

    private String typeStr;

    public String getTypeStr() {
        return OtcStoreTypeEnum.getName(super.getType());
    }

    private String currencyName;
    private String userName;
    private String otcName;
    private String realName;

}

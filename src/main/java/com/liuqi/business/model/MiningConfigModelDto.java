package com.liuqi.business.model;

import lombok.Data;

@Data
public class MiningConfigModelDto extends MiningConfigModel{

    private String currencyName;

    private String typeStr;

    public String getTypeStr() {
        if(super.getType()!=null){
            if(super.getType()==0){
                typeStr="矿池";
            }else{
                typeStr="时间戳";
            }
        }
        return typeStr;
    }

    private String image;
}

package com.liuqi.business.model;

import com.liuqi.business.enums.SwitchEnum;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Data
public class ChargeAwardConfigModelDto extends ChargeAwardConfigModel{

    private String onOffStr;

    public String getOnOffStr() {
        return SwitchEnum.getName(super.getOnOff());
    }


    private String awardCurrencyName;

    /**
     * 奖励层数信息
     */
    private List<BigDecimal> awardInfoList;


    public List<BigDecimal> getAwardInfoList() {
        List<BigDecimal> list=new ArrayList<>();
        if(StringUtils.isEmpty(super.getAwardInfo())){
            for(int i=0;i<10;i++){
                list.add(BigDecimal.ZERO);
            }
        }else{
            //已设置的值
            String[] tempArr=super.getAwardInfo().split(",");
            for(String temp:tempArr){
                list.add(new BigDecimal(temp));
            }
            //补充完成
            int bc=10-tempArr.length;
            for(int i=0;i<bc;i++){
                list.add(BigDecimal.ZERO);
            }
        }
        return list;
    }

}

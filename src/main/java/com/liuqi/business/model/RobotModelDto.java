package com.liuqi.business.model;

import com.liuqi.business.enums.SwitchEnum;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class RobotModelDto extends RobotModel {

    //升降  0升 1降
    public static final int UP=0;
    public static final int DOWN=1;

    //类型 0内部价格 1第三方价格
    public static final int TYPE_INNER=0;
    public static final int TYPE_OUTER=1;

    //单据类型 0真实交易  1虚拟交易
    public static final int RUNTYPE_REAL=0;
    public static final int RUNTYPE_VIRTUAL=1;

    private String userName;
    private String realName;
    private String runTypeStr;

    private List<BigDecimal> min;
    private List<BigDecimal> max;
    private List<Integer> sectionState;

    public String getRunTypeStr() {
        runTypeStr="发布买卖";
        if(super.getRunType()!=null &&super.getRunType().equals(RUNTYPE_VIRTUAL)){
            runTypeStr="不发布买卖";
        }
        return runTypeStr;
    }

    private String statusName;


    private String buySwitchStr;

    public String getBuySwitchStr() {
        return SwitchEnum.getName(super.getBuySwitch());
    }

    //卖开关 0关1开
    private String sellSwitchStr;

    public String getSellSwitchStr() {
        return SwitchEnum.getName(super.getSellSwitch());
    }

    //钱包开关 0关1开
    private String walletSwitchStr;

    public String getWalletSwitchStr() {
        return SwitchEnum.getName(super.getWalletSwitch());
    }
    //交易名称
    private String tradeName;
}

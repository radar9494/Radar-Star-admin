package com.liuqi.business.enums;

import com.liuqi.business.dto.SelectDto;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * 开关 0关 1开
 */
public enum SwitchEnum {
    OFF("关", 0),
    ON("开", 1);

    private String name;
    private Integer code;

    SwitchEnum(String name, int code) {
        this.name = name;
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public static String getName(Integer code) {
        if (code != null) {
            for (SwitchEnum e : SwitchEnum.values()) {
                if (e.getCode().equals(code)) {
                    return e.getName();
                }
            }
        }
        return "";
    }

    public static List<SelectDto> getList() {
        List<SelectDto> list = new ArrayList<SelectDto>();
        for (SwitchEnum e : SwitchEnum.values()) {
            list.add(new SelectDto(e.getCode(), e.getName()));
        }
        return list;
    }
    //是否开启
    public static boolean isOn(String switchStr){
        return StringUtils.isNotEmpty(switchStr) && SwitchEnum.ON.getCode().equals(Integer.valueOf(switchStr));
    }
    //是否开启
    public static boolean isOn(Integer switchONOFF){
        return switchONOFF!=null && SwitchEnum.ON.getCode().equals(switchONOFF);
    }
}

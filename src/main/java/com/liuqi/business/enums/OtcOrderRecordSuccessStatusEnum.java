package com.liuqi.business.enums;

import com.liuqi.business.dto.SelectDto;

import java.util.ArrayList;
import java.util.List;

/**
 *
 */
public enum OtcOrderRecordSuccessStatusEnum {
    TRADEING("交易中", 0),
    SUCCESS("成功", 1),
    CANCEL("取消", 2);

    private String name;
    private Integer code;

    OtcOrderRecordSuccessStatusEnum(String name, int code) {
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
            for (OtcOrderRecordSuccessStatusEnum e : OtcOrderRecordSuccessStatusEnum.values()) {
                if (e.getCode().equals(code)) {
                    return e.getName();
                }
            }
        }
        return "";
    }

    public static List<SelectDto> getList() {
        List<SelectDto> list = new ArrayList<SelectDto>();
        for (OtcOrderRecordSuccessStatusEnum e : OtcOrderRecordSuccessStatusEnum.values()) {
            list.add(new SelectDto(e.getCode(), e.getName()));
        }
        return list;
    }

}

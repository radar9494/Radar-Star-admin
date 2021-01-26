package com.liuqi.business.enums;

import com.liuqi.business.dto.SelectDto;

import java.util.ArrayList;
import java.util.List;


public enum ExtractSearchStatusEnum {

    WAIT("待提现", 0),
    PREDEND("准备发送", 1),
    DOING("提现中", 2),
    SUCCESS("提现成功", 3),
    FAIL("提现失败", 4),
    CANCEL("取消", 5);

    private String name;
    private Integer code;

    ExtractSearchStatusEnum(String name, int code) {
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
            for (ExtractSearchStatusEnum e : ExtractSearchStatusEnum.values()) {
                if (e.getCode().equals(code)) {
                    return e.getName();
                }
            }
        }
        return "";
    }

    public static List<SelectDto> getList() {
        List<SelectDto> list = new ArrayList<SelectDto>();
        for (ExtractSearchStatusEnum e : ExtractSearchStatusEnum.values()) {
            list.add(new SelectDto(e.getCode(), e.getName()));
        }
        return list;
    }

}

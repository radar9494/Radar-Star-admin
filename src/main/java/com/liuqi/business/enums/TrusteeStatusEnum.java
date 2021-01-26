package com.liuqi.business.enums;

import com.liuqi.business.dto.SelectDto;

import java.util.ArrayList;
import java.util.List;

/**
 * 币币交易雷子
 */
public enum TrusteeStatusEnum {
    WAIT("待交易", 0),
    SUCCESS("交易完毕", 1),
    CANCEL("取消交易", 2),
    ERROR("异常", 3);

    private String name;
    private Integer code;

    TrusteeStatusEnum(String name, int code) {
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
            for (TrusteeStatusEnum e : TrusteeStatusEnum.values()) {
                if (e.getCode().equals(code)) {
                    return e.getName();
                }
            }
        }
        return "";
    }

    public static List<SelectDto> getList() {
        List<SelectDto> list = new ArrayList<SelectDto>();
        for (TrusteeStatusEnum e : TrusteeStatusEnum.values()) {
            list.add(new SelectDto(e.getCode(), e.getName()));
        }
        return list;
    }

}

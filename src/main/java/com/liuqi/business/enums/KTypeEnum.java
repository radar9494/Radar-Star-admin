package com.liuqi.business.enums;

import com.liuqi.business.dto.SelectDto;

import java.util.ArrayList;
import java.util.List;

/**
 * 1::1m 2:5m 3:15m 4:30m 5:1h 6:1d 7:1week
 */
public enum KTypeEnum {
    ONEM("1分钟", 1),
    FIVEM("5分钟", 2),
    FIFTEENM("15分钟", 3),
    THIRTYM("30分钟", 4),
    ONEH("1小时", 5),
    ONED("1天", 6),
    ONEW("1周", 7);

    private String name;
    private Integer code;

    KTypeEnum(String name, int code) {
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
            for (KTypeEnum e : KTypeEnum.values()) {
                if (e.getCode().equals(code)) {
                    return e.getName();
                }
            }
        }
        return "";
    }

    public static List<SelectDto> getList() {
        List<SelectDto> list = new ArrayList<SelectDto>();
        for (KTypeEnum e : KTypeEnum.values()) {
            list.add(new SelectDto(e.getCode(), e.getName()));
        }
        return list;
    }

}

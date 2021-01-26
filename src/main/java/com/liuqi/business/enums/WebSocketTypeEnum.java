package com.liuqi.business.enums;

import com.liuqi.business.dto.SelectDto;

import java.util.ArrayList;
import java.util.List;

/**
 * websocket参数
 */
public enum WebSocketTypeEnum {
    FAIL("异常", -1),
    K("K线", 0),
    TRADE("交易", 1),
    USER("用户", 2),
    DEPTH("深度图", 3),
    CHECK("心跳检查", 4),
    AREA("区域", 5),
    SUB("订阅成功", 6),
    UNSUB("取消订阅成功", 7),
    OPTIONAL_AREA("自选收藏推送", 12),
    ;

    private String name;
    private Integer code;

    WebSocketTypeEnum(String name, int code) {
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
            for (WebSocketTypeEnum e : WebSocketTypeEnum.values()) {
                if (e.getCode().equals(code)) {
                    return e.getName();
                }
            }
        }
        return "";
    }

    public static List<SelectDto> getList() {
        List<SelectDto> list = new ArrayList<SelectDto>();
        for (WebSocketTypeEnum e : WebSocketTypeEnum.values()) {
            list.add(new SelectDto(e.getCode(), e.getName()));
        }
        return list;
    }

}

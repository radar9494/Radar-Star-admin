package com.liuqi.business.enums;

import com.liuqi.business.dto.SelectDto;

import java.util.ArrayList;
import java.util.List;

/**
 * 协议
 */
public enum MiningWalletLogEnum {
    STATIC("持币收益", 0,""),
    DYNAMIC("推广收益", 1,""),
    TIMESTAMP("时间戳收益", 2,""),
    INNER_TRANSFER("划转", 3,""),
    OUT("转出", 29,""),
    ;

    private String name;
    private Integer code;
    private String show;

    MiningWalletLogEnum(String name, int code, String show) {
        this.name = name;
        this.code = code;
        this.show = show;
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

    public String getShow() {
        return show;
    }

    public void setShow(String show) {
        this.show = show;
    }

    public static String getName(Integer code) {
        if (code != null) {
            for (MiningWalletLogEnum e : MiningWalletLogEnum.values()) {
                if (e.getCode().equals(code)) {
                    return e.getName();
                }
            }
        }
        return "";
    }

    public static String getShow(Integer code) {
        if (code != null) {
            for (MiningWalletLogEnum e : MiningWalletLogEnum.values()) {
                if (e.getCode().equals(code)) {
                    return e.getShow();
                }
            }
        }
        return "";
    }

    public static List<SelectDto> getList() {
        List<SelectDto> list = new ArrayList<SelectDto>();
        for (MiningWalletLogEnum e : MiningWalletLogEnum.values()) {
            list.add(new SelectDto(e.getCode(), e.getName()));
        }
        return list;
    }

    public static boolean hasCode(Integer code) {
        boolean hashCode = false;
        for (MiningWalletLogEnum e : MiningWalletLogEnum.values()) {
            if (e.getCode().equals(code)) {
                hashCode = true;
                break;
            }
        }
        return hashCode;
    }

}

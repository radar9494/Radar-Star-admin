package com.liuqi.business.enums;

import com.liuqi.business.dto.SelectDto;

import java.util.ArrayList;
import java.util.List;

/**
 * 协议
 */
public enum ProtocolEnum {
    ETH("ETH", 1,"erc20"),
    BTC("BTC", 2,"omni"),
    EOS("EOS", 3,"eos"),
    ETC("ETC", 4,"etc"),
    LTC("LTC", 5,"ltc"),
    XRP("XRP", 6,"xrp"),
    TRX("TRX", 7,"trx"),
    RDT("RDT", 8,"rdt"),
    NONE("无用", 100,"");

    private String name;
    private Integer code;
    private String show;

    ProtocolEnum(String name, int code,String show) {
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
            for (ProtocolEnum e : ProtocolEnum.values()) {
                if (e.getCode().equals(code)) {
                    return e.getName();
                }
            }
        }
        return "";
    }

    public static String getShow(Integer code) {
        if (code != null) {
            for (ProtocolEnum e : ProtocolEnum.values()) {
                if (e.getCode().equals(code)) {
                    return e.getShow();
                }
            }
        }
        return "";
    }

    public static List<SelectDto> getList() {
        List<SelectDto> list = new ArrayList<SelectDto>();
        for (ProtocolEnum e : ProtocolEnum.values()) {
            list.add(new SelectDto(e.getCode(), e.getName()));
        }
        return list;
    }

    public static boolean hasCode(Integer code) {
        boolean hashCode = false;
        for (ProtocolEnum e : ProtocolEnum.values()) {
            if (e.getCode().equals(code)) {
                hashCode = true;
                break;
            }
        }
        return hashCode;
    }

}

package com.liuqi.business.enums;

import com.liuqi.business.dto.SelectDto;

import java.util.ArrayList;
import java.util.List;

public enum LockWalletLogTypeEnum {
    SYS("系统", 1,true),
    RELEASE("释放", 2,true),
    RECHARGE("充值", 3,true),
    EXTRACT("提现", 4,true),
    LOCK_INPUT("转入", 5,true),
    LOCK_OUTPUT("转入", 6,true),
    OUT_TRANSFER("外部转入", 7,true);

    private String name;
    private Integer code;
    private boolean using;//是否使用

    LockWalletLogTypeEnum(String name, Integer code, boolean using) {
        this.name = name;
        this.code = code;
        this.using = using;
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
        for (LockWalletLogTypeEnum e : LockWalletLogTypeEnum.values()) {
            if (e.getCode().equals(code)) {
                return e.getName();
            }
        }
        return "";
    }

    public static List<SelectDto> getList() {
        List<SelectDto> list = new ArrayList<SelectDto>();
        for (LockWalletLogTypeEnum e : LockWalletLogTypeEnum.values()) {
            if(e.using) {
                list.add(new SelectDto(e.getCode(), e.getName()));
            }
        }
        return list;
    }
}

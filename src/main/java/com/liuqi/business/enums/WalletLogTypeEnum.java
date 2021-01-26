package com.liuqi.business.enums;

import com.liuqi.business.dto.SelectDto;

import java.util.ArrayList;
import java.util.List;

public enum WalletLogTypeEnum {
    SYS("系统", 1,true,4),
    TRANSFER("转账", 2,false,0),
    RECHARGE("充值", 3,true,0),
    EXTRACT("提现", 4,true,0),
    TRADE_BUY("交易买", 5,true,0),
    TRADE_SELL("交易卖", 6,true,0),
    FINANCING("融资融币", 7,false,0),
    CTC("ctc", 8,false,0),
    OTC("otc", 9,true,1),
    SUPERNODE("超级节点", 10,true,0),
    RELEASE("锁仓释放", 11,true,0),
    OUT_TRANSFER("外部转入", 12,true,0),
    CHARGE_AWARD("手续费分红", 13,true,0),
    LOCK_INPUT("转入锁仓", 14,true,0),
    OTC_INPUT("转入otc", 15,true,0),
    WAIT_RELEASE("待用释放", 16,true,0),
    EXTRACT_OTHER("提现PT", 17,true,0),
    POINT("点卡", 18,true,0),
    BUY_ACTIVATION("购买激活码",19,true,0),
    SCHEDULE("排单",20,true,0),
    EXCHANGE("兑换",22,true,0),
    MINING_IN("挖矿转入",24,true,2),
    RAISE("募集",25,true,0),
    ACTIVE("激活",26,true,0),
    INNER_TRANSFER("划转",27,true,4),
    MINING_REDEMPTION("挖矿赎回",28,true,2),
    MINING_OUT("挖矿转出",29,true,2),
    TRANSFER_FEES("转账矿工费",30,true,0),
    NET_WORK_FREE("网关手续费",31,true,0),
    EXTRACT_RATE("提现手续费", 32,true,0),
    OTC_PLEDGE("申请OTC质押", 33,true,0),
    OTC_PLEDGE_CANCEL("取消OTC质押", 34,true,0),
    OTC_PLEDGE_CANCEL_RATE("取消OTC质押手续费", 35,true,0),
    ;

    private String name;
    private Integer code;
    private boolean using;//是否使用
    private Integer walletType;

    WalletLogTypeEnum(String name, Integer code,boolean using,Integer walletType) {
        this.name = name;
        this.code = code;
        this.using = using;
        this.walletType = walletType;
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
        for (WalletLogTypeEnum e : WalletLogTypeEnum.values()) {
            if (e.getCode().equals(code)) {
                return e.getName();
            }
        }
        return "";
    }

    public Integer getWalletType() {
        return walletType;
    }

    public void setWalletType(Integer walletType) {
        this.walletType = walletType;
    }

    public static List<SelectDto> getList(Integer walletType) {
        List<SelectDto> list = new ArrayList<SelectDto>();
        for (WalletLogTypeEnum e : WalletLogTypeEnum.values()) {
            if(e.using&&e.getWalletType()==walletType||e.getWalletType()==4) {
                list.add(new SelectDto(e.getCode(), e.getName()));
            }
        }
        return list;
    }
}

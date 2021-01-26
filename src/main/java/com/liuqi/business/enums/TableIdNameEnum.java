package com.liuqi.business.enums;

/**
 * 分表id
 */
public enum TableIdNameEnum {
    KDATA("t_k_data_id"),
    TRADE_RECORD("t_trade_record_id"),
    TRADE_RECORD_USER("t_trade_record_user_id"),
    TRUSTEE("t_trustee_id"),
    USER_WALLET_LOG("t_user_wallet_log_id"),
    LOCK_WALLET_LOG("t_lock_wallet_log_id");

    private String name;

    TableIdNameEnum(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


}

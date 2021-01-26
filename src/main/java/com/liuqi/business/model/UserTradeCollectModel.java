package com.liuqi.business.model;

import lombok.Data;

@Data
public class UserTradeCollectModel {
    /**
     * serialVersionUID
     */
    private static final long serialVersionUID = 1L;
    //用户
    private Long userId;
    //交易对
    private Long tradeId;

}

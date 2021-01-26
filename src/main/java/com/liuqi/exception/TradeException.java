package com.liuqi.exception;

import lombok.Data;

@Data
public class TradeException extends RuntimeException {
    //交易异常订单id
    private Long tradeId;

    public TradeException() {
        super();
    }

    public TradeException(String message) {
        super(message);
    }

    public TradeException(String message, Throwable cause) {
        super(message, cause);
    }

    public TradeException(String message, Long tradeId) {
        super(message);
        this.tradeId=tradeId;
    }
}


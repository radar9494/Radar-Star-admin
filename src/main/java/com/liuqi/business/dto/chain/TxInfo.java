package com.liuqi.business.dto.chain;

import com.liuqi.business.enums.TradeStatusEnum;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

@Data
public class TxInfo implements Serializable {

    private String sendAddress;

    private String toAddress;

    private String hash;

    private int block;

    private BigDecimal quantity;

    /**
     * TradeStatusEnum
     NONE("暂无交易", 0),
     DOING("处理中", 2),
     SUCCESS("成功", 3),
     FIAL("失败", 4);
     */
    private Integer status;

    /**
     *  错误信息
     */
    private String msg;

    /**
     * 表示特殊币种
     *      例如 usdt存propertyid值31
     */
    private String id;

    private Date time;

    //ERC20 接受地址和数量
    private String input;
    /**
     * 报文
     */
    private String info;

    private String statusStr;

    public String getStatusStr() {
        return TradeStatusEnum.getName(status);
    }
}

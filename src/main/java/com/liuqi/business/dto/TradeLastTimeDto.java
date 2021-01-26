package com.liuqi.business.dto;

import cn.hutool.core.date.DateField;
import cn.hutool.core.date.DateTime;
import com.liuqi.business.enums.SwitchEnum;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
public class TradeLastTimeDto implements Serializable{
    private Long tradeId;
    private String time;
    private String tradeName;
    private boolean error;
    private TradeInfoDto tradeInfoDto;
    private BigDecimal price;
    //开关 开关 0关 1开
    private Integer tradeSwitch;
    private String tradeSwitchStr;
    public TradeLastTimeDto() {
    }

    public TradeLastTimeDto(Long tradeId, String time, String tradeName, BigDecimal price, TradeInfoDto tradeInfoDto, Integer tradeSwitch) {
        this.tradeId = tradeId;
        this.time = time;
        this.tradeName = tradeName;
        this.price = price;
        this.tradeInfoDto = tradeInfoDto;
        this.tradeSwitch = tradeSwitch;
    }

    public boolean isError() {
        //是否异常 时间为空或者 1分钟未更新时间
        return StringUtils.isEmpty(this.time)|| DateTime.of(this.time,"yyyy-MM-dd HH:mm:ss").compareTo(DateTime.now().offset(DateField.MINUTE,-1))<=0;
    }

    public String getTradeSwitchStr() {
        return SwitchEnum.getName(tradeSwitch);
    }
}

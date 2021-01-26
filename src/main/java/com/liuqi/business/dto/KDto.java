package com.liuqi.business.dto;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

public class KDto implements Serializable{

    private BigDecimal close;//关盘价
    private BigDecimal open;//开盘价
    private BigDecimal high;//最高价
    private BigDecimal low;//最低价
    private BigDecimal volume;//数量
    private Date date;//时间
    private Long time;//时间

    public BigDecimal getClose() {
        if(close.compareTo(BigDecimal.ZERO)==0){
            close=BigDecimal.ZERO;
        }
        return close;
    }

    public void setClose(BigDecimal close) {
        this.close = close;
    }

    public BigDecimal getOpen() {
        if(open.compareTo(BigDecimal.ZERO)==0){
            open=BigDecimal.ZERO;
        }
        return open;
    }

    public void setOpen(BigDecimal open) {
        this.open = open;
    }

    public BigDecimal getHigh() {
        if(high.compareTo(BigDecimal.ZERO)==0){
            high=BigDecimal.ZERO;
        }
        return high;
    }

    public void setHigh(BigDecimal high) {
        this.high = high;
    }

    public BigDecimal getLow() {
        if(low.compareTo(BigDecimal.ZERO)==0){
            low=BigDecimal.ZERO;
        }
        return low;
    }

    public void setLow(BigDecimal low) {
        this.low = low;
    }

    public BigDecimal getVolume() {
        if(volume.compareTo(BigDecimal.ZERO)==0){
            volume=BigDecimal.ZERO;
        }
        return volume;
    }

    public void setVolume(BigDecimal volume) {
        this.volume = volume;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public Long getTime() {
        return date.getTime();
    }

    public void setTime(Long time) {
        this.time = time;
    }
}

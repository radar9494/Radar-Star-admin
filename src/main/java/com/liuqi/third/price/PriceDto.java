package com.liuqi.third.price;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

@Data
public class PriceDto implements Serializable {
    private String searchName;
    private BigDecimal price;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone="GMT+8")
    private Date date;

    public PriceDto() {
    }

    public PriceDto(String searchName, BigDecimal price) {
        this.searchName = searchName;
        this.price = price;
        this.date=new Date();
    }
}

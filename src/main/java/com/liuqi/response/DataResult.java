package com.liuqi.response;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

@Data
public class DataResult<T> implements Serializable{
    private int code;
    private String msg;
    private Long count;
    private List<T> data;
    private BigDecimal total;

    public DataResult(){}

    public DataResult(Long count, List<T> data) {
        this.count = count;
        this.data = data;
    }
}

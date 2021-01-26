package com.liuqi.business.dto;

import lombok.Data;

@Data
public class SelectDto {

    private Object key;
    private Object name;

    public SelectDto() {
    }

    public SelectDto(Object key, Object name) {
        this.key = key;
        this.name = name;
    }
}

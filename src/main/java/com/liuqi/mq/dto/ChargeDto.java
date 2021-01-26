package com.liuqi.mq.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class ChargeDto implements Serializable {

    private Long recordId;
    public ChargeDto() {
    }

    public ChargeDto(Long recordId) {
        this.recordId = recordId;
    }
}

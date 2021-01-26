package com.liuqi.mq.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class ReleaseDto implements Serializable {

    private Long recordId;
    private Long tradeId;
    public ReleaseDto() {
    }

    public ReleaseDto(Long recordId,Long tradeId) {
        this.recordId = recordId;
        this.tradeId = tradeId;
    }
}

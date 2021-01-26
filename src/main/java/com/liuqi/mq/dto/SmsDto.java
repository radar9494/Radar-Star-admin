package com.liuqi.mq.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class SmsDto implements Serializable{



    private String message;
    private String phone;
    private boolean chain;

    public SmsDto() {

    }
    public SmsDto(boolean chain,String phone,String message) {
        this.message = message;
        this.phone = phone;
        this.chain = chain;
    }
}

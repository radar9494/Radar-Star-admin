package com.liuqi.mq.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class EmailDto implements Serializable{

    private String message;
    private String email;
    private String title;
    private String sign;

    public EmailDto() {

    }

    public EmailDto(String message, String email, String title, String sign) {
        this.message = message;
        this.email = email;
        this.title = title;
        this.sign = sign;
    }
}

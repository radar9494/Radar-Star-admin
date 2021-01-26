package com.liuqi.business.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * @author tanyan
 * @create 2020-02=09
 * @description
 */
@Data
public class CaptchaDto implements Serializable {
    private String key;
    private String data;

    public CaptchaDto(){}
    public CaptchaDto(String key, String data){
        this.key=key;
        this.data=data;
    }
}

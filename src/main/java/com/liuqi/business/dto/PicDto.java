package com.liuqi.business.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * @author tanyan
 * @create 2020-02=18
 * @description
 */
@Data
public class PicDto implements Serializable {

    //大图
    private String pic;

    //略缩图  application配置文件中thumb-image配置大小
    private String smallPic;

    public PicDto() {
    }

    public PicDto(String pic, String smallPic) {
        this.pic = pic;
        this.smallPic = smallPic;
    }
}

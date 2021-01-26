package com.liuqi.third.coindog.dto;

import lombok.Data;

import java.util.List;

@Data
public class CoinLivesDto {

    // 快讯id
    private String id;

    //快讯内容
    private String content;

    // 链接名称
    private String link_name;

    // 链接地址
    private String link;

    // 星级 （5、4、3、2、1、0）
    private String grade;

    // 是否标红； red 标红
    private String highlight_color;

    // 图片地址，分别对应原图地址、缩略图地址和图片大小
    private List<CoinImagesDto> images;

    // 快讯时间
    private String created_at;

    // 看涨数量(点赞)
    private String up_counts;

    // 看跌数量(点踩)
    private String down_counts;

    //暂时无用
    private String zan_status;
}

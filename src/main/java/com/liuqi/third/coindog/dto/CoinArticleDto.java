package com.liuqi.third.coindog.dto;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;

@Data
public class CoinArticleDto {

    // 文章ID
    private Long id;

    // 文章地址
    private String url;

    // 文章标题
    private String title;

    // 文章摘要
    private String summary;

    // 文章内容
    private String content;

    // 发表时间
    @JSONField(name = "published_at")
    private String publishedAt;

    // 来源
    private String resource;

    // 来源地址
    @JSONField(name = "resource_url")
    private String resourceUrl;

    // 作者
    private String author;

    //缩略图
    private String thumbnail;
}

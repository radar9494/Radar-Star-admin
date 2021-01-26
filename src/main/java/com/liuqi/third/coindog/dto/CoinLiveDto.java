package com.liuqi.third.coindog.dto;

import lombok.Data;

import java.util.List;

/**
 * 默认值 : id=0&flag=down，获取最新的 {limit} 条快讯记录
 * 使用 : id=100&flag=down, 获取小于id 100 的 {limit} 条快讯记录，适用于获取历史快讯
 * 使用 : id=200&flag=up, 获取大于id 200 的 {limit} 条快讯记录，适用于获取最新快讯（注意，
 * 依然会返回小于id为200的数据）
 */
@Data
public class CoinLiveDto {

    /**
     * （可选）获取的条数限制，取值（1-200），默认 20
     */
    private Integer limit;

    /**
     * （可选）从第几条快讯ID进行查询，默认 0
     */
    private Integer id;

    /**
     * （可选）结合id，是向上（大于）查询还是向下（小于）查询，取值up、down，默认down
     */
    private Integer flag;


    /**
     * 快讯数量
     */
    private Integer news;

    /**
     * 总的快讯数量
     */
    private Integer count;

    /**
     * 结果中最新的快讯ID
     */
    private String top_id;

    /**
     * 结果中最后的快讯ID
     */
    private String bottom_id;

    /**
     * 内容集合
     */
    private List<CoinListDto> list;
}

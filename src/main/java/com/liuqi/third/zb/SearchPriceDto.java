package com.liuqi.third.zb;

import lombok.Data;

@Data
public class SearchPriceDto {

    private String vol; //数量
    private String last;//最新价格
    private String sell;//买价
    private String buy;//买价
    private String high;//最高价
    private String low;//最低价
}

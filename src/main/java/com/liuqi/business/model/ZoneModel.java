package com.liuqi.business.model;

import lombok.Data;

@Data
public class ZoneModel {
    /**
     * serialVersionUID
     */
    private static final long serialVersionUID = 1L;
    //区号
    private String zone;
    //名称
    private String name;
    //中文名称
    private String cnName;
    //位置
    private Integer position;
    //状态 0不1使用 1使用
    private Integer status;
}

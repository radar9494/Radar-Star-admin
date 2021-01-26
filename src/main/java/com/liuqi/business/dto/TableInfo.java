package com.liuqi.business.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class TableInfo implements Serializable{
    //数据库名称
    private String tableName;
    //数据库类型
    private String tableComment;

}

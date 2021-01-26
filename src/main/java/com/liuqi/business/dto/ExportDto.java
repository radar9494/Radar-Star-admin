package com.liuqi.business.dto;

import lombok.Data;

import java.util.List;

/**
 * @author tanyan
 * @create 2020-03=30
 * @description
 */
@Data
public class ExportDto {
    private String tableName;
    private String tableComment;
    private List<ColumnInfo> columnList;

    public ExportDto() {
    }

    public ExportDto(String tableName, String tableComment,List<ColumnInfo> columnList) {
        this.tableName = tableName;
        this.tableComment = tableComment;
        this.columnList = columnList;
    }
}

package com.liuqi.business.service;

import com.liuqi.business.dto.ColumnInfo;
import com.liuqi.business.dto.TableInfo;

import java.util.List;
import java.util.Map;

public interface GenerateService {

    /**
     *
     * @return
     */
    List<TableInfo> getAllTableName();

    /**
     *
     * @return
     */
    List<ColumnInfo> listTableColumn(String tableName);

    /**
     *
     * @return
     */
    List<ColumnInfo> listTableColumnNotIn(String tableName,List<String> list);

    /**
     *
     * @return
     */
    List<ColumnInfo> listTableColumnIn(String tableName,List<String> list);
}

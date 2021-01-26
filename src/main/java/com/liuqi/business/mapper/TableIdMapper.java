package com.liuqi.business.mapper;


import com.liuqi.business.model.TableIdModel;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface TableIdMapper {


    void insert(@Param("tableName") String tableName, @Param("list")List<TableIdModel> list);

    void  deleteHistory(@Param("tableName") String tableName);
}

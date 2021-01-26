package com.liuqi.business.mapper;


import com.liuqi.business.model.AddressModel;
import org.apache.ibatis.annotations.Param;

public interface AddressMapper {

    void updateUsing(@Param("id") Long id, @Param("tableName")String tableName);

    AddressModel getNoUserAddress(@Param("tableName")String tableName);
}

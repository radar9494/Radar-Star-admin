package com.liuqi.business.mapper;

import com.liuqi.base.BaseMapper;
import com.liuqi.business.model.SubAccountModel;
import com.liuqi.business.model.SubAccountModelDto;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;


public interface SubAccountMapper extends BaseMapper<SubAccountModel,SubAccountModelDto>{


    @Delete("delete from t_sub_account where id=#{id}")
    void deleteById(Long id);
    @Select("select count(*) from t_sub_account where user_id=#{userId} and sub_id=#{subId}")
    int getByUserIdSubId(@Param("userId") Long userId,@Param("subId") Long id);
}

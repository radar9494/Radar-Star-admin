package com.liuqi.business.mapper;

import com.liuqi.base.BaseMapper;
import com.liuqi.business.model.TimestampLevelModel;
import com.liuqi.business.model.TimestampLevelModelDto;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;


public interface TimestampLevelMapper extends BaseMapper<TimestampLevelModel,TimestampLevelModelDto>{


    @Select("select * from t_timestamp_level where user_id=#{userId}")
    TimestampLevelModel getByUserId(Long userId);

    @Update("\tupdate t_timestamp_level\n" +
            "\t\tset\n" +
            "\t\ttree_level=tree_level+#{changeTreeLevel},\n" +
            "\t\ttree_info=replace(tree_info,#{userTreeInfo},#{replaceTreeInfo})\n" +
            "\t\twhere  tree_info like CONCAT(#{userTreeInfo},'%');")
    void changeLevel(@Param("userTreeInfo") String treeInfo, @Param("changeTreeLevel") int changeTreeLevel, @Param("replaceTreeInfo") String replaceTreeInfo);
}

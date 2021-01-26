package com.liuqi.business.mapper;

import com.liuqi.base.BaseMapper;
import com.liuqi.business.model.UserModel;
import com.liuqi.business.model.UserModelDto;
import org.apache.ibatis.annotations.Select;

import java.util.Date;
import java.util.List;
import java.util.Map;


public interface UserMapper extends BaseMapper<UserModel, UserModelDto> {

  /**
   * phone
   * email
   * name
   * inviteCode
   * @return
   */
  UserModelDto queryUnique(UserModelDto dto);

  int getTotal();

  List<Long> queryIdByLikeName(String name);


  @Select("select * from t_user where address=#{address}")
  UserModel getByAddress(String address);

  @Select(" SELECT * FROM  (SELECT\n" +
          "\tid \n" +
          "FROM\n" +
          "\tt_user \n" +
          "WHERE\n" +
          "\tactive_date < #{activeDate} ORDER BY active_date DESC LIMIT 5)  t1   UNION ALL\n" +
          "\n" +
          " SELECT * FROM  (SELECT id FROM t_user WHERE active_date > #{activeDate} \n" +
          "ORDER BY\n" +
          "\tactive_date ASC \n" +
          "\tLIMIT 5)t2;")
  List<Long> getTimestamSub(Date activeDate);
@Select("select * from t_user where address=#{phone} or name=#{phone}")
    UserModelDto queryByNameOrAddress(String phone);
     @Select(" SELECT id FROM t_user WHERE active_date < #{activeDate} ORDER BY active_date DESC LIMIT 5 ")
    List<Long> getTimestamSub1(Date activeDate);
    @Select(" SELECT id FROM t_user WHERE active_date > #{activeDate} ORDER BY active_date ASC LIMIT 5  ")
    List<Long> getTimestamSub2(Date activeDate);
}

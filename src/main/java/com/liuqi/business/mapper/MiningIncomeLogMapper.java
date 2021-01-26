package com.liuqi.business.mapper;

import com.liuqi.base.BaseMapper;
import com.liuqi.business.dto.CoordinateDto;
import com.liuqi.business.model.MiningIncomeLogModel;
import com.liuqi.business.model.MiningIncomeLogModelDto;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDate;
import java.util.Date;
import java.util.List;


public interface MiningIncomeLogMapper extends BaseMapper<MiningIncomeLogModel, MiningIncomeLogModelDto> {

    @Select("select num as y, create_time as x from t_mining_income_log  where DATE_SUB(CURDATE(), INTERVAL 7 DAY) <= date(create_time) and user_id = #{userId} and type = #{type} ")
    List<CoordinateDto> findByUserId(@Param("userId") long userId,@Param("type") byte type);


    List<MiningIncomeLogModel> findSumByUserId(@Param("userId")long userId, @Param("date")LocalDate date);


    @Select("select ifnull(sum(num),0) from t_mining_income_log where user_id=#{userId}")
    BigDecimal getTotal(Long userId);
    @Select("select ifnull(sum(num),0) from t_mining_income_log where user_id=#{userId} and type=#{type}")
    BigDecimal getTotalByType(@Param("userId") long userId,@Param("type") int i);

    @Select("select ifnull(sum(num),0) from t_mining_income_log where user_id=#{userId} and type=#{type} and currency_id=#{currencyId}")
    BigDecimal getTotalByTypeByCurrencyId(@Param("userId") long userId,@Param("type") int i,@Param("currencyId")Long currencyId);
    @Select("select ifnull(sum(num),0) from t_mining_income_log where user_id=#{userId}   and currency_id=#{currencyId} and create_time >#{date}")
    BigDecimal yesteartDayTotal(@Param("userId") long userId, @Param("currencyId") Long currencyId,@Param("date") Date date);
}

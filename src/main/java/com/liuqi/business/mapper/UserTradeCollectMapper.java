package com.liuqi.business.mapper;


import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface UserTradeCollectMapper {

    List<Long> getByUserId(@Param("userId") Long userId);

    void deleteByUserId(@Param("userId") Long userId);

    void deleteByTradeId(@Param("tradeId") Long tradeId);

    void saveCollect(@Param("userId") Long userId, @Param("tradeId") Long tradeId);

    void cancelCollect(@Param("userId") Long userId, @Param("tradeId") Long tradeId);

    int hasCollect(@Param("userId") Long userId, @Param("tradeId") Long tradeId);
}

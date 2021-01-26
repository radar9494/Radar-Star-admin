package com.liuqi.business.service;

import com.liuqi.response.ReturnResponse;

import java.math.BigDecimal;

/**
 * description: MiniingHandleService <br>
 * date: 2020/6/9 11:00 <br>
 * author: chenX <br>
 * version: 1.0 <br>
 */
public interface MiningHandleService {

    ReturnResponse config(long userId,Long currencyId);

    ReturnResponse home(long userId);

    ReturnResponse distribution();

    ReturnResponse earningsGraph(long userId);

    ReturnResponse transfer(long userId, BigDecimal num,Long currencyId);

    ReturnResponse rollOut(long userId, BigDecimal num,Long currencyId);

    ReturnResponse withdrawInfo(long userId,Long currencyId);

    ReturnResponse withdraw(long userId, BigDecimal num,Long currencyId);

    void taskHandle(long miningId);

    ReturnResponse miningLog(long userId, int pageNum, int pageSize);

    ReturnResponse miningIncomeInfo(long userId,Long currencyId);

    ReturnResponse miningIncomeLog(long userId,Long currencyId,  int pageNum, int pageSize);

    void income(Long currencyId);


    ReturnResponse getTotal(long userId);

    ReturnResponse init(long userId);

    ReturnResponse getList(long userId, Integer type,Long currencyId);

    ReturnResponse currencyList();

    ReturnResponse getUsing(long userId, Long currencyId);

    ReturnResponse miningIncomeTotal(long userId,Integer type);
}

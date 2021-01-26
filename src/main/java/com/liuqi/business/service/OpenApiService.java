package com.liuqi.business.service;

import com.liuqi.business.dto.api.request.*;
import com.liuqi.business.dto.api.response.WalletRespDto;
import com.liuqi.response.APIResult;

import javax.servlet.http.HttpServletRequest;

/**
 * description: OpenApiService <br>
 * date: 2020/5/20 16:18 <br>
 * author: chenX <br>
 * version: 1.0 <br>
 */
public interface OpenApiService {

    APIResult symbols();

    APIResult trade(String symbol);

    APIResult depth(String symbol,Integer gear);

    APIResult quotation(String symbol);


    APIResult<?> wallet(WalletDto wallet);

    APIResult<?> record(RecordDto record);

    APIResult<?> publish(OrderPublishDto order);

    APIResult<?> cancel(OrderDto order);

    APIResult<?> queryOrder(OrderDto order);

    APIResult<?> queryOrderList(OrderListDto orderList);

    APIResult<?> queryOrderRecordList(OrderRecordDto orderRecord);


    APIResult<?> allWallet(WalletDto wallet, HttpServletRequest request);
}

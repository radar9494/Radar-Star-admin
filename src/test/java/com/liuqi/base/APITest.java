package com.liuqi.base;

import cn.hutool.http.HttpRequest;
import com.liuqi.utils.APISignUtils;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;


/**
 * @author tanyan
 * @create 2020-05=15
 * @description
 */
public class APITest {

    private static final String apiKey="apiKey";
    private static final String secretKey="secretKey";


    public static void request(String method,Map<String,Object> params){
        String timestampStr=System.currentTimeMillis()+"";
        HttpRequest request= HttpRequest.post("http://127.0.0.1:8081"+method);
        request.header("apiKey",apiKey);
        request.header("timestamp",timestampStr);
        String sign= APISignUtils.signData(timestampStr,params,secretKey);
        //  String curSign= APISignUtils.signData(timestampStr,params,secretKey);
        request.header("sign",sign);
        System.out.println("---sign->"+sign);
        String result=request.form(params).execute().body();
        System.out.println(result);
    }

    public static void main(String[] args){
     //   1查询单一币种资产
        APITest.allWallet("RDT");
     //   2查询所有币种资产
//        APITest.wallet("");
//      //  3查询记录
//        APITest.record("USDT");
//        //4发布
//        APITest.publish("BTC-USDT",1,"0.001","1",1);
//      //  5取消交易
//        APITest.cancel(1487L);
//       // 6查询订单
//        APITest.orderQuery(1487L);
//       // 7查询订单列表
//        APITest.orderQueryList("BTC-USDT",null,null,null);
//       // 8成交记录
//        APITest.orderRecordList("BTC-USDT",0,null,null);
    }

    public static void wallet(String currency){
        Map<String,Object> params =new HashMap<>();
        params.put("currency",currency);
        APITest.request("/api/v1/wallet",params);
    }

    public static void allWallet(String currency){
        Map<String,Object> params =new HashMap<>();
        params.put("currency",currency);
        APITest.request("/api/v1/allWallet",params);
    }


    public static void record(String currency){
        Map<String,Object> params =new HashMap<>();
        params.put("currency",currency);
        params.put("pageNum",1);
        params.put("pageSize",10);
        APITest.request("/api/v1/record",params);
    }

    public static void publish(String currency,Integer tradeType,String quantity,String price,Integer transactionType){
        Map<String,Object> params =new HashMap<>();
        params.put("symbol",currency);
        params.put("tradeType",tradeType);
        params.put("quantity",quantity);
        params.put("price",price);
        params.put("transactionType",transactionType);
        APITest.request("/api/v1/order/publish",params);
    }

    public static void cancel(Long orderId){
        Map<String,Object> params =new HashMap<>();
        params.put("orderId",orderId);
        APITest.request("/api/v1/order/cancel",params);
    }

    public static void orderQuery(Long orderId){
        Map<String,Object> params =new HashMap<>();
        params.put("orderId",orderId);
        APITest.request("/api/v1/order/query",params);
    }

    public static void orderQueryList(String symbol, Integer status, Date startTime,Date endTime){
        Map<String,Object> params =new HashMap<>();
        params.put("symbol",symbol);
        params.put("status",status);
        params.put("pageNum",1);
        params.put("pageSize",10);
        params.put("startTime",startTime);
        params.put("endTime",endTime);
        APITest.request("/api/v1/order/queryList",params);
    }
    public static void orderRecordList(String symbol,Integer tradeType, Date startTime,Date endTime){
        Map<String,Object> params =new HashMap<>();
        params.put("symbol",symbol);
        params.put("tradeType",tradeType);
        params.put("startTime",startTime);
        params.put("endTime",endTime);
        params.put("pageNum",1);
        params.put("pageSize",10);
        APITest.request("/api/v1/orderRecord/queryList",params);
    }
}

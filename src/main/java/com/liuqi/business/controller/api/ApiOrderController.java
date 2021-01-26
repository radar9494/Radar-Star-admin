package com.liuqi.business.controller.api;

import com.alibaba.fastjson.JSONObject;
import com.liuqi.business.dto.api.request.*;
import com.liuqi.business.enums.api.ApiResultEnum;
import com.liuqi.business.model.UserApiKeyModelDto;
import com.liuqi.business.service.OpenApiService;
import com.liuqi.business.service.UserApiKeyService;
import com.liuqi.response.APIResult;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.Map;

@RestController
@RequestMapping(value = "/api/v1")
public class ApiOrderController extends BaseApiController {

    @Autowired
    private OpenApiService openApiService;
    @Autowired
    private UserApiKeyService userApiKeyService;

    /**
     * 1获取资产信息
     * @return
     */
    @ApiOperation(value = "1获取资产信息")
    @ApiImplicitParams({
            @ApiImplicitParam(dataType ="String",name="currency" ,value = "币种",required = false,paramType = "query")
    })
    @PostMapping(value = "/wallet")
    public APIResult<?> wallet(@Valid WalletDto wallet, BindingResult bindingResult, HttpServletRequest request){
        System.out.println("wallet请求参数"+ JSONObject.toJSONString(wallet));
        if (bindingResult.hasErrors()) {
            return APIResult.fail(ApiResultEnum.ERROR_PARAMS,this.getErrorInfo(bindingResult));
        }
        Map<String,Object> params= beanToMap(wallet);
        params.remove("userId");
        //验证是否合法
        APIResult<Long> result=checkUser(request,params);
        if(!APIResult.success(result)){
            return result;
        }
        Long userId=result.getData();
        wallet.setUserId(userId);
        return openApiService.wallet(wallet);
    }

    /**
     * 2获取流水
     * @return
     */
    @ApiOperation(value = "获取流水")
    @ApiImplicitParams({
            @ApiImplicitParam(dataType ="String",name="currency" ,value = "币种",required = false,paramType = "query"),
            @ApiImplicitParam(dataType ="int",name="type" ,value = "类型",required = false,paramType = "query"),
            @ApiImplicitParam(dataType ="date",name="startTime" ,value = "开始时间",required = false,paramType = "query"),
            @ApiImplicitParam(dataType ="date",name="endTime" ,value = "结束时间",required = false,paramType = "query"),
            @ApiImplicitParam(dataType ="date",name="pageNum" ,value = "页数",required = false,paramType = "query"),
            @ApiImplicitParam(dataType ="date",name="pageSize" ,value = "每页条数",required = false,paramType = "query"),
    })
    @PostMapping(value = "/record")
    public APIResult<?> record(@Validated RecordDto record, BindingResult bindingResult, HttpServletRequest request) {
        System.out.println("record请求参数"+ JSONObject.toJSONString(record));
        if (bindingResult.hasErrors()) {
            return APIResult.fail(ApiResultEnum.ERROR_PARAMS,this.getErrorInfo(bindingResult));
        }
        Map<String,Object> params= beanToMap(record);
        params.remove("userId");
        //验证是否合法
        APIResult<Long> result=checkUser(request,params);
        if(!APIResult.success(result)){
            return result;
        }
        Long userId=result.getData();
        record.setUserId(userId);
        return openApiService.record(record);
    }

    /**
     * 发布交易
     *
     * @return
     */
    @ApiOperation(value = "发布交易")
    @ApiImplicitParams({
            @ApiImplicitParam(dataType = "String", name = "symbol", value = "交易对", required = true, paramType = "query"),
            @ApiImplicitParam(dataType = "int", name = "tradeType", value = "交易类型0买  1卖", required = true, paramType = "query"),
            @ApiImplicitParam(dataType = "BigDecimal", name = "quantity", value = "数量", required = true, paramType = "query"),
            @ApiImplicitParam(dataType = "BigDecimal", name = "price", value = "价格", required = true, paramType = "query"),
            @ApiImplicitParam(dataType = "int", name = "transactionType", value = "0 限价交易 1 市价交易", required = true, paramType = "query")
    })
    @PostMapping(value = "/order/publish")
    public APIResult<?> publish(@Validated OrderPublishDto order, BindingResult bindingResult, HttpServletRequest request) {
        System.out.println("publish请求参数"+ JSONObject.toJSONString(order));
        if (bindingResult.hasErrors()) {
            return APIResult.fail(ApiResultEnum.ERROR_PARAMS,this.getErrorInfo(bindingResult));
        }
        Map<String,Object> params= beanToMap(order);
        params.remove("userId");
        //验证是否合法
        APIResult<Long> result=checkUser(request,params);
        if(!APIResult.success(result)){
            return result;
        }
        Long userId=result.getData();
        order.setUserId(userId);
        return openApiService.publish(order);
    }

    /**
     * 取消交易
     *
     * @return
     */
    @ApiOperation(value = "取消交易")
    @ApiImplicitParams({
            @ApiImplicitParam(dataType = "Long", name = "orderId", value = "订单id", required = true, paramType = "query")
    })
    @PostMapping(value = "/order/cancel")
    public APIResult<?> cancel(@Validated OrderDto order, BindingResult bindingResult, HttpServletRequest request) {
        System.out.println("cancel请求参数"+ JSONObject.toJSONString(order));
        if (bindingResult.hasErrors()) {
            return APIResult.fail(ApiResultEnum.ERROR_PARAMS,this.getErrorInfo(bindingResult));
        }
        Map<String,Object> params= beanToMap(order);
        params.remove("userId");
        //验证是否合法
        APIResult<Long> result=checkUser(request,params);
        if(!APIResult.success(result)){
            return result;
        }
        Long userId=result.getData();
        order.setUserId(userId);
        return openApiService.cancel(order);
    }


    /**
     * 查询交易
     *
     * @return
     */
    @ApiOperation(value = "查询订单")
    @ApiImplicitParams({
            @ApiImplicitParam(dataType = "Long", name = "orderId", value = "订单id", required = true, paramType = "query")
    })
    @PostMapping(value = "/order/query")
    public APIResult<?> queryOrder(@Validated OrderDto order, BindingResult bindingResult, HttpServletRequest request) {
        System.out.println("query请求参数"+ JSONObject.toJSONString(order));
        if (bindingResult.hasErrors()) {
            return APIResult.fail(ApiResultEnum.ERROR_PARAMS,this.getErrorInfo(bindingResult));
        }
        Map<String,Object> params= beanToMap(order);
        params.remove("userId");
        //验证是否合法
        APIResult<Long> result=checkUser(request,params);
        if(!APIResult.success(result)){
            return result;
        }
        Long userId=result.getData();
        order.setUserId(userId);
        return openApiService.queryOrder(order);
    }


    /**
     * 查询订单列表
     *
     * @return
     */
    @ApiOperation(value = "查询订单列表")
    @ApiImplicitParams({
            @ApiImplicitParam(dataType = "String", name = "symbol", value = "交易对", required = true, paramType = "query"),
            @ApiImplicitParam(dataType ="int",name="status" ,value = "状态",required = false,paramType = "query"),
            @ApiImplicitParam(dataType ="date",name="startTime" ,value = "开始时间",required = false,paramType = "query"),
            @ApiImplicitParam(dataType ="date",name="endTime" ,value = "结束时间",required = false,paramType = "query"),
            @ApiImplicitParam(dataType ="date",name="pageNum" ,value = "页数",required = false,paramType = "query"),
            @ApiImplicitParam(dataType ="date",name="pageSize" ,value = "每页条数",required = false,paramType = "query"),
    })
    @PostMapping(value = "/order/queryList")
    public APIResult<?> queryList(@Validated OrderListDto orderList, BindingResult bindingResult, HttpServletRequest request) {
        System.out.println("queryList请求参数"+ JSONObject.toJSONString(orderList));
        if (bindingResult.hasErrors()) {
            return APIResult.fail(ApiResultEnum.ERROR_PARAMS,this.getErrorInfo(bindingResult));
        }
        Map<String,Object> params= beanToMap(orderList);
        params.remove("userId");
        //验证是否合法
        APIResult<Long> result=checkUser(request,params);
        if(!APIResult.success(result)){
            return result;
        }
        Long userId=result.getData();
        orderList.setUserId(userId);
        return openApiService.queryOrderList(orderList);
    }


    /**
     * 查询订单列表
     *
     * @return
     */
    @ApiOperation(value = "查询成交列表")
    @ApiImplicitParams({
            @ApiImplicitParam(dataType = "String", name = "symbol", value = "交易对", required = true, paramType = "query"),
            @ApiImplicitParam(dataType ="int",name="tradeType" ,value = "0买 1卖",required = false,paramType = "query"),
            @ApiImplicitParam(dataType ="date",name="startTime" ,value = "开始时间",required = false,paramType = "query"),
            @ApiImplicitParam(dataType ="date",name="endTime" ,value = "结束时间",required = false,paramType = "query"),
            @ApiImplicitParam(dataType ="date",name="pageNum" ,value = "页数",required = false,paramType = "query"),
            @ApiImplicitParam(dataType ="date",name="pageSize" ,value = "每页条数",required = false,paramType = "query"),
    })
    @PostMapping(value = "/orderRecord/queryList")
    public APIResult<?> queryList(@Validated OrderRecordDto orderRecord, BindingResult bindingResult, HttpServletRequest request) {
        System.out.println("orderRecord请求参数"+ JSONObject.toJSONString(orderRecord));
        if (bindingResult.hasErrors()) {
            return APIResult.fail(ApiResultEnum.ERROR_PARAMS,this.getErrorInfo(bindingResult));
        }
        Map<String,Object> params= beanToMap(orderRecord);
        params.remove("userId");
        //验证是否合法
        APIResult<Long> result=checkUser(request,params);
        if(!APIResult.success(result)){
            return result;
        }
        Long userId=result.getData();
        orderRecord.setUserId(userId);
        return openApiService.queryOrderRecordList(orderRecord);
    }



    /**
     * 1获取资产信息
     * @return
     */
    @ApiOperation(value = "1获取所有资产信息")
    @ApiImplicitParams({
            @ApiImplicitParam(dataType ="String",name="currency" ,value = "币种",required = false,paramType = "query")
    })
    @PostMapping(value = "/allWallet")
    public APIResult<?> allWallet(@Valid WalletDto wallet, BindingResult bindingResult, HttpServletRequest request){
        System.out.println("allWallett请求参数"+ JSONObject.toJSONString(wallet));
        if (bindingResult.hasErrors()) {
            return APIResult.fail(ApiResultEnum.ERROR_PARAMS,this.getErrorInfo(bindingResult));
        }
        Map<String,Object> params= beanToMap(wallet);
        params.remove("userId");
        //验证是否合法
        APIResult<Long> result=checkUser(request,params);
        if(!APIResult.success(result)){
            return result;
        }
        Long userId=result.getData();
        wallet.setUserId(userId);
        return openApiService.allWallet(wallet,request);
    }


}

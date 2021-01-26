package com.liuqi.response;

import com.alibaba.fastjson.JSONObject;
import com.liuqi.business.enums.api.ApiResultEnum;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;
import java.util.HashMap;

/**
 * @author tanyan
 * @create 2020-07=20
 * @description
 */
@Data
@ToString
public class APIResult<T> implements Serializable {

    //返回编码
    private int code;
    //返回信息
    private String msg;
    //返回对象
    private T data;
    //返回时间
    private Long timestamp;

    public APIResult() {
        this.timestamp= System.currentTimeMillis();
    }

    public APIResult(int code, String msg, T data) {
        this.code = code;
        this.msg = msg;
        this.data = data;
        this.timestamp=System.currentTimeMillis();
    }

    public static APIResult<JSONObject> success(){
        return new APIResult<>(ApiResultEnum.SUCCESS.getCode(),ApiResultEnum.SUCCESS.getName(),new JSONObject());
    }

    public static<T> APIResult<T> success(T data){
        return new APIResult<>(ApiResultEnum.SUCCESS.getCode(),ApiResultEnum.SUCCESS.getName(),data);
    }

    public static<T> APIResult<T> success(String msg, T data){
        return new APIResult<>(ApiResultEnum.SUCCESS.getCode(),msg,data);
    }

    public static APIResult<JSONObject> successMsg(String msg){
        return new APIResult<>(ApiResultEnum.SUCCESS.getCode(),msg,new JSONObject());
    }

    public static<T> APIResult<T> fail(ApiResultEnum rs, T data){
        if(data==null){
            return new APIResult(rs.getCode(),rs.getName(),new HashMap<>());
        }
        return new APIResult(rs.getCode(),rs.getName(),data);
    }


    public static boolean success(APIResult<?> result){
        return result!=null && ApiResultEnum.SUCCESS.getCode().equals(result.getCode());
    }

}

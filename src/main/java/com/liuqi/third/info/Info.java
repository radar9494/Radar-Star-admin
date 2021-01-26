package com.liuqi.third.info;


import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpUtil;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import java.util.List;

public class Info {
    public static final String URL="http://api.coindog.com/live/list";


    public static  List<InfoDto> getNewInfo(){
        List<InfoDto> infoList=null;
        String url=URL+"?limit=2";
        HttpRequest request=HttpUtil.createGet(url);
        String result=request.execute().body();
        JSONObject obj=JSONObject.parseObject(result);
        JSONArray arr=obj.getJSONArray("list");
        if(arr!=null && arr.size()>0){
            String lives=JSONObject.parseObject(arr.get(0).toString()).getString("lives");
            infoList=JSONArray.parseArray(lives,InfoDto.class);
        }
        return infoList;
    }
}

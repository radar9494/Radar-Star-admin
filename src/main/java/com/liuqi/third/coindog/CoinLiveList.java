package com.liuqi.third.coindog;

import com.alibaba.fastjson.JSONObject;
import com.liuqi.third.coindog.dto.CoinListDto;
import com.liuqi.third.coindog.dto.CoinLiveDto;
import com.liuqi.third.coindog.dto.CoinLivesDto;
import com.liuqi.utils.HttpsUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * 金色财经快讯
 * @author yjx
 */
@Component
@Slf4j
public class CoinLiveList {

    private static String URL= "http://api.coindog.com/live/list";

    public List<CoinLivesDto> getLiveList(String limit, String id, String flag){

        List<CoinLivesDto> results = new ArrayList<>();

        String secretKey = "secretKey";
        String accessKey = "accessKey";

        String url = URL+"?limit="+limit+"&id="+id+"&flag="+flag+"&"+ accessKey +"&"+ secretKey;

        List<CoinListDto> list = new ArrayList<>();
        try{
            String s = HttpsUtils.sendGet(url,"UTF-8");

            CoinLiveDto coinLiveDto = JSONObject.parseObject(s, CoinLiveDto.class);
            if (coinLiveDto==null){
                return null;
            }
            list = coinLiveDto.getList();
        }catch (Exception e){
            e.printStackTrace();
        }

        for (CoinListDto coinListDto : list) {
            List<CoinLivesDto> lives = coinListDto.getLives();
            results.addAll(lives);
        }

        return results;

    }

    public static void main(String[] args) {
        //默认值 : id=0&flag=down，获取最新的 {limit} 条快讯记录
//        getLiveList("20","0","down");

        //使用 : id=200&flag=up, 获取大于id 200 的 {limit} 条快讯记录，适用于获取最新快讯（注意，依然会返回小于id为200的数据）
        CoinLiveList c = new CoinLiveList();
        List<CoinLivesDto> list = c.getLiveList("20", "0", "down");


            for (CoinLivesDto life : list) {
//                System.out.println(UnicodeUtil.toString(life.getContent()));
                System.out.println(life.getId()+":"+life.getContent()+":"+life.getHighlight_color());
            }

//        String s = UnicodeUtil.toString("\\u3010\\u52a8\\u6001 | Coinmerce\\u4ea4\\u6613\\u6240\\u5f00\\u653e\\u6bd4\\u7279\\u5e01\\u94bb\\u77f3\\uff08BCD)\\u6b27\\u5143\\u6cd5\\u5e01\\u5151\\u6362\\u3011\\u6b27\\u6d32\\u6570\\u5b57\\u8d27\\u5e01\\u4ea4\\u6613\\u6240Coinmerce\\u5728\\u5176\\u6cd5\\u5e01\\u4ea4\\u6613\\u533a\\u4e0a\\u7ebf\\u6bd4\\u7279\\u5e01\\u94bb\\u77f3\\uff08BCD)\\uff0c\\u652f\\u6301\\u6b27\\u5143\\u6cd5\\u5e01\\u4ea4\\u6613BCD\\u3002");
//        System.out.println(s);
//        StringBuilder sb = new StringBuilder(s);
//
//        String substring = sb.substring(sb.indexOf("】")+1);
//        String substring1 = sb.substring(1, sb.indexOf("】"));
//        System.out.println(substring);
//        System.out.println(substring1);
    }
}

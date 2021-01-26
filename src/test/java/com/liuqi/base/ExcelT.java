package com.liuqi.base;

import cn.hutool.core.io.FileUtil;
import cn.hutool.poi.excel.ExcelReader;
import cn.hutool.poi.excel.ExcelUtil;
import com.alibaba.fastjson.JSONObject;

import java.util.List;
import java.util.Map;

public class ExcelT {
    public static void main(String[] args) {
        ExcelReader reader = ExcelUtil.getReader(FileUtil.file("E;/BM.xlsx"));
        Map<String, String> head = reader.getHeaderAlias();
        System.out.println("head-->" + JSONObject.toJSONString(head));
        List<Map<String, Object>> date = reader.readAll();
        int i = 1;
        for (Map<String, Object> map : date) {
            System.out.println("data-->" + i + "-->" + JSONObject.toJSONString(head));
            i++;
        }
    }
}

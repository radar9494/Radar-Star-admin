package com.liuqi.base;

import cn.hutool.poi.excel.ExcelReader;
import cn.hutool.poi.excel.ExcelUtil;

import java.io.File;
import java.io.FileWriter;
import java.util.List;
import java.util.Map;

public class Excel2SqlAddressUtil {

    public static void main(String[] args){

        try {
            getEOSList("hneos","t_address_eos");
            getXRPList("hnxrp","t_address_xrp");
            //getList("E:/BtcAddress0613User.xlsx","t_address_btc");
            //getList("E:/EtcAddress0613User.xlsx","t_address_etc");
            //getList("E:/EthAddress0614.xlsx","t_address_eth");
            //getList("E:/XcuAddress.xlsx","t_address_xcu");
            //getList("E:/LtcAddress0613User.xlsx","t_address_ltc");
            //getList("E:/XrpAddress0613User.xlsx","t_address_xrp");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private static void getList(String path,String tableName)throws Exception{
        System.out.println(tableName+"--开始--");
        File file = new File("E:/"+tableName+".sql");
        FileWriter fw = new FileWriter(file);
        ExcelReader reader = ExcelUtil.getReader(path);
        List<Map<String,Object>> list=reader.readAll();
        if(list!=null && list.size()>0){
            int count=0;
            for(Map<String,Object> map:list){
                count++;
                String str = "insert into "+tableName+"(path,address)values('"+map.get("path").toString()+"','" +  map.get("address").toString() + "');";
                if(count%5000==0){
                    str=str+"commit;";
                }
                fw.write(str);
                System.out.println("执行--》" + count);

            }
        }
        System.out.println(tableName+"--结束--");
        Thread.sleep(5000);
    }
    private static void getEOSList(String project,String tableName)throws Exception{
        System.out.println("eos--开始--");
        File file = new File("E:/"+tableName+".sql");
        FileWriter fw = new FileWriter(file);
        for(int i=0;i<50000;i++){
            String str = "insert into "+tableName+"(path,address)values('" + (100000 + i) + "','"+project+ (100000 + i) + "');";
            fw.write(str);
            System.out.println("eos执行--》" + i);
        }
        System.out.println("eos--结束--");
        Thread.sleep(5000);
    }

    private static void getXRPList(String project,String tableName)throws Exception{
        System.out.println("xrp--开始--");
        File file = new File("E:/"+tableName+".sql");
        FileWriter fw = new FileWriter(file);
        for(int i=0;i<50000;i++){
            String str = "insert into "+tableName+"(path,address)values('" + (100000 + i) + "','"+project+ (100000 + i) + "');";
            fw.write(str);
            System.out.println("xrp执行--》" + i);
        }
        System.out.println("xrp--结束--");
        Thread.sleep(5000);
    }
}

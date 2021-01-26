package com.liuqi.business.dto;

import com.liuqi.utils.MysqlType2BeanUtil;
import com.liuqi.utils.Tool;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;

@Data
public class ColumnInfo implements Serializable{
    //数据库名称
    private String name;
    //数据库类型
    private String type;
    private String columnType;
    //
    private String columnKey;
    private String extra;
    //备注
    private String comment;

    private String entityName;
    //首字母大写字段
    private String capEntityName;
    //对应实体类型
    private String entityType;
    //是否数字类型
    private boolean num;
    //是否数字类型
    private boolean str;

    public String getEntityName() {
        if(StringUtils.isNotEmpty(name)){
            entityName= Tool.lineToHump(name);
        }
        return entityName;
    }

    public String getCapEntityName() {
        if(StringUtils.isNotEmpty(getEntityName())) {
            capEntityName=StringUtils.capitalize(entityName);
        }
        return capEntityName;
    }

    public String getEntityType() {
        if(StringUtils.isNotEmpty(type)) {
            entityType= MysqlType2BeanUtil.getBeanType(type);
        }
        return entityType;
    }

    public boolean isNum() {
        return MysqlType2BeanUtil.isNum(type);
    }

    public boolean isStr() {
        return "String".equalsIgnoreCase(getEntityType());
    }

    //前台返回字段  1input  2radio  3select 4日期
    private int addUpdateType;
    //枚举类型 0否 1是
    private int enumType;
    //枚举json
    private String enumStr;
    //是否查询 0否 1是
    private String frontShow;
    //前台返回字段  1input  3select 4日期
    private String frontQueryType;



    //枚举名称
    private String enumName="";
}

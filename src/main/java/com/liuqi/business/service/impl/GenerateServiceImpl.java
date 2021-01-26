package com.liuqi.business.service.impl;

import cn.hutool.db.SqlRunner;
import com.liuqi.business.dto.ColumnInfo;
import com.liuqi.business.dto.TableInfo;
import com.liuqi.business.service.GenerateService;
import com.liuqi.config.ShardingMastSlaveConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;
import java.sql.SQLException;
import java.util.List;

@Service
public class GenerateServiceImpl implements GenerateService{

    @Value("${default.dataSource}")
    private String dataSourceName;
    @Autowired
    private ShardingMastSlaveConfig shardingMastSlaveConfig;


    private SqlRunner getSqlRunner(){
        DataSource dataSource= shardingMastSlaveConfig.getDataSources().get(dataSourceName);
        return SqlRunner.create(dataSource);
    }

    @Override
    public List<TableInfo> getAllTableName() {
        SqlRunner runner =getSqlRunner();
        List<TableInfo> list = null;
        try {
            list = runner.query("select table_name,table_comment from information_schema.TABLES where TABLE_SCHEMA=(select database())",TableInfo.class,null);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    @Override
    public List<ColumnInfo> listTableColumn(String tableName) {
        SqlRunner runner = getSqlRunner();
        List<ColumnInfo> resultList = null;
        StringBuffer sb=new StringBuffer(" select COLUMN_NAME as name,DATA_TYPE as type,COLUMN_TYPE as columnType,COLUMN_KEY as columnKey,EXTRA as extra,COLUMN_COMMENT as comment ");
        sb.append(" from information_schema.COLUMNS where TABLE_SCHEMA = (select database()) and TABLE_NAME=?");
        try {
            resultList = runner.query(sb.toString(),ColumnInfo.class,tableName);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return resultList;
    }

    @Override
    public List<ColumnInfo> listTableColumnNotIn(String tableName,List<String> list) {
        SqlRunner runner = getSqlRunner();
        List<ColumnInfo> resultList = null;
        StringBuffer sb=new StringBuffer(" select COLUMN_NAME as name,DATA_TYPE as type,COLUMN_KEY as columnKey,EXTRA as extra,COLUMN_COMMENT as comment ");
        sb.append(" from information_schema.COLUMNS where TABLE_SCHEMA = (select database()) and TABLE_NAME=?").append(" and COLUMN_NAME not in(");
        for(int i=0,length=list.size();i<length;i++){
            sb.append("'").append(list.get(i)).append("'");
            if(i<length-1){
                sb.append(",");
            }
        }
        sb.append(")");
        try {
            resultList = runner.query(sb.toString(),ColumnInfo.class,tableName);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return resultList;
    }

    @Override
    public List<ColumnInfo> listTableColumnIn(String tableName,List<String> list) {
        SqlRunner runner=getSqlRunner();
        List<ColumnInfo> resultList = null;
        StringBuffer sb=new StringBuffer("select COLUMN_NAME as name,DATA_TYPE as type,COLUMN_KEY as columnKey,EXTRA as extra,COLUMN_COMMENT as comment ");
        sb.append("from information_schema.COLUMNS where TABLE_SCHEMA = (select database()) and TABLE_NAME=?").append(" and COLUMN_NAME  in(");
        for(int i=0,length=list.size();i<length;i++){
            sb.append("'").append(list.get(i)).append("'");
            if(i<length-1){
                sb.append(",");
            }
        }
        sb.append(")");
        try {
            resultList = runner.query(sb.toString(),ColumnInfo.class,tableName);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return resultList;
    }
}

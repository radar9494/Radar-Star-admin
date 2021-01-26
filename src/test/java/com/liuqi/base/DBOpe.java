package com.liuqi.base;

import cn.hutool.db.DbUtil;
import cn.hutool.db.Entity;
import cn.hutool.db.SqlRunner;
import cn.hutool.db.ds.simple.SimpleDataSource;
import com.alibaba.druid.pool.DruidDataSource;

import javax.sql.DataSource;
import java.sql.SQLException;
import java.util.List;

public class DBOpe {


    public static void main(String[] args){
        DataSource ds = new SimpleDataSource("jdbc:mysql://localhost:3306/dbName", "root", "123456");
        SqlRunner runner = SqlRunner.create(ds);

        try {
            //新增
            int count=runner.insert(Entity.create("t_table")//表名
                                .set("id",1)//复赋值字段
                                .set("name","ty"));
            //查询
            List<Entity> list=runner.find(Entity.create("t_table").set("id",1));

            //修改
            runner.update(Entity.create("t_table").set("name","ty"),
                          Entity.create("t_table").set("id",1));

            //修改
            runner.del(Entity.create("t_table").set("id",1));
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }
}

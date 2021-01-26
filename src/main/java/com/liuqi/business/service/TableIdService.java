package com.liuqi.business.service;


import com.liuqi.business.enums.TableIdNameEnum;

import java.util.List;

public interface TableIdService {
    /**
     * 获取id
     *
     * @return
     */
    Long getNextId(TableIdNameEnum tableIdName);

    /**
     * 获取id
     *
     * @return
     */
    void batchId(String key,String tableIdName);

    /**
     * 获取表
     * @param tableIdName
     */
    void deleteHistory(String tableIdName);
}

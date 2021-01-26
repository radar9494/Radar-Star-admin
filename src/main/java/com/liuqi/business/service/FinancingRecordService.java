package com.liuqi.business.service;

import com.liuqi.base.BaseService;
import com.liuqi.business.model.FinancingConfigModel;
import com.liuqi.business.model.FinancingRecordModel;
import com.liuqi.business.model.FinancingRecordModelDto;

import java.math.BigDecimal;
import java.util.List;

public interface FinancingRecordService extends BaseService<FinancingRecordModel,FinancingRecordModelDto>{

    /**
     * 获取我的融资信息
     *
     * @param configId
     * @param userId
     * @return
     */
    List<FinancingRecordModelDto> getByConfigAndUserId(Long configId, Long userId);


    /**
     * 参与融资
     *
     * @param config
     * @param quantity
     * @param userId
     */
    void apply(FinancingConfigModel config, BigDecimal quantity, Long userId);

    /**
     * 添加融资币到钱包
     *
     * @param recordId
     * @param userId  当前操作用户
     * @param checkUser 检查记录用户==userId
     * @param checkStatus 检查状态==未处理
     */
    void addToWallet(Long recordId, Long userId, boolean checkUser,boolean checkStatus,Long adminId);
    /**
     * 添加融资币到钱包
     *
     * @param record
     * @param userId  当前操作用户
     * @param checkUser 检查记录用户==userId
     * @param checkStatus 检查状态==未处理
     */
    void addToWallet(FinancingRecordModel record, Long userId, boolean checkUser,boolean checkStatus,Long adminId);

    /**
     * 获取以融资数量
     *
     * @param configId
     * @return
     */
    BigDecimal getConfigQuantity(Long configId);

    /**
     * 领取
     * @param config
     * @param userId
     */
    void give(FinancingConfigModel config, Long userId);
}

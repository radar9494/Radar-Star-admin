package com.liuqi.business.service;

import com.github.pagehelper.PageInfo;
import com.liuqi.base.BaseService;
import com.liuqi.business.model.OtcOrderModelDto;
import com.liuqi.business.model.OtcOrderRecordModel;
import com.liuqi.business.model.OtcOrderRecordModelDto;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public interface OtcOrderRecordService extends BaseService<OtcOrderRecordModel,OtcOrderRecordModelDto>{

    /**
     * 获取我未付款订单的数量
     * @param userId
     * @return
     */
    int getMyBuyNoPay(Long userId);

    /**
     * 生成订单记录
     * @param userId
     * @param order
     * @param quantity
     * @return
     */
    Long createRecord(Long userId, OtcOrderModelDto order, BigDecimal quantity);

    /**
     * 判断主单是否能取消  判断子单中  状态为WAIT，WAITPAY，WAITGATHERING，APPEAL的数量
     * @param orderId
     * @return
     */
    boolean canCancel(Long orderId);

    /**
     * 接单  修改状态为待支付
     * @param userId
     * @param recordId
     */
    void take(Long userId,Long recordId);
    /**
     * 付款  修改状态为代收款
     * @param userId
     * @param recordId
     */
    void pay(Long userId,Long recordId,Integer payType);

    /**
     * 收款  修改状态为成功
     * @param userId
     * @param recordId
     */
    void gathering(Long userId,Long recordId);
    /**
     * 取消
     * @param userId
     * @param recordId
     */
    void cancel(Long userId,Long recordId);
    /**
     * 申诉
     * @param userId
     * @param recordId
     */
    void appeal(Long userId,Long recordId,String content);
    /**
     * 申诉成功
     * @param userId
     * @param recordId
     */
    void appealSuccess(Long userId,Long recordId,String remark);
    /**
     * 申诉失败
     * @param userId
     * @param recordId
     */
    void appealFail(Long userId,Long recordId,String remark);

    /**
     * 超时自动取消  判断订单状态=传入的状态时取消
     * @param recordId
     * @param status
     */
    void autoCancel(Long recordId, Integer status);

    /**
     * 统计otc数量
     * @param userIdList
     * @param tradeType
     * @return
     */
    Map<Long, BigDecimal> stat(List<Long> userIdList, Integer tradeType) ;

    /**
     * 获取已交易成功数量
     * @param orderId
     * @return
     */
    BigDecimal getSuccessQuantity(Long orderId);

    OtcOrderRecordModel getMyWaitPay(Long userId);
}

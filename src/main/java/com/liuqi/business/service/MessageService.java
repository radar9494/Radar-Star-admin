package com.liuqi.business.service;

import com.liuqi.base.BaseService;
import com.liuqi.business.model.MessageModel;
import com.liuqi.business.model.MessageModelDto;

public interface MessageService extends BaseService<MessageModel,MessageModelDto>{

    /**
     * 插入消息
     * @param userId
     * @param content
     */
    void insertMessage(Long userId,String content);

    /**
     * 插入错误消息  做判断  只插入一次
     * @param userId
     * @param type
     * @param content
     */
    void insertMessageError(Long userId,Integer type,String content);

    /**
     * 阅读
     * @param userId
     * @param id
     */
    void readMessage(Long userId,Long id);

    /**
     * 获取用户未读信息数量
     * @param userId
     * @return
     */
    Integer getNotReadCount(Long userId);

    /**
     * 获取用户今日插入的类型
     * @param userId
     * @param type
     * @return
     */
    MessageModelDto getTodayByType(Long userId,Integer type);
}

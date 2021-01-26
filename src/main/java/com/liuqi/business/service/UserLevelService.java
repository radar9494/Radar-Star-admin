package com.liuqi.business.service;

import com.liuqi.base.BaseService;
import com.liuqi.business.dto.UserTreeDto;
import com.liuqi.business.model.UserLevelModel;
import com.liuqi.business.model.UserLevelModelDto;

import java.util.List;

public interface UserLevelService extends BaseService<UserLevelModel,UserLevelModelDto>{

    /**
     * 获取用户的层级信息
     * @param userId
     * @return
     */
    UserLevelModelDto getByUserId(Long userId);

    /**
     *  初始化用户层级信息
      * @param userId
     * @param inviteCode
     */
    void initLevel(Long userId, String inviteCode);

    /**
     * 获取所有下级
     * @param userId
     * @return
     */
    List<Long> getAllSubIdList(Long userId);

    /**
     * 获取指定层级下级
     * @param userId
     * @return
     */
    List<Long> getAssignSubIdList(Long userId,Integer nextLevel);

    /**
     * 获取上级列表
     *      不包含自己
     * @param userId
     * @return
     */
    List<String> getParent(Long userId);

    /**
     * 修改层级
     * @param m
     * @param parentId
     */
    void changeLevel(UserLevelModelDto m, Long parentId);

    /**
     * 获取下级
     * @param parentId
     * @return
     */
    List<UserTreeDto> getTreeByParentId(Long parentId);
    /**
     * 获取用户树
     * @param userId
     * @return
     */
    List<UserTreeDto> getTreeByUserId(Long userId);

    List<UserTreeDto> findTreeByParentId(long userId);

    List<Long> findByParentId(long userId);
}

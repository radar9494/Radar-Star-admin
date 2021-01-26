package com.liuqi.business.service;

import com.liuqi.base.BaseService;
import com.liuqi.business.dto.MenuDto;
import com.liuqi.business.model.MenuModel;
import com.liuqi.business.model.MenuModelDto;

import java.util.List;
import java.util.Map;

public interface MenuService extends BaseService<MenuModel, MenuModelDto> {

    List<MenuModelDto> getInfoByRoleId(Long roleId);

    /**
     * 获取用户角色的菜单
     *
     * @return
     */
    List<Long> getByRoleId(Long roleId);


    /**
     * 通过父id查询菜单
     * @param parentId
     * @return
     */
    List<MenuModelDto> findByParentId(Long parentId);


    /**
     * 查询一级菜单
     *
     * @return
     */
    List<MenuModelDto> findFirstLevel();

    /**
     * 获取所有的菜单
     */
    List<MenuDto> getAllMenuAndSelect(Long roleId);

    /**
     * 用户的菜单
     *
     * @param userId
     * @return
     */
    List<MenuDto> getByUserId(Long userId);

    /**
     * 查询菜单权限
     * @param map
     * @return
     */
    List<MenuModelDto> queryMenuAndPermission(Map<String,Object> map);
    /**
     * 获取所有子菜单
     * @return
     */
    List<MenuModelDto> getAllChild();
}

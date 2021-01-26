package com.liuqi.business.service;

import java.util.List;

/**
 * 角色
 */
public interface RoleMenuService {
    /**
     * 批量插入
     * @param roleId
     * @param menuId
     */
    void insert(Long roleId, Long menuId);
    /**
     * 删除角色菜单
     * @param roleId
     */
    void deleteByRoleId(Long roleId);

    /**
     * 关联菜单 权限
     * @param roleId
     * @param menus
     * @param permissions
     */
    void rel(Long roleId, List<Long> menus,List<Long> permissions);
}

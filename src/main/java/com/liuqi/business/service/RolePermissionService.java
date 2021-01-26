package com.liuqi.business.service;

import java.util.List;

/**
 * 角色权限
 */
public interface RolePermissionService {
    /**
     * 批量插入
     * @param roleId
     * @param permissionId
     */
    void insert(Long roleId, Long permissionId);
    /**
     * 删除角色权限
     * @param roleId
     */
    void deleteByRoleId(Long roleId);

    /**
     * 关联群贤
     * @param roleId
     * @param permissionIds
     */
    void rel(Long roleId, List<Long> permissionIds);

    /**
     * 获取角色权限
     * @param role
     * @return
     */
    List<String> getPermissionNameByRole(Long roleId);
    /**
     * 获取角色权限id
     * @param role
     * @return
     */
    List<Long> getPermissionIdByRole(Long roleId);
}

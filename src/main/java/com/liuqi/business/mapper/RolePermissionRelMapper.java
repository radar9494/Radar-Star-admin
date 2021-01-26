package com.liuqi.business.mapper;

import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 角色菜单Mapper
 */
public interface RolePermissionRelMapper {
    void insert(@Param("roleId") Long roleId, @Param("permissionId") Long permissionId);

    void deleteByRoleId(Long roleId);

    List<String> getPermissionNameByRole(Long roleId);

    List<Long> getPermissionIdByRole(Long roleId);
}

package com.liuqi.business.mapper;

import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 角色菜单Mapper
 */
public interface RoleMenuRelMapper {
    void insert(@Param("roleId") Long roleId, @Param("menuId") Long menuId);

    void deleteByRoleId(Long roleId);

}

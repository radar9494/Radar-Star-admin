package com.liuqi.business.service.impl;


import com.liuqi.business.mapper.RoleMenuRelMapper;
import com.liuqi.business.service.RoleMenuService;
import com.liuqi.business.service.RolePermissionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.RowIdLifetime;
import java.util.List;

@Service
@Transactional(readOnly = true)
public class RoleMenuServiceImpl implements RoleMenuService {

    @Autowired
    private RoleMenuRelMapper roleMenuRelMapper;

    @Autowired
    private RolePermissionService rolePermissionService;

    /**
     * 批量插入
     *
     * @param roleId
     * @param menuIds
     */
    @Override
    @Transactional
    public void insert(Long roleId, Long menuId) {
        roleMenuRelMapper.insert(roleId, menuId);
    }

    /**
     * 删除角色菜单
     *
     * @param roleId
     */
    @Override
    @Transactional
    public void deleteByRoleId(Long roleId) {
        roleMenuRelMapper.deleteByRoleId(roleId);
    }

    @Override
    @Transactional
    public void rel(Long roleId, List<Long> menus, List<Long> permissions) {
        //角色菜单
        this.deleteByRoleId(roleId);
        if(menus!=null && menus.size()>0) {
            for(Long menuId:menus) {
                this.insert(roleId, menuId);
            }
        }
        //角色权限
        rolePermissionService.rel(roleId,permissions);
    }
}

package com.liuqi.business.service.impl;


import com.liuqi.business.mapper.RoleMenuRelMapper;
import com.liuqi.business.mapper.RolePermissionRelMapper;
import com.liuqi.business.service.RoleMenuService;
import com.liuqi.business.service.RolePermissionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class RolePermissionServiceImpl implements RolePermissionService {

    @Autowired
    private RolePermissionRelMapper rolePermissionRelMapper;


    /**
     * 批量插入
     *
     * @param roleId
     * @param permissionIds
     */
    @Override
    @Transactional
    public void insert(Long roleId, Long permissionId) {
        rolePermissionRelMapper.insert(roleId, permissionId);
    }

    /**
     * 删除角色菜单
     *
     * @param roleId
     */
    @Override
    @Transactional
    public void deleteByRoleId(Long roleId) {
        rolePermissionRelMapper.deleteByRoleId(roleId);
    }

    @Override
    @Transactional
    public void rel(Long roleId, List<Long> permissionIds) {
        this.deleteByRoleId(roleId);
        if(permissionIds!=null && permissionIds.size()>0) {
            for (Long permissionId:permissionIds) {
                this.insert(roleId, permissionId);
            }
        }

    }

    @Override
    public List<String> getPermissionNameByRole(Long roleId) {
        return rolePermissionRelMapper.getPermissionNameByRole(roleId);
    }

    @Override
    public List<Long> getPermissionIdByRole(Long roleId) {
        return rolePermissionRelMapper.getPermissionIdByRole(roleId);
    }
}

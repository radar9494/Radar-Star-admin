package com.liuqi.business.mapper;

import com.liuqi.base.BaseMapper;
import com.liuqi.business.model.MenuModel;
import com.liuqi.business.model.MenuModelDto;

import java.util.List;
import java.util.Map;


public interface MenuMapper extends BaseMapper<MenuModel,MenuModelDto>{

    List<MenuModelDto> findByParentId(Long parentId);

    List<MenuModelDto> findFirstLevel();

    List<Long> getByRoleId(Long roleId);

    List<MenuModelDto> getInfoByRoleId(Long roleId);

    /**
     * 获取 菜单及下的权限按钮信息
     * @param map
     * @return
     */
    List<MenuModelDto> queryMenuAndPermission(Map<String, Object> map);

    List<MenuModelDto> getAllChild();
}

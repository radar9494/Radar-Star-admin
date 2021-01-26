package com.liuqi.business.model;

import lombok.Data;

import java.io.Serializable;

@Data
public class RoleMenuRel implements Serializable {

    /**
     * 编号
     */
    private Long id;

    /**
     * 角色id
     */
    private Long roleId;
    /**
     * 菜单id
     */
    private Long menuId;
}
